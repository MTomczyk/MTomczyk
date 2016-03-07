package utils;

/**
 * Created by Michał on 2015-02-11.
 * Simple Insertion Sort ASC Order
 */

// TODO WRITE TEST

public class InsertionSortInteger
{
    public static int data[] = null;
    public static int pointer = -1;

    public static void init(int n)
    {
        data = new int[n];
        pointer = -1;
    }

    public static void step(int value)
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
