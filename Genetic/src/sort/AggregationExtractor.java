package sort;

import interfaces.ISpecimen;
import extractor.interfaces.IValueExtractor;

public class AggregationExtractor implements IValueExtractor
{
	@Override
	public Double getValue(Object o)
	{
		ISpecimen s = (ISpecimen) o;
		return s.getAlternative().getAggregatedEvaluation();
	}

}
