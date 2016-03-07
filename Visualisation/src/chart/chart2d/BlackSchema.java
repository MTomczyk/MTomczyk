package chart.chart2d;

import draw.Schema;
import draw.color.Color;

import java.util.HashMap;

/**
 * Created by Michał on 2014-10-09.
 *
 */
public class BlackSchema extends Schema
{
    public BlackSchema()
    {
        this.color = new HashMap<>(6);
        this.color.put("BACKGROUND", new Color(49.0d, 51.0d, 54.0d, 255.0d));
        this.color.put("DRAWAREA", new Color(43.0d, 43.0d, 43.0d, 255.0d));
        this.color.put("BORDER", new Color(85.0d, 85.0d, 85.0d, 255.0d));
        this.color.put("MAINGRIDLINE", new Color(85.0d, 85.0d, 85.0d, 255.0d));
        this.color.put("AUXILIARYGRIDLINE", new Color(45.0d, 45.0d, 45.0d, 255.0d));
        this.color.put("FONTCOLOR", new Color(187.0d, 187, 187, 255.0d));
    }

}