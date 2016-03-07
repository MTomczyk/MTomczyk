package sort.preference.single.order;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.maker.ordering.interfaces.IOrderingDM;
import interfaces.ISpecimen;
import sort.Log;
import sort.functions.CrowdingDistance;
import sort.functions.Duplication;
import sort.functions.Front;
import sort.functions.Sort;
import sort.interfaces.ILog;
import sort.interfaces.ISorter;
import standard.Common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Michał on 2014-11-13.
 */
public class NEMO0_KNOWN implements ISorter
{
    public static class Params
    {
        public IOrderingDM _dm = null;
        public int _populSize = 0;
        public double _epsilon = Common.EPSILON;
    }

    // -- DATA
    private ArrayList<ISpecimen> _pareto = null;
    private ArrayList<ISpecimen> _reproductionPool = null;

    private IOrderingDM _dm = null;

    private int _populSize = 0;

    double _epsilon = Common.EPSILON;

    public NEMO0_KNOWN(Params p)
    {
        this._dm = p._dm;
        this._epsilon = p._epsilon;
        this._populSize = p._populSize;
    }


    @Override
    public ILog sort(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, int generation)
    {
        // IGNORE DUPLICATES
        ArrayList<ISpecimen> duplicates = Duplication.extractDuplicates(specimens, criteria, _epsilon, 2);

        // GET FRONTS AND PARETO
        LinkedList<LinkedList<Integer>> front = Front.getDominationFrontList(specimens, criteria, _epsilon);

        double crowding[] = null;
        if ((_dm == null) || (_dm.getModel() == null))
            crowding = CrowdingDistance.getDistanceFrontWithNormalization(front, specimens, criteria);

        // EVALUATE
        int frontNo = 0;
        for (LinkedList<Integer> f : front)
        {
            for (Integer s : f)
            {
                IAlternative a = specimens.get(s).getAlternative();
                double v;
                if (crowding != null)
                {
                    v = (double) frontNo + (1.0d - _epsilon - (crowding[s] * 0.5d));
                } else
                {
                    double eval = _dm.evaluate(a);
                    v = (double) frontNo + (1.0d - _epsilon - (eval * 0.5d));
                }


                a.setAggregatedEvaluation(v);
            }
            frontNo++;
        }

        // SORT
        Sort.sortByAggregatedValue(specimens);

        // CREATE PARETO / AFTER KILL
        {
            ArrayList<ISpecimen> toConsider = new ArrayList<>(_populSize);
            for (int i = 0; i < _populSize; i++)
                toConsider.add(specimens.get(i));
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

    @Override
    public ArrayList<ISpecimen> getPareto()
    {
        return this._pareto;
    }

    @Override
    public ArrayList<ISpecimen> getReproductionPool()
    {
        return this._reproductionPool;
    }

    public void setOrderingDM(IOrderingDM dm)
    {
        this._dm = dm;
    }

    public IOrderingDM getOrderingDM()
    {
        return _dm;
    }
}
