package draw;

import draw.color.Color;

import java.util.HashMap;

/**
 * Created by Michał on 2014-10-08.
 *
 */
public class Schema
{
    public HashMap<String, Color> color = null;

    public Schema()
    {

    }

    @SuppressWarnings("unused")
    public Schema(HashMap<String, Color> color)
    {
        this.color = color;
    }

}
