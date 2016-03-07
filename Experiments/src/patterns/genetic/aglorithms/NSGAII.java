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
public class NSGAII
{
    @SuppressWarnings("UnusedParameters")
    public static IGenetic getNSGAII(String name, int populationSize, int populationResized, ArrayList<ICriterion> criteria , ArrayList<INormalization> normalizations,
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

        sort.classic.NSGAII.NSGAII.Params p = new sort.classic.NSGAII.NSGAII.Params();
        p._populSize = populationSize;
        p._rangeFromCriterion = false;
        p._normalization = normalizations;

        pGenetic._sorter = new sort.classic.NSGAII.NSGAII(p);

        if (name != null) pGenetic.name = name;
        else  pGenetic.name = "NSGAII";

        return new Genetic(pGenetic);
    }


    @SuppressWarnings("UnusedParameters")
    public static IGenetic getTParetoNSGAII(String name, int populationSize, int populationResized, ArrayList<ICriterion> criteria , ArrayList<INormalization> normalizations,
                                     IReproducer reproducer, IEvaluator evaluator, IInitializer initializer, IKiller killer, ISelector selector,
                                     IChoice elicitation, IMaintain historyMaintain, int timeOut, double t[], Object problem)
    {
        LaumannsParetoGenetic.Params pGenetic = new LaumannsParetoGenetic.Params();
        pGenetic._populationSize = populationSize;
        pGenetic._criteria = criteria;

        pGenetic._reproducer = reproducer;
        pGenetic._evaluator = evaluator;
        pGenetic._initializer = initializer;
        pGenetic._killer = killer;
        pGenetic._selector = selector;
        pGenetic._t = t;
        pGenetic._problem = problem;

        sort.classic.NSGAII.NSGAII.Params p = new sort.classic.NSGAII.NSGAII.Params();
        p._populSize = populationSize;
        p._rangeFromCriterion = false;
        p._normalization = normalizations;
        p._steadyState = true;

        pGenetic._sorter = new sort.classic.NSGAII.NSGAII(p);

        if (name != null) pGenetic.name = name;
        else  pGenetic.name = "NSGAII_TPARETO";

        return new LaumannsParetoGenetic(pGenetic);
    }


}
