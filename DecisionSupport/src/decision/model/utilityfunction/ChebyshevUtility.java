package decision.model.utilityfunction;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.model.interfaces.IModel;
import extractor.interfaces.IAlternativeExtractor;
import utils.UtilityFunction;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-07-12.
 *
 */
public class ChebyshevUtility implements IModel
{
    private ArrayList<UtilityFunction> _utilityFunctions = null;
    private ArrayList<ICriterion> _criteria = null;

    public ChebyshevUtility(ArrayList<UtilityFunction> utilityFunctions, ArrayList<ICriterion> criteria)
    {
        this._utilityFunctions = utilityFunctions;
        this._criteria = criteria;
    }

    @Override
    public double rateCandidate(IAlternative alternative)
    {
        return UtilityFunction.getChebyshevUtility(alternative, _utilityFunctions, _criteria);
    }

    @Override
    public double[] rateCandidate(ArrayList<IAlternative> alternatives)
    {
        double results[] = new double[alternatives.size()];
        for (int i = 0; i < alternatives.size(); i++)
        {
            results[i] = UtilityFunction.getChebyshevUtility(alternatives.get(i), _utilityFunctions, _criteria);
        }
        return results;
    }

    @Override
    @SuppressWarnings("all")
    public double[] rateCandidate(Object arrayOfObjects, IAlternativeExtractor extractor)
    {
        ArrayList<Object> alternatives = (ArrayList<Object>) arrayOfObjects;
        double results[] = new double[alternatives.size()];

        for (int i = 0; i < alternatives.size(); i++)
        {
            results[i] = UtilityFunction.getChebyshevUtility(extractor.getValue(alternatives.get(i)), _utilityFunctions,
                    _criteria);
        }
        return results;
    }

    @Override
    public boolean hasModel()
    {
        return _utilityFunctions != null;
    }

    @Override
    @SuppressWarnings("all")
    public void setModel(Object model)
    {
        ArrayList<UtilityFunction> utilityFunctions = (ArrayList<UtilityFunction>) model;
        this._utilityFunctions = utilityFunctions;
    }

    @Override
    public Object getModel()
    {
        return _utilityFunctions;
    }
}