package sort.common;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.filter.interfaces.IFilter;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 08.02.2016.
 */
public class Common
{
    @SuppressWarnings("Convert2streamapi")
    public static ArrayList<IAlternative> applyChoiceFilter(IFilter filter, ArrayList<IAlternative> alternatives,
                                                            ArrayList<ICriterion> criteria)
    {
        if (filter == null) return alternatives;
        ArrayList<IAlternative> tmp = new ArrayList<>();
        for (IAlternative a: alternatives)
                if (filter.isAccepted(a, criteria))
                    tmp.add(a);
        return tmp;
    }
}
