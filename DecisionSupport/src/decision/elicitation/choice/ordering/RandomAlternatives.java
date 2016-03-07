package decision.elicitation.choice.ordering;

import alternative.interfaces.IAlternative;
import decision.maker.ordering.Order;
import decision.elicitation.choice.ordering.iterfaces.IChoice;
import decision.maker.ordering.interfaces.IOrderingDM;
import exception.ArraySizeException;
import extractor.alternative.FromAlternative;
import extractor.interfaces.IAlternativeExtractor;
import org.apache.commons.math3.random.MersenneTwister;
import utils.InsertionSortInteger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Michał on 2015-02-12.
 * <p/>
 * This class provides elicitation model when pair of alternatives from given set is randomly chosen (uniform distribution)
 * and given to DM to compare.
 */
public class RandomAlternatives implements IChoice
{
    private MersenneTwister _g = null;
    private int _k = 2;

    /**
     * Simple constructor.
     */
    public RandomAlternatives()
    {
        _g = new MersenneTwister(System.currentTimeMillis());
    }

    /**
     * Simple constructor.
     *
     * @param g Random number generator.
     */
    public RandomAlternatives(MersenneTwister g)
    {
        this._g = g;
    }

    /**
     * Simple constructor.
     *
     * @param g Random number generator.
     * @param k Number of chosen alternatives
     */
    public RandomAlternatives(MersenneTwister g, int k)
    {
        this._g = g;
        this._k = k;
    }

    @Override
    public Order getOrder(ArrayList<IAlternative> alternatives, IOrderingDM dm, IOrderingDM estimatedDM)
    {
        return getOrder(alternatives, new FromAlternative(), dm, estimatedDM);
    }

    @Override
    public Order getOrder(Object arrayOfObjects, IAlternativeExtractor extractor, IOrderingDM dm, IOrderingDM estimatedDM)
    {
        ArrayList<IAlternative> toOrder = getAlternativesToCompare(arrayOfObjects, extractor,estimatedDM);
        return dm.order(toOrder);
    }

    @Override
    public ArrayList<IAlternative> getAlternativesToCompare(Object arrayOfObjects, IAlternativeExtractor extractor, IOrderingDM estimatedDM)
    {
        @SuppressWarnings("all")
        ArrayList<Object> alternatives = (ArrayList<Object>) arrayOfObjects;

        if (alternatives.size() < _k) try
        {
            throw new ArraySizeException("Pair to Compare: Array size < " + _k + " ( " + alternatives.size() + " )");
        } catch (ArraySizeException e)
        {
            e.printStackTrace();
        }

        InsertionSortInteger.init(_k);
        int used = 0;

        LinkedList<Integer> oStored = new LinkedList<>();

        for (int i = 0; i < _k; i++)
        {
            int E = _g.nextInt(alternatives.size() - used);

            int stored[] = InsertionSortInteger.data;
            for (int j = 0; j < used; j++)
            {
                if (E >= stored[j]) E++;
            }
            used++;
            InsertionSortInteger.step(E);
            oStored.add(E);
        }

        ArrayList<IAlternative> result = new ArrayList<>(_k);
        result.addAll(oStored.stream().map(s -> extractor.getValue(alternatives.get(s))).collect(Collectors.toList()));
        return result;
    }



    @Override
    public void addFeedback(Order o)
    {

    }

    @Override
    public int getRequiredAlternatives()
    {
        return _k;
    }


}
