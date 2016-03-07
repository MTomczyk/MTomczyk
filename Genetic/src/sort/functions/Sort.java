package sort.functions;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import sort.AggregationExtractor;
import sort.CriterionExtractor;
import tree.binary.BinaryTree;

import java.util.ArrayList;

/**
 * Created by Michał on 2014-11-10.
 *
 */
public class Sort
{
    public static void sortByAggregatedValue(ArrayList<ISpecimen> specimen)
    {
        BinaryTree<ISpecimen> gTree = new BinaryTree<>(new AggregationExtractor());

        specimen.forEach(gTree::insert);

        specimen.set(0, gTree.search());
        ISpecimen tmp;
        int pos = 0;
        while ((tmp = gTree.next()) != null)
        {
            pos++;
            specimen.set(pos, tmp);
        }
    }

    public static ArrayList<BinaryTree<ISpecimen>> sortedTrees(ArrayList<ISpecimen> specimen,
                                                               ArrayList<ICriterion> criteria)
    {
        ArrayList<BinaryTree<ISpecimen>> result = new ArrayList<>(criteria.size());

        for (ICriterion c: criteria)
        {
            BinaryTree<ISpecimen> t = new BinaryTree<ISpecimen>(new CriterionExtractor(c));
            result.add(t);
            specimen.forEach(t::insert);
        }
        return result;
    }

}
