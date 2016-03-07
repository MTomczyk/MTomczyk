package interfaces;

import criterion.interfaces.ICriterion;

import java.util.ArrayList;

public interface IEvaluator
{
	void evaluate(ArrayList<ICriterion> criteria, ArrayList<ISpecimen> specimens, Object problem, boolean log);

	void evaluate(ArrayList<ICriterion> criteria, ISpecimen specimen, Object problem, boolean log);
}
