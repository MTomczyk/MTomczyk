package reproducer.interfaces;

import java.util.ArrayList;

import interfaces.ISpecimen;
import org.apache.commons.math3.random.MersenneTwister;

public interface IReproducer
{
    ArrayList<ISpecimen> reproduce(ArrayList<IParents> parents, Object problem, int generation,
                                   MersenneTwister generator);



}
