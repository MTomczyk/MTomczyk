package normalization;

import normalization.interfaces.INormalization;
import standard.Common;

/**
 * Created by Michał on 2015-02-09.
 * This class performs min max normalization.
 */
public class MinMaxNormalization implements INormalization
{
    private double min = 0.0d;
    private double max = 1.0d;
    private double diff = 1.0d;

    /**
     * Simple constructor with default values:
     * Max = 1.0d;
     * Min = 0.0d;
     */
    public MinMaxNormalization()
    {
        this.setParams(0.0d, 1.0);
    }

    /**
     * Simple constructor.
     * @param min Min value.
     * @param max Max value.
     */
    public MinMaxNormalization(double min, double max)
    {
        this.setParams(min, max);
    }

    /**
     * Get normalized value.
     * @param value Input value.
     * @return Normalized value.
     */
    @Override
    public double getNormalized(double value)
    {
        return (value - min) / diff;
    }

    /**
     * Reset data.
     * Max = -INF
     * Min = INF
     */
    @Override
    public void reset()
    {
        this.max = Common.MIN_DOUBLE;
        this.min = Common.MAX_DOUBLE;
        this.diff = 1.0d;
    }

    /**
     * Correct Max, Min according to given value
     * @param value value
     */
    @Override
    public void update(double value)
    {
        if (value > this.max) this.max = value;
        if (value < this.min) this.min = value;
    }

    /**
     * Set class params. Here it simply calculates diff = max - min used in normalization.
     */
    @Override
    public void perform()
    {
        this.setParams(min, max);
    }

    /**
     * Set parameters.
     * @param min Min value.
     * @param max Max value.
     */
    public void setParams(double min, double max)
    {
        if (Double.compare(min, max) >= 0)
        {
            throw new IllegalArgumentException("Min >= Max");
        }

        this.max = max;
        this.min = min;
        this.diff = this.max - this.min;
    }
}
