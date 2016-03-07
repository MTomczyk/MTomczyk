package standard;


public class Point
{
    private double _values[] = null;

    public Point()
    {
        double value[] = new double[2];
        this.init(value);
    }

    public Point(double value[])
    {
        this.init(value);
    }

    public Point(double x)
    {
        double value[] = {x};
        this.init(value);
    }

    public Point(double x, double y)
    {
        double value[] = {x, y};
        this.init(value);
    }

    public Point(double x, double y, Double z)
    {
        double value[] = {x, y, z};
        this.init(value);
    }

    public void init(double values[])
    {
        this._values = values;
    }

    public Double getX()
    {
        return this._values[0];
    }

    public Double getY()
    {
        if (this._values.length > 1) return this._values[1];
        else return null;
    }

    public double [] getValues()
    {
        return this._values;
    }

    public Double getZ()
    {
        if (this._values.length > 2) return this._values[2];
        else return null;
    }

    public void print()
    {
        for (double v : _values)
            System.out.print(v + " ");
        System.out.println("");
    }
}

