package sort.interfaces;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;

import java.util.ArrayList;

public interface ISorter
{
    ILog sort(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, int generation);
    ArrayList<ISpecimen> getPareto();
    ArrayList<ISpecimen> getReproductionPool();
}
