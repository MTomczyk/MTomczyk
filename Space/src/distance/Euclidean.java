package distance;

import distance.interfaces.IDistance;
import exception.ArraySizeException;
import normalization.interfaces.INormalization;
import standard.Point;

import java.util.ArrayList;


/**
 * Created by Michał on 2015-02-09.
 *
 * This class calculates euclidean distance between two points.
 */

public class Euclidean implements IDistance
{

    /**
     * Parameters for Euclidean distance.
     */
    public static class Params
    {
        /** Array of weights for each dimension. If NULL: weights are equal = 1.0*/
        public ArrayList<Double> weight = null;
        /** Array of INormalization for each dimension. If NULL: normalizations are not performed.*/
        public ArrayList<INormalization> normalization = null;
    }

    protected ArrayList<Double> _weight = null;
    protected ArrayList<INormalization> _normalization = null;

    /**
     * Simple constructor.
     */
    public Euclidean()
    {
        this(null);
    }

    /**
     * Simple constructor with parameters
     * @param p Parameters
     */
    public Euclidean(Params p)
    {
        if (p != null)
        {
            this._weight = p.weight;
            this._normalization = p.normalization;
        }
    }

    protected void validateData(Point A, Point B)
    {
        int l = A.getValues().length;
        if (B.getValues().length != l) try
        {
            throw new ArraySizeException("Point A size != Point B size");
        } catch (ArraySizeException e)
        {
            e.printStackTrace();
        }

        if ((_weight != null) && (_weight.size() != l)) try
        {
            throw new ArraySizeException("Weight size != Points size");
        } catch (ArraySizeException e)
        {
            e.printStackTrace();
        }

        if ((_normalization != null) && (_normalization.size() != l)) try
        {
            throw new ArraySizeException("Normalization size != Points size");
        } catch (ArraySizeException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get distance between two points.
     * @param A First point.
     * @param B Second point.
     * @return Distance between two points.
     */
    @Override
    public double getDistance(Point A, Point B)
    {
        this.validateData(A, B);

        int l = A.getValues().length;
        double distance = 0.0d;

        for (int i = 0; i < l; i++)
        {
            double vA = A.getValues()[i];
            if (_normalization != null) vA = _normalization.get(i).getNormalized(vA);
            double vB = B.getValues()[i];
            if (_normalization != null) vB = _normalization.get(i).getNormalized(vB);
            double dV = vA - vB;
            dV *= dV;
            if (_weight != null) dV *= _weight.get(i);
            distance += dV;
        }

        if (_normalization != null) distance /= ((double) l);
        distance = Math.sqrt(distance);
        return distance;
    }

    @Override
    public void setNormalizations(ArrayList<INormalization> normalizations)
    {
        this._normalization = normalizations;
    }
}
