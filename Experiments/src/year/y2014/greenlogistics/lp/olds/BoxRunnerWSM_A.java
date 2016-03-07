package year.y2014.greenlogistics.lp.olds;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Specimen;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import net.sf.javailp.*;
import standard.Common;
import tree.binary.BinaryTree;
import utils.BoxExtractor;
import utils.LPBox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by MTomczyk on 09.11.2015.
 */
public class BoxRunnerWSM_A
{

    public static boolean isEqual(double a[], double b[], double epsilon)
    {
        for (int i = 0; i < a.length; i++)
        {
            if (Math.abs(a[i] - b[i]) > epsilon) return false;
        }
        return true;
    }

    @SuppressWarnings("UnusedParameters")
    public static boolean toDeleteSolution(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> c, Double ns[], LPBox b0, double epsilon)
    {
        for (ISpecimen s: specimens)
        {
            double v[] = s.getAlternative().getEvaluationVector(c);
            double A[] = new double[3];
            double B[] = new double[3];

            for (int i = 0; i < 3; i++)
            {
                A[i] = (v[i] - b0.lower[i]) / (b0.upper[i] - b0.lower[i]);
                B[i] = (ns[i] - b0.lower[i]) / (b0.upper[i] - b0.lower[i]);
            }


            int eq = 0;
            for (int i = 0; i < v.length; i++)
            {
                if (Math.abs(A[i] - B[i]) < 0.01) eq++;
            }
            if (eq == 3) return true;
        }
        return false;
    }

    public static double eps = Common.EPSILON;

    @SuppressWarnings("unused")
    public static LPBox getBox(ArrayList<LPBox> usedBoxes, LinkedList<LPBox> boxes, LPBox b0)
    {
        double d[] = {b0.upper[0] - b0.lower[0], b0.upper[1] - b0.lower[1], b0.upper[2] - b0.lower[2]};

        LPBox bestBox = null;
        double bestDist = -1.0d;

        for (LPBox b: boxes)
        {
            double sum = 0.0d;
            for (LPBox usedBox : usedBoxes)
            {
                double A[] = {(usedBox.lower[0] + usedBox.upper[0]) / 2.0d,
                        (usedBox.lower[1] + usedBox.upper[1]) / 2.0d,
                        (usedBox.lower[2] + usedBox.upper[2]) / 2.0d};
                double B[] = {(b.lower[0] + b.upper[0]) / 2.0d,
                        (b.lower[1] + b.upper[1]) / 2.0d,
                        (b.lower[2] + b.upper[2]) / 2.0d};

                double dv[] = new double[3];
                dv[0] = Math.pow((A[0] - B[0]) / d[0], 2.0d);
                dv[1] = Math.pow((A[1] - B[1]) / d[1], 2.0d);
                dv[2] = Math.pow((A[2] - B[2]) / d[2], 2.0d);
                sum += Math.sqrt(dv[0] + dv[1] + dv[2]);
            }
            if (sum > bestDist)
            {
                bestDist = sum;
                bestBox = b;
            }
        }

        //System.out.println(bestDist);

        return bestBox;
    }

    public static void main(String[] args)
    {
        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);
        ArrayList<ISpecimen> pareto = new ArrayList<>(1000);

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") ArrayList<LPBox> usedBoxes = new ArrayList<>(2000);
        LinkedList<LPBox> boxes = new LinkedList<>();
        boxes.add(getStartingBox());

        LPBox b0 = boxes.getFirst().getClone();

        double zi[] = boxes.getFirst().lower.clone();
        double zm[] = boxes.getFirst().upper.clone();

        for (int i = 0; i < 3; i++)
        {
            boxes.getFirst().upper[i] += 0.0000001d;
            boxes.getFirst().lower[i] -= 0.0000001d;
        }

        while (!boxes.isEmpty())
        {
            if (pareto.size() == 100) break;

            LPBox box = boxes.getLast();

            //box.print();
            Double za[][] = {
                    getSolutionForWeights(1.0d / 3.0d, 1.0d / 3.0d, 1.0d / 3.0d, box),
            getSolutionForWeights(1,0,0, box),
            getSolutionForWeights(0,1,0, box),
            getSolutionForWeights(0,0,1, box)
            };

            //System.out.println("  " + z[0] + " " + z[1] + " " + z[2]);
            for (Double z[]: za)
            {
                if ((z == null) || (toDeleteSolution(pareto, criteria, z, b0, 0.01d)))
                    boxes.remove(box);
                else
                {
                    usedBoxes.add(box);

                    double zs[] = {z[0], z[1], z[2]};
                    IAlternative alternative = new Alternative("A", criteria);
                    alternative.setEvaluationVector(zs, criteria);
                    ISpecimen specimen = new Specimen("S", criteria);
                    specimen.setAlternative(alternative);
                    pareto.add(specimen);

                    //System.out.println(pareto.size() + " " + boxes.size());
                    if (pareto.size() == 1000) break;

                    LinkedList<LPBox> newBoxes = boxes.stream().collect(Collectors.toCollection(LinkedList::new));

                    ArrayList<LinkedList<LPBox>> partial = generateNewBoxesVSplit(boxes, zs, zi, newBoxes);

                /*if ((partial.get(0).size() == 0) ||
                        (partial.get(1).size() == 0) ||
                        (partial.get(2).size() == 0))
                {
                    boxes.removeFirst();
                    pareto.remove(pareto.size() - 1);
                    continue;
                }*/

                    updateIndividualSubsets(partial, newBoxes, zs, zi, zm);
                    boxes = newBoxes;
                }
            }


        }

