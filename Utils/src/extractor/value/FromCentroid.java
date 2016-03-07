package extractor.value;

import centroid.Centroid;
import extractor.interfaces.IValueExtractor;

/**
 * Created by MTomczyk on 15.02.2016.
 */
public class FromCentroid implements IValueExtractor
{
    private int _criterion = 0;

    public FromCentroid(int criterion)
    {
        _criterion = criterion;
    }

    @Override
    public Double getValue(Object o)
    {
        Centroid c = (Centroid) o;
        return c.data[_criterion];
    }
}
