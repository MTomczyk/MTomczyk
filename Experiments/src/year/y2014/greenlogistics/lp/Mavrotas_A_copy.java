package year.y2014.greenlogistics.lp;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Specimen;
import chart.cube3d.Cube3D;
import chart.cube3d.WhiteSchema;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import dataset.DataSet;
import decision.elicitation.choice.ordering.BestRandom;
import decision.elicitation.choice.ordering.iterfaces.IChoice;
import decision.maker.ordering.Order;
import decision.maker.ordering.OrderingDM;
import decision.maker.ordering.interfaces.IOrderingDM;
import decision.model.utilityfunction.PartialSumUtility;
import draw.color.gradients.RedBlue;
import extractor.alternative.FromSpecimen;
import interfaces.ISpecimen;
import net.sf.javailp.*;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import patterns.weights.weights_3d;
import sort.functions.Duplication;
import standard.Common;
import standard.Point;
import standard.Range;
import utils.UtilityFunction;
import year.y2014.greenlogistics.A.DataA;
import year.y2014.greenlogistics.exp_c_full.interactiveA.ReferenceSolutions;
import year.y2014.greenlogistics.exp_visualization.Separate;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 25.11.2015.
 */
public class Mavrotas_A_copy
{
    public static void main(String args[])
    {
        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);


        DataA data = new DataA();

        int div = 10;
        int steps = 25;
        int trials = 100;

        double resultParetoSize[][] = new double[steps][trials];
        double resultsBestWSMDist[][] = new double[steps][trials];
        double resultsBestECMDist[][] = new double[steps][trials];
        double resultsBestUtil[][] = new double[steps][trials];
        double resultsAverageUtil[][] = new double[steps][trials];

        MersenneTwister generator = new MersenneTwister(System.currentTimeMillis());

