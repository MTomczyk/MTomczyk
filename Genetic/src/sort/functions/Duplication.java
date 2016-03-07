package sort.functions;

import criterion.interfaces.ICriterion;
import utils.Domination;
import interfaces.ISpecimen;

import java.util.ArrayList;

/**
 * Created by Michał on 2014-10-11.
 *
 */

// TODO JAVADOC TEST
public class Duplication
{
    // TODO EXCEPTION ??
    public static ArrayList<ISpecimen> extractDuplicates(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria,
                                                         double epsilon, int min)
    {
        ArrayList<ISpecimen> duplicated = new ArrayList<>(specimens.size());
        boolean deleted[] = new boolean[specimens.size()];

        for (int i = 0; i < specimens.size(); i++)
        {
            if (deleted[i]) continue;
            for (int j = i + 1; j < specimens.size(); j++)
                if ((Domination.isEqual(specimens.get(i).getAlternative(), specimens.get(j).getAlternative(), criteria,
                        epsilon))) deleted[j] = true;

        }

        int removed = 0;
        for (int i = 0; i < deleted.length; i++)
        {
            if (deleted[i])
            {
                duplicated.add(specimens.get(i - removed));
                specimens.remove(i - removed);
                removed++;
                if (specimens.size() <= min) break;
            }
        }

        return duplicated;
    }

    @SuppressWarnings("unused")
    public static int shiftDuplicated(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, double epsilon)
    {

        int shifted = 0;
        double lastEval = specimens.get(specimens.size() - 1).getAlternative().getAggregatedEvaluation();

        for (int i = 0; i < specimens.size() - shifted; i++)
        {
            for (int j = i + 1; j < specimens.size() - shifted; j++)
            {
                if ((Domination.isEqual(specimens.get(i).getAlternative(), specimens.get(j).getAlternative(), criteria,
                        epsilon)))
                {
                    ISpecimen last = specimens.get(j);
                    last.getAlternative().setAggregatedEvaluation(lastEval + 1.0d);
                    for (int k = j; k < specimens.size() - 1; k++)
                        specimens.set(k, specimens.get(k + 1));
                    specimens.set(specimens.size() - 1, last);
                    shifted++;
                    j--;
                }
            }
        }

        return shifted;
    }

}
