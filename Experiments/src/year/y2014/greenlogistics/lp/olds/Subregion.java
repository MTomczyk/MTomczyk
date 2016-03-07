package year.y2014.greenlogistics.lp.olds;

import standard.Common;
import standard.Range;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 16.11.2015.
 */
public class Subregion
{
    public ArrayList<ArrayList<Range>> e = new ArrayList<>();

    public Subregion(int dimensions)
    {
        for (int i = 0; i < dimensions; i++)
            e.add(new ArrayList<>());
    }

    public void print()
    {
        for (ArrayList <Range> ar: e)
        {
            System.out.println("--------------");
            for (Range r: ar)
                System.out.println(r.left + " " + r.right);
        }

    }

    public void add(ArrayList<Range> nr)
    {
        for (int i = 0; i < e.size(); i++)
        {
            if (i >= nr.size()) break;
            if (nr.get(i) == null) continue;
            ArrayList<Range> toMerge = new ArrayList<>(e.get(i).size());
            double max = nr.get(i).right;
            double min = nr.get(i).left;

            for (Range r : e.get(i))
            {
                if ((nr.get(i).isInRange(r.left)) || (nr.get(i).isInRange(r.right)))
                {
                    toMerge.add(r);
                    if (r.right > max) max = r.right;
                    if (r.left < min) min = r.left;
                }
            }

            for (Range r : toMerge)
                e.get(i).remove(r);

            e.get(i).add(new Range(min, max));
        }
    }

    public boolean contains(ArrayList<Range> r)
    {
        for (int i = 0; i < e.size(); i++)
        {
            if (i >= r.size()) break;
            if (r.get(i) == null) continue;

            @SuppressWarnings("unused") double min = Common.MAX_DOUBLE;
            @SuppressWarnings("unused") double max = Common.MIN_DOUBLE;

            boolean isInside = false;

            for (int j = 0; j < e.get(i).size(); j++)
            {
                if (
                        (e.get(i).get(j).isInRange(r.get(i).left)) &&
                                (e.get(i).get(j).isInRange(r.get(i).right)) )
                {
                    isInside = true;
                    break;
                }
            }
            if (!isInside) return false;
        }

        return true;
    }

}
