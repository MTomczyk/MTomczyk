package measure.population.specimen;

import interfaces.ISpecimen;
import measure.population.specimen.interfaces.IFeatureExtractor;

// TODO JAVADOC TEST

/**
 * Created by Michał on 2015-02-14.
 *
 */
public class AggregatedExtractor implements IFeatureExtractor
{
    public AggregatedExtractor()
    {

    }

    @Override
    public String getKey()
    {
        return "Aggregated";
    }

    @Override
    public Object getValue(ISpecimen s)
    {
        return s.getAlternative().getAggregatedEvaluation();
    }

}
