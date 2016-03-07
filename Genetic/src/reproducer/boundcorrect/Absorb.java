package reproducer.boundcorrect;

import reproducer.boundcorrect.interfaces.IBoundCorrect;
import standard.Range;

/**
 * Created by Michał on 2015-03-04.
 *
 * Correct value (f.i. after mutation) if new value is not legal.
 */
public class Absorb implements IBoundCorrect
{
    /**
     * If new value is smaller/greater than left/right boundary, corrected is set to left/right boundary.
     * @param r Given available value range.
     * @param v Value to correct.
     * @return Corrected value.
     */
    @Override
    public double correct(Range r, double v)
    {
        if (v < r.left) v = r.left;
        else if (v > r.right) v = r.right;
        return v;
    }
}
