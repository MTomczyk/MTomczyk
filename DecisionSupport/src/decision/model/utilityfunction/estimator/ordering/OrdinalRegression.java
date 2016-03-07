package decision.model.utilityfunction.estimator.ordering;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.maker.ordering.Order;
import decision.model.utilityfunction.estimator.ordering.interfaces.IModelOrderEstimator;
import linearprogramming.or.interfaces.IOrdinalRegression;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Michał on 2015-07-13.
 *
 */
public class OrdinalRegression implements IModelOrderEstimator
{
    private ArrayList<ICriterion> _criteria = null;
    private IOrdinalRegression _ordinalRegression = null;

    public OrdinalRegression(IOrdinalRegression ordinalRegression, ArrayList<ICriterion> criteria)
    {
        this._ordinalRegression = ordinalRegression;
        this._criteria = criteria;
    }

    @Override
    public Object getEstimatedModel(ArrayList<IAlternative> alternatives, LinkedList<Order> history)
    {
        return _ordinalRegression.getUtility(alternatives, _criteria, history);
    }
}
