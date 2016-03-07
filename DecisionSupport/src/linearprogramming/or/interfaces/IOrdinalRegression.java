package linearprogramming.or.interfaces;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.maker.ordering.Order;
import decision.maker.ordering.interfaces.IOrderingDM;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import tree.binary.BinaryTree;
import utils.UtilityFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Michał on 2015-02-13.
 */
public interface IOrdinalRegression
{
    ArrayList<UtilityFunction> getUtility(ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria,
                                          LinkedList<Order> dmHistory);

    ArrayList<UtilityFunction> getUtility(ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria,
                                          HashMap<IOrderingDM, LinkedList<Order>> dmsHistory, ArrayList<IOrderingDM> dms);

    Result getResult(Solver solver,
                     ArrayList<BinaryTree<IAlternative>> sortedInComparisons,
                     ArrayList<BinaryTree<IAlternative>> sortedAll,
                     ArrayList<IAlternative> alternativesInComparisons,
                     ArrayList<IAlternative> alternativesAll,
                     ArrayList<IAlternative> alternatives,
                     ArrayList<ICriterion> criteria,
                     HashMap<IOrderingDM, LinkedList<Order>> dmsHistory,
                     ArrayList<IOrderingDM> dms);

    Result getResultWithBinaryConstraints(Solver solver,
                            ArrayList<BinaryTree<IAlternative>> sortedInComparisons,
                            ArrayList<BinaryTree<IAlternative>> sortedAll,
                            ArrayList<IAlternative> alternativesInComparisons,
                            ArrayList<IAlternative> allAlternatives,
                            ArrayList<IAlternative> alternatives,
                            ArrayList<ICriterion> criteria,
                            HashMap<IOrderingDM, LinkedList<Order>> dmsHistory,
                            ArrayList<IOrderingDM> dms,
                            String binaryKey, int binaryBase);
}
