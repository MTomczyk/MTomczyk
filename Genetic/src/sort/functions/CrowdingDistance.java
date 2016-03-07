package sort.functions;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;

import java.util.ArrayList;
import java.util.LinkedList;

import normalization.MinMaxNormalization;
import normalization.interfaces.INormalization;
import org.apache.commons.math3.stat.StatUtils;
import shared.SC;
import standard.Range;
import tree.binary.BinaryTree;

public class CrowdingDistance
{
    public static double[] getDistanceFrontWithNormalization(
            LinkedList<LinkedList<Integer>> front,
            ArrayList<ISpecimen> specimens,
            ArrayList<ICriterion> criteria)
    {
        ArrayList<Range> r = RangeMaker.getRange(specimens, criteria);
        ArrayList<INormalization> normalizations = new ArrayList<>();
        for (int i = 0; i < criteria.size(); i++)
            normalizations.add(new MinMaxNormalization(r.get(i).left, r.get(i).right));
        double crowding[] = CrowdingDistance.getDistanceFront(front, specimens, normalizations, criteria);
        double max = StatUtils.max(crowding);
        for (int i = 0; i < crowding.length; i++)
            crowding[i] /= max;
        return crowding;
    }


    public static double[] getDistanceFront(LinkedList<LinkedList<Integer>> front,
                                            ArrayList<BinaryTree<ISpecimen>> trees,
                                            ArrayList<ISpecimen> specimens,
                                            ArrayList<INormalization> normalizations,
                                            ArrayList<ICriterion> criteria)
    {
        double result[] = new double[specimens.size()];
        int nCriteria = criteria.size();

        for (LinkedList<Integer> l : front)
        {
            // Prepare Trees
            for (int i = 0; i < nCriteria; i++)
            {
                trees.get(i).clear();
                for (Integer s : l)
                    trees.get(i).insert(specimens.get(s));
            }

            for (Integer s : l)
            {
                double crowding = 0.0d;

                // for each criterion
                for (int i = 0; i < nCriteria; i++)
                {
                    trees.get(i).setSearch(specimens.get(s));
                    ISpecimen previous = trees.get(i).previous();
                    if (previous == null)
                    {
                        crowding = nCriteria;
                        break;
                    }
                    trees.get(i).setSearch(specimens.get(s));
                    ISpecimen next = trees.get(i).next();
                    if (next == null)
                    {
                        crowding = nCriteria;
                        break;
                    }

                    {
                        if (normalizations != null)
                        {
                            INormalization n = normalizations.get(i);
                            double vA = next.getAlternative().getEvaluationAt(criteria.get(i));
                            double vB = previous.getAlternative().getEvaluationAt(criteria.get(i));

                            crowding += Math.abs(n.getNormalized(vA) - n.getNormalized(vB));
                        } else
                        {
                            double vA = next.getAlternative().getEvaluationAt(criteria.get(i));
                            double vB = previous.getAlternative().getEvaluationAt(criteria.get(i));
                            crowding += Math.abs(vA - vB);
                        }
                    }
                }

                crowding /= (double) (nCriteria);

                if ((crowding < 0.0d) || (crowding > 1.0d))
                {
                    SC.getInstance().log("Crowding Distance: bound error\n");
                }

                result[s] = crowding;
            }
        }

        return result;
    }


    public static double[] getDistanceFront(LinkedList<LinkedList<Integer>> front,
                                            ArrayList<ISpecimen> specimens,
                                            ArrayList<INormalization> normalizations,
                                            ArrayList<ICriterion> criteria)
    {
        int nCriteria = criteria.size();

        ArrayList<BinaryTree<ISpecimen>> tree = new ArrayList<>(nCriteria);
        for (ICriterion aCriteria : criteria)
        {
            BinaryTree<ISpecimen> tmp = new BinaryTree<>(aCriteria.getExtractor());
            if (aCriteria.isGain()) tmp.setDirection(false);
            tree.add(tmp);
        }

        return getDistanceFront(front, tree, specimens, normalizations, criteria);
    }
}
