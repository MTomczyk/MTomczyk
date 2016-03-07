package measure.population.comprehensive;

import interfaces.IGenetic;
import interfaces.ISpecimen;
import measure.population.interfaces.IPopulationComprehensive;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 10.09.2015.
 */
public class ElapsedTime implements IPopulationComprehensive
{

    @Override
    public Object getValue(IGenetic genetic, int generation, ArrayList<ISpecimen> specimen)
    {
        return genetic.getElapsedTime();
    }

    @Override
    public String getKey()
    {
        return "ElapsedTime";
    }
}
