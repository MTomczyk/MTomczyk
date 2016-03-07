package chart.cube3d;

import java.util.ArrayList;

import dataset.DataSet;
import draw.color.Color;
import draw.color.Gradient;
import standard.Point;
import standard.Range;

public class Test
{

    public static void main(String[] args)
    {
        Cube3D c = new Cube3D(new Range(0, 1), new Range(0, 1), new Range(0, 1), new BlackSchema());

        ArrayList<Point> ap = new ArrayList<>(3);
        ap.add(new Point(0.25, 0.25, 0.25));
        ap.add(new Point(0.5, 0.5, 0.5));
        ap.add(new Point(0.75, 0.75, 0.75));

        DataSet ds = new DataSet(ap, new Gradient(new Color(255.0d, 0.0d, 0.0, 255.0d)));

        ArrayList<DataSet> d = new ArrayList<>(1);
        d.add(ds);

        c.setDataSet(d);

        c.setVisible(true);

    }

}
