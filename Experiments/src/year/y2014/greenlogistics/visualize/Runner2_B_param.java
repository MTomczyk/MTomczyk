package year.y2014.greenlogistics.visualize;

import base.Genetic;
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
import year.y2014.greenlogistics.B.DataB;
import year.y2014.greenlogistics.B.EvaluatorB;
import year.y2014.greenlogistics.Initializer;
import year.y2014.greenlogistics.Reproducer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTomczyk on 16.11.2015.
 */
public class Runner2_B_param
{

    public static void main(String args[])
    {
        int ps = Integer.parseInt(args[0]);
        double e = Double.parseDouble(args[1]);

        System.out.println(ps + " " + e);

        int generations = 1000;
        int populationSize = ps;
        int populationResized = ps * 2;

        // -- SELECTOR ---------------
        Tournament.Params tParams = new Tournament.Params();
        tParams._pickLimit = 2;
        tParams._k = 2;
        tParams._probability = 0.9d;
        ISelector selector = new Tournament(tParams);



        HashMap<String, Range> costMap = new HashMap<>(3);
        costMap.put("space", new Range(825500.0f, 956800.0f));
        costMap.put("tp_space", new Range(825500.0f*1.0d, 956800.0f));
        costMap.put("display", new Range(825500.0f, 956800.0f));
        costMap.put("mdvf", new Range(825500.0f, 956800.0f));
        costMap.put("nsgaii", new Range(825500.0f, 956800.0f));
        costMap.put("spea2", new Range(825500.0f, 956800.0f));

        HashMap<String, Range> co2Map = new HashMap<>(3);
        co2Map.put("space", new Range(537900.0f, 621400.0f));
        co2Map.put("tp_space", new Range(537900.0f*1.0d, 621400.0f));
        co2Map.put("display", new Range(537900.0f, 621400.0f));
        co2Map.put("mdvf", new Range(537900.0f, 621400.0f));
        co2Map.put("nsgaii", new Range(537900.0f, 621400.0f));
        co2Map.put("spea2", new Range(537900.0f, 621400.0f));

        HashMap<String, Range> pmMap = new HashMap<>(3);
        pmMap.put("space", new Range(4400.0f, 27600.0f));
        pmMap.put("tp_space", new Range(4400.0f*1.0d, 27600.0f));
        pmMap.put("display", new Range(4400.0f, 27600.0f));
        pmMap.put("mdvf", new Range(4400.0f, 27600.0f));
        pmMap.put("nsgaii", new Range(4400.0f, 27600.0f));
        pmMap.put("spea2", new Range(4400.0f, 27600.0f));

        // NORMALIZATION


        ArrayList<ICriterion> criterion = new ArrayList<>(3);

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
        ArrayList<IGenetic> genetic = new ArrayList<>(2);

        //---------------------------------------
        {
            Genetic.Params pGenetic = new Genetic.Params();
            pGenetic._populationSize = populationSize;
            pGenetic._populationResize = populationResized;
            pGenetic._initializer = new Initializer();
            pGenetic._evaluator = new EvaluatorB();
            pGenetic._problem = new DataB();
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
            LaumannsParetoGenetic.Params pGenetic = new LaumannsParetoGenetic.Params();
            pGenetic._populationSize = populationSize;
            pGenetic._initializer = new Initializer();
            pGenetic._evaluator = new EvaluatorB();
            pGenetic._problem = new DataB();
            pGenetic._epsilon = Common.EPSILON;
            pGenetic._killer = null;
            pGenetic._t = new double[]{e,e,e};

            {
                sort.classic.NSGAII.NSGAII.Params pNSGAII = new sort.classic.NSGAII.NSGAII.Params();
                pNSGAII._populSize = populationSize;
                pNSGAII._epsilon = Common.EPSILON;
                pNSGAII._rangeFromCriterion = true;
                pNSGAII._steadyState = true;
                pGenetic._sorter = new sort.classic.NSGAII.NSGAII(pNSGAII);
            }

            pGenetic._reproducer = new Reproducer();
            pGenetic._criteria = criterion;
            pGenetic._selector = selector;
            pGenetic.name = "LAUMANNS_NSGAII";
            LaumannsParetoGenetic NSGAII = new LaumannsParetoGenetic(pGenetic);
            genetic.add(NSGAII);
        }


        // RUN
        int repeat[] = {1,ps};

        IRunner g = new runner.Runner(genetic, criterion, null, repeat);

        g.init();


        int n = 0;


        for (int i = 0; i < generations; i++)
        {
            //if (genetic.get(1).getPareto().size() % 25 == 0)
            //    System.out.println(genetic.get(1).getPareto().size() + " " + genetic.get(1).getElapsedTime());
            if (i == generations - 1)
                System.out.println(i + " " + genetic.get(1).getPareto().size() + " " + genetic.get(1).getElapsedTime());
            n += genetic.get(1).getPareto().size();
            g.step(i);
        }

        System.out.println(String.format("AVG %f", n / (double) generations));
        System.out.println(genetic.get(1).getPareto().size());


    }

}
