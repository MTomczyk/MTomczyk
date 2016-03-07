package measure.population.comprehensive;

import criterion.interfaces.ICriterion;
import decision.maker.ordering.interfaces.IOrderingDM;
import distance.interfaces.IDistance;
import interfaces.IGenetic;
import interfaces.ISpecimen;
import measure.population.interfaces.IPopulationComprehensive;
import standard.Point;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 11.09.2015.
 */
public class OrderingBestDistanceObjective implements IPopulationComprehensive
{
    private ArrayList<ICriterion> _criteria;
    private IDistance _distance = null;
    private double _reference[] = null;
    IOrderingDM _dm = null;

    public OrderingBestDistanceObjective(IOrderingDM dm, IDistance distance, double reference[], ArrayList<ICriterion> criteria)
    {
        this._dm = dm;
        this._distance = distance;
        this._criteria = criteria;
        this._reference = reference;
    }

    @Override
    public Object getValue(IGenetic genetic, int generation, ArrayList<ISpecimen> specimen)
    {
        double max = -1.0d;
        double bestObjective[] = null;

        for (ISpecimen aSpecimen : specimen)
        {
            double v = _dm.evaluate(aSpecimen.getAlternative());
            if (v > max)
            {
                max = v;
                bestObjective = aSpecimen.getAlternative().getEvaluationVector(_criteria);
            }
        }

        return _distance.getDistance(new Point(_reference), new Point(bestObjective));
    }

    @Override
    public String getKey()
    {
        return "OrderingBestDistanceObjective";
    }
}
