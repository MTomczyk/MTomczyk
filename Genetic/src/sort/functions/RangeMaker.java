package sort.functions;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;

import java.util.ArrayList;

import standard.Common;
import standard.Range;

public class RangeMaker
{
    public static ArrayList<Range> getRange(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria)
    {
        int nCriteria = criteria.size();
        ArrayList<Range> range = new ArrayList<>(nCriteria);
        for (int j = 0; j < criteria.size(); j++)
            range.add(new Range(Common.MAX_DOUBLE, Common.MIN_DOUBLE));

        for (ISpecimen specimen : specimens)
        {
            for (int j = 0; j < criteria.size(); j++)
            {
                double v = specimen.getAlternative().getEvaluationAt(criteria.get(j));
                if (v < range.get(j).left) range.get(j).left = v;
                if (v > range.get(j).right) range.get(j).right = v;
            }
        }
        return range;
    }
}
