package chart.chart2d;

import draw.Schema;
import draw.color.Color;

import java.util.HashMap;

/**
 * Created by Michał on 2014-10-09.
 *
 */
public class WhiteSchema extends Schema
{
    public WhiteSchema()
    {
        this.color = new HashMap<>(6);
        this.color.put("BACKGROUND", new Color(255.0d, 255.0d, 255.0d, 255.0d));
        this.color.put("DRAWAREA", new Color(250.0d, 250.0d, 250.0d, 255.0d));
        this.color.put("BORDER", new Color(0.0d, 0.0d, 0.0d, 255.0d));
        this.color.put("MAINGRIDLINE", new Color(100.0d, 100.0d, 100.0d, 255.0d));
        this.color.put("AUXILIARYGRIDLINE", new Color(160.0d, 160.0d, 160.0d, 255.0d));
        this.color.put("FONTCOLOR", new Color(0.0d, 0.0d, 0.0d, 255.0d));
    }

}
