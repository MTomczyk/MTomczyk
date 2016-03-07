package runner.drawer;


import java.util.ArrayList;

import chart.chart2d.BlackSchema;
import chart.cube3d.Cube3D;
import chart.cube3d.WhiteSchema;
import criterion.interfaces.ICriterion;
import dataset.DataSet;
import draw.color.Color;
import draw.color.Gradient;
import runner.interfaces.IRunnerDrawer;
import standard.Point;
import standard.Range;
import interfaces.IGenetic;
import interfaces.ISpecimen;

/**
 * Cube drawer. It can be used to visualize evolutionary algorithms with 3-objective problem.
 * It draws only pareto points.
 */
public class CubePareto implements IRunnerDrawer
{
    public ArrayList<Gradient> gradient = new ArrayList<>(10);
    public ArrayList<DataSet> dataSet = null;
    public Cube3D cube = null;

    public boolean _fromPareto = false;

    public CubePareto()
    {

    }

    public CubePareto(boolean fromPareto)
    {
        this._fromPareto = fromPareto;
    }

    /**
     * Init cube.
     * @param genetic Array of genetic algorithms.
     * @param criterion Array of criteria.
     */
    @Override
    public void init(ArrayList<IGenetic> genetic, ArrayList<ICriterion> criterion)
    {
        // --- PREPARE COLORS
        gradient.add(new Gradient(new Color(255.0d, 0.0d, 0.0d, 255.0d)));
        gradient.add(new Gradient(new Color(0.0d, 255.0d, 0.0d, 255.0d)));
        gradient.add(new Gradient(new Color(0.0d, 0.0d, 255.0d, 255.0d)));
        gradient.add(new Gradient(new Color(255.0d, 255.0d, 0.0d, 255.0d)));
        gradient.add(new Gradient(new Color(0.0d, 255.0d, 255.0d, 255.0d)));
        gradient.add(new Gradient(new Color(255.0d, 0.0d, 255.0d, 255.0d)));
        gradient.add(new Gradient(new Color(60.0d, 120.0d, 180.0d, 255.0d)));
        gradient.add(new Gradient(new Color(120.0d, 60.0d, 180.0d, 255.0d)));
        gradient.add(new Gradient(new Color(180.0d, 120.0d, 60.0d, 255.0d)));
        // --- PREPARE CUBE
        this.dataSet = new ArrayList<>(genetic.size());
        for (int i = 0; i < genetic.size(); i++)
            this.dataSet.add(null);

        Range range = criterion.get(0).getRange().get("display");
        Range rx = new Range(range.left, range.right);
        range = criterion.get(1).getRange().get("display");
        Range ry = new Range(range.left, range.right);
        range = criterion.get(2).getRange().get("display");
        Range rz = new Range(range.left, range.right);

        cube = new Cube3D(rx, ry, rz, new WhiteSchema());
    }

    /**
     * Update cube.
     * @param genetic Array of genetic algorithms.
     * @param criterion Array of criteria.
     */
    @Override
    public void update(ArrayList<IGenetic> genetic, ArrayList<ICriterion> criterion)
    {
        for (int i = 0; i < genetic.size(); i++)
        {
            ArrayList<ISpecimen> pareto = null;
            if (_fromPareto) pareto = genetic.get(i).getPareto();
            else pareto = genetic.get(i).getToDraw();
            ArrayList<Point> points = new ArrayList<>(pareto.size());

            for (ISpecimen s : pareto)
            {
                Point p = null;
                if (criterion.size() == 1) p = new Point(s.getAlternative().getEvaluationAt(criterion.get(0)));
                else if (criterion.size() == 2) p = new Point(s.getAlternative().getEvaluationAt(criterion.get(0)),
                        s.getAlternative().getEvaluationAt(criterion.get(1)));
                else if (criterion.size() == 3) p = new Point(s.getAlternative().getEvaluationAt(criterion.get(0)),
                        s.getAlternative().getEvaluationAt(criterion.get(1)),
                        s.getAlternative().getEvaluationAt(criterion.get(2)));
                points.add(p);
            }

            this.dataSet.set(i, new DataSet(points, gradient.get(i)));
            this.cube.setDataSet(dataSet);
        }
    }

    /**
     * Direct update if you want to draw some other points. For each of data set it needs to be an genetic algorithm.
     * @param genetic Array of genetic algorithms.
     * @param specimen Array of set of specimens for each each g. algorithm.
     * @param criterion Array of criteria.
     */
    @Override
    public void updateDirect(ArrayList<IGenetic> genetic, ArrayList<ArrayList<ISpecimen>> specimen,
                             ArrayList<ICriterion> criterion)
    {
        for (int i = 0; i < specimen.size(); i++)
        {
            ArrayList<ISpecimen> pareto = specimen.get(i);
            ArrayList<Point> points = new ArrayList<>(pareto.size());

            for (ISpecimen s : pareto)
            {
                Point p = null;
                if (criterion.size() == 1) p = new Point(s.getAlternative().getEvaluationAt(criterion.get(0)));
                else if (criterion.size() == 2) p = new Point(s.getAlternative().getEvaluationAt(criterion.get(0)),
                        s.getAlternative().getEvaluationAt(criterion.get(1)));
                else if (criterion.size() == 3) p = new Point(s.getAlternative().getEvaluationAt(criterion.get(0)),
                        s.getAlternative().getEvaluationAt(criterion.get(1)),
                        s.getAlternative().getEvaluationAt(criterion.get(2)));
                points.add(p);
            }

            this.dataSet.set(i, new DataSet(points, gradient.get(i)));
            this.cube.setDataSet(dataSet);
        }
    }

    /**
     * Makes cube visible.
     */
    @Override
    public void start()
    {
        cube.setVisible(true);
    }

    /**
     * Hides cube.
     */
    @Override
    public void stop()
    {
        cube.setVisible(false);
    }

}
