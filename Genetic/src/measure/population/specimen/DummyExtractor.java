package measure.population.specimen;

import interfaces.ISpecimen;
import measure.population.specimen.interfaces.IFeatureExtractor;

/**
 * Created by Michał on 2015-02-23.
 *
 */
public class DummyExtractor implements IFeatureExtractor
{
    private String _name = "NaN";

    public DummyExtractor(String name)
    {
        this._name = name;
    }

    @Override
    public String getKey()
    {
        return _name;
    }

    @Override
    public Object getValue(ISpecimen s)
    {
        return null;
    }
}
