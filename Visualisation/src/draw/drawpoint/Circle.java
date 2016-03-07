package draw.drawpoint;

import draw.drawpoint.interfaces.IDrawPoint;
import utils.DoubleRound;

import java.awt.*;

/**
 * Created by Michał on 2014-10-08.
 *
 */
public class Circle implements IDrawPoint
{
    private double radius = 1.0d;

    public Circle(double radius)
    {
        this.radius = radius;
    }


    @Override
    public void draw(Graphics g, draw.color.Color color, double x, double y, double[] args)
    {
        double r = radius;
        if (args!= null) r = args[0];

        g.setColor(color.getAwtColor());
        int left = DoubleRound.toInt(x - r / 2.0d);
        int bottom = DoubleRound.toInt(y - r / 2.0d);
        int rad = DoubleRound.toInt(r);
        g.fillOval(left, bottom, rad, rad);
    }
}
