package extractor.value;

import extractor.interfaces.IValueExtractor;

/**
 * Created by Michał on 2014-10-21.
 *
 */
public class DoubleExtractor implements IValueExtractor
{
    @Override
    public Double getValue(Object o)
    {
        return (Double)o;
    }
}
