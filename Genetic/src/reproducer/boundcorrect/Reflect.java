package reproducer.boundcorrect;

import reproducer.boundcorrect.interfaces.IBoundCorrect;
import standard.Range;

/**
 * Created by Michał on 2015-03-04.
 *
 * Correct value (f.i. after mutation) if new value is not legal.
 */
public class Reflect implements IBoundCorrect
{
    /**
     * Removes from given value the available range until this value will become legal.
     * @param r Given available value range.
     * @param v Value to correct.
     * @return Corrected value.
     */
    @Override
    public double correct(Range r, double v)
    {
        if (v < r.left)
        {
            while (!r.isInRange(v)) v += r.getRange();
        } else if (v > r.right)
        {
            while (!r.isInRange(v)) v -= r.getRange();
        }
        return v;
    }
}
