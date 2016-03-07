package linearprogramming;

import net.sf.javailp.Linear;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Michał on 2015-03-08.
 *
 */


public class ConstraintCreator
{
    private static LinkedList<String> varDoubles = null;
    private static HashMap<String, Double> coefficientDoubles = null;
    private static LinkedList<String> varInts = null;
    private static HashMap<String, Integer> coefficientInts = null;

    public static void init()
    {
        varDoubles = new LinkedList<String>();
        varInts = new LinkedList<String>();
        coefficientDoubles = new HashMap<String, Double>();
        coefficientInts = new HashMap<String, Integer>();
    }

    public static void addDouble(String var, Double coefficient)
    {
        if (ConstraintCreator.coefficientDoubles.get(var) == null)
        {
            ConstraintCreator.varDoubles.add(var);
            ConstraintCreator.coefficientDoubles.put(var, coefficient);
        } else
        {
            Double current = ConstraintCreator.coefficientDoubles.get(var);
            current += coefficient;
            ConstraintCreator.coefficientDoubles.put(var, current);
        }
    }

    public static void addInt(String var, Integer coefficient)
    {
        if (ConstraintCreator.coefficientInts.get(var) == null)
        {
            ConstraintCreator.varInts.add(var);
            ConstraintCreator.coefficientInts.put(var, coefficient);
        } else
        {
            Integer current = ConstraintCreator.coefficientInts.get(var);
            current += coefficient;
            ConstraintCreator.coefficientInts.put(var, current);
        }
    }

    public static void addVarsToLinear(Linear linear)
    {
        for (String s : varDoubles)
        {
            linear.add(coefficientDoubles.get(s), s);
        }

        for (String s : varInts)
        {
            linear.add(coefficientInts.get(s), s);
        }
    }

}
