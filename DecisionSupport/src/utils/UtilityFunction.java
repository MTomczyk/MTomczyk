package utils;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import normalization.MinMaxNormalization;
import normalization.interfaces.INormalization;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.stat.StatUtils;
import shared.SC;
import simplex.WeightsGenerator;
import standard.Common;
import standard.Point;
import standard.Range;

import java.util.ArrayList;

/**
 * Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *         <p/>
 *         It represents utility function (piecewise linear, continous, monotonic).
 *         Also provides methods for creating and using utility functions.
 */
public class UtilityFunction
{
    private Point _points[] = null;
    int _pointer = 0;

    /**
     * Calculates utility using linear interpolation. If x < x0 (x > xn) then
     * y = y0 (y = yn).
     *
     * @param x Input value.
     * @return Value of function.
     */
    public double getUtility(double x)
    {
        if (x <= _points[0].getX()) return _points[0].getY();
        else if (x >= _points[this._pointer - 1].getX()) return _points[this._pointer - 1].getY();

        for (int i = 1; i < _points.length; i++)
        {
            if (Double.compare(_points[i].getX(), x) == 0) return _points[i].getY();
            else if (_points[i].getX() >= x)
            {
                if (Double.compare(_points[i].getY(), _points[i - 1].getY()) == 0) return _points[i].getY();

                double result;
                if (_points[i].getY() > _points[i - 1].getY())
                {
                    result = _points[i - 1].getY();
                    result += ((x - _points[i - 1].getX()) * (_points[i].getY() - _points[i - 1].getY()) / (_points[i].getX() - _points[i - 1].getX()));
                    return result;
                } else
                {
                    result = _points[i].getY();
                    result += ((_points[i].getX() - x) * (_points[i - 1].getY() - _points[i].getY()) / (_points[i].getX() - _points[i - 1].getX()));
                    return result;
                }

            }
        }

        return 0.0d;
    }

    /**
     * Just print Utility Function.
     * Format:
     * y0, y1,..., yn
     * x0, x1,..., xn
     */
    public String print()
    {
        String s = "y ";

        System.out.printf("y ");
        for (int i = 0; i < this._pointer; i++)
        {
            String sx = String.format("%.16f   ", this._points[i].getY());
            System.out.printf(sx);
            s += sx;
        }

        System.out.printf("\n");
        System.out.printf("x ");
        s += "\nx ";

        for (int i = 0; i < this._pointer; i++)
        {
            String sx = String.format("%.16f   ", this._points[i].getX());
            System.out.printf(sx);
            s += sx;
        }

        System.out.printf("\n");
        s += "\n";

        return s;
    }

    /**
     * Simple constructor.
     *
     * @param points Array of characteristic points.
     */
    public UtilityFunction(Point points[])
    {
        this._points = points;
        this._pointer = this._points.length;
    }

    /**
     * Simple constructor.
     *
     * @param size Fixed number of characteristics points you can add.
     */
    public UtilityFunction(int size)
    {
        this._points = new Point[size];
    }

    /**
     * Add characteristic point to function. It orders points by x-value.
     * Number of points must be less/equal then given fixed size in constructor.
     * You cannot add new point if you created function from array of characteristics points.
     *
     * @param value New characteristic point.
     */
    public void add(Point value)
    {
        if (value == null)
        {
            SC.getInstance().log("Utility Function: null Point value\n");
            return;
        }

        this._points[this._pointer] = value;
        this._pointer++;

        for (int i = this._pointer - 1; i >= 1; i--)
        {
            if (value.getX() < this._points[i - 1].getX())
            {
                this._points[i] = this._points[i - 1];
                this._points[i - 1] = value;
            } else break;
        }
    }

    /**
     * Reset function. Delete all points.
     *
     * @param size Fixed number of characteristic points.
     */
    public void reset(int size)
    {
        this._pointer = 0;
        this._points = new Point[size];
    }

    /**
     * Return array of characteristic points.
     *
     * @return array of characteristic points.
     */
    public Point[] getPoint()
    {
        return this._points;
    }

    /**
     * Set new array of characteristic points.
     *
     * @param points new array of characteristic points.
     */
    public void setPoint(Point points[])
    {
        this._points = points;
    }


    /**
     * It creates random utility function for each criterion with fixed number of characteristic points.
     * This functions are: monotonic, piecewise linear, x from 0 (x0 = 0) to 1 (xn = 1), y from 0 to yn where sum of yn (for each criterion) is equal 1.
     * Two points for each function are boundary points (x = 0 or x = 1), so if you want 2 'inside' points cPoints parameter must be 4.
     * Random number generator use uniform distribution to generate numbers.
     * Distribution: regression -> y = x;
     *
     * @param cPoints   Number of characteristic points.
     * @param criteria  Array of criteria.
     * @param generator Random number generation.
     * @return Return utility function for each criterion.
     */


