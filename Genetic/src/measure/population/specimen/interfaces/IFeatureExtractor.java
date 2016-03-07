package measure.population.specimen.interfaces;

import interfaces.ISpecimen;

/**
 * Created by Michał on 2015-02-14.
 *
 */
public interface IFeatureExtractor
{
    String getKey();
    Object getValue(ISpecimen s);
}
