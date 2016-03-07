package decision.maker.ordering;

import alternative.interfaces.IAlternative;
import extractor.interfaces.IAlternativeExtractor;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *         <p/>
 *         This class represents ordering of alternatives for ordering purposes.
 *         Array of Array:
 *         First Level Array: domination sort
 *         Second Level Array: Equality sort
 */
public class Order
{
    /**
     * Alternatives ordering.
     */
    public ArrayList<ArrayList<IAlternative>> _orders = null;

    /**
     * Simple constructor
     */
    public Order()
    {

    }

    /**
     * Simple constructor.
     *
     * @param orders orders of alternatives in an ArrayList
     */
    private Order(ArrayList<ArrayList<IAlternative>> orders)
    {
        this._orders = orders;
    }

    /**
     * Simple constructor
     *
     * @param alternativesHierarchy Array of Array of IAlternative (Sorted by domination and equality)
     * @return Order object
     */
    public static Order getFromData(ArrayList<ArrayList<IAlternative>> alternativesHierarchy)
    {
        return new Order(alternativesHierarchy);
    }

    /**
     * Simple constructor
     *
     * @param alternativesHierarchy Array of IAlternative (Sorted by domination)
     * @return Order object
     */
    public static Order getFromArray(ArrayList<IAlternative> alternativesHierarchy)
    {
        ArrayList<ArrayList<IAlternative>> parse = new ArrayList<>(alternativesHierarchy.size());
        for (IAlternative a : alternativesHierarchy)
        {
            ArrayList<IAlternative> eq = new ArrayList<>(1);
            eq.add(a);
            parse.add(eq);
        }
        return new Order(parse);
    }

    /**
     * Order
     *
     * @param alternatives Array of IAlternative (sorted by utility)
     * @param utilities    Array of utilities (must be sorted desc (or asc if you want)
     * @param epsilon      used when compare equality of two utilities
     * @return Order of alternatives
     */
    // MUST BE SORTED
    public static Order getFromArrayAndUtility(ArrayList<IAlternative> alternatives, double utilities[], double epsilon)
    {
        ArrayList<ArrayList<IAlternative>> data = new ArrayList<>(utilities.length);

        ArrayList<IAlternative> eq = new ArrayList<>(utilities.length);
        eq.add(alternatives.get(0));

        for (int i = 1; i < utilities.length; i++)
        {
            if ((utilities[i] > utilities[i - 1] - epsilon) && (utilities[i] < utilities[i - 1] + epsilon))
            {
                eq.add(alternatives.get(i));
            } else
            {
                data.add(eq);
                eq = new ArrayList<>(utilities.length);
                eq.add(alternatives.get(i));
            }
        }

        data.add(eq);

        return new Order(data);
    }

    /**
     * Order
     *
     * @param epsilon used when compare equality of two utilities
     * @return Order of alternatives
     */
    // MUST BE SORTED
    public static Order getFromArrayAndUtility(Object arrayOfObjects, IAlternativeExtractor extractor, double utilities[], double epsilon)
    {
        @SuppressWarnings("all")
        ArrayList<Object> alternative = (ArrayList<Object>) arrayOfObjects;

        ArrayList<ArrayList<IAlternative>> data = new ArrayList<>(utilities.length);

        ArrayList<IAlternative> eq = new ArrayList<>(utilities.length);
        eq.add(extractor.getValue(alternative.get(0)));

        for (int i = 1; i < utilities.length; i++)
        {
            if ((utilities[i] > utilities[i - 1] - epsilon) && (utilities[i] < utilities[i - 1] + epsilon))
            {
                eq.add(extractor.getValue(alternative.get(i)));
            } else
            {
                data.add(eq);
                eq = new ArrayList<>(utilities.length);
                eq.add(extractor.getValue(alternative.get(i)));
            }
        }

        data.add(eq);

        return new Order(data);
    }


    /**
     * Just parse ordering to Array.
     *
     * @return Array of IAlternative sorted according to Order.
     */
    public ArrayList<IAlternative> getSortedArray()
    {
        int size = 0;
        for (ArrayList<IAlternative> a : _orders)
            size += a.size();

        ArrayList<IAlternative> result = new ArrayList<>(size);

        for (ArrayList<IAlternative> a : _orders)
            result.addAll(a.stream().collect(Collectors.toList()));
        return result;
    }

    public boolean isSingleEqualOrder()
    {
        return (_orders.size() == 1) && (_orders.get(0).size() == 2);
    }
}
