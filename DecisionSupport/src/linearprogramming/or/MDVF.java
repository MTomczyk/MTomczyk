package linearprogramming.or;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.maker.ordering.Order;
import decision.maker.ordering.OrderingDM;
import decision.maker.ordering.interfaces.IOrderingDM;
import decision.model.utilityfunction.PartialSumUtility;
import decision.model.interfaces.IModel;
import linearprogramming.SlopeConstraints;
import utils.RangeMaker;
import utils.Sorter;
import utils.UtilityFunction;
import tree.binary.BinaryTree;
import linearprogramming.BaseConstraints;
import linearprogramming.OrderConstraints;
import linearprogramming.UtilityFunctionCreator;
import linearprogramming.or.interfaces.IOrdinalRegression;
import net.sf.javailp.*;
import standard.Common;
import standard.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Michał on 2015-02-12.
 * <p/>
 * This class implements IOrdinalRegression lp a MDVF representative function.
 */

public class MDVF implements IOrdinalRegression
{
    public static int LINEAR = 0;
    public static int PIECEWISE_LINEAR = 1;
    public static int GENERAL = 2;

    public static class Params
    {
        public boolean _rangeFromCriterion = true;
        public double _solverTimeout = 500.0d;
        public double _epsilon = Common.EPSILON;
        public double _monotonicEpsilon = 0.0d;
        public double _acceptanceThreshold = Common.EPSILON;
        public int _MODE = PIECEWISE_LINEAR;
        public boolean _acceptNegativeEpsilon = false;
        public boolean _forceRectangularSpace = true;
    }

    private Params _p = null;

    public MDVF()
    {
        this._p = new Params();
    }

    public MDVF(Params p)
    {
        this._p = p;
    }

    private static boolean DEBUG = false;

    /**
     * Returns array of utility functions for each criterion.
     *
     * @param alternatives Array of alternatives. Used only if criterion has no range "mdvf".
     * @param criteria     Array of criterion.
     * @param dmHistory    DM elicitation history.
     * @return Array of utility functions for each criterion.
     */
    @Override
    public ArrayList<UtilityFunction> getUtility(ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria,
                                                 LinkedList<Order> dmHistory)
    {
        ArrayList<IOrderingDM> aDM = new ArrayList<>(1);
        IModel model = new PartialSumUtility(null, criteria);

        IOrderingDM dummyDM = new OrderingDM(model);
        aDM.add(dummyDM);

        HashMap<IOrderingDM, LinkedList<Order>> map = new HashMap<>();
        map.put(dummyDM, dmHistory);

        return getUtility(alternatives, criteria, map, aDM);
    }


