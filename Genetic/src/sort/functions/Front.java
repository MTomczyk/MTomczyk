package sort.functions;

import criterion.interfaces.ICriterion;
import extractor.alternative.FromSpecimen;
import utils.Domination;
import interfaces.ISpecimen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Front
{
    /**
     * Calculates strength of solutions: number of dominated solutions by solution.
     *
     * @param specimens  Array of specimens.
     * @param domination Domination matrix.
     * @return Strength of solution.
     */
    public static int[] calculateStrength(ArrayList<ISpecimen> specimens, boolean domination[][])
    {
        int strength[] = new int[specimens.size()];

        for (int i = 0; i < specimens.size(); i++)
        {
            for (int j = 0; j < specimens.size(); j++)
            {
                if (i == j) continue;
                if (domination[i][j]) strength[i]++;
            }
        }
        return strength;
    }

    /**
     * Calculate strength of solutions.
     *
     * @param specimens Array of specimens.
     * @param criteria  Array of criteria.
     * @param epsilon   Very small value.
     * @return Strength of solutions.
     */
    public static int[] calculateStrength(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria,
                                          double epsilon)
    {
        boolean domination[][] = Domination.computeDominationMatrix(specimens, new FromSpecimen(), criteria, epsilon);
        return calculateStrength(specimens, domination);
    }

    /**
     * Calculates each solution weakness (sum of strengths of solutions which are dominating this solution).
     *
     * @param specimens Array of specimens
     * @param criteria  Array of criteria
     * @param epsilon   Epsilon
     * @return Array of weaknesses.
     */
    public static int[] calculateWeakness(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria,
                                          double epsilon)
    {
        int weakness[] = new int[specimens.size()];
        boolean domination[][] = Domination.computeDominationMatrix(specimens, new FromSpecimen(), criteria, epsilon);
        int strength[] = calculateStrength(specimens, domination);

        for (int i = 0; i < specimens.size(); i++)
        {
            for (int j = 0; j < specimens.size(); j++)
            {
                if (i == j) continue;
                if (domination[j][i]) weakness[i] += strength[j];
            }
        }
        return weakness;
    }

    public static ArrayList<ISpecimen> getPareto(ArrayList<ISpecimen> specimen, ArrayList<ICriterion> criterion,
                                                 double epsilon)
    {
        ArrayList<ISpecimen> result = new ArrayList<>(specimen.size());
        if (specimen.size() == 0) return result;

        for (int i = 0; i < specimen.size(); i++)
        {
            boolean pass = true;
            for (int j = 0; j < specimen.size(); j++)
            {
                if (i == j) continue;
                if (Domination.isDominating(specimen.get(j).getAlternative(), specimen.get(i).getAlternative(),
                        criterion, epsilon))
                {
                    pass = false;
                    break;
                }
            }

            if (pass) result.add(specimen.get(i));
        }

        if (result.size() == 0) result.add(specimen.get(0));

        return result;
    }

    public static int[] parseToVector(LinkedList<LinkedList<Integer>> fronts, int specimens)
    {
        int front[] = new int[specimens];
        int frontNo = -1;
        for (LinkedList<Integer> f : fronts)
        {
            frontNo++;
            for (Integer i : f)
            {
                front[i] = frontNo;
            }
        }
        return front;
    }

    @SuppressWarnings("unused")
    public static ArrayList<ArrayList<ISpecimen>> getDominationFrontSpecimen(ArrayList<ISpecimen> specimen,
                                                                             ArrayList<ICriterion> criterion,
                                                                             double epsilon)
    {
        LinkedList<LinkedList<Integer>> list = getDominationFrontList(specimen, criterion, epsilon);
        ArrayList<ArrayList<ISpecimen>> result = new ArrayList<>(list.size());

        for (LinkedList<Integer> s : list)
        {
            ArrayList<ISpecimen> as = new ArrayList<>(s.size());
            as.addAll(s.stream().map(specimen::get).collect(Collectors.toList()));
            result.add(as);
        }
        return result;
    }

    public static LinkedList<LinkedList<Integer>> getDominationFrontList(ArrayList<ISpecimen> specimen,
                                                                         ArrayList<ICriterion> criterion,
                                                                         double epsilon)
    {
        LinkedList<LinkedList<Integer>> front = new LinkedList<>();
        ArrayList<LinkedList<Integer>> S = new ArrayList<>(specimen.size());
        ArrayList<Integer> n = new ArrayList<>(specimen.size());


        // INIT
        for (int i = 0; i < specimen.size(); i++)
        {
            n.add(0);
            //noinspection MismatchedQueryAndUpdateOfCollection
            LinkedList<Integer> s = new LinkedList<>();
            S.add(s);
        }

        LinkedList<Integer> f = new LinkedList<>();

        // Prepare counter and sets
        for (int i = 0; i < specimen.size(); i++)
        {

            for (int j = 0; j < specimen.size(); j++)
            {
                if (i == j) continue;
                boolean dA = Domination.isDominating(specimen.get(i).getAlternative(), specimen.get(j).getAlternative(),
                        criterion, epsilon);
                boolean dB = Domination.isDominating(specimen.get(j).getAlternative(), specimen.get(i).getAlternative(),
                        criterion, epsilon);

                if (dA) S.get(i).add(j);
                else if (dB) n.set(i, n.get(i) + 1);
            }

            if (n.get(i) == 0) f.add(i);
        }
        front.add(f);

        while (true)
        {
            LinkedList<Integer> l = front.getLast();

            f = new LinkedList<>();

            for (Integer p : l)
            {
                for (Integer q : S.get(p))
                {
                    int val = n.get(q) - 1;
                    n.set(q, val);
                    if (val == 0) f.add(q);
                }
            }

            if (!f.isEmpty()) front.add(f);
            else break;
        }
        return front;

    }
}
