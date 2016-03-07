package utils;

/**
 * Created by MTomczyk on 09.11.2015.
 */
public class LPBox
{

    public double lower[];
    public double upper[];

    public LPBox(int dimensions)
    {
        lower = new double[dimensions];
        upper = new double[dimensions];
        for (int i = 0; i < dimensions; i++)
        {
            lower[i] = 0.0d;
            upper[i] = 1.0d;
        }
    }

    public boolean isInside(double p[])
    {
        for (int i = 0; i < lower.length; i++)
            if ((p[i] < lower[i]) || (p[i] > upper[i])) return false;
        return true;
    }

    public static int UPPER = 0;
    public static int LOWER = 1;

    public static int GREATER = 0;
    public static int SMALLER = 1;

    public static int STRICT = 0;
    public static int WEAK = 1;
    public static int MIXED = 2;

    public boolean validate()
    {
        for (int i = 0; i < lower.length; i++)
        {
            if (lower[i] > upper[i]) return false;
        }
        return true;
    }

    public LPBox getClone()
    {
        LPBox box = new LPBox(lower.length);
        box.lower = lower.clone();
        box.upper = upper.clone();
        return box;
    }

    public void print()
    {
        for (int i = 0; i < lower.length; i++)
        {
            System.out.println(lower[i] + " " + upper[i]);
        }
    }

    public boolean getComparison(double p[], int boundary, int direction,int relation, double epsilon)
    {
        int strict = 0;
        int weak = 0;

        for (int i = 0; i < lower.length; i++)
        {
            if (boundary == UPPER)
            {
                if (direction == GREATER)
                {
                    if (p[i] + epsilon > upper[i]) strict++;
                    if (p[i] + epsilon >= upper[i]) weak++;
                }
                else if (direction == SMALLER)
                {
                    if (p[i] - epsilon < upper[i]) strict++;
                    if  (p[i] - epsilon <= upper[i]) weak++;
                }
            }
            else if (boundary == LOWER)
            {
                if (direction == GREATER)
                {
                    if (p[i] + epsilon > lower[i]) strict++;
                    if (p[i] + epsilon >= lower[i]) weak++;
                }
                else if (direction == SMALLER)
                {
                    if (p[i] - epsilon < lower[i]) strict++;
                    if (p[i] - epsilon <= lower[i]) weak++;
                }
            }
        }

        //System.out.println(strict + " " + weak);
        if (weak < lower.length) return false;
        if ((weak == lower.length) && (relation == WEAK)) return true;
        if ((strict == lower.length) && (relation == STRICT)) return true;
        //noinspection RedundantIfStatement
        if ((strict > 0) && (relation == MIXED)) return true;

        return false;
    }


}
