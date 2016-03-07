package utils;

/**
 * Created by Michał on 2015-02-11.
 * Simple Insertion Sort ASC Order
 */
public class InsertionSortDouble
{
    public static double data[] = null;
    public static int pointer = -1;

    public static void init(int n)
    {
        data = new double[n];
        pointer = -1;
    }

    public static void step(double value)
    {
        if (pointer >= data.length - 1)
        {
            if (value < data[pointer])
            {
                data[pointer] = value;
            }
            else return;
        }
        else
        {
            pointer++;
            data[pointer] = value;
        }

        if (pointer == 0) return;

        for (int i = pointer; i > 0; i--)
        {
            if (data[i - 1] > data[i])
            {
                data[i] = data[i - 1];
                data[i - 1] = value;
            }
            else break;
        }
    }
}
