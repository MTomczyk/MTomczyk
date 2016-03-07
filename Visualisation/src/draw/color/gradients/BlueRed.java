package draw.color.gradients;

import draw.color.Color;
import draw.color.ColorAssignment;
import draw.color.Gradient;

import java.util.ArrayList;

/**
 * Created by Michał on 2014-10-08.
 *
 */
public class BlueRed extends Gradient
{
    public BlueRed()
    {
        ColorAssignment caRed = new ColorAssignment(1.0d, new Color(255.0d,0.0,0.0,255.0d));
        ColorAssignment caBlue = new ColorAssignment(0.0d, new Color(0.0d,0.0d,255.0,255.0d));

        this.point = new ArrayList<>(2);
        this.point.add(caBlue);
        this.point.add(caRed);
    }


}
