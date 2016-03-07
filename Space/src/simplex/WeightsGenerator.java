package simplex;

import org.apache.commons.math3.random.MersenneTwister;
import utils.InsertionSortDouble;

/**
 * Created by Michał on 2015-02-11.
 * Generates vector of weights (normalized and sum to 1.0) with uniform distribution.
 */
public class WeightsGenerator
{
    /**
     *  Generates vector of weights (normalized and sum to 1.0) with uniform distribution.
     * @param n number of weights.
     * @param g Random number generator.
     * @return Vector of weights.
     */
    public static double[] getUniformWeights(int n, MersenneTwister g)
    {
        double w[] = new double[n];

        InsertionSortDouble.init(n + 1);
        InsertionSortDouble.step(0.0d);
        for (int i = 0; i < n - 1; i++)
        {
            double v = g.nextDouble();
            InsertionSortDouble.step(v);
        }
        InsertionSortDouble.step(1.0d);

        double tmp[] = InsertionSortDouble.data;

        for (int i = 1; i < tmp.length; i++)
            w[i - 1] = tmp[i] - tmp[i - 1];

        return w;
    }
}
