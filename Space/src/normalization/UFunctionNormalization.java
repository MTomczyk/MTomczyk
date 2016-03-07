package normalization;

import normalization.interfaces.INormalization;
import utils.UtilityFunction;

/**
 * Created by Michał on 2015-03-13.
 */
public class UFunctionNormalization implements INormalization
{
    private UtilityFunction _function = null;

    public UFunctionNormalization(UtilityFunction function)
    {
        this._function = function;
    }

    @Override
    public double getNormalized(double value)
    {
        return _function.getUtility(value);
    }

    @Override
    public void reset()
    {

    }

    @Override
    public void update(double value)
    {

    }

    @Override
    public void perform()
    {

    }
}
