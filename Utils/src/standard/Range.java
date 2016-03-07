package standard;


public class Range
{
    public double left = 0.0d;
    public double right = 1.0d;

    public Range(double left, double right)
    {
        this.left = left;
        this.right = right;
    }

    public boolean isInRange(double arg)
    {
        return !((arg < this.left) || (arg > this.right));
    }

    public double getRange()
    {
        return this.right - this.left;
    }

}
