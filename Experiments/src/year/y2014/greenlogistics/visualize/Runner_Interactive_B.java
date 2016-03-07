package year.y2014.greenlogistics.visualize;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Genetic;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.BestRandom;
import decision.elicitation.historymaintain.ordering.BaseMaintain;
import decision.elicitation.rules.BaseRule;
import decision.maker.ordering.OrderingDM;
import decision.maker.ordering.interfaces.IOrderingDM;
import decision.model.utilityfunction.PartialSumUtility;
import extractor.alternative.FromSpecimen;
import extractor.interfaces.IValueExtractor;
import interfaces.IGenetic;
import interfaces.ISpecimen;
import killer.Killer;
import org.apache.commons.math3.random.MersenneTwister;
import patterns.genetic.aglorithms.SPEA2;
import patterns.genetic.aglorithms.linear.NEMO0_SPEA2;
import patterns.genetic.aglorithms.nemo0.NEMO0_PATTERN;
import runner.drawer.CubePareto;
import runner.interfaces.IRunner;
import select.Tournament;
import select.interfaces.ISelector;
import sort.CriterionExtractor;
import standard.Common;
import standard.Point;
import standard.Range;
import utils.UtilityFunction;
import year.y2014.greenlogistics.B.DataB;
import year.y2014.greenlogistics.B.EvaluatorB;
import year.y2014.greenlogistics.Initializer;
import year.y2014.greenlogistics.Reproducer;
import year.y2014.greenlogistics.lp.WSM_A;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTomczyk on 16.11.2015.
 */
public class Runner_Interactive_B
{

    public static void main(String args[])
    {
        int generations = 1000;
        int populationSize = 200;
        int populationResized = 400;

        // -- SELECTOR ---------------
        Tournament.Params tParams = new Tournament.Params();
        tParams._pickLimit = 2;
        tParams._k = 2;
        tParams._probability = 0.9d;
        ISelector selector = new Tournament(tParams);


        HashMap<String, Range> costMap = new HashMap<>(3);
        costMap.put("space", new Range(825500.0f, 956800.0f));
        costMap.put("tp_space", new Range(825500.0f*0.0d, 956800.0f));
        costMap.put("display", new Range(825500.0f, 956800.0f));
        costMap.put("mdvf", new Range(825500.0f, 956800.0f));
        costMap.put("nsgaii", new Range(825500.0f, 956800.0f));
        costMap.put("spea2", new Range(825500.0f, 956800.0f));

        HashMap<String, Range> co2Map = new HashMap<>(3);
        co2Map.put("space", new Range(537900.0f, 621400.0f));
        co2Map.put("tp_space", new Range(537900.0f*0.0d, 621400.0f));
        co2Map.put("display", new Range(537900.0f, 621400.0f));
        co2Map.put("mdvf", new Range(537900.0f, 621400.0f));
        co2Map.put("nsgaii", new Range(537900.0f, 621400.0f));
        co2Map.put("spea2", new Range(537900.0f, 621400.0f));

        HashMap<String, Range> pmMap = new HashMap<>(3);
        pmMap.put("space", new Range(4400.0f, 27600.0f));
        pmMap.put("tp_space", new Range(4400.0f*0.0d, 27600.0f));
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


        ArrayList<UtilityFunction> uf = new ArrayList<>(3);

        double w1 = 0.5d;
        double w2 = 0.0d;
        double w3 = 0.5d;

        uf.add(new UtilityFunction(2));
        uf.get(0).add(new Point(825500.0f, w1));
        uf.get(0).add(new Point(956800.0f, 0.000000d));

        uf.add(new UtilityFunction(2));
        uf.get(1).add(new Point(537900.0f, w2));
        uf.get(1).add(new Point(621400.0f, 0.000000d));

        uf.add(new UtilityFunction(2));
        uf.get(2).add(new Point(4400.0f, w3));
        uf.get(2).add(new Point(27600.0f, 0.000000d));

        double ref[] = WSM_A.getResultForWeights(w1, w2, w3);
        IAlternative a = new Alternative("A", criterion);
        a.setEvaluationVector(ref, criterion);

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
            @SuppressWarnings("unused") Genetic NSGAII = new Genetic(pGenetic);
            //genetic.add(NSGAII);
        }
        {
            IGenetic g = SPEA2.getSPEA2("SPEA2", populationSize, populationResized, criterion, null,
                    new Reproducer(), new EvaluatorB(), new Initializer(), new Killer(), selector,
                    new BestRandom(new MersenneTwister(System.currentTimeMillis()), 2), new BaseMaintain(1000), 10000,
                    new DataB());
            genetic.add(g);
        }
        {
            @SuppressWarnings("unused") IGenetic g = NEMO0_PATTERN.getNEMO0("NEMO0_LINEAR", populationSize, populationResized,
                    criterion, new OrderingDM(new PartialSumUtility(uf, criterion)),
                    new Reproducer(), new EvaluatorB(),
                    new Initializer(), new Killer(), selector,
                    new BestRandom(new MersenneTwister(System.currentTimeMillis()), 2),
                    new BaseRule(40, 0), new BaseMaintain(10000), 10000,
                    new DataB(), null, NEMO0_PATTERN.INT_LINEAR);
            //genetic.add(g);
        }
        {
            IGenetic g = NEMO0_SPEA2.getNEMO0_SPEA2("SPEA_NEMO", populationSize, populationResized, populationSize, criterion,
                    uf, new Reproducer(), new EvaluatorB(), new Initializer(), new Killer(), selector, new BestRandom(new MersenneTwister(System.currentTimeMillis()), 2),
                    new BaseRule(40, 0), new BaseMaintain(10000), 100000, new DataB(), null);
            genetic.add(g);
        }


