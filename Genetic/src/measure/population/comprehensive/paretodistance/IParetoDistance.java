package measure.population.comprehensive.paretodistance;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 11.02.2016.
 */
public interface IParetoDistance
{
    double getDistanceToPareto(ISpecimen solution);
}
