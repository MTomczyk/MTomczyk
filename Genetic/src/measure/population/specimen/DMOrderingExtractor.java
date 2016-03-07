package measure.population.specimen;

import decision.maker.ordering.interfaces.IOrderingDM;
import interfaces.ISpecimen;
import measure.population.specimen.interfaces.IFeatureExtractor;

// TODO JAVADOC TEST

/**
 * Created by Michał on 2015-02-14.
 *
 */
public class DMOrderingExtractor implements IFeatureExtractor
{
    public IOrderingDM _dm = null;
    public String _name = null;

    public DMOrderingExtractor(IOrderingDM dm, String name)
    {
        this._dm = dm;
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
        return _dm.evaluate(s.getAlternative());
    }

}
