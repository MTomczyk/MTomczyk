package year.y2014.greenlogistics.lp.adptative;

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
import sort.functions.Front;
import standard.Common;
import standard.Point;
import standard.Range;
import tree.binary.BinaryTree;
import utils.BoxExtractor;
import utils.LPBox;
import year.y2014.greenlogistics.B.DataB;
import year.y2014.greenlogistics.lp.Constraints_B;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by MTomczyk on 18.11.2015.
 */
public class adaptiveWSM_B
{
    public static double eps = Common.EPSILON;

    public static void main(String args[])
    {
        long beginTime = System.currentTimeMillis();

        LPBox b0 = initStartingBox();
        b0.print();

        double zi[] = b0.lower.clone();
        double zm[] = b0.upper.clone();
        double minA[] = {(zm[0] - zi[0]) / 65.0d, (zm[1] - zi[1]) / 65.0d, (zm[2] - zi[2]) / 65.0d};
        double maxD[] = {(zm[0] - zi[0]) / 65.0d, (zm[1] - zi[1]) / 65.0d, (zm[2] - zi[2]) / 65.0d};

        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);
        ArrayList<ISpecimen> pareto = new ArrayList<ISpecimen>();
        LinkedList<LPBox> boxes = new LinkedList<LPBox>();
        boxes.add(b0);

        int solverRuns = 0;

        while (!boxes.isEmpty())
        {
            LPBox box = boxes.getFirst();
            LPBox tmpBox = box.getClone();
            tmpBox.lower = zi.clone();

            ISpecimen zs;
            if ((tmpBox.upper[0] - tmpBox.lower[0] - minA[0] - maxD[0] < 0.0d)
                || (tmpBox.upper[1] - tmpBox.lower[1] - minA[1] - maxD[1] < 0.0d)
                || (tmpBox.upper[2] - tmpBox.lower[2] - minA[2] - maxD[2] < 0.0d))
                zs = null;
            else
            {
                zs = getOptimalSolution(tmpBox, criteria, minA, maxD);
                solverRuns ++;
            }


            if (zs == null)
            {
                boxes.removeFirst();
                continue;
            }
            pareto.add(zs);
            System.out.println(pareto.size());
            if (pareto.size() == 1000) break;

            LinkedList<LPBox> newBoxes = new LinkedList<LPBox>();
            for (LPBox b : boxes) newBoxes.add(b);

            ArrayList<LinkedList<LPBox>> partial = generateNewBoxesVSplit(boxes,
                    zs.getAlternative().getEvaluationVector(criteria), zi, newBoxes);
            updateIndividualSubsets(partial, newBoxes,
                    zs.getAlternative().getEvaluationVector(criteria), zi, zm);
            boxes = newBoxes;
        }

        System.out.println("Pareto: " + pareto.size() + " Solver Runs: " + solverRuns);
        drawAll(pareto, criteria);

        long endTime = System.currentTimeMillis();
        System.out.println("Took: " + (endTime - beginTime) / 1000.0d);

        /*for (ISpecimen s: pareto)
        {
            System.out.println(s.getAlternative().getEvaluationAt(criteria.get(0)) + " " +
                    s.getAlternative().getEvaluationAt(criteria.get(1)) + " " +
                    s.getAlternative().getEvaluationAt(criteria.get(2)));
        }*/
        {
            double e[] = {825416.76,	572868.88,	27657.888};
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(e, criteria);
            ISpecimen dS = new Specimen("S", criteria);
            dS.setAlternative(a);
            pareto.add(dS);
        }
        {
            double e[] = {875437.6526,	537868.52,	13175.4864};
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(e, criteria);
            ISpecimen dS = new Specimen("S", criteria);
            dS.setAlternative(a);
            pareto.add(dS);
        }
        {
            double e[] = {969039.5837,	621484.52,	4610.8352};
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(e, criteria);
            ISpecimen dS = new Specimen("S", criteria);
            dS.setAlternative(a);
            pareto.add(dS);
        }

