package measure.population.comprehensive;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.maker.ordering.interfaces.IOrderingDM;
import interfaces.IGenetic;
import interfaces.ISpecimen;
import measure.population.interfaces.IPopulationComprehensive;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 11.09.2015.
 */
public class OrderingAverageDistanceUtility implements IPopulationComprehensive
{
    private IAlternative _dummyAlternative = null;
    IOrderingDM _dm = null;

    public OrderingAverageDistanceUtility(IOrderingDM dm, double reference[], ArrayList<ICriterion> criteria)
    {
        this._dm = dm;
        this._dummyAlternative = new Alternative("D", criteria);
        if ((reference != null) && (criteria != null))
            this._dummyAlternative.setEvaluationVector(reference, criteria);
    }

    @Override
    public Object getValue(IGenetic genetic, int generation, ArrayList<ISpecimen> specimen)
    {
        double U = 0.0d;

        for (ISpecimen aSpecimen : specimen)
            U += _dm.evaluate(aSpecimen.getAlternative());
        U /= (double) specimen.size();

        double uR = _dm.evaluate(_dummyAlternative);
        return (uR - U) / uR;
    }

    @Override
    public String getKey()
    {
        return "OrderingAverageDistanceUtility";
    }
}
