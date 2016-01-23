package year.y2014.greenlogistics.visualize;

import base.Genetic;
import base.LaumannsApproximationGenetic;
import base.LaumannsParetoGenetic;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import extractor.interfaces.IValueExtractor;
import interfaces.IGenetic;
import killer.Killer;
import runner.drawer.CubePareto;
import runner.interfaces.IRunner;
import select.Tournament;
import select.interfaces.ISelector;
import sort.CriterionExtractor;
import standard.Common;
import standard.Range;
import year.y2014.greenlogistics.A.DataA;
import year.y2014.greenlogistics.A.EvaluatorA;
import year.y2014.greenlogistics.Initializer;
import year.y2014.greenlogistics.Reproducer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTomczyk on 16.11.2015.
 */
public class Runner {

    public static void main(String args[])
    {
        int generations = 1000;
        int populationSize = 50;
        int populationResized = 100;

        // -- SELECTOR ---------------
        Tournament.Params tParams = new Tournament.Params();
        tParams._pickLimit = 2;
        tParams._k = 2;
        tParams._probability = 0.9d;
        ISelector selector = new Tournament(tParams);


        HashMap<String, Range> costMap = new HashMap<String, Range>(3);
        costMap.put("space", new Range(830000.0f, 1050000.0f));
        costMap.put("display", new Range(830000.0f, 1050000.0f));
        costMap.put("mdvf", new Range(830000.0f, 1050000.0f));
        costMap.put("nsgaii", new Range(830000.0f, 1050000.0f));

        HashMap<String, Range> co2Map = new HashMap<String, Range>(3);
        co2Map.put("space", new Range(530000.0f, 580000.0f));
        co2Map.put("display", new Range(530000.0f, 580000.0f));
        co2Map.put("mdvf", new Range(530000.0f, 580000.0f));
        co2Map.put("nsgaii", new Range(530000.0f, 580000.0f));

        HashMap<String, Range> pmMap = new HashMap<String, Range>(3);
        pmMap.put("space", new Range(2000.0f, 15000.0f));
        pmMap.put("display", new Range(2000.0f, 15000.0f));
        pmMap.put("mdvf", new Range(2000.0f, 15000.0f));
        pmMap.put("nsgaii", new Range(2000.0f, 15000.0f));

        // NORMALIZATION


        ArrayList<ICriterion> criterion = new ArrayList<ICriterion>(3);

        criterion.add(new Criterion("Cost", false, null, costMap));
        IValueExtractor e1 = new CriterionExtractor(criterion.get(0));
        criterion.get(0).setExtractor(e1);

        criterion.add(new Criterion("CO2", false, null, co2Map));
        IValueExtractor e2 = new CriterionExtractor(criterion.get(1));
        criterion.get(1).setExtractor(e2);

        criterion.add(new Criterion("PM", false, null, pmMap));
        IValueExtractor e3 = new CriterionExtractor(criterion.get(2));
        criterion.get(2).setExtractor(e3);

        // -- GENETIC ---------------
        ArrayList<IGenetic> genetic = new ArrayList<IGenetic>(2);

        //---------------------------------------
        {
            Genetic.Params pGenetic = new Genetic.Params();
            pGenetic._populationSize = populationSize;
            pGenetic._populationResize = populationResized;
            pGenetic._initializer = new Initializer();
            pGenetic._evaluator = new EvaluatorA();
            pGenetic._problem = new DataA();
            pGenetic._killer = new Killer();

            {
                sort.classic.NSGAII.NSGAII.Params pNSGAII = new sort.classic.NSGAII.NSGAII.Params();
                pNSGAII._populSize = populationSize;
                pNSGAII._epsilon = Common.EPSILON;
                pNSGAII._rangeFromCriterion = true;
                pGenetic._sorter = new sort.classic.NSGAII.NSGAII(pNSGAII);
            }

            pGenetic._reproducer = new Reproducer();
            pGenetic._criteria = criterion;
            pGenetic._selector = selector;
            pGenetic.name = "NSGAII";
            Genetic NSGAII = new Genetic(pGenetic);
            genetic.add(NSGAII);
        }
        {
            LaumannsApproximationGenetic.Params pGenetic = new LaumannsApproximationGenetic.Params();
            pGenetic._populationSize = populationSize;
            pGenetic._initializer = new Initializer();
            pGenetic._evaluator = new EvaluatorA();
            pGenetic._problem = new DataA();
            pGenetic._epsilon = Common.EPSILON;
            pGenetic._killer = null;
            //pGenetic._t = new double[]{37000.0*0.05,8300.0*0.05,2200.0*0.05};
            pGenetic._t = new double[]{0.01,0.01,0.01};
            {
                sort.classic.NSGAII.NSGAII.Params pNSGAII = new sort.classic.NSGAII.NSGAII.Params();
                pNSGAII._populSize = populationSize;
                pNSGAII._epsilon = Common.EPSILON;
                pNSGAII._rangeFromCriterion = true;
                pGenetic._sorter = new sort.classic.NSGAII.NSGAII(pNSGAII);
            }

            pGenetic._reproducer = new Reproducer();
            pGenetic._criteria = criterion;
            pGenetic._selector = selector;
            pGenetic.name = "LAUMANNS_NSGAII";
            LaumannsApproximationGenetic NSGAII = new LaumannsApproximationGenetic(pGenetic);
            genetic.add(NSGAII);
        }

        // RUN
        int repeat[] = {1, 50};

        IRunner g = new runner.Runner(genetic, criterion, new CubePareto(), repeat);

        g.init();
        /*System.out.println(
                genetic.get(1).getPareto().size() + " " +
                        genetic.get(1).getSpecimens().size());
*/
        for (int i = 0; i < generations; i++)
        {
            System.out.println(genetic.get(1).getPareto().size());
            g.step(i);
        }


    }

}