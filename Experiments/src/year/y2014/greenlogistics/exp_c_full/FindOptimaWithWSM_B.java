package year.y2014.greenlogistics.exp_c_full;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Specimen;
import chart.cube3d.Cube3D;
import chart.cube3d.WhiteSchema;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import dataset.DataSet;
import draw.color.gradients.RedBlue;
import interfaces.ISpecimen;
import net.sf.javailp.*;
import patterns.weights.weights_3d;
import standard.Point;
import standard.Range;
import year.y2014.greenlogistics.B.DataB;
import year.y2014.greenlogistics.lp.Constraints_B;


import java.util.ArrayList;

/**
 * Created by MTomczyk on 09.11.2015.
 */
public class FindOptimaWithWSM_B
{

    public static void main(String[] args)
    {
        DataB data = new DataB();
        long startTime = System.nanoTime();

        ArrayList<Integer> pCost = new ArrayList<Integer>(100);
        ArrayList<Integer> pCO2 = new ArrayList<Integer>(100);
        ArrayList<Integer> pPM = new ArrayList<Integer>(100);

        for (int i = 0; i < 100; i++)
        {
            double w1 = weights_3d.data[i][0] / (131.3d * 1000.0d);
            double w2 = weights_3d.data[i][1] / (83.5d * 1000.0d);
            double w3 = weights_3d.data[i][2] / (23.2d * 1000.0d);

            Problem problem = new Problem();
            Constraints_B.addWSMObjective(problem, data, w1, w2, w3, 0.0d, 0.0d, 0.0d,
                    0.0d, 1000000000.0d, 0.0d, 1000000000.0d, 0.0d, 1000000000.0d);

            Constraints_B.addConstraints(problem, data);


            SolverFactory factory = new SolverFactoryLpSolve();
            factory.setParameter(Solver.VERBOSE, 0);
            factory.setParameter(Solver.TIMEOUT, 500);
            Solver solver = factory.get();
            Result result = solver.solve(problem);

            if (result != null)
            {
                double e[] = Constraints_B.eval(result, data);
                if (e[0] > 1.0d)
                {

                    pCost.add((int) (e[0]));
                    pCO2.add((int) (e[1]));
                    pPM.add((int) (e[2]));
                }
            }

        }

        System.out.printf("-------------- \n");

        long endTime = System.nanoTime();
        System.out.println("Took " + (endTime - startTime) + " ns");

        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);
        ArrayList<ISpecimen> pareto = new ArrayList<ISpecimen>();

        for (int i = 0; i < 100; i++)
        {
            String str = String.format("{" + pCost.get(i) + ".0d , " + pCO2.get(i) + ".0d , " + pPM.get(i) + ".0d },");
            System.out.println(str);
            double e[] = {pCost.get(i), pCO2.get(i), pPM.get(i)};
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(e, criteria);
            ISpecimen s = new Specimen("S", criteria);
            s.setAlternative(a);
            pareto.add(s);
        }
        System.out.println(pareto.size());
        drawAll(pareto, criteria);
    }

    public static void drawAll(ArrayList<ISpecimen> pareto, ArrayList<ICriterion> criteria)
    {
        Cube3D cube = new Cube3D(new Range(825500.0f, 956800.0f), new Range(537900.0f, 621400.0f)
                , new Range(4400.0f, 27600.0f), new WhiteSchema());
        ArrayList<Point> points = new ArrayList<Point>(pareto.size());
        for (ISpecimen s : pareto)
        {
            Point p = new Point(s.getAlternative().getEvaluationAt(criteria.get(0)),
                    s.getAlternative().getEvaluationAt(criteria.get(1)),
                    s.getAlternative().getEvaluationAt(criteria.get(2)));
            points.add(p);
        }

        DataSet ds = new DataSet(points);
        ds.setGradient(new RedBlue());
        ArrayList<DataSet> ads = new ArrayList<DataSet>();
        ads.add(ds);

        cube.setDataSet(ads);
        cube.setVisible(true);
    }

}
