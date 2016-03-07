package patterns.genetic.aglorithms.linear;

import base.Genetic;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.filter.interfaces.IFilter;
import decision.elicitation.choice.ordering.iterfaces.IChoice;
import decision.elicitation.historymaintain.ordering.interfaces.IMaintain;
import decision.elicitation.rules.interfaces.IRule;
import decision.maker.ordering.OrderingDM;
import decision.model.utilityfunction.PartialSumUtility;
import interfaces.IEvaluator;
import interfaces.IGenetic;
import interfaces.IInitializer;
import killer.interfaces.IKiller;
import reproducer.interfaces.IReproducer;
import select.interfaces.ISelector;
import standard.Common;
import utils.UtilityFunction;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-03-23.
 */
public class NEMO0_KNOWN
{
    @SuppressWarnings("UnusedParameters")
    public static IGenetic getNEMO0_KNOWN(String name, int populationSize, int populationResized, ArrayList<ICriterion> criteria, ArrayList<UtilityFunction> utilityFunctions,
                                    IReproducer reproducer, IEvaluator evaluator, IInitializer initializer, IKiller killer, ISelector selector,
                                    IChoice elicitation, IRule elicitationRule, IMaintain historyMaintain, int timeOut)
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


        sort.preference.single.order.NEMO0_KNOWN.Params pNEMO0_KNOWN = new sort.preference.single.order.NEMO0_KNOWN.Params();
        {
            pNEMO0_KNOWN._epsilon = Common.EPSILON;
            pNEMO0_KNOWN._dm = new OrderingDM(new PartialSumUtility(utilityFunctions, criteria));
            pNEMO0_KNOWN._populSize = populationSize;
        }

        pGenetic._sorter = new sort.preference.single.order.NEMO0_KNOWN(pNEMO0_KNOWN);

        if (name != null) pGenetic.name = name;
        else  pGenetic.name = "NEMO0_KNOWN";

        return new Genetic(pGenetic);
    }

}