        for (ISpecimen s : pareto)
        {
            double a = s.getAlternative().getEvaluationAt(criteria.get(0));
            double b = s.getAlternative().getEvaluationAt(criteria.get(1));
            double c = s.getAlternative().getEvaluationAt(criteria.get(2));
            System.out.println(a + " " + b + " " + c);
        }
    }


    public static ArrayList<LinkedList<LPBox>> generateNewBoxesVSplit(LinkedList<LPBox> boxes,
                                                                    double zs[], double zi[], LinkedList<LPBox> newBoxes)
    {

        ArrayList<LinkedList<LPBox>> partial = new ArrayList<>(3);
        partial.add(new LinkedList<>());
        partial.add(new LinkedList<>());
        partial.add(new LinkedList<>());

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
                            if ((sortedJ.get(m+1).upper[j] != sortedJ.get(m).upper[j]) ||
                                    (sortedJ.get(m+1).upper[k] <= sortedJ.get(m).upper[k])) break;

                            LPBox tmp = sortedJ.get(m);
                            sortedJ.set(m, sortedJ.get(m+1));
                            sortedJ.set(m+1, tmp);
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
                            if (!(isEqual(sortedJ.get(m+1).upper, sortedJ.get(m).upper, eps))
                                    || (sortedJ.get(m+1).lower[j] >= sortedJ.get(m).lower[j])) break;

                            LPBox tmp = sortedJ.get(m);
                            sortedJ.set(m, sortedJ.get(m+1));
                            sortedJ.set(m+1, tmp);
                        }
                    }
                }


            } else if (Q == 1)
                sortedJ.add(partial.get(i).get(0));

            sortedJ.stream().filter(b -> !b.validate()).forEach(b -> System.out.println("BOX VALIDATE"));


            if (sortedJ.size() > 1)
                for (int l = 1; l < Q; l++)
                {
                    double uj1 = sortedJ.get(l - 1).upper[j];
                    double uj2 = sortedJ.get(l).upper[j];
                    double uk1 = sortedJ.get(l - 1).upper[k];
                    double uk2 = sortedJ.get(l).upper[k];
                    double vj1 = sortedJ.get(l - 1).lower[j];
                    double vj2 = sortedJ.get(l).lower[j];
                    double vk1 = sortedJ.get(l - 1).lower[k];
                    double vk2 = sortedJ.get(l).lower[k];
                    boolean eq = isEqual(sortedJ.get(l-1).upper,sortedJ.get(l).upper,eps);

                    if ((int) uj1 > (int) uj2)
                    {
                        System.out.println("SORT ERROR v0 at " + i);
                        System.out.println(uj1 + " " + uj2);
                        System.out.println(uk1 + " " + uk2);
                        System.out.println(vj1 + " " + vj2);
                        System.out.println(vk1 + " " + vk2);
                    }

                    if ((int) uk1 < (int) uk2)
                    {
                        System.out.println("SORT ERROR v1 at " + i);
                        System.out.println(uj1 + " " + uj2);
                        System.out.println(uk1 + " " + uk2);
                        System.out.println(vj1 + " " + vj2);
                        System.out.println(vk1 + " " + vk2);
                    }

                    if ((eq)&&((int)vj1>(int)vj2))
                    {
                        System.out.println("SORT ERROR v2 at " + i);
                        System.out.println(uj1 + " " + uj2);
                        System.out.println(uk1 + " " + uk2);
                        System.out.println(vj1 + " " + vj2);
                        System.out.println(vk1 + " " + vk2);
                    }
                    if ((eq)&&((int)vk1<(int)vk2))
                    {
                        System.out.println("SORT ERROR v3 at " + i);
                        System.out.println(uj1 + " " + uj2);
                        System.out.println(uk1 + " " + uk2);
                        System.out.println(vj1 + " " + vj2);
                        System.out.println(vk1 + " " + vk2);
                    }

                }

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

    public static LPBox getStartingBox()
    {
        LPBox box = new LPBox(3);
        box.lower[0] = 0.0d;
        box.lower[1] = 0.0d;
        box.lower[2] = 0.0d;
        box.upper[0] = 10000000.0d;
        box.upper[1] = 10000000.0d;
        box.upper[2] = 10000000.0d;

        Double A[] = getSolutionForWeights(1.0d, 0.0, 0.0, box);
        Double B[] = getSolutionForWeights(0.0d, 1.0, 0.0, box);
        Double C[] = getSolutionForWeights(0.0d, 0.0, 1.0, box);

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

    @SuppressWarnings("unused")
    public static Double[] getSolutionForWeights(double w1, double w2, double w3, LPBox box)
    {
        DataA data = new DataA();

        long startTime = System.nanoTime();

        double dCost = box.upper[0] - box.lower[0];
        double dCO2 = box.upper[1] - box.lower[1];
        double dPM = box.upper[2] - box.lower[2];

        double n1 = w1 / (dCost);
        double n2 = w2 / (dCO2);
        double n3 = w3 / (dPM);

        Problem problem = new Problem();
        Constraints_A.addWSMObjective(problem, data, n1, n2, n3, 0.0d, 0.0d, 0.0d,
                box.lower[0], box.upper[0], box.lower[1], box.upper[1], box.lower[2], box.upper[2]);

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
}
