package linearprogramming;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import utils.UtilityFunction;
import tree.binary.BinaryTree;
import net.sf.javailp.Result;
import standard.Common;
import standard.Point;

import java.util.ArrayList;

/**
 * Created by Michał on 2014-10-11.
 * <p/>
 * This class makes Array of UtilityFunctions based on results taken from LP methods (UTA, etc).
 */
public class UtilityFunctionCreator
{
    /**
     * Makes Array of UtilityFunctions based on results taken from LP methods (UTA, etc).
     *
     * @param result   Result object (LP).
     * @param trees    Binary trees (sorted according to evaluation on each criterion separately). The same tree witch is used in f.i. UTA MDVF,
     *                 or BaseConstraints (monotonic constraints).
     * @param criteria Array of criteria.
     * @return Array of created UtilityFunctions.
     */
    public static ArrayList<UtilityFunction> getUtilityFunction(Result result, ArrayList<BinaryTree<IAlternative>> trees,
                                                                ArrayList<ICriterion> criteria)
    {
        ArrayList<UtilityFunction> uF = new ArrayList<UtilityFunction>(criteria.size());

        for (int i = 0; i < criteria.size(); i++)
        {
            UtilityFunction f = new UtilityFunction(trees.get(i).getSize());

            IAlternative A = trees.get(i).search();
            double pX = A.getEvaluationAt(criteria.get(i));
            Number v = result.get(String.format("u%d(" + Common.doubleFormat + ")", i, pX));
            if (v != null)
            {
                f.add(new Point(A.getEvaluationAt(criteria.get(i)), v.doubleValue()));
            }

            while ((A = trees.get(i).next()) != null)
            {
                double x = A.getEvaluationAt(criteria.get(i));
                v = result.get(String.format("u%d(" + Common.doubleFormat + ")", i, x));
                if (v == null) continue;
                if (Double.compare(x, pX) == 0) continue;
                f.add(new Point(A.getEvaluationAt(criteria.get(i)), v.doubleValue()));
                pX = x;
            }
            uF.add(f);
        }
        return uF;
    }


}
