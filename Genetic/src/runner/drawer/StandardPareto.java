package runner.drawer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import chart.ChartFrame;
import chart.Drawer;
import chart.chart2d.BlackSchema;
import chart.chart2d.Chart2D;
import criterion.interfaces.ICriterion;
import dataset.DataSet;
import draw.color.Color;
import draw.color.Gradient;
import interfaces.IGenetic;
import interfaces.ISpecimen;
import runner.interfaces.IRunnerDrawer;
import standard.Point;
import standard.Range;

/**
 * Standard drawer. It can be used to visualize evolutionary algorithms.
 * It produce chart for each different pair of criterion.
 * It draws only pareto points.
 */
public class StandardPareto implements IRunnerDrawer
{
    private ArrayList<ArrayList<ArrayList<DataSet>>> dataSet = null;

    private ChartFrame baseFrame[][];
    public ArrayList<Gradient> gradient = new ArrayList<>(10);

    /**
     * Init drawer.
     * @param genetic Array of genetic algorithms.
     * @param criterion Array of criteria.
     */
    @Override
    public void init(ArrayList<IGenetic> genetic, ArrayList<ICriterion> criterion)
    {

        // --- PREPARE COLORS
        gradient.add(new Gradient(new Color(255.0d, 0.0d, 0.0d, 255.0d)));
        gradient.add(new Gradient(new Color(0.0d, 255.0d, 0.0d, 255.0d)));
        gradient.add(new Gradient(new Color(100.0d, 100.0d, 255.0d, 255.0d)));
        gradient.add(new Gradient(new Color(255.0d, 255.0d, 0.0d, 255.0d)));
        gradient.add(new Gradient(new Color(0.0d, 255.0d, 255.0d, 255.0d)));
        gradient.add(new Gradient(new Color(255.0d, 0.0d, 255.0d, 255.0d)));

        gradient.add(new Gradient(new Color(123.0d, 255.0d, 0.0d, 255.0d)));
        gradient.add(new Gradient(new Color(0.0d, 255.0d, 123.0d, 255.0d)));
        gradient.add(new Gradient(new Color(255.0d, 123.0d, 255.0d, 255.0d)));
        gradient.add(new Gradient(new Color(123.0d, 0.0d, 255.0d, 255.0d)));


        // --- PREPARE CHARTS
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        double margin = 100.0d;
        int c = criterion.size();

        this.dataSet = new ArrayList<>(c);
        this.baseFrame = new ChartFrame[c][c];

        double h = (height - 2 * margin) / (double) (c);
        double w = (width - 2 * margin) / (double) (c);

        for (int i = 0; i < c; i++)
        {
            this.dataSet.add(new ArrayList<>(c - 1));

            for (int j = 0; j < c; j++)
            {
                if (i == j)
                {
                    this.dataSet.get(i).add(null);
                    continue;
                }

                this.dataSet.get(i).add(new ArrayList<>(genetic.size()));

                for (int k = 0; k < genetic.size(); k++)
                    this.dataSet.get(i).get(j).add(null);

                Range rA = criterion.get(i).getRange().get("display");
                Range rB = criterion.get(j).getRange().get("display");

                Chart2D hMap = new Chart2D(new BlackSchema(), rA, rB, new Range(0.0d, 1.0d));
                hMap.setLegendPosition(Drawer.LEGEND_TOP + Drawer.LEGEND_RIGHT);

                double x = margin + (j) * w;
                double y = margin + (i) * h;

                ChartFrame bFrame = new ChartFrame((int) w, (int) h, hMap);
                bFrame.setLocation((int) x, (int) y);

                baseFrame[i][j] = bFrame;
            }
        }
    }

    /**
     * Update drawer.
     * @param genetic Array of genetic algorithms.
     * @param criterion Array of criteria.
     */
    @Override
    public void update(ArrayList<IGenetic> genetic, ArrayList<ICriterion> criterion)
    {
        for (int i = 0; i < genetic.size(); i++)
        {
            ArrayList<ISpecimen> pareto = genetic.get(i).getToDraw();

            for (int j = 0; j < criterion.size(); j++)
            {
                for (int k = 0; k < criterion.size(); k++)
                {
                    if (j == k) continue;

                    ArrayList<Point> points = new ArrayList<>(pareto.size());

                    for (ISpecimen s : pareto)
                    {
                        Point p = new Point(s.getAlternative().getEvaluationAt(criterion.get(j)),
                                s.getAlternative().getEvaluationAt(criterion.get(k)), 0.0d);
                        points.add(p);
                    }
                    dataSet.get(j).get(k).set(i, new DataSet(points, gradient.get(i), null, genetic.get(i).getName()));

                    Chart2D hMap = (Chart2D) baseFrame[j][k].getChart();

                    hMap.setData(dataSet.get(j).get(k));
                    hMap.redraw(false);

                    baseFrame[j][k].setChart(hMap);
                }
            }

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

            for (int j = 0; j < criterion.size(); j++)
            {
                for (int k = 0; k < criterion.size(); k++)
                {
                    if (j == k) continue;

                    ArrayList<Point> points = new ArrayList<>(pareto.size());

                    for (ISpecimen s : pareto)
                    {
                        Point p = new Point(s.getAlternative().getEvaluationAt(criterion.get(j)),
                                s.getAlternative().getEvaluationAt(criterion.get(k)), 0.0d);
                        points.add(p);
                    }

                    if (dataSet.get(j) == null) System.out.println("A");
                    if (dataSet.get(j).get(k) == null) System.out.println("B");
                    if (gradient.get(i) == null) System.out.println("C");
                    if (genetic.get(i) == null) System.out.println("D");


                    dataSet.get(j).get(k).set(i,
                            new DataSet(points, gradient.get(i), null, genetic.get(i).getName()));

                    Chart2D hMap = (Chart2D) baseFrame[j][k].getChart();

                    hMap.setData(dataSet.get(j).get(k));
                    hMap.redraw(false);

                    baseFrame[j][k].setChart(hMap);
                }
            }

        }
    }


    /**
     * Makes charts visible.
     */
    @Override
    public void start()
    {
        for (int i = 0; i < baseFrame.length; i++)
            for (int j = 0; j < baseFrame[i].length; j++)
                if (i != j) baseFrame[i][j].setVisible(true);
    }

    /**
     * Hide charts.
     */
    @Override
    public void stop()
    {
        for (int i = 0; i < baseFrame.length; i++)
            for (int j = 0; j < baseFrame[i].length; j++)
                if (i != j) baseFrame[i][j].setVisible(false);
    }

}