        IChoice choice = new BestRandom(generator, 1);
        for (int trial = 0; trial < trials; trial++)
        {
            Range r[] = getRanges();

            double w1 = weights_3d.data[trial][0];
            double w2 = weights_3d.data[trial][1];
            double w3 = weights_3d.data[trial][2];

            //w1 = 0.5d;
            //w2 = 0.0d;
            //w3 = 0.5d;

            ArrayList<UtilityFunction> uf = new ArrayList<>(3);

            uf.add(new UtilityFunction(2));
            uf.get(0).add(new Point(843633.68, w1));
            uf.get(0).add(new Point(1047988.73, 0.000000d));

            uf.add(new UtilityFunction(2));
            uf.get(1).add(new Point(535039.184, w2));
            uf.get(1).add(new Point(571223.026, 0.000000d));

            uf.add(new UtilityFunction(2));
            uf.get(2).add(new Point(2712.256, w3));
            uf.get(2).add(new Point(14803.36, 0.000000d));

            IOrderingDM dm = new OrderingDM(new PartialSumUtility(uf,criteria));

            IAlternative optAlt = new Alternative("A", criteria);
            optAlt.setEvaluationVector(ReferenceSolutions._data[trial],criteria);
            double optimumWSM = dm.evaluate(optAlt);
            double optimumECM = getECMOptimum(dm, criteria);

            for (int step = 0; step < steps; step++)
            {
                System.out.println("STEP: " + step);
                ArrayList<ISpecimen> pareto = new ArrayList<>(100);
                for (int a = 0; a <= div; a++)
                {
                    for (int b = 0; b <= div; b++)
                    {
                        double co2Upper = r[1].right - (double) a * r[1].getRange() / (double) div;
                        double pmUpper = r[2].right - (double) b * r[2].getRange() / (double) div;

                        if (a == div) co2Upper = r[1].left;
                        if (a == 0) co2Upper = r[1].right;
                        if (b == div) pmUpper = r[2].left;
                        if (b == 0) pmUpper = r[2].right;

                        SolverFactory factory = new SolverFactoryLpSolve(); // use lp_solve
                        factory.setParameter(Solver.VERBOSE, 0);
                        factory.setParameter(Solver.TIMEOUT, 500);
                        Solver solver = factory.get();
                        Problem problem = new Problem();

                        double eps[] = {0.0d, 0.0d, 0.0d};
                        Constraints_A.addECMObjective(problem, data, 0, co2Upper, pmUpper, eps,
                                r[0].left, r[0].right, r[1].left, r[1].right, r[2].left, r[2].right, false, null);

                        Constraints_A.addConstraints(problem, data);
                        Result result = solver.solve(problem);

                        if (result != null)
                        {
                            double e[] = Constraints_A.eval(result, data);
                            if (e[0] > 1.0d)
                            {
                                IAlternative alt = new Alternative("A", criteria);
                                e[0] = Math.ceil(e[0]);
                                e[1] = Math.ceil(e[1]);
                                e[2] = Math.ceil(e[2]);
                                alt.setEvaluationVector(e, criteria);
                                ISpecimen spec = new Specimen("S", criteria);
                                spec.setAlternative(alt);
                                pareto.add(spec);
                            }
                        }
                    }
                }

                Duplication.extractDuplicates(pareto, criteria, Common.EPSILON, 2);

                if (pareto.size() < 2)
                {
                    for (int step2 = step; step2 < steps; step2++)
                    {
                        resultParetoSize[step2][trial] = resultParetoSize[step - 1][trial];
                        resultsBestECMDist[step2][trial] = resultsBestECMDist[step - 1][trial];
                        resultsBestWSMDist[step2][trial] = resultsBestWSMDist[step - 1][trial];
                        resultsBestUtil[step2][trial] = resultsBestUtil[step - 1][trial];
                        resultsAverageUtil[step2][trial] = resultsAverageUtil[step - 1][trial];
                    }
                    break;
                }

                ArrayList<IAlternative> alternatives = choice.getAlternativesToCompare(pareto, new FromSpecimen(), dm);
                Order o = dm.order(alternatives);
                ArrayList<IAlternative> ordered = o.getSortedArray();

                /*System.out.println(ordered.get(0).getEvaluationAt(criteria.get(0)) + " " +
                        ordered.get(0).getEvaluationAt(criteria.get(1)) + " " +
                        ordered.get(0).getEvaluationAt(criteria.get(2)));
                System.out.println(ordered.get(1).getEvaluationAt(criteria.get(0)) + " " +
                        ordered.get(1).getEvaluationAt(criteria.get(1)) + " " +
                        ordered.get(1).getEvaluationAt(criteria.get(2)));

                System.out.println(dm.evaluate(ordered.get(0)) + " " + dm.evaluate(ordered.get(1)));*/

                Order bestOrder = dm.order(pareto, new FromSpecimen());
                ArrayList<IAlternative> bestOrdered = bestOrder.getSortedArray();

                double results[] = dm.evaluate(pareto, new FromSpecimen());
                Mean mean = new Mean();

                double bestUtil = dm.evaluate(bestOrdered.get(0));
                double averageUtil = mean.evaluate(results);

                double distWSM = (optimumWSM - dm.evaluate(bestOrdered.get(0))) / optimumWSM;
                double distECM = (optimumECM - dm.evaluate(bestOrdered.get(0))) / optimumECM;

                System.out.println(step + " : " + optimumWSM + " " + bestUtil + " " + averageUtil + " " + distWSM + " " + distECM);

                for (int c = 0; c < 3; c++)
                {
                    double ref = ordered.get(0).getEvaluationAt(criteria.get(c));

                    double dv = r[c].right - ref;
                    double alpha = 0.5d;
                    r[c].right = ref + dv * alpha;

                    //double v2 = ordered.get(1).getEvaluationAt(criteria.get(c));
                    //if (v2 > v1)
                    //{
                    // System.out.println("UPdDATE " + c + " " + v1 + " " + v2 + " " + r[c].right);
                    //   r[c].right = v2 - 0.5d;
                    //}

                    //if ((v2 >= r[c].left) && (v2 <= v1))
                    //    r[c].left = v2;
                }

                resultParetoSize[step][trial] = pareto.size();
                resultsBestECMDist[step][trial] = distECM;
                resultsBestWSMDist[step][trial] = distWSM;
                resultsBestUtil[step][trial] = bestUtil;
                resultsAverageUtil[step][trial] = averageUtil;

                //if (step == steps - 1)
                //drawAll(pareto, criteria);

                System.out.println("STEP : " + step);
                for (ISpecimen s: pareto)
                {
                    double v0 = s.getAlternative().getEvaluationAt(criteria.get(0));
                    double v1 = s.getAlternative().getEvaluationAt(criteria.get(1));
                    double v2 = s.getAlternative().getEvaluationAt(criteria.get(2));
                    String str = String.format("%.4f %.4f %.4f", v0, v1,v2);
                    System.out.println(str.replace('.',','));
                }

            }

        }

        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();

