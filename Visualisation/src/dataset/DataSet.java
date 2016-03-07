package dataset;

import java.util.ArrayList;

import draw.color.Gradient;
import draw.drawpoint.interfaces.IDrawPoint;
import standard.Point;

public class DataSet
{
    private ArrayList<Point> _data = null;
    private Gradient _gradient = null;
    private IDrawPoint _drawPoint = null;
    private String _name = null;

    public DataSet(ArrayList<Point> data, Gradient gradient)
    {
        this._data = data;
        this._gradient = gradient;
    }

    public DataSet(ArrayList<Point> data, Gradient gradient, IDrawPoint drawPoint, String name)
    {
        this._data = data;
        this._gradient = gradient;
        this._drawPoint = drawPoint;
        this._name = name;
    }

    public DataSet(ArrayList<Point> data)
    {
        this._data = data;
    }
    public DataSet(Gradient gradient)
    {
        this._gradient = gradient;
    }

    public ArrayList<Point> getData()
    {
        return _data;
    }
    public void setData(ArrayList<Point> data)
    {
        this._data = data;
    }

    public Gradient getGradient()
    {
        return _gradient;
    }
    public void setGradient(Gradient gradient)
    {
        this._gradient = gradient;
    }

    public IDrawPoint getDrawPoint()
    {
        return _drawPoint;
    }
    @SuppressWarnings("unused")
    public void setDrawPoint(IDrawPoint drawPoint)
    {
        this._drawPoint = drawPoint;
    }

    public String getName()
    {
        return _name;
    }
    public void setName(String name)
    {
        this._name = name;
    }
}
