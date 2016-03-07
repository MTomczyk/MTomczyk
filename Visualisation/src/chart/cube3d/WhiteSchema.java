package chart.cube3d;

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
        this.color.put("BORDER", new Color(0.0d, 0.0d, 0.0d, 255.0d));
    }

}
