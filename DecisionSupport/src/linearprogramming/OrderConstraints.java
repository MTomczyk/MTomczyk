package linearprogramming;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.maker.ordering.Order;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.Problem;
import standard.Common;

import java.util.ArrayList;

/**
 * Created by Michał on 2014-10-11.
 * <p/>
 * This function adds ordering constraints (ordering made by DM) to Problem object (LP).
 */
public class OrderConstraints
{
    /**
     * Add ordering constraints with e for each grater comparison. Used to check whether solution exists (MAX e).
     *
     * @param problem  Problem object (LP).
     * @param order    Order made by DM.
     * @param criteria Array of criteria.
     */
    public static void addOrderConstraints(Problem problem, Order order, ArrayList<ICriterion> criteria, String fixedPlus, String fixedMinus)
    {
        int used = 0;
        ArrayList<IAlternative> alternative = order.getSortedArray();

        int tmpSize = order._orders.get(0).size();
        int idx = 0;

        for (int i = 1; i < alternative.size(); i++)
        {
            Linear linear = new Linear();
            tmpSize--;

            boolean added = false;
            for (int j = 0; j < criteria.size(); j++)
            {
                if (!String.format("u%d(" + Common.doubleFormat + ")", j,
                        alternative.get(i - 1).getEvaluationAt(criteria.get(j))).equals(
                        String.format("u%d(" + Common.doubleFormat + ")", j,
                                alternative.get(i).getEvaluationAt(criteria.get(j)))))
                {

                    linear.add(1.0d, String.format("u%d(" + Common.doubleFormat + ")", j,
                            alternative.get(i - 1).getEvaluationAt(criteria.get(j))));

                    linear.add(-1.0d, String.format("u%d(" + Common.doubleFormat + ")", j,
                            alternative.get(i).getEvaluationAt(criteria.get(j))));

                    added = true;
                }
            }

            if (!added) continue;


            if (tmpSize > 0)
            {
                problem.add(linear, "=", 0.0d);
            } else
            {
                if (fixedPlus != null)
                {
                    linear.add(-1.0d, fixedPlus);
                    linear.add(1.0d, fixedMinus);
                }
                idx++;
                tmpSize = order._orders.get(idx).size();
                problem.add(linear, ">=", 0.0d);

            }
        }

    }

    @SuppressWarnings("all")
    public static int addOrderConstraintsMDVF(Problem problem, Order order, ArrayList<ICriterion> criteria,
                                              int baseNumber, String alpha, boolean acceptNegativeEpsilon)
    {
        return addOrderConstraintsMDVF(problem, order, criteria, baseNumber, alpha, true, acceptNegativeEpsilon);
    }


    /**
     * Add ordering constraints with individual e_i for each grater comparison. Used with MDVF model. Adds alpha <= every e_i.
     * Then MAX alpha.
     *
     * @param problem         Problem object (LP).
     * @param order           Order made by DM.
     * @param criteria        Array of criteria.
     * @param baseNumber      id of e_i
     * @param alpha           text id = alpha. You can name it beta f.i..
     * @param separateEpsilon TRUE = every new constraint in ordering (when ordering f.i. has 10 ordered elements) takes new e_i: baseNumber + j.
     *                        FALSE = every constraint in ordering takes the same id for e_i.
     * @return Number of different e_i. 1 when separateEpsilon = FALSE.
     */
    public static int addOrderConstraintsMDVF(Problem problem, Order order, ArrayList<ICriterion> criteria,
                                              int baseNumber, String alpha, boolean separateEpsilon, boolean acceptNegativeEpsilon)
    {
        int used = 0;
        ArrayList<IAlternative> alternative = order.getSortedArray();

        int tmpSize = order._orders.get(0).size();
        int idx = 0;

        for (int i = 1; i < alternative.size(); i++)
        {
            Linear linear = new Linear();
            tmpSize--;

            boolean added = false;
            for (int j = 0; j < criteria.size(); j++)
            {
                if (!String.format("u%d(" + Common.doubleFormat + ")", j,
                        alternative.get(i - 1).getEvaluationAt(criteria.get(j))).equals(
                        String.format("u%d(" + Common.doubleFormat + ")", j,
                                alternative.get(i).getEvaluationAt(criteria.get(j)))))
                {

                    linear.add(1.0d, String.format("u%d(" + Common.doubleFormat + ")", j,
                            alternative.get(i - 1).getEvaluationAt(criteria.get(j))));

                    linear.add(-1.0d, String.format("u%d(" + Common.doubleFormat + ")", j,
                            alternative.get(i).getEvaluationAt(criteria.get(j))));

                    added = true;
                }
            }

            if (!added) continue;


            if (tmpSize > 0)
            {
                problem.add(linear, "=", 0.0d);
            } else
            {
                linear.add(-1.0d, String.format("e(%d)", baseNumber + used));
                if (acceptNegativeEpsilon) linear.add(1.0d, String.format("ne(%d)", baseNumber + used));
                idx++;
                tmpSize = order._orders.get(idx).size();
                problem.add(linear, ">=", 0.0d);

                if (separateEpsilon)
                {
                    linear = new Linear();
                    linear.add(1.0d, alpha);
                    if (acceptNegativeEpsilon) linear.add(-1.0d, "n"+alpha);
                    linear.add(-1.0d, String.format("e(%d)", baseNumber + used));
                    if (acceptNegativeEpsilon) linear.add(1.0d, String.format("ne(%d)", baseNumber + used));
                    problem.add(linear, "<=", 0.0d);
                }
            }
            if (separateEpsilon) used++;
        }

        if (!separateEpsilon) used++;

        return used;
    }

