package select.interfaces;

import java.util.ArrayList;

import interfaces.ISpecimen;
import reproducer.interfaces.IParents;
import org.apache.commons.math3.random.MersenneTwister;

public interface ISelector
{
    ArrayList<IParents> select(ArrayList<ISpecimen> specimens, int number, MersenneTwister generator);
}
