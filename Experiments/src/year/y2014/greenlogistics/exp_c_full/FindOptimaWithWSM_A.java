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
import year.y2014.greenlogistics.A.DataA;
import year.y2014.greenlogistics.lp.Constraints_A;
import year.y2014.greenlogistics.lp.WSM_A;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 09.11.2015.
 */
public class FindOptimaWithWSM_A
{


    public static void main(String[] args)
    {
        DataA data = new DataA();
        long startTime = System.nanoTime();

        ArrayList<Integer> pCost = new ArrayList<Integer>(100);
        ArrayList<Integer> pCO2 = new ArrayList<Integer>(100);
        ArrayList<Integer> pPM = new ArrayList<Integer>(100);

        for (int i = 0; i < 100; i++)
        {
            //System.out.p
            double res[] = WSM_A.getResultForWeights(weights_3d.data[i][0],weights_3d.data[i][1],weights_3d.data[i][2]);

            if (res != null)
            {
                pCost.add((int)res[0]);
                pCO2.add((int)res[1]);
                pPM.add((int)res[2]);
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
        //1047988.73	571223.026	14803.36
        //843633.68	535039.184	2712.256
        //204355.0496	36183.842	12091.104

        Cube3D cube = new Cube3D(new Range(843633.68, 1047988.73), new Range(535039.184, 571223.026)
                , new Range(2712.256, 14803.36), new WhiteSchema());
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
