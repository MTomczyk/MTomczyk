package sort;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import extractor.interfaces.IValueExtractor;

public class CriterionExtractor implements IValueExtractor
{
	ICriterion criterion = null;

	public CriterionExtractor(ICriterion criterion)
	{
		this.criterion = criterion;
	}

	@Override
	public Double getValue(Object o)
	{
		ISpecimen s = (ISpecimen) o;
		return s.getAlternative().getEvaluationAt(criterion);
	}
}
