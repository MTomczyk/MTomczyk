package linearprogramming;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.Problem;
import standard.Common;
import tree.binary.BinaryTree;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 03.11.2015.
 */
public class SlopeConstraints
{
    public static void addLinearConstraints(Problem problem, ArrayList<BinaryTree<IAlternative>> tree,
                                            ArrayList<ICriterion> criteria)
    {
        // FOR EACH CRITERION
        for (int i = 0; i < criteria.size(); i++)
        {
            ICriterion C = criteria.get(i);
            IAlternative P1 = tree.get(i).search();
            IAlternative P2;
            while ((P2 = tree.get(i).next()) != null)
            {
                if (Double.compare(P2.getEvaluationAt(criteria.get(i)), P1.getEvaluationAt(criteria.get(i))) == 0)
                    continue;
                break;
            }

            if (P2 == null) continue;
            IAlternative P3;

            while ((P3 = tree.get(i).next()) != null)
            {
                if (Double.compare(P3.getEvaluationAt(criteria.get(i)), P2.getEvaluationAt(criteria.get(i))) == 0)
                    continue;

                double x1 = P1.getEvaluationAt(C);
                double x2 = P2.getEvaluationAt(C);
                double x3 = P3.getEvaluationAt(C);

                String u1 = String.format("u%d(" + Common.doubleFormat + ")",i,x1);
                String u2 = String.format("u%d(" + Common.doubleFormat + ")",i,x2);
                String u3 = String.format("u%d(" + Common.doubleFormat + ")",i,x3);

                double denomR = x3 - x2;
                double denomL = x2 - x1;

                ConstraintCreator.init();
                ConstraintCreator.addDouble(u2, 1.0d/ denomL);
                ConstraintCreator.addDouble(u1, -1.0d/ denomL);
                ConstraintCreator.addDouble(u3, -1.0d/ denomR);
                ConstraintCreator.addDouble(u2, 1.0d/ denomR);

                Linear linear = new Linear();
                ConstraintCreator.addVarsToLinear(linear);

                problem.add(linear, "=", 0.0d);


                P1 = P2;
                P2 = P3;
            }
        }
    }

    public static void addLinearWithToleranceConstraints(Problem problem, ArrayList<BinaryTree<IAlternative>> tree,
                                            ArrayList<ICriterion> criteria, double tolerance)
    {
        // FOR EACH CRITERION
        for (int i = 0; i < criteria.size(); i++)
        {
            ICriterion C = criteria.get(i);
            IAlternative P1 = tree.get(i).search();
            IAlternative P2;
            while ((P2 = tree.get(i).next()) != null)
            {
                if (Double.compare(P2.getEvaluationAt(criteria.get(i)), P1.getEvaluationAt(criteria.get(i))) == 0)
                    continue;
                break;
            }

            if (P2 == null) continue;
            IAlternative P3;

            while ((P3 = tree.get(i).next()) != null)
            {
                if (Double.compare(P3.getEvaluationAt(criteria.get(i)), P2.getEvaluationAt(criteria.get(i))) == 0)
                    continue;

                double x1 = P1.getEvaluationAt(C);
                double x2 = P2.getEvaluationAt(C);
                double x3 = P3.getEvaluationAt(C);

                String u1 = String.format("u%d(" + Common.doubleFormat + ")",i,x1);
                String u2 = String.format("u%d(" + Common.doubleFormat + ")",i,x2);
                String u3 = String.format("u%d(" + Common.doubleFormat + ")",i,x3);

                double denomR = x3 - x2;
                double denomL = x2 - x1;

                ConstraintCreator.init();
                ConstraintCreator.addDouble(u2, 1.0d/ denomL);
                ConstraintCreator.addDouble(u1, -1.0d/ denomL);
                ConstraintCreator.addDouble(u3, -1.0d/ denomR);
                ConstraintCreator.addDouble(u2, 1.0d/ denomR);

                Linear linear = new Linear();
                ConstraintCreator.addVarsToLinear(linear);

                Linear linear2 = new Linear();
                ConstraintCreator.addVarsToLinear(linear2);

                problem.add(linear, ">=", -tolerance);
                problem.add(linear2, "<=", tolerance);


                P1 = P2;
                P2 = P3;
            }
        }
    }

