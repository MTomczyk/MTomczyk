package extractor.alternative;

import alternative.interfaces.IAlternative;
import extractor.interfaces.IAlternativeExtractor;
import interfaces.ISpecimen;

/**
 * Created by Michał on 2015-03-20.
 *
 */
public class FromSpecimen implements IAlternativeExtractor
{
    @Override
    public IAlternative getValue(Object o)
    {
        return ((ISpecimen) o).getAlternative();
    }
}
