package chart;

import dataset.DataSet;
import draw.color.Color;
import standard.Common;
import standard.Margin;
import standard.Point;
import standard.Range;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Michał on 2014-10-08.
 *
 */
public abstract class BaseChart extends JPanel
{
    protected HashMap <String, Color> color = new HashMap<>();

    protected ArrayList<DataSet> data = null;

    protected Range rangeX = new Range(Common.MAX_DOUBLE, Common.MIN_DOUBLE);
    protected Range rangeY = new Range(Common.MAX_DOUBLE, Common.MIN_DOUBLE);
    protected Range rangeZ = new Range(Common.MAX_DOUBLE, Common.MIN_DOUBLE);

    private Margin margin = new Margin(50.0d,30.0d,30.0d,30.0d);

    private int legendPosition = Drawer.LEGEND_NONE;

    public BaseChart()
    {

    }

    public void redraw()
    {
        this.redraw(false);
    }

    public void redraw(boolean scale)
    {
        if (scale) this.recalculateRanges();
        this.repaint();
    }

    @SuppressWarnings("ConstantConditions")
    public void recalculateRanges()
    {
        rangeX.left = Common.MAX_DOUBLE;
        rangeX.right = Common.MIN_DOUBLE;
        rangeY.left = Common.MAX_DOUBLE;
        rangeY.right = Common.MIN_DOUBLE;
        rangeZ.left = Common.MAX_DOUBLE;
        rangeZ.right = Common.MIN_DOUBLE;

        for (int i = 0; i < this.getData().size(); i++)
        {
            if (data.get(i) == null)
                continue;

            for (Point p : data.get(i).getData())
            {
                if (p.getX() < rangeX.left)
                    rangeX.left = p.getX();
                if (p.getX() > rangeX.right)
                    rangeX.right = p.getX();
                if (p.getY() < rangeY.left)
                    rangeY.left = p.getY();
                if (p.getY() > rangeY.right)
                    rangeY.right = p.getY();

                if (rangeZ == null) continue;
                if (p.getZ() == null) continue;

                if (p.getZ() < rangeZ.left)
                    rangeZ.left = p.getZ();
                if (p.getZ() > rangeZ.right)
                    rangeZ.right = p.getZ();
            }

        }
    }

    @SuppressWarnings("unused")
    public void saveDataSets(String path)
    {

    }

    @SuppressWarnings("unused")
    public void loadDataSets(String path)
    {

    }

    public ArrayList<DataSet> getData()
    {
        return data;
    }

    public void setData(ArrayList<DataSet> data)
    {
        this.data = data;
    }

    public Margin getMargin()
    {
        return margin;
    }

    @SuppressWarnings("unused")
    public void setMargin(Margin margin)
    {
        this.margin = margin;
    }

    public int getLegendPosition()
    {
        return legendPosition;
    }

    public void setLegendPosition(int legendPosition)
    {
        this.legendPosition = legendPosition;
    }
}
