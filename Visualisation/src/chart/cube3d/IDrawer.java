package chart.cube3d;

import dataset.DataSet;
import draw.color.Color;
import standard.Range;

import javax.media.opengl.GL2;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTomczyk on 06.01.2016.
 */
public interface IDrawer
{
    void draw(GL2 gl, ArrayList<DataSet> dataSet, Range rx, Range ry, Range rz, HashMap<String, Color> color);
}
