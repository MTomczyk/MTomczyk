package chart.cube3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import dataset.DataSet;
import draw.color.Gradient;
import standard.Point;
import standard.Range;

public class Drawer implements IDrawer
{
	public static void drawDataSet(GL2 gl, ArrayList<DataSet> dataSet, Range rx, Range ry, Range rz)
	{
		if (dataSet == null)
			return;

		for (int i = 0; i < dataSet.size(); i++)
		{
			DataSet ds = dataSet.get(i);

			for (int j = 0; j < ds.getData().size(); j++)
			{
				Point p = ds.getData().get(j);

				double px = (p.getX() - rx.left) / (rx.right - rx.left);
				double py = (p.getY() - ry.left) / (ry.right - ry.left);
				double pz = (p.getZ() - rz.left) / (rz.right - rz.left);
				
				if (px > 1.0d) continue;
				if (py > 1.0d) continue;
				if (pz > 1.0d) continue;
				if (px < 0.0d) continue;
				if (py < 0.0d) continue;
				if (pz < 0.0d) continue;
				
				Gradient g = ds.getGradient();
				Color c = g.getColor(pz).getAwtColor();
				gl.glColor3f((float) c.getRed() / 255.0f, (float) c.getGreen() / 255.0f,
						(float) c.getBlue() / 255.0f);

				//gl.glPointSize(1.0f + 5.0f * (1.0f - (float) pz));
                gl.glPointSize(5.0f);

				gl.glBegin(GL2.GL_POINTS);
				gl.glVertex3f((float) px - 0.5f, (float) py - 0.5f, -(float) pz + 0.5f);
				gl.glEnd();

				// GREY ACC TO DATA SET
				float gray = 0.8f;
				
				gray -= (0.5f * (float)i/(float)dataSet.size());
				
				gl.glColor3f(gray, gray, gray);
				gl.glBegin(GL2.GL_POINTS);
				gl.glVertex3f(0.5f, (float) py - 0.5f, -(float) pz + 0.5f);
				gl.glVertex3f((float) px - 0.5f, 0.5f, -(float) pz + 0.5f);
				gl.glVertex3f((float) px - 0.5f, (float) py - 0.5f, -0.5f);
				gl.glEnd();
			}

		}

	}

	@Override
	public void draw(GL2 gl, ArrayList<DataSet> dataSet, Range rx, Range ry, Range rz, HashMap<String, draw.color.Color> color)
	{
        draw.color.Color bo = color.get("BORDER");

		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f((float)bo.r, (float)bo.g, (float)bo.b);

		gl.glVertex3f(-0.5f, +0.5f, +0.5f);
		gl.glVertex3f(+0.5f, +0.5f, +0.5f);

		gl.glVertex3f(-0.5f, -0.5f, +0.5f);
		gl.glVertex3f(+0.5f, -0.5f, +0.5f);

		gl.glVertex3f(-0.5f, +0.5f, -0.5f);
		gl.glVertex3f(+0.5f, +0.5f, -0.5f);

		gl.glVertex3f(-0.5f, -0.5f, -0.5f);
		gl.glVertex3f(+0.5f, -0.5f, -0.5f);

		gl.glVertex3f(+0.5f, -0.5f, +0.5f);
		gl.glVertex3f(+0.5f, +0.5f, +0.5f);

		gl.glVertex3f(-0.5f, -0.5f, +0.5f);
		gl.glVertex3f(-0.5f, +0.5f, +0.5f);

		gl.glVertex3f(+0.5f, -0.5f, -0.5f);
		gl.glVertex3f(+0.5f, +0.5f, -0.5f);

		gl.glVertex3f(-0.5f, -0.5f, -0.5f);
		gl.glVertex3f(-0.5f, +0.5f, -0.5f);

		gl.glVertex3f(+0.5f, +0.5f, -0.5f);
		gl.glVertex3f(+0.5f, +0.5f, +0.5f);

		gl.glVertex3f(-0.5f, +0.5f, -0.5f);
		gl.glVertex3f(-0.5f, +0.5f, +0.5f);

		gl.glVertex3f(+0.5f, -0.5f, -0.5f);
		gl.glVertex3f(+0.5f, -0.5f, +0.5f);

		gl.glVertex3f(-0.5f, -0.5f, -0.5f);
		gl.glVertex3f(-0.5f, -0.5f, +0.5f);

		gl.glEnd();

		Drawer.drawDataSet(gl, dataSet, rx, ry, rz);
	}

}