    /**
     * Returns array of utility functions for each criterion.
     *
     * @param alternatives Array of alternatives. Used only if criterion has no range "mdvf".
     * @param criteria     Array of criterion.
     * @param dmsHistory   DMs elicitation history.
     * @param dms          Array of DMs.
     * @return Array of utility functions for each criterion.
     */
    @Override
    public ArrayList<UtilityFunction> getUtility(ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria,
                                                 HashMap<IOrderingDM, LinkedList<Order>> dmsHistory,
                                                 ArrayList<IOrderingDM> dms)
    {
        // CHECK SIZE OF POOL
        int size = 0;
        for (IOrderingDM odm : dms)
            for (Order o : dmsHistory.get(odm))
                for (ArrayList<IAlternative> ds : o._orders)
                    size += ds.size();


        // CREATE POOL
        ArrayList<IAlternative> alternativesInComparisons = new ArrayList<>(size);
        for (IOrderingDM odm : dms)
            for (Order o : dmsHistory.get(odm))
                for (ArrayList<IAlternative> ds : o._orders)
                    alternativesInComparisons.addAll(ds.stream().collect(Collectors.toList()));

        ArrayList<IAlternative> alternativesAll = new ArrayList<>(alternativesInComparisons.size() + alternatives.size());
        for (IOrderingDM odm : dms)
            for (Order o : dmsHistory.get(odm))
                for (ArrayList<IAlternative> ds : o._orders)
                    alternativesAll.addAll(ds.stream().collect(Collectors.toList()));
        alternativesAll.addAll(alternatives.stream().collect(Collectors.toList()));


        // CREATE SORTED BINARY TREES
        ArrayList<BinaryTree<IAlternative>> sortedInComparisons = Sorter.getSortedByCriterion(alternativesInComparisons, criteria);
        ArrayList<BinaryTree<IAlternative>> sortedAll = Sorter.getSortedByCriterion(alternativesAll, criteria);


        // FIND BEST/WORST VALUES FOR EACH CRITERION
        ArrayList<Range> range = new ArrayList<>(criteria.size());

        if (_p._rangeFromCriterion) for (ICriterion c : criteria)
            range.add(c.getRange().get("mdvf"));
        else
        {
            range = RangeMaker.getRange(alternatives, criteria);
            ArrayList<Range> tmpRange = RangeMaker.getRange(alternativesAll, criteria);
            for (int i = 0; i < criteria.size(); i++)
            {
                if (tmpRange.get(i).left < range.get(i).left) range.get(i).left = tmpRange.get(i).left;
                if (tmpRange.get(i).right > range.get(i).right) range.get(i).right = tmpRange.get(i).right;
            }
        }


        // ADD DUMMY OBJECTS TO TREES : NADIR/UTOPIA
        {
            IAlternative NADIR = new Alternative("NADIR", criteria);
            IAlternative UTOPIA = new Alternative("UTOPIA", criteria);
            for (int i = 0; i < range.size(); i++)
            {
                ICriterion c = criteria.get(i);
                if (c.isGain())
                {
                    NADIR.setEvaluationAt(c, range.get(i).left);
                    UTOPIA.setEvaluationAt(c, range.get(i).right);
                } else
                {
                    NADIR.setEvaluationAt(c, range.get(i).right);
                    UTOPIA.setEvaluationAt(c, range.get(i).left);
                }
            }

            if (_p._forceRectangularSpace)
            {
                ArrayList<Range> tmpRange = RangeMaker.getRange(alternativesAll, criteria);
                for (int i = 0; i < criteria.size(); i++)
                {
                    if (tmpRange.get(i).left > range.get(i).left) tmpRange.get(i).left = range.get(i).left;
                    if (tmpRange.get(i).right < range.get(i).right) tmpRange.get(i).right = range.get(i).right;
                }

                double dv = 0.0d;
                for (Range aTmpRange : tmpRange)
                {
                    double tmp = Math.abs(aTmpRange.getRange());
                    if (tmp > dv)
                        dv = tmp;
                }
                for (ICriterion c : criteria)
                {
                    double b = UTOPIA.getEvaluationAt(c);
                    if (DEBUG) System.out.println(" ============= " + b + " " + dv);
                    if (c.isGain()) b -= dv;
                    else b += dv;
                    NADIR.setEvaluationAt(c, b);

                }
            }


            for (int i = 0; i < criteria.size(); i++)
            {
                sortedInComparisons.get(i).insert(NADIR);
                sortedInComparisons.get(i).insert(UTOPIA);
                sortedAll.get(i).insert(NADIR);
                sortedAll.get(i).insert(UTOPIA);
            }
        }


        // REPEAT UNTIL FIND A SOLUTION
        SolverFactory factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, _p._solverTimeout);
        Solver solver = factory.get();
        Result result = null;

        @SuppressWarnings("unused") int cnt[] = new int [dms.size()];

        if (DEBUG) System.out.println("-----------------A");
        int attempts = 5;
        while (attempts-- > 0) {

            if (DEBUG)
            {
                System.out.println("B");
                for (IOrderingDM odm : dms)
                    System.out.print(" " + dmsHistory.get(odm).size());
            }

            Result r = getResult(solver, sortedInComparisons, sortedAll, alternativesInComparisons, alternativesAll, alternatives, criteria, dmsHistory, dms);
            if (DEBUG) System.out.println("C");
            if (r != null) {
                result = r;
                break;
            }
            if (DEBUG) System.out.println("D");
            for (IOrderingDM odm : dms)
                if (dmsHistory.get(odm).size() == 0)
                    return null;

            dms.stream().filter(odm -> dmsHistory.get(odm).size() != 0).forEach(odm -> dmsHistory.get(odm).removeFirst());
        }

