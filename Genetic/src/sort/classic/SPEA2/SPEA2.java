package sort.classic.SPEA2;

import criterion.interfaces.ICriterion;
import distance.Euclidean;
import distance.interfaces.IDistance;
import interfaces.ISpecimen;
import normalization.MinMaxNormalization;
import normalization.interfaces.INormalization;
import sort.Log;
import sort.functions.Duplication;
import sort.functions.Front;
import sort.functions.RangeMaker;
import sort.functions.Sort;
import sort.interfaces.ILog;
import sort.interfaces.ISorter;
import standard.Common;
import standard.Point;
import standard.Range;
import utils.InsertionSortDouble;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SPEA2 implements ISorter
{

    private ArrayList<ISpecimen> _pareto = null;
    private ArrayList<ISpecimen> _archive = null;

    private IDistance _distance = null;

    private double _epsilon = Common.EPSILON;
    private boolean _rangeFromCriterion = false;
    private int _archiveSize = 0;
    private int _truncDepth = 1;

    public static class Params
    {
        public IDistance _distance = null;
        public double _epsilon = Common.EPSILON;
        public boolean _rangeFromCriterion = false;
        public int _archiveSize = 0;
        public int _truncDepth = 1;

        public Params()
        {

        }
    }

    public SPEA2(Params p)
    {
        this._epsilon = p._epsilon;
        this._rangeFromCriterion = p._rangeFromCriterion;
        this._archiveSize = p._archiveSize;
        this._truncDepth = p._truncDepth;
        this._archive = new ArrayList<>(_archiveSize);
        this._distance = p._distance;
    }

    @Override
    public ArrayList<ISpecimen> getPareto()
    {
        return _pareto;
    }

    @Override
    public ArrayList<ISpecimen> getReproductionPool()
    {
        return this._archive;
    }

    @Override
    public ILog sort(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, int generation)
    {
        // IGNORE DUPLICATES
        ArrayList<ISpecimen> duplicates = Duplication.extractDuplicates(specimens, criteria, _epsilon, 2);
        // CREATE UNION
        ArrayList<ISpecimen> union = createUnion(specimens, _archive);

        // CREATE NORMALIZATIONS
        IDistance distance;
        if (_distance == null)
        {
            ArrayList<INormalization> normalizations;
            ArrayList<Range> range;
            // --- CREATE RANGES --------
            {
                if (_rangeFromCriterion)
                {
                    range = new ArrayList<>(criteria.size());
                    range.addAll(criteria.stream().map(c -> c.getRange().get("spea2")).collect(Collectors.toList()));
                } else
                {
                    range = RangeMaker.getRange(union, criteria);
                }

                normalizations = new ArrayList<>(criteria.size());

                normalizations.addAll(range.stream().map(r -> new MinMaxNormalization(r.left, r.right)).collect(Collectors.toList()));
            }

            Euclidean.Params p = new Euclidean.Params();
            p.normalization = normalizations;
            p.weight = null;
            distance = new Euclidean(p);
        }
        else distance = _distance;


        // EVALUATE AND SORT
        evaluateAndSort(union, criteria, distance);

        // CREATE NEW ARCHIVE
        ArrayList<ISpecimen> newArchive = new ArrayList<>(_archiveSize);

        // COPY PARETO SOLUTIONS TO NEW ARCHIVE
        {
            ArrayList<ISpecimen> pareto = Front.getPareto(union, criteria, _epsilon);
            newArchive.addAll(pareto.stream().collect(Collectors.toList()));
        }


        // TRUNC ARCHIVE
        if (newArchive.size() > this._archiveSize) this.truncArchive(newArchive, criteria, distance);

        // CREATE PARETO)
        {
            this._pareto = new ArrayList<>(_archiveSize);
            _pareto.addAll(newArchive.stream().collect(Collectors.toList()));
        }

        if (newArchive.size() < this._archiveSize) this.fillArchive(newArchive, union);

        if (newArchive.size() != this._archiveSize)
        {
            if (newArchive.size() < this._archiveSize)
                System.out.println("SPEA2 Size Error (Less)");
            else
                System.out.println("SPEA2 Size Error (More)");
        }

        // --- SET NEW ARCHIVE --------
        this._archive = newArchive;


        // --- RESTORE SHIFTED
        specimens.addAll(duplicates.stream().collect(Collectors.toList()));

        int shifted = duplicates.size();
        ILog log = new Log();
        log.addLog("shifted", shifted);
        return log;
    }

    private void evaluateAndSort(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, IDistance distance)
    {
        // CALC WEAKNESS
        int weakness[] = Front.calculateWeakness(specimens, criteria, _epsilon);
        // CALC - KNEAREST DIST

        int k = (int) (Math.sqrt(specimens.size()) + 0.5d);
        {
            for (int s = 0; s < specimens.size(); s++)
            {
                InsertionSortDouble.init(k);

                Point A = new Point(specimens.get(s).getAlternative().getEvaluationVector(criteria));
                for (int c = 0; c < specimens.size(); c++)
                {
                    if (s == c) continue;
                    Point B = new Point(specimens.get(c).getAlternative().getEvaluationVector(criteria));

                    double d = distance.getDistance(A, B);
                    InsertionSortDouble.step(d);
                }

                double d = InsertionSortDouble.data[k - 1];
                specimens.get(s).getAlternative().setAggregatedEvaluation(d);
            }

            for (int s = 0; s < specimens.size(); s++)
            {
                double d = specimens.get(s).getAlternative().getAggregatedEvaluation();
                d = 1.0d / (d + 2.0d);
                double eval = (double) weakness[s] + 0.25d + (0.5d * d);
                specimens.get(s).getAlternative().setAggregatedEvaluation(eval);
            }
        }

        Sort.sortByAggregatedValue(specimens);
    }


    public void truncArchive(ArrayList<ISpecimen> archive, ArrayList<ICriterion> criteria, IDistance distance)
    {
        int DEPTH = this._truncDepth;

        int over = archive.size() - this._archiveSize;
        if (over == 0) return;

        // FOR EACH OVER
        for (int o = 0; o < over; o++)
        {
            int minPointer = 0;
            double minDistance = Common.MAX_DOUBLE;

            for (int s = 0; s < archive.size(); s++)
            {
                InsertionSortDouble.init(DEPTH);

                Point A = new Point(archive.get(s).getAlternative().getEvaluationVector(criteria));
                for (int c = 0; c < archive.size(); c++)
                {
                    if (s == c) continue;
                    Point B = new Point(archive.get(c).getAlternative().getEvaluationVector(criteria));

                    double d = distance.getDistance(A, B);
                    InsertionSortDouble.step(d);
                }

                double d = InsertionSortDouble.data[DEPTH - 1];

                if (d < minDistance)
                {
                    minDistance = d;
                    minPointer = s;
                }
            }

            archive.remove(minPointer);
        }
    }

    public void fillArchive(ArrayList<ISpecimen> newArchive, ArrayList<ISpecimen> union)
    {
        int remain = this._archiveSize - newArchive.size();
        if (remain == 0) return;

        for (ISpecimen anUnion : union)
        {
            if (anUnion.getAlternative().getAggregatedEvaluation() < 1.0d) continue;
            newArchive.add(anUnion);
            remain--;
            if (remain == 0) break;
        }
    }

    private ArrayList<ISpecimen> createUnion(ArrayList<ISpecimen> specimens, ArrayList<ISpecimen> archive)
    {
        ArrayList<ISpecimen> union = new ArrayList<>(specimens.size() + archive.size());
        union.addAll(specimens.stream().collect(Collectors.toList()));
        union.addAll(archive.stream().collect(Collectors.toList()));
        return union;
    }
}
