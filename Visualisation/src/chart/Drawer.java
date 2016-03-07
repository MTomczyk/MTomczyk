package chart;

import dataset.DataSet;
import draw.color.Color;
import draw.drawpoint.interfaces.IDrawPoint;
import draw.drawpoint.Circle;
import utils.DoubleRound;
import standard.Point;
import standard.Range;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.FontMetrics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Michał on 2014-10-08.
 *
 */
public class Drawer
{
    public static int ALIGN_LEFT = 0;
    public static int ALIGN_CENTER = 1;
    public static int ALIGN_RIGHT = 2;

    public static int ALIGN_TOP = 3;
    public static int ALIGN_BOTTOM = 4;

    public static int LEGEND_NONE = -1;
    public static int LEGEND_LEFT = 1;
    public static int LEGEND_RIGHT = 2;
    public static int LEGEND_TOP = 4;
    @SuppressWarnings("unused")
    public static int LEGEND_BOTTOM = 8;

    public static IDrawPoint _drawer = new Circle(5.0d);

    public static void drawLegend(ArrayList<DataSet> data, Graphics g, Rectangle bounds, Font font, Color fontColor,
                                  Color background, Color border, int position)
    {
        if (position == LEGEND_NONE) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(font);

        FontMetrics fontMetrics = g2d.getFontMetrics();

        double maxStr = 0.0d;

        if ((data != null) && (data.size() != 0)) for (DataSet d : data)
        {
            if (d.getName() == null) continue;
            int w = fontMetrics.stringWidth(d.getName());
            if (w > maxStr) maxStr = w;
        }

        double args[] = {6.0d, 4.0d};

        assert data != null;
        double eHeight = 10.0d + data.size() * args[0];
        if (data.size() > 1) eHeight += (data.size() - 1) * args[1];
        double eWidth = maxStr + args[0] * 5 + args[1] * 4 + 15.0d;

        double x = 0.0d;
        double y = 0.0d;

        int xMod = position & 3;
        if (xMod == 0) x = bounds.x + (double) bounds.width / 2.0d - eWidth / 2.0d;
        else if (xMod == 1) x = bounds.x;
        else if (xMod == 2) x = bounds.x + bounds.width - eWidth;

        int yMod = position & 12;
        if (yMod == 0) y = bounds.y + (double) bounds.height / 2.0d - eHeight / 2.0d;
        else if (yMod == 4) y = bounds.y;
        else if (yMod == 8) y = bounds.y + bounds.height - eHeight;

        g.setColor(background.getAwtColor());
        g.fillRect(DoubleRound.toInt(x), DoubleRound.toInt(y), DoubleRound.toInt(eWidth), DoubleRound.toInt(eHeight));

        g.setColor(border.getAwtColor());
        g.drawRect(DoubleRound.toInt(x), DoubleRound.toInt(y), DoubleRound.toInt(eWidth), DoubleRound.toInt(eHeight));


        for (int i = 0; i < data.size(); i++)
        {
            double xp = x + 5.0d + args[0] / 2.0d;
            double yp = y + 5.0d + args[0] / 2.0d + i * (args[0] + args[1]);

            for (int j = 0; j <= 4; j++)
            {
                Color c = data.get(i).getGradient().getColor((double) j * 0.25d);

                if (data.get(i).getDrawPoint() == null)
                    Drawer._drawer.draw(g, c, DoubleRound.toInt(xp + j * (args[0] + args[1])),
                            DoubleRound.toInt(yp), args);
                else data.get(i).getDrawPoint().draw(g, c, DoubleRound.toInt(xp + j * (args[0] + args[1])),
                        DoubleRound.toInt(yp), args);
            }

            xp = x + args[0] * 5 + args[1] * 4 + 10.0d;
            yp += font.getSize() / 2.0d;

            Drawer.drawString(g, font, fontColor, ALIGN_LEFT, data.get(i).getName(), xp, yp);
        }

    }

    private static Point getPosition(Rectangle bounds, Point p, Range rangeX, Range rangeY, Range rangeZ)
    {
        double px = (p.getX() - rangeX.left) / (rangeX.right - rangeX.left);
        double x = bounds.x + (double) bounds.width * px;

        double py = (p.getY() - rangeY.left) / (rangeY.right - rangeY.left);
        double y = bounds.y + bounds.height - (double) bounds.height * py;

        double pz = 0.0d;
        if (p.getZ() != null) pz = (p.getZ() - rangeZ.left) / (rangeZ.right - rangeZ.left);

        return new Point(x, y, pz);
    }

    @SuppressWarnings("UnusedParameters")
    private static boolean isPointCorrect(Rectangle bounds, Point p, Range rangeX, Range rangeY, Range rangeZ)
    {
        return rangeX.isInRange(p.getX()) && rangeY.isInRange(p.getY()) && !((p.getZ() != null) && (!rangeZ.isInRange(p.getZ())));
    }

