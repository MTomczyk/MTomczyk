package sort.functions;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import standard.Range;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 13.11.2015.
 */
public class Box
{

    public static double[] getBox(IAlternative alternative,
                                  ArrayList<ICriterion> criteria,
                                  ArrayList<Range> ranges, double t[])
    {
        double evaluation[] = alternative.getEvaluationVector(criteria);
        double result[] = new double[evaluation.length];
        for (int i = 0; i < result.length; i++)
        {
            Range r = ranges.get(i);
            boolean gain = criteria.get(i).isGain();

            double e = evaluation[i];
            e = (e - r.left) / (r.right - r.left);

            //if (e < 0.0d) System.out.println("BELOW!!!");
            //if (e > 1.0d) System.out.println("UPPER!!!" + " " + e + " " + i + " " + evaluation[i]);


            if (!gain) e = (1.0 - e) + 1.0d;
            else e += 1.0d;

            //double e = evaluation[i];
            //if (!gain) e = (r.right - e) + 1.0d;
            //else e += (1.0d - r.left);

            double v = Math.log(e) / Math.log(1.0d + t[i]);
            v = Math.floor(v);
            result[i] = v;
        }

        for (int i = 0; i < result.length; i++)
            if (!criteria.get(i).isGain())
                result[i] *= -1.0d;

        return result;
    }




    public static double[] getBox(IAlternative alternative, ArrayList<ICriterion> criteria, double t[])
    {
        double evaluation[] = alternative.getEvaluationVector(criteria);
        double result[] = new double[evaluation.length];
        for (int i = 0; i < result.length; i++)
        {
            Range r = criteria.get(i).getRange().get("tp_space");
            boolean gain = criteria.get(i).isGain();

            double e = evaluation[i];
            e = (e - r.left) / (r.right - r.left);

            //if (e < 0.0d) System.out.println("BELOW!!!");
            //if (e > 1.0d) System.out.println("UPPER!!!" + " " + e + " " + i + " " + evaluation[i]);


            if (!gain) e = (1.0 - e) + 1.0d;
            else e += 1.0d;

            //double e = evaluation[i];
            //if (!gain) e = (r.right - e) + 1.0d;
            //else e += (1.0d - r.left);

            double v = Math.log(e) / Math.log(1.0d + t[i]);
            v = Math.floor(v);
            result[i] = v;
        }

        for (int i = 0; i < result.length; i++)
            if (!criteria.get(i).isGain())
                result[i] *= -1.0d;

        return result;
    }

    public static IAlternative getDummyAlternativeBox(IAlternative alternative,
                                                      ArrayList<ICriterion> criteria,
                                                      ArrayList<Range> ranges,
                                                      double t[])
    {
        double e[] = getBox(alternative, criteria, ranges, t);
        IAlternative dAlternative = new Alternative(alternative.getName() + "_DUMMY", criteria);
        dAlternative.setEvaluationVector(e, criteria);
        return dAlternative;
    }

    public static IAlternative getDummyAlternativeBox(IAlternative alternative, ArrayList<ICriterion> criteria, double t[])
    {
        double e[] = getBox(alternative, criteria, t);
        IAlternative dAlternative = new Alternative(alternative.getName() + "_DUMMY", criteria);
        dAlternative.setEvaluationVector(e, criteria);
        return dAlternative;
    }

}
