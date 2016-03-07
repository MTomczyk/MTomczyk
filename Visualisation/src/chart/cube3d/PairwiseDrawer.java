package chart.cube3d;

import dataset.DataSet;
import draw.color.Color;
import standard.Point;
import standard.Range;

import javax.media.opengl.GL2;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTomczyk on 06.01.2016.
 */
public class PairwiseDrawer implements IDrawer
{
    public ArrayList<DataSet> _dataSet = null;

    @Override
    public void draw(GL2 gl, ArrayList<DataSet> dataSet, Range rx, Range ry, Range rz, HashMap<String, Color> color)
    {


        if (_dataSet == null) return;

        for (DataSet ds : _dataSet)
        {
            Point pB = ds.getData().get(0);

            double pxB = (pB.getX() - rx.left) / (rx.right - rx.left);
            double pyB = (pB.getY() - ry.left) / (ry.right - ry.left);
            double pzB = (pB.getZ() - rz.left) / (rz.right - rz.left);

            Point pW = ds.getData().get(1);

            double pxW = (pW.getX() - rx.left) / (rx.right - rx.left);
            double pyW = (pW.getY() - ry.left) / (ry.right - ry.left);
            double pzW = (pW.getZ() - rz.left) / (rz.right - rz.left);

            gl.glPointSize(1.0f);
            gl.glColor3f(0.0f, 0.0f, 1.0f);

            gl.glBegin(GL2.GL_LINES);
            gl.glVertex3f((float) pxB - 0.5f, (float) pyB - 0.5f, -(float) pzB + 0.5f);
            gl.glVertex3f((float) pxW - 0.5f, (float) pyW - 0.5f, -(float) pzW + 0.5f);
            gl.glEnd();

            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glPointSize(10.0f);

            gl.glBegin(GL2.GL_POINTS);
            gl.glVertex3f((float) pxW - 0.5f, (float) pyW - 0.5f, -(float) pzW + 0.5f);
            gl.glEnd();

            gl.glColor3f(0.0f, 1.0f, 0.0f);
            gl.glPointSize(10.0f);

            gl.glBegin(GL2.GL_POINTS);
            gl.glVertex3f((float) pxB - 0.5f, (float) pyB - 0.5f, -(float) pzB + 0.5f);
            gl.glEnd();
        }
    }
}
