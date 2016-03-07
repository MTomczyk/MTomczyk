package chart.cube3d;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.gl2.GLUgl2;

import dataset.DataSet;
import draw.color.Color;
import utils.DoubleRound;
import standard.Range;

public class Listener implements GLEventListener
{
    private GLUgl2 glu = new GLUgl2();

    private double zoom = 2.0d;
    private double rotateX = 0.0d;
    private double rotateY = 0.0d;

    private ArrayList<DataSet> dataSet = null;
    private Range rx = null;
    private Range ry = null;
    private Range rz = null;
    private HashMap<String, Color> color = null;

    private IDrawer _baseDrawer = new Drawer();
    private ArrayList<IDrawer> _additionalDrawers = null;

    public Listener(Range rx, Range ry, Range rz, HashMap<String, Color> color)
    {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.color = color;
    }

    @Override
    public void display(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        Color bg = this.color.get("BACKGROUND");

        gl.glClearColor(DoubleRound.toInt(bg.r) / 255.0f, DoubleRound.toInt(bg.g) / 255.0f, DoubleRound.toInt(bg.b) / 255.0f, 255);
        gl.glLoadIdentity();

        glu.gluLookAt(0f, 0f, zoom, 0f, 0f, 0f, 0f, 1f, 0f);

        gl.glRotatef((float) rotateX, 0f, 1f, 0f);
        gl.glRotatef((float) rotateY, 1f, 0f, 0f);
        _baseDrawer.draw(gl, dataSet, rx, ry, rz, color);

        if (_additionalDrawers != null)
        {
            for (IDrawer d: _additionalDrawers)
                d.draw(gl, null, rx, ry, rz, color);
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable)
    {
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glClearDepthf(1.0f);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(x, y, width, height);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0f, (float) width / height, 1f, 10f);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public double getZoom()
    {
        return zoom;
    }

    public void setZoom(double zoom)
    {
        if (zoom < 1.0d) this.zoom = 1.0d;
        else if (zoom > 3.0d) this.zoom = 3.0d;
        else this.zoom = zoom;
    }

    public double getRotateX()
    {
        return rotateX;
    }

    public void setRotateX(double rotateX)
    {
        this.rotateX = rotateX;
    }

    public double getRotateY()
    {
        return rotateY;
    }

    public void setRotateY(double rotateY)
    {
        this.rotateY = rotateY;
    }

    @SuppressWarnings("unused")
    public ArrayList<DataSet> getDataSet()
    {
        return dataSet;
    }

    public void setDataSet(ArrayList<DataSet> dataSet)
    {
        this.dataSet = dataSet;
    }

    public ArrayList<IDrawer> getAdditionalDrawers()
    {
        return _additionalDrawers;
    }

    public void setAdditionalDrawers(ArrayList<IDrawer> drawers)
    {
        this._additionalDrawers = drawers;
    }
}