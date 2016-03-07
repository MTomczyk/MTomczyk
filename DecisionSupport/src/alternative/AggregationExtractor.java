package alternative;

import alternative.interfaces.IAlternative;
import extractor.interfaces.IValueExtractor;

/**
 *  This class extracts aggregated value (evaluation) from IAlternative object.
 *  Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *
 */
public class AggregationExtractor implements IValueExtractor
{
    /**
     * Extract aggregated value from IAlternative.
     *
     * @param o here it needs to be an {@See IAlternative} object.
     * @return Extracted value.
     */
    @Override
    public Double getValue(Object o)
    {
        return ((IAlternative) o).getAggregatedEvaluation();
    }
}
