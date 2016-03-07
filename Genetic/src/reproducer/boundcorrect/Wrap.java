package reproducer.boundcorrect;

import reproducer.boundcorrect.interfaces.IBoundCorrect;
import standard.Range;

/**
 * Created by Michał on 2015-03-04.
 *
 * Correct value (f.i. after mutation) if new value is not legal.
 */
public class Wrap implements IBoundCorrect
{
    /**
     * Wrap given value around left/right range value until this value will become legal.
     * @param r Given available value range.
     * @param v Value to correct.
     * @return Corrected value.
     */
    @Override
    public double correct(Range r, double v)
    {
        while (!r.isInRange(v))
        {
            if (v < r.left) v = 2.0 * r.left - v;
            else if (v > r.right) v = 2.0 * r.right - v;
        } return v;
    }
}
