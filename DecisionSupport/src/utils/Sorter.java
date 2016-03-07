package utils;

import alternative.AggregationExtractor;
import alternative.CriterionExtractor;
import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import tree.binary.BinaryTree;

import java.util.ArrayList;


public class Sorter
{
    /**
     * It sorts alternatives values on each criterion separately using binary tree.
     *
     * @param alternatives Array of alternatives.
     * @param criteria     Array of criteria.
     * @return Array of binary trees which all contain sorted alternatives. Each tree for each criterion separately.
     */
    public static ArrayList<BinaryTree<IAlternative>> getSortedByCriterion(ArrayList<IAlternative> alternatives,
                                                                           ArrayList<ICriterion> criteria)
    {
        return getSortedByCriterion(alternatives, null, criteria);
    }

    /**
     * It sorts alternatives values on each criterion separately using binary tree.
     *
     * @param alternatives           Array of alternatives.
     * @param additionalAlternatives Additional alternatives pool (alternative + additional set will be considered).
     * @param criteria               Array of criteria.
     * @return Array of binary trees which all contain sorted alternatives. Each tree for each criterion separately.
     */
    public static ArrayList<BinaryTree<IAlternative>> getSortedByCriterion(ArrayList<IAlternative> alternatives,
                                                                           ArrayList<IAlternative> additionalAlternatives,
                                                                           ArrayList<ICriterion> criteria)
    {
        ArrayList<BinaryTree<IAlternative>> tree = new ArrayList<BinaryTree<IAlternative>>(criteria.size());
        for (ICriterion aCriterion : criteria)
        {
            BinaryTree<IAlternative> tmp = new BinaryTree<IAlternative>(new CriterionExtractor(aCriterion));
            if (aCriterion.isGain()) tmp.setDirection(false);
            tree.add(tmp);
        }

        for (int i = 0; i < criteria.size(); i++)
        {
            tree.get(i).clear();

            for (IAlternative a : alternatives)
            {
                tree.get(i).insert(a);
            }

            if (additionalAlternatives != null)
            {
                for (IAlternative a : additionalAlternatives)
                {
                    tree.get(i).insert(a);
                }
            }
        }

        return tree;
    }

    /**
     * It sorts alternatives basing on aggregated evaluation values. Input is not sorted!
     *
     * @param alternatives Array of alternatives.
     * @return Binary Tree of IAlternative
     */
    public static BinaryTree<IAlternative> getSortedByAggregatedEvaluation(ArrayList<IAlternative> alternatives)
    {
        BinaryTree<IAlternative> tree = new BinaryTree<IAlternative>(new AggregationExtractor());
        for (IAlternative anAlternative : alternatives) tree.insert(anAlternative);
        return tree;
    }

    /**
     * It sorts alternatives basing on aggregated evaluation values. Input is sorted.
     *
     * @param alternatives Array of IAlternative
     */
    public static void sortByAggregatedEvaluation(ArrayList<IAlternative> alternatives)
    {
        BinaryTree<IAlternative> tree = getSortedByAggregatedEvaluation(alternatives);

        // SORT ----------------
        IAlternative tmp = tree.search();
        alternatives.set(0, tmp);

        int pos = 0;
        while ((tmp = tree.next()) != null)
        {
            pos++;
            alternatives.set(pos, tmp);
        }
    }
}
