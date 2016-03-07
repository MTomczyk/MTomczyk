package draw.color;

import utils.DoubleRound;

public class Color
{
    public double r = 0.0d;
    public double g = 0.0d;
    public double b = 0.0d;
    public double a = 0.0d;

    public Color()
    {

    }

    public Color(double r, double g, double b, double a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public java.awt.Color getAwtColor()
    {
        return new java.awt.Color(DoubleRound.toInt(this.r), DoubleRound.toInt(this.g), DoubleRound.toInt(this.b));
    }


}
