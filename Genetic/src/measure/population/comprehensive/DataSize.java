package measure.population.comprehensive;

import interfaces.IGenetic;
import interfaces.ISpecimen;
import measure.population.interfaces.IPopulationComprehensive;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 10.09.2015.
 */
public class DataSize implements IPopulationComprehensive
{
    @Override
    public Object getValue(IGenetic genetic, int generation, ArrayList<ISpecimen> specimen)
    {
        if (specimen == null) return 0.0d;
        return (double) specimen.size();
    }

    @Override
    public String getKey()
    {
        return "DataSize";
    }
}
