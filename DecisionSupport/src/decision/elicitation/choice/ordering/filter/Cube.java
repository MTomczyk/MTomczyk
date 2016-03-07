package decision.elicitation.choice.ordering.filter;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.filter.interfaces.IFilter;
import extractor.interfaces.IAlternativeExtractor;
import standard.Range;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 08.02.2016.
 */
public class Cube implements IFilter
{
    private ArrayList<Range> _range = null;

    public Cube(ArrayList<Range> ranges)
    {
        this._range = ranges;
    }

    @Override
    public boolean isAccepted(IAlternative alternative, ArrayList<ICriterion> criteria)
    {
        for (int i = 0; i < criteria.size(); i++)
        {
            Range r = _range.get(i);
            if (!r.isInRange(alternative.getEvaluationAt(criteria.get(i)))) return false;
        }
        return true;
    }

    @Override
    public boolean isAccepted(Object object, IAlternativeExtractor extractor, ArrayList<ICriterion> criteria)
    {
        IAlternative a = extractor.getValue(object);
        for (int i = 0; i < criteria.size(); i++)
        {
            Range r = _range.get(i);
            if (!r.isInRange(a.getEvaluationAt(criteria.get(i)))) return false;
        }
        return true;
    }
}
