package utils;

import java.util.ArrayList;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import extractor.alternative.FromAlternative;
import extractor.interfaces.IAlternativeExtractor;
import interfaces.ISpecimen;

/**
 * Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *         <p/>
 *         It provides methods for relation of dominance.
 */
public class Domination
{
    /**
     * Check if A is good at least lp B.
     *
     * @param A        Alternative A.
     * @param B        Alternative B.
     * @param criteria Array of criteria.
     * @param epsilon  Epsilon (small value) for comparing small double values.
     * @return TRUE - A is good at least lp B. FALSE - otherwise.
     */
    public static boolean isGoodAtLeastAs(IAlternative A, IAlternative B, ArrayList<ICriterion> criteria,
                                          double epsilon)
    {
        return isGoodAtLeastAs(A, B, criteria, null, epsilon);
    }

    /**
     * Check if A is good at least lp B.
     *
     * @param A        Alternative A.
     * @param B        Alternative B.
     * @param criteria Array of criteria.
     * @param mask     Array of TRUE/FALSE values. FALSE on n-th position: n-th criterion doesn't matter in this relation.
     * @param epsilon  Epsilon (small value) for comparing small double values.
     * @return TRUE - A is good at least lp B. FALSE - otherwise.
     */
    public static boolean isGoodAtLeastAs(IAlternative A, IAlternative B, ArrayList<ICriterion> criteria,
                                          boolean[] mask, double epsilon)
    {
        for (int i = 0; i < criteria.size(); i++)
        {
            if ((mask != null) && (!mask[i])) continue;

            if (criteria.get(i).isGain())
            {
                if (B.getEvaluationAt(criteria.get(i)) > A.getEvaluationAt(criteria.get(i)) + epsilon) return false;
            } else
            {
                if (B.getEvaluationAt(criteria.get(i)) + epsilon < A.getEvaluationAt(criteria.get(i))) return false;
            }
        }
        return true;
    }

    /**
     * Check if A dominates B. It is constructed lp a logical expression: IsGoodAtLeast And NOT Equal
     *
     * @param A        Alternative A.
     * @param B        Alternative B.
     * @param criteria Array of criteria.
     * @param epsilon  Epsilon (small value) for comparing small double values.
     * @return TRUE - A dominates B. FALSE - otherwise.
     */
    public static boolean isDominating(IAlternative A, IAlternative B, ArrayList<ICriterion> criteria, double epsilon)
    {
        return (isGoodAtLeastAs(A, B, criteria, epsilon)) && (!isEqual(A, B, criteria, epsilon));
    }

    public static boolean isTMultipleDominating(IAlternative A, IAlternative B, ArrayList<ICriterion> criteria, double t[], double epsilon)
    {
        IAlternative dummy = A.clone();
        for (int i = 0; i < criteria.size(); i++)
        {
            double multi = (1 - t[i]);
            if (criteria.get(i).isGain()) multi = (1 + t[i]);
            double v = dummy.getEvaluationAt(criteria.get(i));
            dummy.setEvaluationAt(criteria.get(i), v * multi);
        }

        return (isGoodAtLeastAs(dummy, B, criteria, epsilon)) && (!isEqual(dummy, B, criteria, epsilon));
    }

    /**
     * Check if A equals B.
     *
     * @param A        Alternative A.
     * @param B        Alternative B.
     * @param criteria Array of criteria.
     * @param epsilon  Epsilon (small value) for comparing small double values.
     * @return TRUE - A equals B. FALSE - otherwise.
     */
    public static boolean isEqual(IAlternative A, IAlternative B, ArrayList<ICriterion> criteria, double epsilon)
    {
        return isEqual(A, B, criteria, null, epsilon);
    }

    /**
     * Check if A equals B.
     *
     * @param A        Alternative A.
     * @param B        Alternative B.
     * @param criteria Array of criteria.
     * @param mask     Array of TRUE/FALSE values. FALSE on n-th position: n-th criterion doesn't matter in this relation.
     * @param epsilon  Epsilon (small value) for comparing small double values.
     * @return TRUE - A equals B. FALSE - otherwise.
     */
    public static boolean isEqual(IAlternative A, IAlternative B, ArrayList<ICriterion> criteria, boolean[] mask,
                                  double epsilon)
    {
        for (int i = 0; i < criteria.size(); i++)
        {
            if ((mask != null) && (!mask[i])) continue;

            if ((A.getEvaluationAt(criteria.get(i)) < B.getEvaluationAt(
                    criteria.get(i)) - epsilon) || (A.getEvaluationAt(criteria.get(i)) > B.getEvaluationAt(
                    criteria.get(i)) + epsilon)) return false;
        }

        return true;
    }


    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------

    /**
     * Calculates domination matrix.
     *
     * @param alternatives Array of alternatives.
     * @param criteria     Array of criteria.
     * @param epsilon      Epsilon
     * @return Matrix of domination
     */
    public static boolean[][] computeDominationMatrix(ArrayList<IAlternative> alternatives,
                                                      ArrayList<ICriterion> criteria, double epsilon)
    {
        return computeDominationMatrix(alternatives, new FromAlternative(), criteria, epsilon);
    }

    /**
     * Calculates domination matrix.
     *
     * @param objects   must be an ArrayList of objects.
     * @param extractor object extractor from ArrayList
     * @param criteria  Array of criteria.
     * @param epsilon   Epsilon
     * @return Matrix of domination
     */
    public static boolean[][] computeDominationMatrix(Object objects, IAlternativeExtractor extractor,
                                                      ArrayList<ICriterion> criteria, double epsilon)
    {
        @SuppressWarnings("all")
        ArrayList<Object> alternatives = (ArrayList<Object>) objects;

        boolean domination[][] = new boolean[alternatives.size()][alternatives.size()];

        for (int i = 0; i < alternatives.size(); i++)
        {
            for (int j = 0; j < alternatives.size(); j++)
            {
                if (i == j) continue;
                IAlternative A = extractor.getValue(alternatives.get(i));
                IAlternative B = extractor.getValue(alternatives.get(j));
                if (Domination.isDominating(A, B, criteria, epsilon))
                {
                    domination[i][j] = true;
                }
            }
        }
        return domination;

    }

    public static boolean isNonDominated(IAlternative alternative, ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria, double epsilon)
    {
        for (IAlternative a:alternatives)
        {
            if (isDominating(a, alternative,criteria, epsilon)) return false;
        }

        return true;
    }
}
