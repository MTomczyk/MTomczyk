package year.y2014.greenlogistics.lp;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Specimen;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import net.sf.javailp.*;
import sort.functions.Duplication;
import sort.functions.Front;
import standard.Common;
import year.y2014.greenlogistics.A.DataA;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 23.11.2015.
 */
public class WSM_A
{
    public static double[] getResultForWeights(double w1r, double w2r, double w3r, double u1, double u2, double u3)
    {
        DataA data = new DataA();

        double w1 = w1r / (204355.0496 * 1000.0d);
        double w2 = w2r / (36183.842 * 1000.0d);
        double w3 = w3r / (12091.104 * 1000.0d);

        Problem problem = new Problem();
        Constraints_A.addWSMObjective(problem, data, w1, w2, w3, 0.0d, 0.0d, 0.0d,
                0.0d, u1, 0.0d, u2, 0.0d, u3, null ,null, null);

        Constraints_A.addConstraints(problem, data);


        SolverFactory factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 500);
        Solver solver = factory.get();
        Result result = solver.solve(problem);

        double res[] = null;

        if (result != null)
        {
            double e[] = Constraints_A.eval(result, data);
            if (e[0] > 1.0d)
            {
                res = new double[3];
                res[0] = Math.ceil(e[0]);
                res[1] = Math.ceil(e[1]);
                res[2] = Math.ceil(e[2]);

            }
        }
        return res;

    }

    public static double[] getResultForWeights(double w1r, double w2r, double w3r)
    {
        return getResultForWeights(w1r, w2r, w3r, Common.MAX_DOUBLE, Common.MAX_DOUBLE, Common.MAX_DOUBLE);

    }

    public static void main(String[] args)
    {
        DataA data = new DataA();

        long startTime = System.nanoTime();

        int spr = 115;
        ArrayList<Integer> pCost = new ArrayList<>(spr * spr * 2);
        ArrayList<Integer> pCO2 = new ArrayList<>(spr * spr * 2);
        ArrayList<Integer> pPM = new ArrayList<>(spr * spr * 2);
        double par = 1.0d / (double) spr;

        @SuppressWarnings("unused") int size = 0;
        int iteration = 0;

        for (int a = 0; a <= spr; a++)
        {
            System.out.println(a);
            for (int b = 0; b <= spr - a; b++)
            {

                int c = spr - a - b;
                iteration++;
                //System.out.print(iteration + " : ");

                double w1 = (double) a * par / (189.0d * 1000.0d);
                double w2 = (double) b * par / (35.5d * 1000.0d);
                double w3 = (double) c * par / (12.1d * 1000.0d);

                Problem problem = new Problem();
                //Constraints_A.addWSMObjective(problem, data, w1, w2, w3, 0.0d, 0.0d, 0.0d,
                //        null, null, null,null,null,null);
                Constraints_A.addWSMObjective(problem, data, w1, w2, w3, 0.0d, 0.0d, 0.0d,
                        0.0d, 1000000000.0d, 0.0d, 1000000000.0d, 0.0d, 1000000000.0d, null,null,null);

                Constraints_A.addConstraints(problem, data);

                SolverFactory factory = new SolverFactoryLpSolve();
                factory.setParameter(Solver.VERBOSE, 0);
                factory.setParameter(Solver.TIMEOUT, 500);
                Solver solver = factory.get();
                Result result = solver.solve(problem);



                if (result != null)
                {
                    double e[] = Constraints_A.eval(result, data);
                    if (e[0] > 1.0d) {

                        pCost.add( (int) Math.ceil(e[0]) );
                        pCO2.add((int)Math.ceil(e[1]));
                        pPM.add((int)Math.ceil(e[2]));
                    }
                }

                // System.out.println(cost + " " + co2 + " " + pm);
            }
        }

        System.out.printf("-------------- " + iteration + " \n");

        long endTime = System.nanoTime();
        System.out.println("Took " + (endTime - startTime) + " ns");

        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);

        ArrayList<ISpecimen> specimens = new ArrayList<>(pCost.size());
        for (int i = 0; i < pCost.size(); i++)
        {
            double e[] = {pCost.get(i), pCO2.get(i), pPM.get(i)};
            IAlternative alternative = new Alternative(String.format("A%d", i), criteria);
            alternative.setEvaluationVector(e, criteria);
            ISpecimen specimen = new Specimen(String.format("A%d", i), criteria);
            specimen.setAlternative(alternative);
            specimens.add(specimen);
        }
        System.out.println("BEFORE: " + specimens.size());
        @SuppressWarnings("unused") ArrayList<ISpecimen> duplicates = Duplication.extractDuplicates(specimens, criteria, Common.EPSILON, 1);
        System.out.println("AFTER: " + specimens.size());

        ArrayList<ISpecimen> pareto = Front.getPareto(specimens, criteria, Common.EPSILON);
        System.out.println("Pareto " + pareto.size());
        for (ISpecimen s: pareto)
        {
            System.out.println(s.getAlternative().getEvaluationAt(criteria.get(0)) + " " +
                    s.getAlternative().getEvaluationAt(criteria.get(1)) + " " +
                    s.getAlternative().getEvaluationAt(criteria.get(2)) );
        }

        // COST-PM
        {
            ArrayList<ICriterion> costPM = Criterion.getCriterionArray("C", 2, false);
            ArrayList<ISpecimen> costPMSpec = new ArrayList<>(pareto.size());
            for (ISpecimen sp: pareto)
            {
                double e[] = {sp.getAlternative().getEvaluationAt(criteria.get(0)),
                        sp.getAlternative().getEvaluationAt(criteria.get(2))};
                IAlternative a = new Alternative("A", costPM);
                a.setEvaluationVector(e, costPM);
                ISpecimen dS = new Specimen("S", costPM);
                dS.setAlternative(a);
                costPMSpec.add(dS);
            }
            ArrayList<ISpecimen> p = Front.getPareto(costPMSpec, costPM, Common.EPSILON);
            for (ISpecimen sp: p)
            {
                System.out.println(sp.getAlternative().getEvaluationAt(costPM.get(0)) + " " +
                        sp.getAlternative().getEvaluationAt(costPM.get(1)));
            }
        }
        System.out.println("-----------------------");
        // COST-CO
        {
            ArrayList<ICriterion> costCO2 = Criterion.getCriterionArray("C", 2, false);
            ArrayList<ISpecimen> costPMSpec = new ArrayList<>(pareto.size());
            for (ISpecimen sp: pareto)
            {
                double e[] = {sp.getAlternative().getEvaluationAt(criteria.get(0)),
                        sp.getAlternative().getEvaluationAt(criteria.get(1))};
                IAlternative a = new Alternative("A", costCO2);
                a.setEvaluationVector(e, costCO2);
                ISpecimen dS = new Specimen("S", costCO2);
                dS.setAlternative(a);
                costPMSpec.add(dS);
            }
            ArrayList<ISpecimen> p = Front.getPareto(costPMSpec, costCO2, Common.EPSILON);
            for (ISpecimen sp: p)
            {
                System.out.println(sp.getAlternative().getEvaluationAt(costCO2.get(0)) + " " +
                        sp.getAlternative().getEvaluationAt(costCO2.get(1)));
            }
        }
    }
}
