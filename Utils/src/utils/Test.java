package utils;

import java.util.ArrayList;

public class Test
{

    public static void main(String[] args)
    {
        Shuffle<Integer> s = new Shuffle<>();

        int size = 25;
        int bucket[][] = new int[size][size];
        int trials = 1000000;

        long startTime = System.nanoTime();

        for (int i = 0; i < trials; i++)
        {
            ArrayList<Integer> a = new ArrayList<>(size);
            for (int j = 0; j < size; j++)
                a.add(j);
            s.shuffle(a);

            for (int j = 0; j < size; j++)
                bucket[j][a.get(j)]++;
        }

        for (int i = 0; i < size; i++)
        {
            System.out.printf("%d : ", i);
            for (int j = 0; j < size; j++)
                System.out.printf("%.4f ", (double) bucket[i][j] / (double) trials);
            System.out.printf("\n");
        }

        long endTime = System.nanoTime();
        System.out.println("Took " + (endTime - startTime) / 1000000 + " ms");
    }
}
