package sort.functions;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import normalization.MinMaxNormalization;
import normalization.interfaces.INormalization;
import standard.Range;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by MTomczyk on 16.02.2016.
 */
public class Normalization
{
    public static ArrayList<INormalization> getNormalizationsFromEvaluations(ArrayList<ISpecimen> specimens,
                                                              ArrayList<ICriterion> criteria)
    {
        ArrayList<INormalization> _normalizations = new ArrayList<>(criteria.size());
        ArrayList <Range> range = RangeMaker.getRange(specimens, criteria);
        _normalizations = new ArrayList<>(criteria.size());
        _normalizations.addAll(range.stream().map(r -> new MinMaxNormalization(r.left, r.right)).collect(Collectors.toList()));
        return _normalizations;
    }
}
