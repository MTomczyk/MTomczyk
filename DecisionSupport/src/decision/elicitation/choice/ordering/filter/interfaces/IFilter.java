package decision.elicitation.choice.ordering.filter.interfaces;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import extractor.interfaces.IAlternativeExtractor;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 08.02.2016.
 */
public interface IFilter
{
    boolean isAccepted(IAlternative alternative, ArrayList<ICriterion> criteria);
    boolean isAccepted(Object object, IAlternativeExtractor extractor, ArrayList<ICriterion> criteria);
}
