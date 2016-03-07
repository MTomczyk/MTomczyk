package linearprogramming;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import net.sf.javailp.*;
import standard.Common;

import java.util.ArrayList;

/**
 * Created by Micha³ on 2015-02-12.
 * <p/>
 * This class implements IOrdinalRegression lp a MDVF representative function.
 */

public class SumOfUtilitiesConstraints
{
    public static void addSumOfUtilitiesConstraint(Problem problem, ArrayList<IAlternative> alternatives,
                                                   ArrayList<ICriterion> criteria, String reference)
    {
        Linear linear = getSumOfUtilitiesLinear(problem, alternatives, criteria);
        linear.add(-1.0d, reference);
        problem.add(linear,"=", 0.0d);
    }

    public static Linear getSumOfUtilitiesLinear(Problem problem, ArrayList<IAlternative> alternatives,
                                                   ArrayList<ICriterion> criteria)
    {
        Linear linear = new Linear();
        ConstraintCreator.init();

        for (IAlternative a: alternatives)
        {
            for (int i = 0; i < criteria.size(); i++)
            {
                ICriterion c = criteria.get(i);
                double v = a.getEvaluationAt(c);
                String u = String.format("u%d(" + Common.doubleFormat + ")",i,v);
                ConstraintCreator.addDouble(u, 1.0d);
            }
        }
        ConstraintCreator.addVarsToLinear(linear);
        return linear;
    }
}