    public static int addOrderConstraintsDisable(Problem problem, Order order, ArrayList<ICriterion> criteria,
                                                 String baseBinary, double bigValue, int baseBinaryValue,
                                                 double epsilon)
    {
        return addOrderConstraintsDisable(problem, order, criteria, baseBinary, bigValue, baseBinaryValue, epsilon,
                true);
    }


    /**
     * @param problem         Problem object (LP).
     * @param order           Order made by DM.
     * @param criteria        Array of criteria.
     * @param baseBinary      Base name of binary variables M_i.
     * @param bigValue        Some big value. In normalized UTA family problems it is OK to choose 1.
     * @param baseBinaryValue id M_i
     * @param epsilon         Very small positive value.
     * @param separateBinary  TRUE = every new constraint in ordering (when ordering f.i. has 10 ordered elements) takes new M_i: baseBinaryValue + j.
     *                        FALSE = every constraint in ordering takes the same id for M_i.
     * @return Number of different M_i. 1 when separateBinary = FALSE.
     */
    public static int addOrderConstraintsDisable(Problem problem, Order order, ArrayList<ICriterion> criteria,
                                                 String baseBinary, double bigValue, int baseBinaryValue,
                                                 double epsilon, boolean separateBinary)
    {
        int used = 0;
        ArrayList<IAlternative> alternative = order.getSortedArray();

        int tmpSize = order._orders.get(0).size();
        int idx = 0;

        for (int i = 1; i < alternative.size(); i++)
        {
            Linear linear = new Linear();
            linear.add(bigValue, String.format("%s%d", baseBinary, baseBinaryValue + used));

            tmpSize--;

            boolean added = false;
            for (int j = 0; j < criteria.size(); j++)
            {
                if (!String.format("u%d(" + Common.doubleFormat + ")", j,
                        alternative.get(i - 1).getEvaluationAt(criteria.get(j))).equals(
                        String.format("u%d(" + Common.doubleFormat + ")", j,
                                alternative.get(i).getEvaluationAt(criteria.get(j)))))
                {

                    linear.add(1.0d, String.format("u%d(" + Common.doubleFormat + ")", j,
                            alternative.get(i - 1).getEvaluationAt(criteria.get(j))));

                    linear.add(-1.0d, String.format("u%d(" + Common.doubleFormat + ")", j,
                            alternative.get(i).getEvaluationAt(criteria.get(j))));

                    added = true;
                }
            }

            if (!added) continue;

            if (tmpSize > 0)
            {
                problem.add(linear, "=", 0.0d);
            } else
            {
                idx++;
                tmpSize = order._orders.get(idx).size();
                problem.add(linear, ">=", epsilon);
            }

            if (separateBinary) used++;
        }

        if (used == 0) used = 1;
        // BINARY
        for (int i = 0; i < used; i++)
        {
            Linear linear = new Linear();
            linear.add(1.0d, String.format("%s%d", baseBinary, baseBinaryValue + i));
            problem.add(linear, "<=", 1);
            problem.setVarType(String.format("%s%d", baseBinary, baseBinaryValue + i), Integer.class);
        }

        return used;
    }



    public static int addOrderConstraintsMDVFAndDisable(Problem problem, Order order, ArrayList<ICriterion> criteria,
                                              int baseNumber, String alpha, boolean separateEpsilon, boolean acceptNegativeEpsilon,
                                                    String baseBinary,    double bigValue)
    {
        int used = 0;
        ArrayList<IAlternative> alternative = order.getSortedArray();

        int tmpSize = order._orders.get(0).size();
        int idx = 0;

        for (int i = 1; i < alternative.size(); i++)
        {
            Linear linear = new Linear();
            linear.add(bigValue, String.format("%s%d", baseBinary, baseNumber + used));

            tmpSize--;

            boolean added = false;
            for (int j = 0; j < criteria.size(); j++)
            {
                if (!String.format("u%d(" + Common.doubleFormat + ")", j,
                        alternative.get(i - 1).getEvaluationAt(criteria.get(j))).equals(
                        String.format("u%d(" + Common.doubleFormat + ")", j,
                                alternative.get(i).getEvaluationAt(criteria.get(j)))))
                {

                    linear.add(1.0d, String.format("u%d(" + Common.doubleFormat + ")", j,
                            alternative.get(i - 1).getEvaluationAt(criteria.get(j))));

                    linear.add(-1.0d, String.format("u%d(" + Common.doubleFormat + ")", j,
                            alternative.get(i).getEvaluationAt(criteria.get(j))));

                    added = true;
                }
            }

            if (!added) continue;


            if (tmpSize > 0)
            {
                problem.add(linear, "=", 0.0d);
            } else
            {
                linear.add(-1.0d, String.format("e(%d)", baseNumber + used));
                if (acceptNegativeEpsilon) linear.add(1.0d, String.format("ne(%d)", baseNumber + used));
                idx++;
                tmpSize = order._orders.get(idx).size();
                problem.add(linear, ">=", 0.0d);

                linear = new Linear();
                linear.add(1.0d, alpha);
                if (acceptNegativeEpsilon) linear.add(-1.0d, "n"+alpha);
                linear.add(-1.0d, String.format("e(%d)", baseNumber + used));
                if (acceptNegativeEpsilon) linear.add(1.0d, String.format("ne(%d)", baseNumber + used));
                problem.add(linear, "<=", 0.0d);
            }

            if (separateEpsilon) used++;
        }

        if (!separateEpsilon) used++;

        if (used == 0) used = 1;

        // BINARY
        for (int i = 0; i < used; i++)
        {
            Linear linear = new Linear();
            linear.add(1.0d, String.format("%s%d", baseBinary, baseNumber + i));
            problem.add(linear, "<=", 1);
            problem.setVarType(String.format("%s%d", baseBinary, baseNumber + i), Integer.class);
        }


        return used;
    }
}
