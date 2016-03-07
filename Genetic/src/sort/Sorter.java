package sort;

import java.util.ArrayList;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import sort.functions.Sort;
import sort.interfaces.ILog;
import sort.interfaces.ISorter;

public class Sorter implements ISorter
{
    ArrayList<ISpecimen> _reproductionPool = null;
    ArrayList<ISpecimen> _pareto = null;

    @Override
    public ILog sort(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, int generation)
    {
        Sort.sortByAggregatedValue(specimens);

        _reproductionPool = specimens;
        _pareto = new ArrayList<>(1);
        _pareto.add(specimens.get(0));

        return null;
    }

    @Override
    public ArrayList<ISpecimen> getPareto()
    {
        return _pareto;
    }

    @Override
    public ArrayList<ISpecimen> getReproductionPool()
    {
        return _reproductionPool;
    }

}
