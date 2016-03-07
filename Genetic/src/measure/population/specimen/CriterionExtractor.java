package measure.population.specimen;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import measure.population.specimen.interfaces.IFeatureExtractor;


// TODO TEST JAVADOC
/**
 * Created by Michał on 2015-02-14.
 *
 */
public class CriterionExtractor implements IFeatureExtractor
{
    private ICriterion _criterion = null;


    public CriterionExtractor(ICriterion criterion)
    {
        this._criterion = criterion;
    }

    @Override
    public String getKey()
    {
        return _criterion.getName();
    }

    @Override
    public Object getValue(ISpecimen s)
    {
        return s.getAlternative().getEvaluationAt(_criterion);
    }

}