    public static void addMaxSlopeConstraints(Problem problem, ArrayList<BinaryTree<IAlternative>> tree,
                                                         ArrayList<ICriterion> criteria, double tolerance)
    {

        // FOR EACH CRITERION
        for (int i = 0; i < criteria.size(); i++)
        {
            ICriterion C = criteria.get(i);
            IAlternative P1 = tree.get(i).search();
            IAlternative P2;
            while ((P2 = tree.get(i).next()) != null)
            {
                if (Double.compare(P2.getEvaluationAt(criteria.get(i)), P1.getEvaluationAt(criteria.get(i))) == 0)
                    continue;

                double x1 = P1.getEvaluationAt(C);
                double x2 = P2.getEvaluationAt(C);

                String u1 = String.format("u%d(" + Common.doubleFormat + ")", i, x1);
                String u2 = String.format("u%d(" + Common.doubleFormat + ")", i, x2);

                double dX = x2 - x1;
                ConstraintCreator.init();
                ConstraintCreator.addDouble(u2, 1.0d / dX);
                ConstraintCreator.addDouble(u1, -1.0d / dX);

                Linear linear = new Linear();
                ConstraintCreator.addVarsToLinear(linear);

                Linear linear2 = new Linear();
                ConstraintCreator.addVarsToLinear(linear2);


                problem.add(linear, ">=", -tolerance);
                problem.add(linear2, "<=", tolerance);

                P1 = P2;
            }

        }
    }

    public static void addMSCVFConstraints(Problem problem, ArrayList<BinaryTree<IAlternative>> tree,
                                          ArrayList<ICriterion> criteria, String ro)
    {
        // FOR EACH CRITERION
        for (int i = 0; i < criteria.size(); i++)
        {
            ICriterion C = criteria.get(i);
            IAlternative P1 = tree.get(i).search();
            IAlternative P2;
            while ((P2 = tree.get(i).next()) != null)
            {
                if (Double.compare(P2.getEvaluationAt(criteria.get(i)), P1.getEvaluationAt(criteria.get(i))) == 0)
                    continue;
                break;
            }

            if (P2 == null) continue;
            IAlternative P3;

            while ((P3 = tree.get(i).next()) != null)
            {
                if (Double.compare(P3.getEvaluationAt(criteria.get(i)), P2.getEvaluationAt(criteria.get(i))) == 0)
                    continue;

                double x1 = P1.getEvaluationAt(C);
                double x2 = P2.getEvaluationAt(C);
                double x3 = P3.getEvaluationAt(C);

                //if (C.isGain())
                //{
                    /*x1 = P3.getEvaluationAt(C);
                    x2 = P2.getEvaluationAt(C);
                    x3 = P1.getEvaluationAt(C);*/
                //}

                String u1 = String.format("u%d(" + Common.doubleFormat + ")",i,x1);
                String u2 = String.format("u%d(" + Common.doubleFormat + ")",i,x2);
                String u3 = String.format("u%d(" + Common.doubleFormat + ")",i,x3);

                {
                    double denomR = x3 - x2;
                    double denomL = x2 - x1;

                    Linear l = new Linear();
                    l.add(1.0d / denomR,u3);
                    l.add(-((1.0d/denomR) + (1.0d/denomL)),u2);
                    l.add(1.0d / denomL,u1);
                    l.add(-1.0d, ro);
                    problem.add(l, "<=", 0.0d);
                }
                {
                    double denomR = x3 - x2;
                    double denomL = x2 - x1;

                    Linear l = new Linear();
                    l.add(-1.0d / denomR,u3);
                    l.add(((1.0d/denomR) + (1.0d/denomL)),u2);
                    l.add(-1.0d / denomL,u1);
                    l.add(-1.0d, ro);
                    problem.add(l, "<=", 0.0d);
                }

                P1 = P2;
                P2 = P3;

            }
        }
    }
}
