package year.y2014.greenlogistics.exp_c_full.interactiveA;

import measure.population.interfaces.IStatisticExtractor;
import measure.population.specimen.interfaces.IFeatureExtractor;

/**
 * Created by MTomczyk on 24.08.2015.
 */
public class ChartInput
{
    public ChartInput(IFeatureExtractor fe, IStatisticExtractor se, IStatisticExtractor ise)
    {
        this.fe = fe;
        this.se = se;
        this.ise = ise;
    }

    public IFeatureExtractor fe = null;
    public IStatisticExtractor se = null;
    public IStatisticExtractor ise = null;
}
