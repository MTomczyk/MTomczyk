package patterns.genetic.aglorithms.linear;

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
import sort.preference.single.order.SPEA2_NEMO0;
import standard.Common;
import utils.UtilityFunction;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-03-23.
 */
public class NEMO0_SPEA2
{
    public static class Package
    {
        public IGenetic _genetic = null;
        public OrderDMRegressionManager _manager = null;
    }

    public static Package getNEMO0_SPEA2_Package(String name, int populationSize, int populationResized, int archiveSize, ArrayList<ICriterion> criteria, ArrayList<UtilityFunction> utilityFunctions,
                                           IReproducer reproducer, IEvaluator evaluator, IInitializer initializer, IKiller killer, ISelector selector,
                                           IChoice choice, IRule elicitationRule, IMaintain historyMaintain,
                                                 int timeOut, Object problem, IFilter filter)
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
            aDM.add(new OrderingDM(new PartialSumUtility(utilityFunctions, criteria)));
            pManager._artificialDMs = aDM;

            {
                MDVF.Params pMDVF = new MDVF.Params();
                pMDVF._epsilon = Common.EPSILON;
                pMDVF._monotonicEpsilon = 0.0d;
                pMDVF._rangeFromCriterion = true;
                pMDVF._solverTimeout = timeOut;
                pManager._or = new MDVF(pMDVF);
            }
        }

        OrderDMRegressionManager manager = new OrderDMRegressionManager(pManager);

        SPEA2_NEMO0.Params pNEMO0 = new SPEA2_NEMO0.Params();
        {
            pNEMO0._epsilon = Common.EPSILON;
            pNEMO0._dm = manager;
            pNEMO0._archiveSize = archiveSize;
            pNEMO0._distance = null;
            pNEMO0._rangeFromCriterion = true;
            pNEMO0._filter = filter;
        }

        pGenetic._sorter = new SPEA2_NEMO0(pNEMO0);

        if (name != null) pGenetic.name = name;
        else pGenetic.name = "NEMO0_SPEA2";

        Package p = new Package();
        p._genetic = new Genetic(pGenetic);
        p._manager = manager;
        return p;
    }

    public static IGenetic getNEMO0_SPEA2(String name, int populationSize, int populationResized, int archiveSize, ArrayList<ICriterion> criteria, ArrayList<UtilityFunction> utilityFunctions,
                                    IReproducer reproducer, IEvaluator evaluator, IInitializer initializer, IKiller killer, ISelector selector,
                                    IChoice elicitation, IRule elicitationRule, IMaintain historyMaintain,
                                          int timeOut, Object problem, IFilter filter)
    {
        Package p = getNEMO0_SPEA2_Package(name, populationSize, populationResized, archiveSize,criteria, utilityFunctions, reproducer, evaluator,
                initializer, killer, selector, elicitation, elicitationRule, historyMaintain, timeOut, problem, filter);
        return p._genetic;
    }


}