        for (int s = 0; s < steps; s++)
        {
            String f = String.format("%d: %f %f %f %f %f %f %f %f %f %f", s,
                    mean.evaluate(resultParetoSize[s]),
                    sd.evaluate(resultParetoSize[s]),
                    mean.evaluate(resultsBestECMDist[s]),
                    sd.evaluate(resultsBestECMDist[s]),
                    mean.evaluate(resultsBestWSMDist[s]),
                    sd.evaluate(resultsBestWSMDist[s]),
                    mean.evaluate(resultsBestUtil[s]),
                    sd.evaluate(resultsBestUtil[s]),
                    mean.evaluate(resultsAverageUtil[s]),
                    sd.evaluate(resultsAverageUtil[s]));
            System.out.println(f);
        }

    }

    public static Range[] getRanges()
    {
        double v1[] = WSM_A.getResultForWeights(1.0d, 0.0d, 0.0d);
        double v2[] = WSM_A.getResultForWeights(0.0d, 1.0d, 0.0d);
        double v3[] = WSM_A.getResultForWeights(0.0d, 0.0d, 1.0d);

        double costMax = v2[0];
        if (v3[0] > costMax) costMax = v3[0];

        double co2Max = v1[1];
        if (v3[1] > co2Max) co2Max = v3[1];

        double pmMax = v1[2];
        if (v2[2] > pmMax) pmMax = v2[2];

        Range r[] = new Range[3];
        r[0] = new Range(v1[0], costMax);
        r[1] = new Range(v2[1], co2Max);
        r[2] = new Range(v3[2], pmMax);

        return r;
    }



    @SuppressWarnings("unused")
    public static void drawAll(ArrayList<ISpecimen> pareto, ArrayList<ICriterion> criteria)
    {
        Cube3D cube = new Cube3D(new Range(843600.0f, 1047900.0f), new Range(535000.0f, 572600.0f)
                , new Range(2700.0f, 14810.0f), new WhiteSchema());
        ArrayList<Point> points = new ArrayList<>(pareto.size());
        for (ISpecimen s : pareto)
        {
            Point p = new Point(s.getAlternative().getEvaluationAt(criteria.get(0)),
                    s.getAlternative().getEvaluationAt(criteria.get(1)),
                    s.getAlternative().getEvaluationAt(criteria.get(2)));
            points.add(p);
        }

        DataSet ds = new DataSet(points);
        ds.setGradient(new RedBlue());
        ArrayList<DataSet> ads = new ArrayList<>();
        ads.add(ds);

        cube.setDataSet(ads);
        cube.setVisible(true);
    }

    public static double getECMOptimum(IOrderingDM dm, ArrayList<ICriterion> criteria)
    {
        double max = -1.0d;
        for (double p[]: Separate.dataECM_A)
        {
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(p,criteria);
            double r = dm.evaluate(a);
            if (r > max) max = r;
        }
        return max;
    }
}
