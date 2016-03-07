package extractor.alternative;

import alternative.interfaces.IAlternative;
import extractor.interfaces.IAlternativeExtractor;

/**
 * Created by Michał on 2015-03-20.
 *
 */
public class FromAlternative implements IAlternativeExtractor
{
    @Override
    public IAlternative getValue(Object o)
    {
        return (IAlternative) o;
    }
}
