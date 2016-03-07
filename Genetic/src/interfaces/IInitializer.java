package interfaces;


import java.util.ArrayList;

import criterion.interfaces.ICriterion;
import org.apache.commons.math3.random.MersenneTwister;

public interface IInitializer
{
	void createInitialPopulation(ArrayList<ICriterion> criteria, ArrayList<ISpecimen> specimens, int populationSize,
                                 MersenneTwister generator, Object problem);
}
