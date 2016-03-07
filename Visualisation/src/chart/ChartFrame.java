package chart;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Michał on 2014-10-09.
 *
 */
public class ChartFrame extends JFrame
{
    private BaseChart chart = null;

    public ChartFrame(int width, int height, BaseChart chart)
    {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setSize(new Dimension(width, height));

        this.chart = chart;
        this.add(chart);
    }

    public BaseChart getChart()
    {
        return chart;
    }

    public void setChart(BaseChart chart)
    {
        this.chart = chart;
    }

}
