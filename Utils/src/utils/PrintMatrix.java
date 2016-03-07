package utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Michał on 2014-10-12.
 *
 */
public class PrintMatrix
{
    @SuppressWarnings("unused")
    public static void printVector2D(int v[])
    {
        for (int i = 0; i < v.length; i++)
        {
            if (i != v.length - 1) System.out.print(v[i] + " ");
            else System.out.print(v[i] + "\n");
        }
    }

    @SuppressWarnings("unused")
    public static void printVector2D(double v[])
    {
        for (double aV : v)
        {
            System.out.print(aV + "\n");
        }
    }

    @SuppressWarnings("unused")
    public static void printMatrix2D(double m[][])
    {
        NumberFormat formatter = new DecimalFormat("0.##E0");

        for (double[] aM : m)
        {
            for (double anAM : aM)
            {
                System.out.printf("%s ", formatter.format(anAM));
            }
            System.out.printf("\n");
        }
    }

    @SuppressWarnings("unused")
    public static void printMatrix2D(boolean m[][])
    {
        for (boolean[] aM : m)
        {
            for (int j = 0; j < m.length; j++)
            {
                if (aM[j]) System.out.printf("T ");
                else System.out.printf("F ");
            }
            System.out.printf("\n");
        }
    }

    @SuppressWarnings("unused")
    public static void printMatrix2D(int m[][])
    {
        for (int[] aM : m)
        {
            for (int j = 0; j < m.length; j++)
            {
                System.out.printf("%d ", aM[j]);
            }
            System.out.printf("\n");
        }
    }

}
