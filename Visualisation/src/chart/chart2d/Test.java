package chart.chart2d;

import chart.ChartFrame;
import chart.Drawer;
import dataset.DataSet;
import draw.color.Color;
import draw.color.Gradient;
import draw.color.gradients.RedGreen;
import draw.drawpoint.Circle;
import draw.drawpoint.Square;
import standard.Point;
import standard.Range;

import java.util.ArrayList;

/**
 * Created by Michał on 2014-10-09.
 *
 */
public class Test
{
    public static void main(String args[])
    {
        Range rX = new Range(0.0d, 1.0d);
        Range rY = new Range(0.0d, 1.0d);
        Range rZ = new Range(0.0d, 1.0d);

        ArrayList <DataSet> aDS = new ArrayList<>(2);
        {
            Gradient gr = new Gradient(new Color(255.0d, 0.0d, 0.0d, 255.0d));
            Point p1 = new Point(0.25d, 0.75d);
            Point p2 = new Point(0.5d, 0.5d);
            Point p3 = new Point(1.0d, 1.0d);

            ArrayList<Point> pS = new ArrayList<>(3);
            pS.add(p1);
            pS.add(p2);
            pS.add(p3);
            DataSet ds = new DataSet(pS, gr, new Circle(10.0d), "Set 1");
            aDS.add(ds);
        }
        {
            Gradient gr = new RedGreen();
            Point p1 = new Point(0.1d, 0.2d, 0.0d);
            Point p2 = new Point(0.9d, 0.7d, 0.25d);
            Point p3 = new Point(1.0d, 0.5d, 0.5d);
            Point p4 = new Point(0.4d, 0.4d, 0.75d);
            Point p5 = new Point(0.7d, 0.35d, 1.0d);

            ArrayList<Point> pS = new ArrayList<>(3);
            pS.add(p1);
            pS.add(p2);
            pS.add(p3);
            pS.add(p4);
            pS.add(p5);

            DataSet ds = new DataSet(pS, gr, new Square(10.0d), "Set 2");
            aDS.add(ds);
        }


        Chart2D bc = new Chart2D(new BlackSchema(), rX, rY, rZ);
        bc.setLegendPosition(Drawer.LEGEND_TOP + Drawer.LEGEND_LEFT);
        bc.setData(aDS);
        bc.setConnect(true);
        bc.redraw();

        ChartFrame cf = new ChartFrame(800,800,bc);
        cf.setLocationRelativeTo(null);
        cf.setVisible(true);



    }

}
