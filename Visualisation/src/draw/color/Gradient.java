package draw.color;


import shared.SC;

import java.util.ArrayList;

/**
 * Created by Michał on 2014-10-08.
 *
 */
public class Gradient
{
    protected ArrayList<ColorAssignment> point = null;

    public Gradient()
    {
        
    }

    public Gradient(Color c)
    {
        this.point = new ArrayList<ColorAssignment>(2);
        ColorAssignment caF = new ColorAssignment(0.0d, c);
        ColorAssignment caS = new ColorAssignment(0.0d, c);
        this.point.add(caF);
        this.point.add(caS);
    }


    public Gradient(ArrayList<ColorAssignment> points)
    {

        this.point = points;
        this.sort();
    }

    public Gradient(int size)
    {

        point = new ArrayList<ColorAssignment>(size);
    }

    public Color getColor(double input)
    {
        if (input < 0.0d)
        {
            SC.getInstance().log("input < 0.0d");
            return null;
        } else if (input > 1.0d)
        {
            SC.getInstance().log("input > 1.0d");
            return null;
        }

        if (point.size() == 1) return point.get(0).color;

        for (int i = 1; i < point.size(); i++)
        {
            if (input < point.get(i).value)
            {
                double proportion = (input - point.get(i - 1).value) / (point.get(i).value - point.get(i - 1).value);
                return this.getColorBetween(point.get(i - 1).color, point.get(i).color, proportion);
            } else if (input == point.get(i).value) return point.get(i).color;
        }

        return point.get(point.size() - 1).color;
    }

    private Color getColorBetween(Color A, Color B, double proportion)
    {
        double r = A.r + proportion * (B.r - A.r);
        double g = A.g + proportion * (B.g - A.g);
        double b = A.b + proportion * (B.b - A.b);
        double a = A.a + proportion * (B.a - A.a);
        return new Color(r, g, b, a);
    }


    public void add(ColorAssignment ca)
    {
        this.point.add(ca);
        this.sort(this.point.size() - 1);
    }

    private void sort()
    {
        for (int i = 1; i < this.point.size(); i++)
            this.sort(i);
    }

    private void sort(int position)
    {
        for (int i = position; i > 0; i--)
        {
            if (this.point.get(i).value > this.point.get(i - 1).value) continue;

            ColorAssignment tmp = this.point.get(i);
            this.point.set(i, this.point.get(i - 1));
            this.point.set(i - 1, tmp);
        }
    }


}