        IOrderingDM dm = new OrderingDM(new PartialSumUtility(uf, criterion));



        // RUN
        int repeat[] = {1, 1,1};

        IRunner g = new runner.Runner(genetic, criterion, new CubePareto(), repeat);

        g.init();
        /*System.out.println(
                genetic.get(1).getPareto().size() + " " +
                        genetic.get(1).getSpecimens().size());
*/
        for (int i = 0; i < generations; i++)
        {
            //System.out.println(genetic.get(2).getPareto().size());

            double A = 0.0d;
            @SuppressWarnings("unused") double B = 0.0d;
            {
                double e[] = dm.evaluate(genetic.get(1).getPareto(),new FromSpecimen());
                for (double d: e)
                    if (d > A) A = d;
            }
            /*{
                double e[] = dm.evaluate(genetic.get(2).getPareto(),new FromSpecimen());
                for (double d: e)
                    if (d > B) B = d;
            }*/
            @SuppressWarnings("unused") double o = dm.evaluate(a);
            //System.out.println(A + " " + B + " " + dm.evaluate(a));
            //System.out.println(i + " " + (o-A)/o + " " + (o-B)/o);

            if ((i == 100)  || (i == 250) || (i == 500) || (i == 700))
            {
                System.out.println(i);
                System.out.println("GEN 0");
                for (ISpecimen s: genetic.get(0).getPareto())
                {
                    String l = String.format("%.2f %.2f %.2f", s.getAlternative().getEvaluationAt(criterion.get(0)),
                            + s.getAlternative().getEvaluationAt(criterion.get(1)),
                            + s.getAlternative().getEvaluationAt(criterion.get(2)));

                    System.out.println(l.replace('.', ','));
                }

                System.out.println("GEN 1");
                for (ISpecimen s: genetic.get(1).getPareto())
                {
                    String l = String.format("%.2f %.2f %.2f", s.getAlternative().getEvaluationAt(criterion.get(0)),
                            + s.getAlternative().getEvaluationAt(criterion.get(1)),
                            + s.getAlternative().getEvaluationAt(criterion.get(2)));

                    System.out.println(l.replace('.', ','));
                }

            }

            g.step(i);
        }


    }

}
