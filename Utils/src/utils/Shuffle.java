package utils;

import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;

public class Shuffle<T>
{
    private MersenneTwister mt = new MersenneTwister(System.currentTimeMillis());

    public void shuffle(ArrayList<T> input)
    {
        for (int i = input.size() - 1; i >= 0; i--)
        {
            int p = this.mt.nextInt(i + 1);
            T tmp = input.get(p);
            for (int j = p; j < i; j++)
                input.set(j, input.get(j + 1));
            input.set(i, tmp);
        }
    }

    public void shuffle(T input[])
    {
        for (int i = input.length - 1; i >= 0; i--)
        {
            int p = this.mt.nextInt(i + 1);
            T tmp = input[p];
            System.arraycopy(input, p + 1, input, p, i - p);
            input[i] = tmp;
        }
    }

    @SuppressWarnings("unused")
    public void shuffle(int input[])
    {
        for (int i = input.length - 1; i >= 0; i--)
        {
            int p = this.mt.nextInt(i + 1);
            int tmp = input[p];
            System.arraycopy(input, p + 1, input, p, i - p);
            input[i] = tmp;
        }
    }
}
