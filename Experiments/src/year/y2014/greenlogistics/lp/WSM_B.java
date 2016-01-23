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
import year.y2014.greenlogistics.B.DataB;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 23.11.2015.
 */
public class WSM_B
{
    public static void main(String[] args)
    {
        DataB data = new DataB();

        long startTime = System.nanoTime();

        int spr = 20;
        ArrayList<Integer> pCost = new ArrayList<Integer>(spr * spr * 2);
        ArrayList<Integer> pCO2 = new ArrayList<Integer>(spr * spr * 2);
        ArrayList<Integer> pPM = new ArrayList<Integer>(spr * spr * 2);
        double par = 1.0d / (double) spr;

        int size = 0;
        int iteration = 0;

        for (int a = 0; a <= spr; a++)
        {
            System.out.println(a);
            for (int b = 0; b <= spr - a; b++)
            {

                int c = spr - a - b;
                iteration++;
                //System.out.print(iteration + " : ");
                double w1 = (double) a * par / (131.3d * 1000.0d);
                double w2 = (double) b * par / (83.5d * 1000.0d);
                double w3 = (double) c * par / (23.2d * 1000.0d);

                Problem problem = new Problem();
                //Constraints_A.addWSMObjective(problem, data, w1, w2, w3, 0.0d, 0.0d, 0.0d,
                //        null, null, null,null,null,null);
                Constraints_B.addWSMObjective(problem, data, w1, w2, w3, 0.0d, 0.0d, 0.0d,
                        0.0d, 1000000000.0d, 0.0d, 1000000000.0d, 0.0d, 1000000000.0d);

                Constraints_B.addConstraints(problem, data);

                SolverFactory factory = new SolverFactoryLpSolve();
                factory.setParameter(Solver.VERBOSE, 0);
                factory.setParameter(Solver.TIMEOUT, 500);
                Solver solver = factory.get();
                Result result = solver.solve(problem);



                if (result != null)
                {
                    double e[] = Constraints_B.eval(result, data);
                    if (e[0] > 1.0d) {

                        pCost.add((int) (e[0]));
                        pCO2.add((int) (e[1]));
                        pPM.add((int) (e[2]));
                    }
                }

                // System.out.println(cost + " " + co2 + " " + pm);
            }
        }

        System.out.printf("-------------- " + iteration + " \n");

        long endTime = System.nanoTime();
        System.out.println("Took " + (endTime - startTime) + " ns");

        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);

        ArrayList<ISpecimen> specimens = new ArrayList<ISpecimen>(pCost.size());
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
        ArrayList<ISpecimen> duplicates = Duplication.extractDuplicates(specimens, criteria, Common.EPSILON, 1);
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
            ArrayList<ISpecimen> costPMSpec = new ArrayList<ISpecimen>(pareto.size());
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
            ArrayList<ISpecimen> costPMSpec = new ArrayList<ISpecimen>(pareto.size());
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
