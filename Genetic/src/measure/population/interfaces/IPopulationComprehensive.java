package measure.population.interfaces;

import interfaces.IGenetic;
import interfaces.ISpecimen;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 10.09.2015.
 */
public interface IPopulationComprehensive
{
    Object getValue(IGenetic genetic, int generation, ArrayList<ISpecimen> specimen);
    String getKey();
}
