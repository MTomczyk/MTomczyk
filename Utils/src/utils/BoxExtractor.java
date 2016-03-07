package utils;

import extractor.interfaces.IValueExtractor;

/**
 * Created by MTomczyk on 09.11.2015.
 */
public class BoxExtractor implements IValueExtractor
{
    private int _i = 0;
    private boolean _upper = true;

    public BoxExtractor(boolean upper, int index)
    {
        this._i = index;
        this._upper = upper;
    }

    @Override
    public Double getValue(Object o) {

        LPBox b = (LPBox) o;
        if (_upper) return b.upper[_i];
        return b.lower[_i];
    }
}
