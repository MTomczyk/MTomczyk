package distance;

import standard.Point;



/**
 * Created by Michał on 2015-02-09.
 *
 * This class calculates chebyshev distance between two points.
 */
public class Chebyshev extends Euclidean
{
    /**
     * Simple constructor with parameters
     * @param p Parameters
     */
    public Chebyshev(Params p)
    {
        if (p != null)
        {
            this._weight = p.weight;
            this._normalization = p.normalization;
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

        double distance = -1.0d;

        for (int i = 0; i < l; i++)
        {
            double vA = A.getValues()[i];
            if (_normalization != null) vA = _normalization.get(i).getNormalized(vA);
            double vB = B.getValues()[i];
            if (_normalization != null) vB = _normalization.get(i).getNormalized(vB);
            double dV = Math.abs(vA - vB);

            if (_weight != null) dV *= _weight.get(i);
            if (dV > distance) distance = dV;
        }

        return distance;
    }


}
