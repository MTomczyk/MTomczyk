package simplex;

import criterion.interfaces.ICriterion;
import standard.Point;
import standard.Range;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 16.02.2016.
 */
public class Convex
{
    public static boolean isConvex(Point p, ArrayList<Point> bounds, ArrayList<ICriterion> criteria)
    {
        if (bounds.size() != criteria.size())
            throw new IllegalArgumentException();

        ArrayList<Range> ranges = new ArrayList<>(criteria.size());
        for (int i = 0; i < criteria.size(); i++)
        {
            double l = bounds.get(0).getValues()[i];
            double r = bounds.get(0).getValues()[i];
            for (int j = 1; j < bounds.size(); j++)
            {
                double nv = bounds.get(j).getValues()[i];
                if (nv > r) r = nv;
                if (nv < l) l = nv;
            }
            ranges.add(new Range(l, r));
        }

        /*System.out.println("-----TUTAJ-----------");
        System.out.println(bounds.get(0).getValues()[0]);
        System.out.println(bounds.get(0).getValues()[1]);
        System.out.println(bounds.get(1).getValues()[0]);
        System.out.println(bounds.get(1).getValues()[1]);
        System.out.println("----------------");*/

        while (!isPointInBox(p, ranges))
        {
            for (int i = 0; i < ranges.size(); i++)
            {
                double dv = ranges.get(i).getRange();
                ranges.get(i).left -= dv;
                ranges.get(i).right += dv;
            }

        }

        return isConvexFromRanges(p, ranges, criteria);
    }

    private static boolean isPointInBox(Point p, ArrayList<Range> ranges)
    {
        for (int i = 0; i < ranges.size(); i++)
            if (!ranges.get(i).isInRange(p.getValues()[i])) return false;
        return true;
    }

    public static boolean isConvexFromRanges(Point p, ArrayList<Range> ranges, ArrayList<ICriterion> criteria)
    {
        if (ranges.size() != criteria.size())
            throw new IllegalArgumentException();

        double r = 0.0d;
        double v[] = p.getValues();

        for (int i = 0; i < criteria.size(); i++)
        {
            if (criteria.get(i).isGain())
                r += (ranges.get(i).right - v[i]) / ranges.get(i).getRange();
            else
                r += (v[i] - ranges.get(i).left) / ranges.get(i).getRange();
        }

        //System.out.println(r);

        if (r < 1.0d) return true;
        return false;
    }
}
