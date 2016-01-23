package year.y2014.greenlogistics.lp.olds;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Specimen;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import net.sf.javailp.*;
import standard.Common;
import standard.Range;
import utils.Domination;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 16.11.2015.
 */
public class laumannA_eps
{

    public static double eps = Common.EPSILON;

    public static void main(String args[])
    {
        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);

        ArrayList<ISpecimen> P = new ArrayList<ISpecimen>(200);
        Subregion Q = new Subregion(3);

        ArrayList<ArrayList<Double>> e = new ArrayList<ArrayList<Double>>(3);
        e.add(null);
        e.add(new ArrayList<Double>(10000));
        e.get(1).add(Common.MIN_DOUBLE);
        e.get(1).add(Common.MAX_DOUBLE);
        e.add(new ArrayList<Double>(10000));
        e.get(2).add(Common.MIN_DOUBLE);
        e.get(2).add(Common.MAX_DOUBLE);

        int i = (P.size() + 1) * (P.size() + 1);
        while (true)
        {
            i = i - 1;
            if (i < 0) break;
            ArrayList<Range> ne = getConstraints(i, e, P);
            if (Q.contains(ne)) continue;
            ISpecimen x = getOpt(ne, criteria);
            if (x == null)
            {
                Q.add(ne);
                continue;
            }
            if (isDominance(P, x, criteria))
            {
                Q.add(ne);
                continue;
            }

            P.add(x);
            Q.add(getUpdatedRange(ne, x, criteria));
            e = updateConstraints(x, criteria, e);

            // ------------------------------------
            i = (P.size() + 1) * (P.size() + 1);
        }

    }

    public static ISpecimen getOpt(ArrayList<Range> r, ArrayList<ICriterion> criteria)
    {
        DataA data = new DataA();
        Problem problem = new Problem();
        Constraints_A.addECMObjective(problem, data, 0, Common.MAX_DOUBLE, Common.MAX_DOUBLE, null);
        Constraints_A.addConstraints(problem, data);

        SolverFactory factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 500);
        Solver solver = factory.get();
        Result result = solver.solve(problem);

        ISpecimen s = null;

        if (result != null)
        {
            double e[] = Constraints_A.eval(result, data);
            if (e[0] > 1.0d)
            {

                double point[] = new double[3];
                point[0] = e[0];
                point[1] = e[1];
                point[2] = e[2];
                s = new Specimen("S", criteria);
                IAlternative a = new Alternative("A", criteria);
                a.setEvaluationVector(point, criteria);
                s.setAlternative(a);
            }
        }

        return s;

    }

    public static boolean isDominance(ArrayList<ISpecimen> P, ISpecimen x, ArrayList<ICriterion> criteria)
    {
        for (ISpecimen s : P)
        {
            if (Domination.isDominating(s.getAlternative(), x.getAlternative(), criteria, eps))
                return true;
        }
        return false;
    }

    public static ArrayList<ArrayList<Double>>
    updateConstraints(ISpecimen x, ArrayList<ICriterion> criteria, ArrayList<ArrayList<Double>> e)
    {
        for (int j = 1; j < 3; j++)
        {
            double y = x.getAlternative().getEvaluationAt(criteria.get(j));
            int i;
            for (i = 0; i < e.get(j).get(i); i++)
                if (y <= e.get(j).get(i)) break;

            e.get(j).add(i, y);
        }

        return e;
    }

    public static ArrayList<Range> getUpdatedRange(ArrayList<Range> ne, ISpecimen x,
                                                   ArrayList<ICriterion> criteria)
    {
        ne.get(1).right = x.getAlternative().getEvaluationAt(criteria.get(1));
        ne.get(2).right = x.getAlternative().getEvaluationAt(criteria.get(2));
        return ne;
    }

    public static ArrayList<Range> getConstraints(int i, ArrayList<ArrayList<Double>> e, ArrayList<ISpecimen> P)
    {
        ArrayList<Range> result = new ArrayList<Range>(3);
        result.add(null);
        for (int j = 1; j < 3; j++)
        {
            int d = i % (P.size() + 1);
            if ((i - d) % (P.size() + 1) != 0) System.out.println("ERR");
            i = (i - d) / (P.size() + 1);
            result.add(new Range(e.get(j).get(d - 1), e.get(j).get(d)));
        }
        return result;
    }

}
