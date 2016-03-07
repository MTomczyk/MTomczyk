package utils;


import java.util.ArrayList;
import java.util.LinkedList;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.maker.ordering.Order;
import standard.Common;
import standard.Range;

/**
 * Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *         <p/>
 *         It provides methods for creating array of ranges
 */
public class RangeMaker
{
    /**
     * Create range for each criterion where values are taken from alternatives.
     *
     * @param alternatives List of alternatives.
     * @param criteria     Array of criterion.
     * @return Array of ranges.
     */
    public static ArrayList<Range> getRange(LinkedList<IAlternative> alternatives, ArrayList<ICriterion> criteria)
    {
        int noOfCriteria = criteria.size();
        ArrayList<Range> range = new ArrayList<Range>(noOfCriteria);
        for (int j = 0; j < criteria.size(); j++)
            range.add(new Range(Common.MAX_DOUBLE, Common.MIN_DOUBLE));

        for (IAlternative a : alternatives)
        {
            for (int j = 0; j < criteria.size(); j++)
            {
                double v = a.getEvaluationAt(criteria.get(j));
                if (v < range.get(j).left) range.get(j).left = v;
                if (v > range.get(j).right) range.get(j).right = v;
            }
        }
        return range;
    }

    /**
     * Create range for each criterion where values are taken from alternatives.
     *
     * @param alternatives Array of alternatives.
     * @param criteria     Array of criterion.
     * @return Array of ranges.
     */
    public static ArrayList<Range> getRange(ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria)
    {
        int noOfCriteria = criteria.size();
        ArrayList<Range> range = new ArrayList<Range>(noOfCriteria);
        for (int j = 0; j < criteria.size(); j++)
            range.add(new Range(Common.MAX_DOUBLE, Common.MIN_DOUBLE));

        for (IAlternative anAlternative : alternatives)
        {
            for (int j = 0; j < criteria.size(); j++)
            {
                double v = anAlternative.getEvaluationAt(criteria.get(j));
                if (v < range.get(j).left) range.get(j).left = v;
                if (v > range.get(j).right) range.get(j).right = v;
            }
        }
        return range;
    }

    /**
     * Create range for each criterion where values are taken from ordering.
     *
     * @param order    Order object.
     * @param criteria Array of criterion.
     * @return Array of ranges.
     */
    public static ArrayList<Range> getRange(Order order, ArrayList<ICriterion> criteria)
    {
        int noOfCriteria = criteria.size();
        ArrayList<Range> range = new ArrayList<Range>(noOfCriteria);
        for (int j = 0; j < criteria.size(); j++)
            range.add(new Range(Common.MAX_DOUBLE, Common.MIN_DOUBLE));

        for (ArrayList<IAlternative> dom : order._orders)
        {
            for (IAlternative e : dom)
            {
                for (int j = 0; j < criteria.size(); j++)
                {
                    double v = e.getEvaluationAt(criteria.get(j));
                    if (v < range.get(j).left) range.get(j).left = v;
                    if (v > range.get(j).right) range.get(j).right = v;
                }
            }
        }
        return range;
    }


}
