package normalization;

import normalization.interfaces.INormalization;

import java.util.LinkedList;

/**
 * Created by Michał on 2015-02-09.
 * This class performs z-score normalization.
 */
public class ZScoreNormalization implements INormalization
{
    private double mean = 0.0d;
    private double standDev = 0.0d;

    private LinkedList<Double> dataSet = null;

    /**
     * Simple constructor.
     * @param mean Mean value.
     * @param standDev StandDev value.
     */
    public ZScoreNormalization(double mean, double standDev)
    {
        this.setParams(mean, standDev);
    }

    /**
     * Get normalized value.
     * @param value Input value.
     * @return Normalized value.
     */
    @Override
    public double getNormalized(double value)
    {
        return (value - mean) / standDev;
    }


    /**
     * Reset data.
     * Mean = 0.0
     * StandDev = 0.0
     */
    @Override
    public void reset()
    {
        dataSet = new LinkedList<>();
        mean = 0.0d;
        standDev = 0.0d;
    }

    /**
     * Add input value to data set.
     * @param value input value.
     */
    @Override
    public void update(double value)
    {
        dataSet.add(value);
        mean += value;
    }

    /**
     * Calculate Mean and StandDev from dataset.
     */
    @Override
    public void perform()
    {
        mean /= dataSet.size();
        for (Double d: dataSet)
            standDev += Math.pow(d - mean, 2.0d);
        standDev /= dataSet.size();
        standDev = Math.sqrt(standDev);

        this.setParams(mean, standDev);
    }

    /**
     * Set parameters.
     * @param mean Mean value.
     * @param standDev StandDev value.
     */
    public void setParams(double mean, double standDev)
    {
        if (Double.compare(standDev, 0.0d) == 0)
        {
            throw new IllegalArgumentException("StandDev = 0.0");
        }
        this.mean = mean;
        this.standDev = standDev;
    }

}
