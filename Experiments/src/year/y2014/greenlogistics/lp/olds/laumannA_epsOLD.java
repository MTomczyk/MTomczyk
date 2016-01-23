package year.y2014.greenlogistics.lp.olds;

import criterion.Criterion;
import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import standard.Common;
import standard.Range;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 16.11.2015.
 */
public class laumannA_epsOLD
{


    public static void main(String args[])
    {
        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);

        ArrayList<ISpecimen> P = new ArrayList<ISpecimen>(200);
        ArrayList<Subregion> Q = new ArrayList<Subregion>(200);

        Subregion e = new Subregion();
        e.e = new ArrayList<Range>(3);
        e.e.add(new Range(Common.MIN_DOUBLE, Common.MAX_DOUBLE));
        e.e.add(new Range(Common.MIN_DOUBLE, Common.MAX_DOUBLE));
        e.e.add(new Range(Common.MIN_DOUBLE, Common.MAX_DOUBLE));

        boolean skipSecondLine = false;

        int i = (P.size() + 1) * (P.size() + 1);
        while (true)
        {
            if (!skipSecondLine)
                i = (P.size() + 1) * (P.size() + 1);
            skipSecondLine = false;
            i = i - 1;
            if (i < 0) break;
            Subregion ne = getConstraints(i, e, P);
            // -----------------
            boolean triggered = false;
            for (Subregion s : Q)
            {
                if (ne.isWithin(s))
                {
                    skipSecondLine = true;
                    triggered = true;
                    break;
                }
            }
            if (triggered) continue;
            // -----------------
            ISpecimen x = getOpt();
            if (x == null)
            {
                Q.add(ne);
                skipSecondLine = true;
                continue;
            }
            if (true)
            {
                Q.add(ne);
                skipSecondLine = true;
                continue;
            }

            P.add(x);
            ne.e.get(1).right = x.getAlternative().getEvaluationAt(criteria.get(1));
            ne.e.get(2).right = x.getAlternative().getEvaluationAt(criteria.get(2));
            Q.add(ne);

        }

    }


    private static Subregion updateConstraints(double y[], Subregion e)
    {
        Subregion result = new Subregion();
        result.e = new ArrayList<Range>(3);

        for (int j = 1; j < 3; j++)
        {
            int i = 1;

        }

        return result;
    }


    private static Subregion getConstraints(int i, Subregion e, ArrayList<ISpecimen> P)
    {
        Subregion result = new Subregion();
        result.e = new ArrayList<Range>(3);
        result.e.add(null);
        for (int j = 1; j < 3; j++)
        {
            int d = i % (P.size() + 1);
            if ((i - d) % (P.size() + 1) != 0) System.out.println("ERR");
            i = (i - d) / (P.size() + 1);
            result.e.add(new Range(e.e.get(j).left, e.e.get(j).right));
        }
        return result;
    }

    private static ISpecimen getOpt()
    {
        return null;
    }

    public static class Subregion
    {
        public ArrayList<Range> e = null;

        public boolean isWithin(Subregion r)
        {
            for (int i = 0; i < r.e.size(); i++)
            {
                if (e.get(i).left <= r.e.get(i).left) return false;
                if (e.get(i).right >= r.e.get(i).right) return false;
            }
            return false;
        }
    }
}
