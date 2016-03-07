package utils;

import extractor.interfaces.IValueExtractor;

/**
 * Created by MTomczyk on 09.11.2015.
 */
public class BoxComplexExtractor implements IValueExtractor
{
    private int _j = 0;
    private int _k = 0;
    private double _multi = 100.0d;

    private double _zi[];
    private double _zm[];

    public BoxComplexExtractor(int j, int k, double multi, double zi[], double zm[])
    {
        this._j = j;
        this._k = k;
        this._multi = multi;
        _zi = zi;
        _zm = zm;
    }

    @Override
    public Double getValue(Object o) {

        LPBox b = (LPBox) o;

        if (_zm != null)
        {
            double L1 = (b.upper[_j] - _zi[_j])/(_zm[_j] - _zi[_j])* Math.pow(_multi, 4.0d) + Math.pow(_multi, 4.0d);
            double L2 = - (b.upper[_k] - _zi[_k])/(_zm[_k] - _zi[_k])*Math.pow(_multi, 3.0d) + Math.pow(_multi, 3.0d);
            double L3 = (b.lower[_j] - _zi[_j])/(_zm[_j] - _zi[_j])*Math.pow(_multi, 2.0d)+ Math.pow(_multi, 2.0d);
            double L4 = - (b.lower[_k] - _zi[_k])/(_zm[_k] - _zi[_k])*Math.pow(_multi, 1.0d) + Math.pow(_multi, 1.0d);

            return L1 + L2 + L3 + L4;
        }
        else
        {
            double L1 = b.upper[_j]  + Math.pow(_multi, 4.0d);
            double L2 = - b.upper[_k] + Math.pow(_multi, 3.0d);
            double L3 = b.lower[_j]+ Math.pow(_multi, 2.0d);
            double L4 = - b.lower[_k]  + Math.pow(_multi, 1.0d);

            return L1 + L2 + L3 + L4;
        }

    }
}
