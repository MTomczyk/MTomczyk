package utils;


import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import distance.interfaces.IDistance;
import standard.Point;

import java.util.ArrayList;

/**
 * Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *         <p/>
 *         It calculates distances between pair of alternatives.
 */

public class Distance
{
    /**
     * Returns distance between two alternatives.
     *
     * @param A        First alternative.
     * @param B        Second alternative.
     * @param criteria Array of criteria.
     * @param distance Distance object.
     * @return Distance between two alternatives.
     */
    public static double getDistance(IAlternative A, IAlternative B, ArrayList<ICriterion> criteria,
                                     IDistance distance)
    {
        double a[] = A.getEvaluationVector(criteria);
        double b[] = B.getEvaluationVector(criteria);

        Point pA = new Point(a);
        Point pB = new Point(b);

        return distance.getDistance(pA, pB);
    }

}
