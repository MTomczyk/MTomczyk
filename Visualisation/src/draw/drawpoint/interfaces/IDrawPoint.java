package draw.drawpoint.interfaces;

import draw.color.Color;

import java.awt.Graphics;

/**
 * Created by Michał on 2014-10-08.
 *
 */
public interface IDrawPoint
{
    void draw(Graphics g, Color color, double x, double y, double[] args);
}
