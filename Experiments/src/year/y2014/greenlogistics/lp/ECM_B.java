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
 * Created by MTomczyk on 09.11.2015.
 */
public class ECM_B
{

    public static double getCostConstraint(int s, int i) {
        double dV = (double) i / (double) s;
        double cost = 956.8d - (131.3 * dV);
        return cost * 1000.0d;
    }

    public static double getCO2Constraint(int s, int i) {
        double dV = (double) i / (double) s;
        double co2 = 621.4d - (83.5 * dV);
        return co2 * 1000.0d;
    }


    public static double getPMConstraint(int s, int i) {
        double dV = (double) i / (double) s;
        double pm = 27.6d - (23.2 * dV);
        return pm * 1000.0d;
    }

    public static void main(String[] args) {

        DataB d = new DataB();

        long startTime = System.nanoTime();

        int s = 16;

        ArrayList<Integer> pCost = new ArrayList<Integer>(s * s);
        ArrayList<Integer> pCO2 = new ArrayList<Integer>(s * s);
        ArrayList<Integer> pPM = new ArrayList<Integer>(s * s);
        ArrayList<Integer> del = new ArrayList<Integer>(s * s);

        System.out.printf("START\n");

        int prob = 0;
        for (int i = 0; i < 3; i++) {
            for (int a = 0; a <= s; a++) {
                System.out.println(a);
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
                       Constraints_B.addECMObjective(problem, d, 0, getCO2Constraint(s, a), getPMConstraint(s, b), eps,
                               null, null, null, null, null, null);
                    else if (i == 1)
                        Constraints_B.addECMObjective(problem, d, 1, getCostConstraint(s, a), getPMConstraint(s, b), eps,
                                null, null, null, null, null, null);
                    else if (i == 2)
                        Constraints_B.addECMObjective(problem, d, 2, getCostConstraint(s, a), getCO2Constraint(s, b), eps,
                                null, null, null, null, null, null);

                    Constraints_B.addConstraints(problem, d);
                    Result result = solver.solve(problem);

                    if (result != null)
                    {
                        double e[] = Constraints_B.eval(result, d);
                        if (e[0] > 1.0d) {

                            pCost.add((int) (e[0]));
                            pCO2.add((int) (e[1]));
                            pPM.add((int) (e[2]));
                        }
                    }



                }

            }
        }
        System.out.println("P: " + prob);

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

        ArrayList<ISpecimen> pareto = Front.getPareto(specimens, criteria, Common.EPSILON);
        System.out.println("Pareto " + pareto.size());
        for (ISpecimen sp: pareto)
        {
            System.out.println(sp.getAlternative().getEvaluationAt(criteria.get(0)) + " " +
                    sp.getAlternative().getEvaluationAt(criteria.get(1)) + " " +
                    sp.getAlternative().getEvaluationAt(criteria.get(2)) );
        }

        System.out.println("----------------------");

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
