package patterns.genetic.aglorithms;

import base.Genetic;
import base.LaumannsParetoGenetic;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.iterfaces.IChoice;
import decision.elicitation.historymaintain.ordering.interfaces.IMaintain;
import interfaces.IEvaluator;
import interfaces.IGenetic;
import interfaces.IInitializer;
import killer.interfaces.IKiller;
import normalization.interfaces.INormalization;
import reproducer.interfaces.IReproducer;
import select.interfaces.ISelector;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-03-23.
 */
public class SPEA2
{
    @SuppressWarnings("UnusedParameters")
    public static IGenetic getSPEA2(String name, int populationSize, int populationResized, ArrayList<ICriterion> criteria , ArrayList<INormalization> normalizations,
                                    IReproducer reproducer, IEvaluator evaluator, IInitializer initializer, IKiller killer, ISelector selector,
                                    IChoice elicitation, IMaintain historyMaintain, int timeOut, Object problem)
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

        sort.classic.SPEA2.SPEA2.Params p = new sort.classic.SPEA2.SPEA2.Params();
        p._archiveSize = populationSize;
        p._rangeFromCriterion = true;
        p._truncDepth = 3;
        p._distance = null;

        pGenetic._sorter = new sort.classic.SPEA2.SPEA2(p);

        if (name != null) pGenetic.name = name;
        else  pGenetic.name = "SPEA2";

        return new Genetic(pGenetic);
    }


    @SuppressWarnings("unused")
    public static IGenetic getTParetoSPEA2(String name, int populationSize, int populationResized, ArrayList<ICriterion> criteria , ArrayList<INormalization> normalizations,
                                     IReproducer reproducer, IEvaluator evaluator, IInitializer initializer, IKiller killer, ISelector selector,
                                     IChoice elicitation, IMaintain historyMaintain, int timeOut, double t[])
    {
        LaumannsParetoGenetic.Params pGenetic = new LaumannsParetoGenetic.Params();
        pGenetic._populationSize = populationSize;
        pGenetic._criteria = criteria;

        pGenetic._t = t;

        pGenetic._reproducer = reproducer;
        pGenetic._evaluator = evaluator;
        pGenetic._initializer = initializer;
        pGenetic._killer = killer;
        pGenetic._selector = selector;

        sort.classic.SPEA2.SPEA2.Params p = new sort.classic.SPEA2.SPEA2.Params();
        p._archiveSize = populationSize;
        p._rangeFromCriterion = true;
        p._truncDepth = 3;
        p._distance = null;

        pGenetic._sorter = new sort.classic.SPEA2.SPEA2(p);

        if (name != null) pGenetic.name = name;
        else  pGenetic.name = "SPEA2_TPARETO";

        return new LaumannsParetoGenetic(pGenetic);
    }


}
