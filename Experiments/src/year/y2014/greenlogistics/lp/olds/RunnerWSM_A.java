package year.y2014.greenlogistics.lp.olds;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Specimen;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import net.sf.javailp.*;
import sort.functions.Duplication;
import standard.Common;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 09.11.2015.
 */
public class RunnerWSM_A {

    public static void main(String[] args)
    {
        DataA data = new DataA();

        long startTime = System.nanoTime();

        int spr = 9;
        ArrayList<Integer> pCost = new ArrayList<>(spr * spr * 2);
        ArrayList<Integer> pCO2 = new ArrayList<>(spr * spr * 2);
        ArrayList<Integer> pPM = new ArrayList<>(spr * spr * 2);
        double par = 1.0d / (double) spr;

        @SuppressWarnings("unused") int size = 0;
        int iteration = 0;

        for (int a = 0; a <= spr; a++)
        {
            for (int b = 0; b <= spr - a; b++)
            {
                int c = spr - a - b;
                iteration++;
                System.out.print(iteration + " : ");

                double w1 = (double) a * par / (189.0d * 1000.0d);
                double w2 = (double) b * par / (35.5d * 1000.0d);
                double w3 = (double) c * par / (12.1d * 1000.0d);

                Problem problem = new Problem();
                //Constraints_A.addWSMObjective(problem, data, w1, w2, w3, 0.0d, 0.0d, 0.0d,
                //        null, null, null,null,null,null);
                Constraints_A.addWSMObjective(problem, data, w1, w2, w3, 0.0d, 0.0d, 0.0d,
                        0.0d, 1000000000.0d, 0.0d,1000000000.0d,0.0d,1000000000.0d);

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

                        pCost.add((int) (e[0]));
                        pCO2.add((int) (e[1]));
                        pPM.add((int) (e[2]));
                    }
                }

               // System.out.println(cost + " " + co2 + " " + pm);
            }
        }

        System.out.printf("-------------- \n");

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

    }

}
