package linearprogramming;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;

import java.util.ArrayList;

import utils.Sorter;
import tree.binary.BinaryTree;
import net.sf.javailp.Linear;
import net.sf.javailp.Problem;
import standard.Common;

/**
 * Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *         <p/>
 *         Add base constraints (UTA family methods): worst/best value of criterion and monotonic constraints.
 */
public class BaseConstraints
{
    /**
     * Adds base constraints to Problem object.
     *
     * @param problem          Problem object.
     * @param tree             Sorted values of criteria separately (See Sorter class).
     * @param criteria        Array of criteria.
     * @param boundaryEqual    FALSE = Worst/Best value >= 1 and <= 1. TRUE = Worst/Best value = 0/1
     * @param monotonicEpsilon A > B + epsilon in monotonic constraints
     */
    public static void addBaseConstraints(Problem problem, ArrayList<BinaryTree<IAlternative>> tree,
                                          ArrayList<ICriterion> criteria, boolean boundaryEqual,
                                          double monotonicEpsilon)
    {
        // FOR EACH CRITERION
        for (int i = 0; i < criteria.size(); i++)
        {
            IAlternative A = tree.get(i).search();
            IAlternative B;
            IAlternative WORST = null;

            // CREATE MONOTONIC CONSTRAINTS

            while ((B = tree.get(i).next()) != null)
            {
                if (Double.compare(B.getEvaluationAt(criteria.get(i)), A.getEvaluationAt(criteria.get(i))) == 0)
                    continue;

                Linear linear = new Linear();
                ConstraintCreator.init();
                ConstraintCreator.addDouble(
                        String.format("u%d(" + Common.doubleFormat + ")", i, A.getEvaluationAt(criteria.get(i))), 1.0d);
                ConstraintCreator.addDouble(String.format("u%d(" + Common.doubleFormat + ")", i, B.getEvaluationAt(criteria.get(i))), -1.0d);
                ConstraintCreator.addVarsToLinear(linear);
                problem.add(linear, ">=", monotonicEpsilon);
                A = B;
                WORST = B;
            }

            // CREATE WORST CONSTRAINTS
            Linear linear = new Linear();
            assert WORST != null;
            linear.add(1.0d,
                    String.format("u%d(" + Common.doubleFormat + ")", i, WORST.getEvaluationAt(criteria.get(i))));

            if (boundaryEqual) problem.add(linear, "=", 0.0d);
            else problem.add(linear, ">=", 0.0d);
        }

        // CREATE BEST CRITERION
        Linear linear = new Linear();
        for (int i = 0; i < criteria.size(); i++)
        {
            IAlternative BEST = tree.get(i).search();
            linear.add(1.0d,
                    String.format("u%d(" + Common.doubleFormat + ")", i, BEST.getEvaluationAt(criteria.get(i))));
        }

        if (boundaryEqual) problem.add(linear, "=", 1.0d);
        else problem.add(linear, "<=", 1.0d);
    }

    /**
     *
     * @param problem Problem object.
     * @param tree Sorted values of criteria separately (See Sorter class).
     * @param criterion Array of ICriterion.
     * @param boundaryEqual TRUE = Worst/Best value >= 1 and <= 1. FALSE = Worst/Best value = 0/1.
     * @param monotonicEpsilon A > B + epsilon in monotonic constraints.
     */

    /**
     * Adds base constraints to Problem object.
     *
     * @param problem          Problem object.
     * @param alternative      Array of IAlternative
     * @param criteria        Array of ICriterion
     * @param boundaryEqual    FALSE = Worst/Best value >= 1 and <= 1. TRUE = Worst/Best value = 0/1.
     * @param monotonicEpsilon A > B + epsilon in monotonic constraints.
     * @return Array of BinaryTree: sorted values for each criterion separately.
     */
    public static ArrayList<BinaryTree<IAlternative>> addBaseConstraintsAndSort(Problem problem,
                                                                                ArrayList<IAlternative> alternative,
                                                                                ArrayList<ICriterion> criteria,
                                                                                boolean boundaryEqual,
                                                                                double monotonicEpsilon)
    {

        ArrayList<BinaryTree<IAlternative>> tree = Sorter.getSortedByCriterion(alternative, criteria);
        addBaseConstraints(problem, tree, criteria, boundaryEqual, monotonicEpsilon);
        return tree;
    }
}
