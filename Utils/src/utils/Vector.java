package utils;

/**
 * Created by Michał on 2014-10-19.
 *
 */
public class Vector
{
    @SuppressWarnings("unused")
    public static double getCosineDistance(double A[], double B[])
    {
        double d = getDotProduct(A, B);
        double lA = getLength(A);
        double lB = getLength(B);

        if (Double.compare(lA, 0.0d) == 0) return 1.0d;
        if (Double.compare(lB, 0.0d) == 0) return 1.0d;

        return d / (lA * lB);
    }

    public static double getDotProduct(double A[], double B[])
    {
        double result = 0.0d;
        for (int i = 0; i < A.length; i++)
            result += A[i] * B[i];
        return result;
    }

    public static double getLength(double V[])
    {
        double result = 0.0d;
        for (double aV : V) result += (aV * aV);
        return Math.sqrt(result);
    }
}
