package draw.drawpoint;

import draw.drawpoint.interfaces.IDrawPoint;
import utils.DoubleRound;

import java.awt.*;

/**
 * Created by Michał on 2014-10-10.
 *
 */
public class Rectangle implements IDrawPoint
{
    private double _shiftLeft = 0.0d;
    private double _shiftTop = 0.0d;
    private double _width = 1.0d;
    private double _height = 1.0d;


    public Rectangle(double shiftLeft, double shiftTop, double width, double height)
    {
        this._shiftLeft = shiftLeft;
        this._shiftTop = shiftTop;
        this._height = height;
        this._width = width;
    }

    @Override
    public void draw(Graphics g, draw.color.Color color, double x, double y, double[] args)
    {
        double shiftLeft = this._shiftLeft;
        double shiftTop = this._shiftTop;
        double width = this._width;
        double height = this._height;

        if (args!= null)
        {
            shiftLeft = args[0];
            shiftTop = args[1];
            width = args[2];
            height = args[3];
        }

        g.setColor(color.getAwtColor());
        int left = DoubleRound.toInt(x - shiftLeft);
        int bottom = DoubleRound.toInt(y - shiftTop);
        g.fillRect(left, bottom, DoubleRound.toInt(width), DoubleRound.toInt(height));
    }
}
