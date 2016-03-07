package reproducer;

import reproducer.boundcorrect.Absorb;
import reproducer.boundcorrect.interfaces.IBoundCorrect;
import org.apache.commons.math3.random.MersenneTwister;
import standard.Range;

/**
 * Created by Michał on 2015-02-16.
 *
 * Method for standard gaussian mutation.
 */
public class StandardGaussianMutation
{
    /**
     * Mutate given value.
     * @param value Given value.
     * @param range Available range of new value. If new value is not legal, then use absorb method.
     * @param standDev Stand. Dev of mutation.
     * @param generator Random number generator.
     * @return Result
     */
    public static double mutation(double value, Range range, double standDev, MersenneTwister generator)
    {
        return mutation(value, range, standDev, generator, new Absorb());
    }

    /**
     * Mutate given value.
     * @param value Given value.
     * @param range Available range of new value.
     * @param standDev Stand. Dev of mutation.
     * @param generator Random number generator.
     * @param boundCorrect method of value correction.
     * @return Result
     */
    public static double mutation(double value, Range range, double standDev, MersenneTwister generator,
                                  IBoundCorrect boundCorrect)
    {
        double add = generator.nextGaussian() * standDev;
        double result = value + add;

        if (!range.isInRange(result)) result = boundCorrect.correct(range, result);

        return result;
    }
}
