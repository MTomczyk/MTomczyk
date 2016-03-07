package chart.cube3d;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import dataset.DataSet;
import draw.Schema;
import draw.color.Color;
import utils.DoubleRound;
import standard.Range;


public class Cube3D extends JFrame implements MouseMotionListener, MouseListener, MouseWheelListener
{
    protected String colorProperties[] = {"BACKGROUND", "BORDER"};
    protected HashMap<String, Color> color = new HashMap<>();

    private Listener glEventListener = null;

    private Double X = null;
    private Double Y = null;


    public Cube3D(Range rX, Range rY, Range rZ, Schema schema)
    {
        this.color.put("BACKGROUND", new Color(255.0d, 255.0d, 255.0d, 255.0d));
        this.color.put("BORDER", new Color(0.0d, 0.0d, 0.0d, 255.0d));

        for (String s : this.colorProperties)
        {
            if ((schema != null) && (schema.color.containsKey(s))) this.color.put(s, schema.color.get(s));
        }

        if (rX == null) rX = new Range(0.0d, 1.0d);
        if (rY == null) rY = new Range(0.0d, 1.0d);
        if (rZ == null) rZ = new Range(0.0d, 1.0d);

        setTitle("Cube3D");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        setSize(DoubleRound.toInt(width * 0.75d), DoubleRound.toInt(height * 0.75d));
        setLocationRelativeTo(null);

        this.glEventListener = new Listener(rX, rY, rZ, color);

        GLCanvas glCanvas = new GLCanvas();

        glCanvas.addGLEventListener(glEventListener);
        add(glCanvas);

        glCanvas.addMouseListener(this);
        glCanvas.addMouseMotionListener(this);
        glCanvas.addMouseWheelListener(this);

        final FPSAnimator animator = new FPSAnimator(glCanvas, 180);
        addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e)
            {
                animator.stop();
                System.exit(0);
            }

        });

        animator.start();
    }

    public void setDataSet(ArrayList<DataSet> dataSet)
    {
        this.glEventListener.setDataSet(dataSet);
    }

    private static final long serialVersionUID = 1L;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        double change = (double) e.getWheelRotation() / 5.0d;
        double val = this.glEventListener.getZoom() + change;
        this.glEventListener.setZoom(val);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        X = null;
        Y = null;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        X = null;
        Y = null;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        X = null;
        Y = null;
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        X = null;
        Y = null;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        double x = e.getX();
        double y = e.getY();

        if (X != null)
        {
            double dx = x - X;
            double dy = y - Y;

            double oldX = this.glEventListener.getRotateX();
            double oldY = this.glEventListener.getRotateY();

            double newX = oldX + dx;
            if (newX > 360.0d) newX -= 360.0d;
            if (newX < 0.0d) newX += 360.0d;

            double newY = oldY + dy;
            if (newY > 360.0d) newY -= 360.0d;
            if (newY < 0.0d) newY += 360.0d;

            this.glEventListener.setRotateX(newX);
            this.glEventListener.setRotateY(newY);

        }

        X = x;
        Y = y;
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        X = null;
        Y = null;
    }

    @SuppressWarnings("unused")
    public ArrayList<IDrawer> getAdditionalDrawers()
    {
        return glEventListener.getAdditionalDrawers();
    }

    @SuppressWarnings("unused")
    public void setAdditionalDrawers(ArrayList<IDrawer> drawers)
    {
        this.glEventListener.setAdditionalDrawers(drawers);
    }

}