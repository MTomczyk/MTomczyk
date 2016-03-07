package sort.preference.single.order;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.filter.interfaces.IFilter;
import decision.manager.ordering.OrderDMRegressionManager;
import distance.interfaces.IDistance;
import interfaces.ISpecimen;
import sort.Log;
import sort.functions.Duplication;
import sort.functions.Front;
import sort.functions.Sort;
import sort.interfaces.ILog;
import sort.interfaces.ISorter;
import standard.Common;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SPEA2_NEMO0 implements ISorter
{

    private ArrayList<ISpecimen> _pareto = null;
    private ArrayList<ISpecimen> _archive = null;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private IDistance _distance = null;

    private double _epsilon = Common.EPSILON;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private boolean _rangeFromCriterion = false;
    private int _archiveSize = 0;
    @SuppressWarnings("FieldCanBeLocal")
    private OrderDMRegressionManager _dm = null;
    private IFilter _filter = null;

    public static class Params
    {
        public OrderDMRegressionManager _dm = null;
        public IDistance _distance = null;
        public double _epsilon = Common.EPSILON;
        public boolean _rangeFromCriterion = false;
        public int _archiveSize = 0;
        public IFilter _filter = null;

        public Params()
        {

        }
    }

    public SPEA2_NEMO0(Params p)
    {
        this._epsilon = p._epsilon;
        this._rangeFromCriterion = p._rangeFromCriterion;
        this._archiveSize = p._archiveSize;
        this._archive = new ArrayList<>(_archiveSize);
        this._distance = p._distance;
        this._dm = p._dm;
        this._filter = p._filter;
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

        // CREATE UNION
        ArrayList<ISpecimen> union = createUnion(specimens, _archive);

        // IGNORE DUPLICATES
        ArrayList<ISpecimen> duplicates = Duplication.extractDuplicates(union, criteria, _epsilon, 2);

        // EVALUATE AND SORT
        evaluateAndSort(union, criteria, generation);

        // CREATE NEW ARCHIVE
        ArrayList<ISpecimen> newArchive = new ArrayList<>(_archiveSize);

        // COPY PARETO SOLUTIONS TO NEW ARCHIVE
        {
            ArrayList<ISpecimen> pareto = Front.getPareto(union, criteria, _epsilon);
            for (ISpecimen s : pareto)
            {
                newArchive.add(s);
                if (newArchive.size() == _archiveSize) break;
            }

        }

        // CREATE PARETO
        {
            this._pareto = new ArrayList<>(_archiveSize);
            _pareto.addAll(newArchive.stream().collect(Collectors.toList()));
        }

        if (newArchive.size() < this._archiveSize) this.fillArchive(newArchive, union);

        if (newArchive.size() != this._archiveSize)
        {
            if (newArchive.size() < this._archiveSize)
                System.out.println("SPEA2 Size Error (Less): " + newArchive.size());
            else
                System.out.println("SPEA2 Size Error (More): " + newArchive.size());
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

    private void evaluateAndSort(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, int generation)
    {
        // CALC WEAKNESS
        int weakness[] = Front.calculateWeakness(specimens, criteria, _epsilon);
        // CALC - KNEAREST DIST

        // UPDATE PREFERENCES WITH PARETO
        if (_dm.getElicitationRule().isElicitationTime(generation))
        {
            ArrayList<IAlternative> paretoAlternatives = new ArrayList<>(specimens.size());
            for (int i = 0; i < weakness.length; i++)
            {
                if (weakness[i] == 0) paretoAlternatives.add(specimens.get(i).getAlternative());
            }

            paretoAlternatives = sort.common.Common.applyChoiceFilter(_filter, paretoAlternatives, criteria);

            _dm.updatePreferences(paretoAlternatives, generation);
            _dm.updateEstimatedDM(paretoAlternatives, criteria);

        }

        for (int s = 0; s < specimens.size(); s++)
        {
            double d = weakness[s];
            double e;
            Double eval[] = _dm.evaluateAlternative(specimens.get(s).getAlternative());

            if ((eval != null) && (eval[0] != null))
            {
                e = d + 0.6d - (0.3d * eval[0]);
            }
            else e = d;

            specimens.get(s).getAlternative().setAggregatedEvaluation(e);
        }

        Sort.sortByAggregatedValue(specimens);
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
