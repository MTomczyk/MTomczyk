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
public class RunnerECM_A {

    public static double getCostConstraint(int s, int i) {
        double dV = (double) i / (double) s;
        double cost = 1032.7d - (189 * dV);
        return cost * 1000.0d;
    }

    public static double getCO2Constraint(int s, int i) {
        double dV = (double) i / (double) s;
        double co2 = 570.6d - (35.5 * dV);
        return co2 * 1000.0d;
    }


    public static double getPMConstraint(int s, int i) {
        double dV = (double) i / (double) s;
        double pm = 14.8d - (12.1 * dV);
        return pm * 1000.0d;
    }

    public static void main(String[] args) {

        DataA d = new DataA();

        long startTime = System.nanoTime();

        int s = 3;

        ArrayList<Integer> pCost = new ArrayList<Integer>(s * s);
        ArrayList<Integer> pCO2 = new ArrayList<Integer>(s * s);
        ArrayList<Integer> pPM = new ArrayList<Integer>(s * s);
        ArrayList<Integer> del = new ArrayList<Integer>(s * s);

        System.out.printf("START\n");

        int prob = 0;
        for (int i = 0; i < 3; i++) {
            for (int a = 0; a <= s; a++) {
                for (int b = 0; b <= s; b++) {
                    prob++;

                    //System.out.println(prob + " " + i + " " + a + " " + b);

                    SolverFactory factory = new SolverFactoryLpSolve(); // use lp_solve
                    factory.setParameter(Solver.VERBOSE, 0);
                    factory.setParameter(Solver.TIMEOUT, 500);
                    Solver solver = factory.get();
                    Problem problem = new Problem();

                    double eps[] = {0.0d, 0.0d, 0.0d};

                    if (i == 0)
                        Constraints_A.addECMObjective(problem, d, 0, getCO2Constraint(s, a), getPMConstraint(s, b), eps);
                    else if (i == 1)
                        Constraints_A.addECMObjective(problem, d, 1, getCostConstraint(s, a), getPMConstraint(s, b), eps);
                    else if (i == 2)
                        Constraints_A.addECMObjective(problem, d, 2, getCostConstraint(s, a), getCO2Constraint(s, b), eps);

                    Constraints_A.addConstraints(problem, d);
                    Result result = solver.solve(problem);

                    if (result != null)
                    {
                        double e[] = Constraints_A.eval(result, d);
                        if (e[0] > 1.0d) {

                            pCost.add((int) (e[0]));
                            pCO2.add((int) (e[1]));
                            pPM.add((int) (e[2]));
                        }
                    }



                }

            }
        }

        /// ----------------------
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
        ArrayList<ISpecimen> duplicates = Duplication.extractDuplicates(specimens,criteria, Common.EPSILON, 1);
        System.out.println("AFTER: " + specimens.size());



    }

}
