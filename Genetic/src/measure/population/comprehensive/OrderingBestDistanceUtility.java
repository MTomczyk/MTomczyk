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
public class OrderingBestDistanceUtility implements IPopulationComprehensive
{
    private IAlternative _dummyAlternative = null;
    IOrderingDM _dm = null;

    public OrderingBestDistanceUtility(IOrderingDM dm, double reference[], ArrayList<ICriterion> criteria)
    {
        this._dm = dm;
        this._dummyAlternative = new Alternative("D", criteria);
        if ((reference != null) && (criteria != null))
            this._dummyAlternative.setEvaluationVector(reference, criteria);
    }

    @Override
    public Object getValue(IGenetic genetic, int generation, ArrayList<ISpecimen> specimen)
    {
        double max = -1.0d;
        IAlternative bestAlternative = null;

        for (ISpecimen aSpecimen : specimen)
        {
            double v = _dm.evaluate(aSpecimen.getAlternative());
            if (v > max)
            {
                max = v;
                bestAlternative = aSpecimen.getAlternative();
            }
        }

        double uR = _dm.evaluate(_dummyAlternative);
        double uB = _dm.evaluate(bestAlternative);
        return (uR - uB) / uR;
    }

    @Override
    public String getKey()
    {
        return "OrderingBestDistanceUtility";
    }
}
