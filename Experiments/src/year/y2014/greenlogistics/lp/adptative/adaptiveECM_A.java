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
import year.y2014.greenlogistics.A.DataA;
import year.y2014.greenlogistics.lp.Constraints_A;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by MTomczyk on 18.11.2015.
 */
public class adaptiveECM_A
{
    public static double eps = Common.EPSILON;

    public static void main(String args[])
    {
        long beginTime = System.currentTimeMillis();

        LPBox b0 = initStartingBox();
        b0.print();

        double zi[] = b0.lower.clone();
        double zm[] = b0.upper.clone();
        double minA[] = {(zm[0] - zi[0]) / 36.0d, (zm[1] - zi[1]) / 36.0d, (zm[2] - zi[2]) / 36.0d};
        double maxD[] = {(zm[0] - zi[0]) / 36.0d, (zm[1] - zi[1]) / 36.0d, (zm[2] - zi[2]) / 36.0d};

        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);
        ArrayList<ISpecimen> pareto = new ArrayList<>();
        LinkedList<LPBox> boxes = new LinkedList<>();
        boxes.add(b0);


        int solverRuns = 0;

        while (!boxes.isEmpty())
        {
            sortBoxes(boxes);
            LPBox box = boxes.getFirst();
            LPBox tmpBox = box.getClone();
            tmpBox.lower = zi.clone();

            ISpecimen zs;
            if ((tmpBox.upper[0] - tmpBox.lower[0] - maxD[0] < 0.0d)
                    || (tmpBox.upper[1] - tmpBox.lower[1] - maxD[1] < 0.0d)
                    || (tmpBox.upper[2] - tmpBox.lower[2] - maxD[2] < 0.0d))
                zs = null;
            else
            {
                zs = getOptimalSolution(tmpBox, criteria, minA, maxD);
                solverRuns++;
            }


            if (zs == null)
            {
                boxes.removeFirst();
                continue;
            }
            pareto.add(zs);
            System.out.println(pareto.size());
            if (pareto.size() == 1000) break;

            LinkedList<LPBox> newBoxes = boxes.stream().collect(Collectors.toCollection(LinkedList::new));

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

        /*for (ISpecimen s : pareto)
        {
            System.out.println(s.getAlternative().getEvaluationAt(criteria.get(0)) + " " +
                    s.getAlternative().getEvaluationAt(criteria.get(1)) + " " +
                    s.getAlternative().getEvaluationAt(criteria.get(2)));
        }*/

        {
            double e[] = {843633.68, 539535.331,	14803.36};
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(e, criteria);
            ISpecimen dS = new Specimen("S", criteria);
            dS.setAlternative(a);
            pareto.add(dS);
        }
        {
            double e[] = {877104.5158,	535039.184,	12754.472};
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(e, criteria);
            ISpecimen dS = new Specimen("S", criteria);
            dS.setAlternative(a);
            pareto.add(dS);
        }
        {
            double e[] = {1047988.73,	571223.026,	2712.256};
            IAlternative a = new Alternative("A", criteria);
            a.setEvaluationVector(e, criteria);
            ISpecimen dS = new Specimen("S", criteria);
            dS.setAlternative(a);
            pareto.add(dS);
        }

        // COST-PM
        {
            ArrayList<ICriterion> costPM = Criterion.getCriterionArray("C", 2, false);
            ArrayList<ISpecimen> costPMSpec = new ArrayList<>(pareto.size());
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
            ArrayList<ISpecimen> costPMSpec = new ArrayList<>(pareto.size());
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

    public static void sortBoxes(LinkedList<LPBox> boxes)
    {
        double min = Common.MAX_DOUBLE;
        int minIndex = 0;

        int i = -1;
        for (LPBox box: boxes)
        {
            i++;
            if (box.lower[0] < min)
            {
                min = box.lower[0];
                minIndex = i;
            }
        }

        LPBox tmp = boxes.getFirst();
        boxes.set(0, boxes.get(minIndex));
        boxes.set(minIndex, tmp);
    }

    public static ArrayList<LinkedList<LPBox>> generateNewBoxesVSplit(LinkedList<LPBox> boxes,
                                                                      double zs[], double zi[], LinkedList<LPBox> newBoxes)
    {

        ArrayList<LinkedList<LPBox>> partial = new ArrayList<>(3);
        partial.add(new LinkedList<>());
        partial.add(new LinkedList<>());
        partial.add(new LinkedList<>());

        int num = -1;
        for (LPBox box : boxes)
        {
            num++;
            if (box.getComparison(zs, LPBox.UPPER, LPBox.SMALLER, LPBox.STRICT, 0.0d))
            {
                int start = 0;
                if (num == 0) start = 1;
                for (int i = start; i < 3; i++)
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

    @SuppressWarnings("UnusedParameters")
    public static void updateIndividualSubsets(ArrayList<LinkedList<LPBox>> partial, LinkedList<LPBox> newBoxes, double zs[], double zi[], double zm[])
    {

        for (int i = 0; i < 3; i++)
        {

            int Q = partial.get(i).size();
            int j = (3 + (i - 1)) % 3;
            int k = (3 + (i + 1)) % 3;

            if (Q == 0) System.out.println("ER");

            ArrayList<LPBox> sortedJ = new ArrayList<>(Q);
            if (Q > 1)
            {
                // SORT -
                BinaryTree<LPBox> jT = new BinaryTree<>(new BoxExtractor(true, j));
                jT.setDirection(true);
                partial.get(i).forEach(jT::insert);
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


            newBoxes.addAll(sortedJ.stream().collect(Collectors.toList()));
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
        Double e[] = getSolution(box, 0, minA, maxD);

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

        Double A[] = getSolution(box, 0, tmp, tmp);
        Double B[] = getSolution(box, 1, tmp, tmp);
        Double C[] = getSolution(box, 2, tmp, tmp);

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

    @SuppressWarnings("UnusedParameters")
    public static Double[] getSolution(LPBox box, int criterion, double minA[], double maxD[])
    {
        DataA data = new DataA();

        @SuppressWarnings("unused") long startTime = System.nanoTime();

        Problem problem = new Problem();

        double U1 = box.upper[1];
        double U2 = box.upper[2];

        if (criterion == 1)
        {
            U1 = box.upper[0];
            U2 = box.upper[2];
        } else if (criterion == 2)
        {
            U1 = box.upper[0];
            U2 = box.upper[1];
        }

        //double eps[] = {maxD[0], maxD[1], maxD[2]};
        double eps[] = {0.0d, 0.0d, 0.0d};
        Constraints_A.addECMObjective(problem, data, criterion, U1, U2, eps,
                box.lower[0] + 0.0d,
                box.upper[0] - maxD[0],
                box.lower[1] + 0.0d,
                box.upper[1] - maxD[1],
                box.lower[2] + 0.0d,
                box.upper[2] - maxD[2],
                false, null);
        Constraints_A.addConstraints(problem, data);

        SolverFactory factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 500);
        Solver solver = factory.get();
        Result result = solver.solve(problem);

        Double point[] = null;

        if (result != null)
        {
            double e[] = Constraints_A.eval(result, data);
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
        Cube3D cube = new Cube3D(new Range(843700.0f, 1032700.0f), new Range(535100.0f, 570600.0f)
                , new Range(2700.0f, 14800.0f), new WhiteSchema());
        ArrayList<Point> points = new ArrayList<>(pareto.size());
        for (ISpecimen s : pareto)
        {
            Point p = new Point(s.getAlternative().getEvaluationAt(criteria.get(0)),
                    s.getAlternative().getEvaluationAt(criteria.get(1)),
                    s.getAlternative().getEvaluationAt(criteria.get(2)));
            points.add(p);
        }

        DataSet ds = new DataSet(points);
        ds.setGradient(new RedBlue());
        ArrayList<DataSet> ads = new ArrayList<>();
        ads.add(ds);

        cube.setDataSet(ads);
        cube.setVisible(true);
    }

}
