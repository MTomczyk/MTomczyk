package chart.cube3d;

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
        this.color = new HashMap<>(2);
        this.color.put("BACKGROUND", new Color(43.0d, 43.0d, 43.0d, 255.0d));
        this.color.put("BORDER", new Color(85.0d, 85.0d, 85.0d, 255.0d));
    }

}