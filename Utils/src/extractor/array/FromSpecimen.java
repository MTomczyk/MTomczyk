package extractor.array;

import criterion.interfaces.ICriterion;
import extractor.interfaces.IArrayExtractor;
import interfaces.ISpecimen;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 15.02.2016.
 */
public class FromSpecimen implements IArrayExtractor
{
    private ArrayList<ICriterion> _criteria = null;

    public FromSpecimen(ArrayList<ICriterion> criteria)
    {
        this._criteria = criteria;
    }

    @Override
    public double[] getValue(Object o)
    {
        ISpecimen s = (ISpecimen) o;
        return s.getAlternative().getEvaluationVector(_criteria);
    }
}