        if (result == null) return null;
        return UtilityFunctionCreator.getUtilityFunction(result, sortedInComparisons, criteria);
    }


    @Override
    public Result getResult(Solver solver,
                            ArrayList<BinaryTree<IAlternative>> sortedInComparisons,
                            ArrayList<BinaryTree<IAlternative>> sortedAll,
                            ArrayList<IAlternative> alternativesInComparisons,
                            ArrayList<IAlternative> allAlternatives,
                            ArrayList<IAlternative> alternatives,
                            ArrayList<ICriterion> criteria,
                            HashMap<IOrderingDM, LinkedList<Order>> dmsHistory,
                            ArrayList<IOrderingDM> dms)
    {
        Problem problem = new Problem();
        Linear obj = new Linear();
        obj.add(1.0d, "a");
        if (_p._acceptNegativeEpsilon)
            obj.add(-1.0d, "na");
        problem.setObjective(obj, OptType.MAX);

        if (_p._MODE == PIECEWISE_LINEAR)
            BaseConstraints.addBaseConstraints(problem, sortedInComparisons, criteria, true, _p._monotonicEpsilon);
        else if (_p._MODE == GENERAL)
            BaseConstraints.addBaseConstraints(problem, sortedInComparisons, criteria, true, _p._monotonicEpsilon);
        else if (_p._MODE == LINEAR)
        {
            BaseConstraints.addBaseConstraints(problem, sortedInComparisons, criteria, true, _p._monotonicEpsilon);
            SlopeConstraints.addLinearConstraints(problem, sortedInComparisons, criteria);
        }


        int h = 0;
        //if ((_p._MODE == PIECEWISE_LINEAR) && (_p._acceptNegativeEpsilon)) h=-1;

        int orderSize = 0;
        for (IOrderingDM odm : dms)
            for (Order o : dmsHistory.get(odm))
            {
                orderSize++;
                //if ((_p._MODE == PIECEWISE_LINEAR) && (_p._acceptNegativeEpsilon)) h++;
                OrderConstraints.addOrderConstraintsMDVF(problem, o, criteria, h,
                        "a", false, _p._acceptNegativeEpsilon);
            }

        {
            Linear l = new Linear();
            l.add(1.0d, "a");
            if (_p._acceptNegativeEpsilon) l.add(-1.0d, "na");

            if (orderSize > 0)
            {
                l.add(-1.0d, String.format("e(%d)", 0));
                if (_p._acceptNegativeEpsilon)  l.add(1.0d, String.format("ne(%d)", 0));
            }

            if (orderSize == 0)
                problem.add(l, "<=", 1.0d);
            else  problem.add(l, "<=", 0.0d);
        }

        if (DEBUG) System.out.println(problem.toString());

        Result r = solver.solve(problem);
        if ((r != null) && (r.get("a") != null))
        {

            Double a = r.get("a").doubleValue();
            if (_p._acceptNegativeEpsilon)
            {
                a -= r.get("na").doubleValue();
            }

            if (((!_p._acceptNegativeEpsilon) && (a > _p._acceptanceThreshold))
                || (_p._acceptNegativeEpsilon))
            {
                return r;
            }
        }


        return null;
    }

    @Override
    public Result getResultWithBinaryConstraints(Solver solver, ArrayList<BinaryTree<IAlternative>> sortedInComparisons, ArrayList<BinaryTree<IAlternative>> sortedAll, ArrayList<IAlternative> alternativesInComparisons, ArrayList<IAlternative> allAlternatives, ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria,
                                                 HashMap<IOrderingDM, LinkedList<Order>> dmsHistory, ArrayList<IOrderingDM> dms, String binaryKey, int binaryBase)
    {

        Problem problem = new Problem();
        Linear obj = new Linear();
        obj.add(-0.5d, "a");
        if (_p._acceptNegativeEpsilon)
            obj.add(+1.0d, "na");


        if (_p._MODE == PIECEWISE_LINEAR)
            BaseConstraints.addBaseConstraints(problem, sortedInComparisons, criteria, true, _p._monotonicEpsilon);
        else if (_p._MODE == GENERAL)
            BaseConstraints.addBaseConstraints(problem, sortedInComparisons, criteria, true, _p._monotonicEpsilon);
        else if (_p._MODE == LINEAR)
        {
            BaseConstraints.addBaseConstraints(problem, sortedInComparisons, criteria, true, _p._monotonicEpsilon);
            SlopeConstraints.addLinearConstraints(problem, sortedInComparisons, criteria);
        }


        int h = -1;
        for (IOrderingDM odm : dms)
            for (Order o : dmsHistory.get(odm))
            {
                h++;
                OrderConstraints.addOrderConstraintsMDVFAndDisable(
                        problem, o, criteria, h, "a", false, _p._acceptNegativeEpsilon,
                        binaryKey, 1.0d);
                obj.add(1.0d, String.format("%s%d", binaryKey,h));
            }

        {
            Linear l = new Linear();
            l.add(1.0d, "a");
            if (_p._acceptNegativeEpsilon)
                l.add(-1.0d, "na");
            problem.add(l, ">=", _p._acceptanceThreshold);
        }

        problem.setObjective(obj, OptType.MIN);

        Result r = solver.solve(problem);

        if ((r != null) && (r.get("a") != null))
        {
            Double a = r.get("a").doubleValue();
            if (_p._acceptNegativeEpsilon)
            {
                a -= r.get("na").doubleValue();
            }
            if (((!_p._acceptNegativeEpsilon) && (a > _p._acceptanceThreshold - Common.EPSILON))
                    || (_p._acceptNegativeEpsilon))
            {
                return r;
            }
        }
        System.out.println("UPS");

        return null;
    }

    public boolean isAcceptNegativeEpsilon()
    {
        return _p._acceptNegativeEpsilon;
    }

}