        // COST-PM
        {
            ArrayList<ICriterion> costPM = Criterion.getCriterionArray("C", 2, false);
            ArrayList<ISpecimen> costPMSpec = new ArrayList<ISpecimen>(pareto.size());
            for (ISpecimen s: pareto)
            {
                double e[] = {s.getAlternative().getEvaluationAt(criteria.get(0)),
                        s.getAlternative().getEvaluationAt(criteria.get(2))};
                IAlternative a = new Alternative("A", costPM);
                a.setEvaluationVector(e, costPM);
                ISpecimen dS = new Specimen("S", costPM);
                dS.setAlternative(a);
                costPMSpec.add(dS);
            }
            ArrayList<ISpecimen> p = Front.getPareto(costPMSpec, costPM, Common.EPSILON);
            for (ISpecimen s: p)
            {
                System.out.println(s.getAlternative().getEvaluationAt(costPM.get(0)) + " " +
                        s.getAlternative().getEvaluationAt(costPM.get(1)));
            }
        }
        System.out.println("-----------------------");
        // COST-CO
        {
            ArrayList<ICriterion> costCO2 = Criterion.getCriterionArray("C", 2, false);
            ArrayList<ISpecimen> costPMSpec = new ArrayList<ISpecimen>(pareto.size());
            for (ISpecimen s: pareto)
            {
                double e[] = {s.getAlternative().getEvaluationAt(criteria.get(0)),
                        s.getAlternative().getEvaluationAt(criteria.get(1))};
                IAlternative a = new Alternative("A", costCO2);
                a.setEvaluationVector(e, costCO2);
                ISpecimen dS = new Specimen("S", costCO2);
                dS.setAlternative(a);
                costPMSpec.add(dS);
            }
            ArrayList<ISpecimen> p = Front.getPareto(costPMSpec, costCO2, Common.EPSILON);
            for (ISpecimen s: p)
            {
                System.out.println(s.getAlternative().getEvaluationAt(costCO2.get(0)) + " " +
                        s.getAlternative().getEvaluationAt(costCO2.get(1)));
            }
        }
    }


    public static ArrayList<LinkedList<LPBox>> generateNewBoxesVSplit(LinkedList<LPBox> boxes,
                                                                      double zs[], double zi[], LinkedList<LPBox> newBoxes)
    {

        ArrayList<LinkedList<LPBox>> partial = new ArrayList<LinkedList<LPBox>>(3);
        partial.add(new LinkedList<LPBox>());
        partial.add(new LinkedList<LPBox>());
        partial.add(new LinkedList<LPBox>());

        for (LPBox box : boxes)
        {
            if (box.getComparison(zs, LPBox.UPPER, LPBox.SMALLER, LPBox.STRICT, 0.0d))
            {
                for (int i = 0; i < 3; i++)
                {
                    if ((zs[i] >= box.lower[i]) && (zs[i] > zi[i]))
                    {
                        LPBox b = box.getClone();
                        b.upper[i] = zs[i];
                        partial.get(i).add(b);
                    }
                }
                newBoxes.remove(box);
            }
        }
        return partial;
    }

    public static void updateIndividualSubsets(ArrayList<LinkedList<LPBox>> partial, LinkedList<LPBox> newBoxes, double zs[], double zi[], double zm[])
    {

        for (int i = 0; i < 3; i++)
        {

            int Q = partial.get(i).size();
            int j = (3 + (i - 1)) % 3;
            int k = (3 + (i + 1)) % 3;

            if (Q == 0) System.out.println("ER");

            ArrayList<LPBox> sortedJ = new ArrayList<LPBox>(Q);
            if (Q > 1)
            {
                // SORT -
                BinaryTree<LPBox> jT = new BinaryTree<LPBox>(new BoxExtractor(true, j));
                jT.setDirection(true);
                for (LPBox b : partial.get(i))
                    jT.insert(b);
                sortedJ.add(jT.search());
                LPBox A;
                while ((A = jT.next()) != null)
                    sortedJ.add(A);

                for (int l = 1; l < Q; l++)
                {
                    if ((sortedJ.get(l).upper[j] == sortedJ.get(l - 1).upper[j]) &&
                            (sortedJ.get(l).upper[k] > sortedJ.get(l - 1).upper[k]))
                    {
                        for (int m = l - 1; m >= 0; m--)
                        {
                            if ((sortedJ.get(m + 1).upper[j] != sortedJ.get(m).upper[j]) ||
                                    (sortedJ.get(m + 1).upper[k] <= sortedJ.get(m).upper[k])) break;

                            LPBox tmp = sortedJ.get(m);
                            sortedJ.set(m, sortedJ.get(m + 1));
                            sortedJ.set(m + 1, tmp);
                        }
                    }
                }

                for (int l = 1; l < Q; l++)
                {
                    if ((isEqual(sortedJ.get(l).upper, sortedJ.get(l - 1).upper, eps))
                            && (sortedJ.get(l).lower[j] < sortedJ.get(l - 1).lower[j]))
                    {
                        for (int m = l - 1; m >= 0; m--)
                        {
                            if (!(isEqual(sortedJ.get(m + 1).upper, sortedJ.get(m).upper, eps))
                                    || (sortedJ.get(m + 1).lower[j] >= sortedJ.get(m).lower[j])) break;

                            LPBox tmp = sortedJ.get(m);
                            sortedJ.set(m, sortedJ.get(m + 1));
                            sortedJ.set(m + 1, tmp);
                        }
                    }
                }


            } else if (Q == 1)
                sortedJ.add(partial.get(i).get(0));

            if (sortedJ.size() > 0)
            {
                sortedJ.get(0).lower[j] = zs[j];
                sortedJ.get(sortedJ.size() - 1).lower[k] = zs[k];
            }

            if (sortedJ.size() > 1)
            {
                for (int l = 1; l < Q; l++)
                {
                    sortedJ.get(l).lower[j] = sortedJ.get(l - 1).upper[j];
                    sortedJ.get(l - 1).lower[k] = sortedJ.get(l).upper[k];
                }
            }


            for (LPBox b : sortedJ)
                newBoxes.add(b);
        }
    }

    public static boolean isEqual(double a[], double b[], double epsilon)
    {
        for (int i = 0; i < a.length; i++)
        {
            if (Math.abs(a[i] - b[i]) > epsilon) return false;
        }
        return true;
    }

    public static ISpecimen getOptimalSolution(LPBox box, ArrayList<ICriterion> criteria, double minA[], double maxD[])
    {
        Double e[] = getSolutionForWeights(0.333d, 0.333d, 0.333d, box, minA, maxD);
        if (e == null) return null;
        IAlternative alternative = new Alternative("A", criteria);
        double eval[] = {e[0], e[1], e[2]};
        alternative.setEvaluationVector(eval, criteria);
        ISpecimen specimen = new Specimen("S", criteria);
        specimen.setAlternative(alternative);
        return specimen;
    }

    public static LPBox initStartingBox()
    {
        LPBox box = new LPBox(3);
        box.lower[0] = 0.0d;
        box.lower[1] = 0.0d;
        box.lower[2] = 0.0d;
        box.upper[0] = 10000000.0d;
        box.upper[1] = 10000000.0d;
        box.upper[2] = 10000000.0d;

        double tmp[] = {0.0d, 0.0d, 0.0d};

        Double A[] = getSolutionForWeights(1.0d, 0.0, 0.0, box, tmp, tmp);
        Double B[] = getSolutionForWeights(0.0d, 1.0, 0.0, box, tmp, tmp);
        Double C[] = getSolutionForWeights(0.0d, 0.0, 1.0, box, tmp, tmp);

        System.out.println(A[0] + " " + A[1] + " " + A[2]);
        System.out.println(B[0] + " " + B[1] + " " + B[2]);
        System.out.println(C[0] + " " + C[1] + " " + C[2]);


        box.lower[0] = A[0];
        box.upper[0] = B[0];
        if (C[0] > box.upper[0])
            box.upper[0] = C[0];

        box.lower[1] = B[1];
        box.upper[1] = A[1];
        if (C[1] > box.upper[1])
            box.upper[1] = C[1];

        box.lower[2] = C[2];
        box.upper[2] = A[2];
        if (B[2] > box.upper[2])
            box.upper[2] = B[2];

        return box;
    }

    public static Double[] getSolutionForWeights(double w1, double w2, double w3, LPBox box, double minA[], double maxD[])
    {
        DataB data = new DataB();

        long startTime = System.nanoTime();

        Problem problem;

        double dCost = box.upper[0] - box.lower[0];
        double dCO2 = box.upper[1] - box.lower[1];
        double dPM = box.upper[2] - box.lower[2];

        double n1 = w1 / (dCost - (minA[0] + maxD[0]));
        double n2 = w2 / (dCO2 - (minA[1] + maxD[1]));
        double n3 = w3 / (dPM - (minA[2] + maxD[2]));

        problem = new Problem();
        Constraints_B.addWSMObjective(problem, data, n1, n2, n3, 0.0d, 0.0d, 0.0d,
                box.lower[0] + minA[0], box.upper[0] - maxD[0], box.lower[1] + minA[1], box.upper[1] - maxD[1], box.lower[2] + minA[2], box.upper[2] - maxD[2]);

        Constraints_B.addConstraints(problem, data);

        SolverFactory factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 500);
        Solver solver = factory.get();
        Result result = solver.solve(problem);

        Double point[] = null;

        if (result != null)
        {
            double e[] = Constraints_B.eval(result, data);
            if (e[0] > 1.0d)
            {

                point = new Double[3];
                point[0] = e[0];
                point[1] = e[1];
                point[2] = e[2];
            }
        }

        return point;
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