    public static void drawDataSet(Graphics g, Rectangle bounds, DataSet data, Range rangeX, Range rangeY, Range rangeZ,
                                   boolean connect)
    {
        Point prev = null;

        for (Point p : data.getData())
        {
            if (!Drawer.isPointCorrect(bounds, p, rangeX, rangeY, rangeZ))
            {
                prev = null;
                continue;
            }

            Point B = Drawer.getPosition(bounds, p, rangeX, rangeY, rangeZ);

            Color c = data.getGradient().getColor(B.getZ());

            if (data.getDrawPoint() == null) Drawer._drawer.draw(g, c, B.getX(), B.getY(), null);
            else data.getDrawPoint().draw(g, c, B.getX(), B.getY(), null);

            if ((connect) && (prev != null) && (Drawer.isPointCorrect(bounds, prev, rangeX, rangeY, rangeZ)))
            {
                Point A = Drawer.getPosition(bounds, prev, rangeX, rangeY, rangeZ);

                g.drawLine(DoubleRound.toInt(B.getX()), DoubleRound.toInt(B.getY()), DoubleRound.toInt(A.getX()),
                        DoubleRound.toInt(A.getY()));
            }

            prev = p;
        }
    }

    public static void drawBorder(Graphics g, Rectangle bounds, Color color)
    {
        g.setColor(color.getAwtColor());
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public static void drawBackground(Graphics g, Rectangle bounds, Color color)
    {
        g.setColor(color.getAwtColor());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public static void drawDrawArea(Graphics g, Rectangle bounds, Color color)
    {
        g.setColor(color.getAwtColor());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public static void drawGridLines(Graphics g, Rectangle bounds, Color color, int grain)
    {
        g.setColor(color.getAwtColor());

        double xL = bounds.x;
        double xR = bounds.x + bounds.width;
        double yT = bounds.y;
        double yB = bounds.y + bounds.height;

        double dy = bounds.height / (double) grain;
        double dx = bounds.width / (double) grain;

        for (int i = 0; i <= grain; i++)
        {
            // HORIZONTAL
            double y = yT + i * dy;
            g.drawLine(DoubleRound.toInt(xL), DoubleRound.toInt(y), DoubleRound.toInt(xR), DoubleRound.toInt(y));
            // VERTICAL
            double x = xL + i * dx;
            g.drawLine(DoubleRound.toInt(x), DoubleRound.toInt(yB), DoubleRound.toInt(x), DoubleRound.toInt(yT));
        }

    }

    public static void drawString(Graphics g, Font font, Color color, int align, String text, double x, double y)
    {
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(color.getAwtColor());
        g2d.setFont(font);

        FontMetrics fontMetrics = g2d.getFontMetrics();
        int strWidth = fontMetrics.stringWidth(text);

        if (align == ALIGN_RIGHT) x -= strWidth;
        else if (align == ALIGN_CENTER) x -= ((double) (strWidth) / 2.0d);

        g2d.drawString(text, DoubleRound.toInt(x), DoubleRound.toInt(y));
    }

    public static void drawHorizontalAxis(Graphics g, Font font, Color color, Rectangle bounds, int grain, int align,
                                          Range valueRange)
    {
        NumberFormat formatter = new DecimalFormat("0.##E0");

        Graphics2D g2d = (Graphics2D) g;
        g.setColor(color.getAwtColor());
        g2d.setFont(font);

        double xL = bounds.x;
        @SuppressWarnings("unused") double xR = bounds.x + bounds.width;
        double dx = bounds.width / (double) grain;

        double yB = bounds.y + bounds.height;
        double yE = yB + 5.0d;
        double yS = yB + 8.0d + font.getSize();

        double dV = (valueRange.right - valueRange.left) / (double) grain;

        if (align == ALIGN_TOP)
        {
            yB = bounds.y;
            yE = yB - 5.0d;
            yS = yB - 8.0d;
        }

        for (int i = 0; i <= grain; i++)
        {
            double x = xL + dx * i;
            double value = valueRange.left + i * dV;

            g.drawLine(DoubleRound.toInt(x), DoubleRound.toInt(yB), DoubleRound.toInt(x), DoubleRound.toInt(yE));
            Drawer.drawString(g, font, color, ALIGN_CENTER, formatter.format(value), x, yS);
        }
    }

    public static void drawVerticalAxis(Graphics g, Font font, Color color, Rectangle bounds, int grain, int align,
                                        Range valueRange)
    {
        NumberFormat formatter = new DecimalFormat("0.##E0");

        Graphics2D g2d = (Graphics2D) g;
        g.setColor(color.getAwtColor());
        g2d.setFont(font);

        FontMetrics fontMetrics = g2d.getFontMetrics();
        int strWidth = fontMetrics.stringWidth(formatter.format(valueRange.right));

        double yT = bounds.y;
        @SuppressWarnings("unused") double yB = yT + bounds.height;
        double dy = bounds.height / (double) grain;

        double xB = bounds.x;
        double xE = xB - 5.0d;
        double xS = xB - 8.0d - (double) strWidth / 2.0d;

        double dV = (valueRange.right - valueRange.left) / (double) grain;

        if (align == ALIGN_RIGHT)
        {
            xB = bounds.x + bounds.width;
            xE = xB + 5.0d;
            xS = xB + 8.0d + (double) strWidth / 2.0d;
        }

        for (int i = 0; i <= grain; i++)
        {
            double y = yT + dy * i;
            double value = valueRange.right - i * dV;

            g.drawLine(DoubleRound.toInt(xB), DoubleRound.toInt(y), DoubleRound.toInt(xE), DoubleRound.toInt(y));
            Drawer.drawString(g, font, color, ALIGN_CENTER, formatter.format(value), xS,
                    y + (double) font.getSize() / 2.0d);
        }
    }


}