    public static ArrayList<UtilityFunction> getRandomNormalizedFunction(int cPoints, ArrayList<ICriterion> criteria,
                                                                         MersenneTwister generator)
    {
        if (cPoints < 2) throw new IllegalArgumentException();
        ArrayList<UtilityFunction> uf = new ArrayList<>(criteria.size());

        double w[] = WeightsGenerator.getUniformWeights(criteria.size(), generator);

        for (int i = 0; i < criteria.size(); i++)
        {
            // X -----------------------------
            InsertionSortDouble.init(cPoints);
            InsertionSortDouble.step(0.0d);
            for (int j = 0; j < cPoints - 2; j++)
                InsertionSortDouble.step(generator.nextDouble());
            InsertionSortDouble.step(1.0d);
            double x[] = InsertionSortDouble.data;

            // Y -----------------------------
            InsertionSortDouble.init(cPoints);
            InsertionSortDouble.step(0.0d);
            for (int j = 0; j < cPoints - 2; j++)
                InsertionSortDouble.step(generator.nextDouble());
            InsertionSortDouble.step(1.0d);
            double yp[] = InsertionSortDouble.data;

            double y[];
            if (criteria.get(i).isGain())
                y = yp;
            else
            {
                y = new double[cPoints];
                for (int j = 0; j < cPoints; j++)
                    y[j] = yp[yp.length - 1 - j];
            }

            Point point[] = new Point[cPoints];
            for (int j = 0; j < cPoints; j++)
                point[j] = new Point(x[j], y[j] * w[i]);
            UtilityFunction u = new UtilityFunction(point);
            uf.add(u);
        }
        return uf;
    }

    /**
     * Check if given utility function is monotonic (not strict).
     *
     * @param uf Utility function.
     * @return TRUE - monotonic; FALSE - otherwise.
     */
    public static boolean validateMonotonic(UtilityFunction uf)
    {
        int l = 0;
        int g = 0;
        int n = uf._points.length;
        for (int i = 1; i < n; i++)
        {
            if (uf._points[i].getY() > uf._points[i - 1].getY()) g++;
            else if (uf._points[i].getY() < uf._points[i - 1].getY()) l++;
        }

        return !((l != 0) && (g != 0));
    }


    /**
     * Calculates Chebyshev utility.
     *
     * @param alternative      Alternative to evaluate.
     * @param utilityFunctions Array of utility functions for each criterion.
     * @param criteria         Array of criteria.
     * @return Chebyshev Utility.
     */
    public static double getChebyshevUtility(IAlternative alternative, ArrayList<UtilityFunction> utilityFunctions,
                                             ArrayList<ICriterion> criteria)
    {

        /*double e[] = new double[criteria.size()];
        for (int i = 0; i < criteria.size(); i++)
        {
            double x = alternative.getEvaluationAt(criteria.get(i));
            double w = utilityFunctions.get(i).getUtility(0.0d);
            double v = w * x;

            e[i] = v;
        }

        return 1.0d - StatUtils.max(e);*/


        double e[] = new double[criteria.size()];
        for (int i = 0; i < criteria.size(); i++)
        {
            double v = utilityFunctions.get(i).getUtility(alternative.getEvaluationAt(criteria.get(i)));
            e[i] = v;
        }

        return 1.0d - StatUtils.max(e);
    }

    /**
     * Calculates utility value.
     *
     * @param alternative      Alternative to evaluate.
     * @param utilityFunctions Array of utility functions for each criterion.
     * @param criteria         Array of criteria.
     * @return Utility value.
     */
    public static double getUtility(IAlternative alternative, ArrayList<UtilityFunction> utilityFunctions,
                                    ArrayList<ICriterion> criteria)
    {
        double u = 0.0d;
        for (int i = 0; i < criteria.size(); i++)
        {
            double e = alternative.getEvaluationAt(criteria.get(i));
            u += utilityFunctions.get(i).getUtility(e);
        }
        return u;
    }

    /**
     * Calculates utility value. Value for each criterion for alternative is interpolated (linear) using given ranges.
     *
     * @param alternative    Alternative to evaluate.
     * @param criteria       Array of criteria.
     * @param normalizations Array of normalization objects for each criterion
     * @return Utility value.
     */

    public static double getUtilityFromNormalizedFunction(IAlternative alternative, ArrayList<UtilityFunction> utilityFunctions,
                                                          ArrayList<ICriterion> criteria,
                                                          ArrayList<INormalization> normalizations)
    {
        double u = 0.0d;
        for (int i = 0; i < criteria.size(); i++)
        {
            double e = alternative.getEvaluationAt(criteria.get(i));
            u += utilityFunctions.get(i).getUtility(normalizations.get(i).getNormalized(e));
        }
        return u;
    }

    /**
     * Calculates utility value. Value for each criterion for alternative is interpolated (linear) using given ranges.
     *
     * @param alternative      Alternative to evaluate.
     * @param utilityFunctions Array of utility functions for each criterion.
     * @param criteria         Array of criteria.
     * @param criterionRange   Key for criterion.range map where custom ranges are stored.
     * @return Utility value.
     */
    public static double getUtilityFromNormalizedFunction(IAlternative alternative, ArrayList<UtilityFunction> utilityFunctions,
                                                          ArrayList<ICriterion> criteria, String criterionRange)
    {
        double u = 0.0d;
        for (int i = 0; i < criteria.size(); i++)
        {
            Range r = criteria.get(i).getRange().get(criterionRange);
            INormalization normalization = new MinMaxNormalization(r.left, r.right);
            double e = alternative.getEvaluationAt(criteria.get(i));
            u += utilityFunctions.get(i).getUtility(normalization.getNormalized(e));
        }
        return u;
    }

    public boolean isUnit()
    {
        if (_points == null) return false;
        if (_points.length != 2) return false;
        double a = _points[0].getY();
        double b = _points[1].getY();
        int A = (int)(a + Common.EPSILON);
        int B = (int)(b + Common.EPSILON);
        return A + B == 1;
    }
}
