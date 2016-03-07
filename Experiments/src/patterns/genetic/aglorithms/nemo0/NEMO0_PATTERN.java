package patterns.genetic.aglorithms.nemo0;

import base.Genetic;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.filter.interfaces.IFilter;
import decision.elicitation.choice.ordering.iterfaces.IChoice;
import decision.elicitation.historymaintain.ordering.interfaces.IMaintain;
import decision.elicitation.rules.interfaces.IRule;
import decision.maker.ordering.OrderingDM;
import decision.maker.ordering.interfaces.IOrderingDM;
import decision.manager.ordering.OrderDMRegressionManager;
import decision.model.utilityfunction.PartialSumUtility;
import interfaces.IEvaluator;
import interfaces.IGenetic;
import interfaces.IInitializer;
import killer.interfaces.IKiller;
import linearprogramming.or.MDVF;
import reproducer.interfaces.IReproducer;
import select.interfaces.ISelector;
import sort.interfaces.ISorter;
import standard.Common;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-03-23.
 */
public class NEMO0_PATTERN
{
    public static int INT_LINEAR = 0;
    public static int INT_PLINEAR = 1;

    public static class Package
    {
        public IGenetic _genetic = null;
        public ISorter _sorter = null;
        public OrderDMRegressionManager _manager = null;
    }

    public static Package getNEMO0_Package(
            String name,
            int populationSize,
            int populationResized,
            ArrayList<ICriterion> criteria,
            IOrderingDM dm,
            IReproducer reproducer,
            IEvaluator evaluator,
            IInitializer initializer,
            IKiller killer,
            ISelector selector,
            IChoice choice,
            IRule elicitationRule,
            IMaintain historyMaintain,
            int timeOut,
            Object problem,
            IFilter filter,
            int INTERNAL_MODEL)
    {
        Genetic.Params pGenetic = new Genetic.Params();
        pGenetic._populationSize = populationSize;
        pGenetic._populationResize = populationResized;
        pGenetic._criteria = criteria;

        pGenetic._reproducer = reproducer;
        pGenetic._evaluator = evaluator;
        pGenetic._initializer = initializer;
        pGenetic._killer = killer;
        pGenetic._selector = selector;

        pGenetic._problem = problem;

        OrderDMRegressionManager.Params pManager = new OrderDMRegressionManager.Params();
        {
            pManager._singleChoice = choice;
            pManager._elicitationRule = elicitationRule;
            pManager._historyMaintain = historyMaintain;

            ArrayList<IOrderingDM> eDM = new ArrayList<>(1);
            eDM.add(new OrderingDM(new PartialSumUtility(null, criteria)));
            pManager._estimatedDMs = eDM;

            ArrayList<IOrderingDM> aDM = new ArrayList<>(1);
            aDM.add(dm);
            pManager._artificialDMs = aDM;

            {
                MDVF.Params pMDVF = new MDVF.Params();
                pMDVF._epsilon = Common.EPSILON;
                pMDVF._monotonicEpsilon = 0.0d;
                pMDVF._rangeFromCriterion = true;
                if (INTERNAL_MODEL == INT_LINEAR)
                    pMDVF._MODE = MDVF.LINEAR;
                else if (INTERNAL_MODEL == INT_PLINEAR)
                    pMDVF._MODE = MDVF.PIECEWISE_LINEAR;
                pMDVF._solverTimeout = timeOut;
                pManager._or = new MDVF(pMDVF);
            }
        }

        OrderDMRegressionManager manager = new OrderDMRegressionManager(pManager);

        sort.preference.single.order.NEMO0.Params pNEMO0 = new sort.preference.single.order.NEMO0.Params();
        {
            pNEMO0._epsilon = Common.EPSILON;
            pNEMO0._dm = manager;
            pNEMO0._populSize = populationSize;
            pNEMO0._filter = filter;
        }

        pGenetic._sorter = new sort.preference.single.order.NEMO0(pNEMO0);

        if (name != null) pGenetic.name = name;
        else pGenetic.name = "NEMO0_PATTERN";

        Package p = new Package();
        p._genetic = new Genetic(pGenetic);
        p._manager = manager;
        p._sorter = pGenetic._sorter;
        return p;
    }

    public static IGenetic getNEMO0(
            String name,
            int populationSize,
            int populationResized,
            ArrayList<ICriterion> criteria,
            IOrderingDM dm,
            IReproducer reproducer,
            IEvaluator evaluator,
            IInitializer initializer,
            IKiller killer,
            ISelector selector,
            IChoice elicitation,
            IRule elicitationRule,
            IMaintain historyMaintain,
            int timeOut,
            Object problem,
            IFilter filter,
            int INTERNAL_MODEL)
    {
        Package p = getNEMO0_Package(
                name,
                populationSize,
                populationResized,
                criteria,
                dm,
                reproducer,
                evaluator,
                initializer,
                killer,
                selector,
                elicitation,
                elicitationRule,
                historyMaintain,
                timeOut,
                problem,
                filter,
                INTERNAL_MODEL);
        return p._genetic;
    }


}
