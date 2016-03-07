package sort.classic.NSGAII;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

import criterion.interfaces.ICriterion;
import normalization.MinMaxNormalization;
import normalization.interfaces.INormalization;
import sort.Log;
import sort.functions.*;
import sort.interfaces.ILog;
import sort.interfaces.ISorter;
import standard.Common;
import standard.Range;
import interfaces.ISpecimen;
import tree.binary.BinaryTree;

public class NSGAII implements ISorter
{

    private ArrayList<ISpecimen> _pareto = null;
    private ArrayList<ISpecimen> _reproductionPool = null;

    private int _populSize = 0;

    private double _epsilon = Common.EPSILON;
    private boolean _rangeFromCriterion = false;
    private boolean _steadyState = false;

    private ArrayList <INormalization> _normalization = null;

    public static class Params
    {
        public Params()
        {

        }

        public Params(int populSize)
        {
            this(populSize, Common.EPSILON, false);
        }

        public Params(int populSize, double epsilon, boolean rangeFromCriterion)
        {
            this._populSize = populSize;
            this._epsilon = epsilon;
            this._rangeFromCriterion = rangeFromCriterion;
            this._steadyState = false;
        }


        public int _populSize = 0;
        public double _epsilon = Common.EPSILON;
        public boolean _rangeFromCriterion = false;
        public ArrayList <INormalization> _normalization = null;
        public boolean _steadyState = false;
    }

    // TODO POSPRAWDZAC!

    public NSGAII(Params p)
    {
        this._populSize = p._populSize;
        this._epsilon = p._epsilon;
        this._rangeFromCriterion = p._rangeFromCriterion;
        this._normalization = p._normalization;
        this._steadyState = p._steadyState;
    }

    // STEADY STATE
    private ArrayList <BinaryTree<ISpecimen>> _sortedTrees = null;

    @Override
    public ArrayList<ISpecimen> getPareto()
    {
        return _pareto;
    }

    @Override
    public ArrayList<ISpecimen> getReproductionPool()
    {
        return this._reproductionPool;
    }

    @Override
    public ILog sort(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, int generation)
    {
        // IGNORE DUPLICATES
        ArrayList<ISpecimen> duplicates = Duplication.extractDuplicates(specimens, criteria, _epsilon, 2);

        // --- CREATE RANGES --------
        if (_normalization == null)
        {
            ArrayList <Range> range;
            if (_rangeFromCriterion)
            {
                range = new ArrayList<>(criteria.size());
                range.addAll(criteria.stream().map(c -> c.getRange().get("nsgaii")).collect(Collectors.toList()));
            }
            else
            {
                range = RangeMaker.getRange(specimens, criteria);
            }

            _normalization = new ArrayList<>(criteria.size());

            _normalization.addAll(range.stream().map(r -> new MinMaxNormalization(r.left, r.right)).collect(Collectors.toList()));
        }

        // CREATE FRONTS
        LinkedList<LinkedList<Integer>> front = Front.getDominationFrontList(specimens, criteria, _epsilon);

        // CALC CROWDING
        double crowding[];
        if (_steadyState)
        {
            if (_sortedTrees == null)
                _sortedTrees = Sort.sortedTrees(specimens, criteria);

            for (int i = 0; i < criteria.size(); i++)
                _sortedTrees.get(i).insert(specimens.get(specimens.size() - 1));

            crowding = CrowdingDistance.getDistanceFront(front, _sortedTrees, specimens, _normalization, criteria);

            for (int i = 0; i < criteria.size(); i++)
                _sortedTrees.get(i).insert(specimens.get(specimens.size() - 1));

        }
        else crowding = CrowdingDistance.getDistanceFront(front, specimens, _normalization, criteria);



        // CALC GLOBAL
        int lv = 0;
        for (LinkedList<Integer> l : front)
        {
            for (Integer s : l)
            {
                specimens.get(s).getAlternative().setAggregatedEvaluation((double) lv + 0.999d - crowding[s] * 0.5d);
            }

            lv++;
        }

        // SORT WITH 1. FRONT NO 2. CROWDING DISTANCE
        Sort.sortByAggregatedValue(specimens);

        // CREATE PARETO / AFTER KILL
        {
            ArrayList<ISpecimen> toConsider = new ArrayList<>(_populSize);
            toConsider.addAll(specimens.stream().collect(Collectors.toList()));
            this._pareto = Front.getPareto(toConsider, criteria, _epsilon);
        }


        // CREATE REPRODUCTION POOL
        this._reproductionPool = specimens;


        // AT DUPLICATES AT THE END
        // TODO IGNORE AND DEAL INSIDE GENETIC/RUNNER
        specimens.addAll(duplicates.stream().collect(Collectors.toList()));

        int shifted = duplicates.size();
        ILog log = new Log();
        log.addLog("shifted", shifted);
        return log;
    }
}
