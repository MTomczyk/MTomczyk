package measure.population.specimen;

import decision.maker.ordering.interfaces.IOrderingDM;
import interfaces.ISpecimen;
import measure.population.specimen.interfaces.IFeatureExtractor;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;

// TODO JAVADOC TEST

/**
 * Created by Michał on 2015-02-14.
 *
 */
public class MultiDMOrderingMinExtractor implements IFeatureExtractor
{
    public ArrayList<IOrderingDM> _dm = null;
    public double _weights[] = null;

    public MultiDMOrderingMinExtractor(ArrayList<IOrderingDM> dm)
    {
        this._dm = dm;
    }

    public MultiDMOrderingMinExtractor(ArrayList<IOrderingDM> dm, double weights[])
    {
        this._dm = dm;
        this._weights = weights;
    }

    @Override
    public String getKey()
    {
        return "MultiDMMin";
    }

    @Override
    public Object getValue(ISpecimen s)
    {
        double v[] = new double[_dm.size()];
        for (int i = 0; i < _dm.size(); i++)
        {
            v[i] = _dm.get(i).evaluate(s.getAlternative());
            if (_weights != null) v[i] *= _weights[i];
        }

        return StatUtils.min(v);
    }
}
