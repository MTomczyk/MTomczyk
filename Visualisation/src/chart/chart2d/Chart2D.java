package chart.chart2d;

import chart.BaseChart;
import chart.Drawer;
import dataset.DataSet;
import draw.Schema;
import draw.color.Color;
import utils.DoubleRound;
import standard.Range;

import java.awt.*;

/**
 * Created by Michał on 2014-10-08.
 *
 */
public class Chart2D extends BaseChart
{
    protected String colorProperties[] = {"BACKGROUND", "DRAWAREA", "BORDER", "MAINGRIDLINE", "AUXILIARYGRIDLINE", "FONTCOLOR"};

    protected int grain = 5;

    private boolean connect = false;

    @SuppressWarnings("unused")
    public Chart2D()
    {
        this.init(null, null, null, null);
    }

    @SuppressWarnings("unused")
    public Chart2D(Schema schema)
    {
        this.init(schema, null, null, null);
    }

    public Chart2D(Schema schema, Range rangeX, Range rangeY, Range rangeZ)
    {
        this.init(schema, rangeX, rangeY, rangeZ);
    }

    protected void init(Schema schema, Range rangeX, Range rangeY, Range rangeZ)
    {
        this.color.put("BACKGROUND", new Color(255.0d, 255.0d, 255.0d, 255.0d));
        this.color.put("DRAWAREA", new Color(230.0d, 230.0d, 230.0d, 230.0d));
        this.color.put("BORDER", new Color(0.0d, 0.0d, 0.0d, 255.0d));
        this.color.put("MAINGRIDLINE", new Color(100.0d, 100.0d, 100.0d, 255.0d));
        this.color.put("AUXILIARYGRIDLINE", new Color(160.0d, 160.0d, 160.0d, 255.0d));
        this.color.put("FONTCOLOR", new Color(0.0d, 0.0d, 0.0d, 255.0d));

        for (String s : this.colorProperties)
        {
            if ((schema != null) && (schema.color.containsKey(s))) this.color.put(s, schema.color.get(s));
        }

        if (rangeX != null) this.rangeX = rangeX;
        else this.rangeX = new Range(0.0d, 1.0d);

        if (rangeY != null) this.rangeY = rangeY;
        else this.rangeY = new Range(0.0d, 1.0d);

        if (rangeZ != null) this.rangeZ = rangeZ;
        else this.rangeZ = new Range(0.0d, 0.0d);
    }

    @Override
    public void redraw()
    {
        this.redraw(false);
    }

    @Override
    public void redraw(boolean scale)
    {
        if (scale) this.recalculateRanges();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        Rectangle drawArea = new Rectangle();
        drawArea.x = DoubleRound.toInt(getMargin().left);
        drawArea.y = DoubleRound.toInt(getMargin().top);
        drawArea.width = DoubleRound.toInt(this.getWidth() - getMargin().left - getMargin().right);
        drawArea.height = DoubleRound.toInt(this.getHeight() - getMargin().top - getMargin().bottom);

        Drawer.drawBackground(graphics, this.getBounds(), this.color.get("BACKGROUND"));
        Drawer.drawDrawArea(graphics, drawArea, this.color.get("DRAWAREA"));
        Drawer.drawGridLines(graphics, drawArea, this.color.get("AUXILIARYGRIDLINE"), 2 * grain);
        Drawer.drawGridLines(graphics, drawArea, this.color.get("MAINGRIDLINE"), grain);
        Drawer.drawBorder(graphics, drawArea, this.color.get("BORDER"));

        Font font = new Font("Helvetica", Font.PLAIN, 12);

        Drawer.drawHorizontalAxis(graphics, font, this.color.get("FONTCOLOR"), drawArea, this.grain,
                Drawer.ALIGN_BOTTOM, this.rangeX);
        Drawer.drawVerticalAxis(graphics, font, this.color.get("FONTCOLOR"), drawArea, this.grain, Drawer.ALIGN_LEFT,
                this.rangeY);

        if (data != null) for (DataSet d : data)
        {
            if (d == null) continue;
            Drawer.drawDataSet(graphics, drawArea, d, rangeX, rangeY, rangeZ, connect);
        }


        if (data != null)
            Drawer.drawLegend(data, graphics, drawArea, font, this.color.get("FONTCOLOR"), this.color.get("BACKGROUND"),
                this.color.get("BORDER"), this.getLegendPosition());
    }


    @SuppressWarnings("unused")
    public int getGrain()
    {
        return grain;
    }

    @SuppressWarnings("unused")
    public void setGrain(int grain)
    {
        this.grain = grain;
    }

    @SuppressWarnings("unused")
    public boolean isConnect()
    {
        return connect;
    }

    public void setConnect(boolean connect)
    {
        this.connect = connect;
    }
}
