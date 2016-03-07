package sort.functions;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import standard.Range;
import utils.Domination;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by MTomczyk on 13.11.2015.
 */
public class TParetoUpdateFunction {
    public static ArrayList<ISpecimen> getSet(ArrayList<ISpecimen> archive, ISpecimen specimen,
                                              ArrayList<ICriterion> criteria, double t[], double epsilon) {

        // DYNAMIC RANGES
        ArrayList<Range> dr = new ArrayList<>(criteria.size());
        {
            for (int i = 0; i < criteria.size(); i++)
                dr.add(criteria.get(i).getRange().get("tp_space"));

            double e[] = specimen.getAlternative().getEvaluationVector(criteria);
            for (int i = 0; i < criteria.size(); i++)
            {
                Range r = dr.get(i);
                if (!r.isInRange(e[i]))
                {
                    //System.out.println("A");
                    return archive;
                }
                //if (e[i] < r.left) r.left = e[i];
                //if (e[i] > r.right) r.right = e[i];
            }
        }



        ArrayList<ISpecimen> result = new ArrayList<>(archive.size());

        //result = archive;
        ArrayList<ISpecimen> D = new ArrayList<>(archive.size());
        ArrayList<ISpecimen> eD = new ArrayList<>(archive.size());

        IAlternative A = Box.getDummyAlternativeBox(specimen.getAlternative(), criteria, t);
        for (ISpecimen a : archive) {
            IAlternative B = Box.getDummyAlternativeBox(a.getAlternative(), criteria, t);
            //System.out.println(A.getEvaluationVector(criteria)[0] + " " + A.getEvaluationVector(criteria)[1]);
            if (!Domination.isDominating(A, B, criteria, epsilon))
                eD.add(a);
            else D.add(a);
        }


        if (D.size() != 0)
        {
            result.addAll(eD.stream().collect(Collectors.toList()));
            result.add(specimen);
            //System.out.println("A");
        }
        else
        {
            ISpecimen fc = firstCondition(archive, specimen, criteria, t, epsilon);
            if (fc != null)
            {
                result.addAll(archive.stream().collect(Collectors.toList()));
                result.add(specimen);
                result.remove(fc);
                //System.out.println("B");
            }
            else if (secondCondition(archive, specimen, criteria, t, epsilon))
            {
                result.addAll(archive.stream().collect(Collectors.toList()));
                result.add(specimen);
                //System.out.println("C");
            }
            else
            {
                result.addAll(archive.stream().collect(Collectors.toList()));
                //System.out.println("D");
            }
        }


        return result;
    }

    private static ISpecimen firstCondition(ArrayList<ISpecimen> archive,
                                            ISpecimen specimen,
                                            ArrayList<ICriterion> criteria,
                                            double t[],
                                            double epsilon)
    {
        IAlternative A = Box.getDummyAlternativeBox(specimen.getAlternative(), criteria,  t);

        for (ISpecimen a : archive)
        {
            IAlternative B = Box.getDummyAlternativeBox(a.getAlternative(), criteria, t);
            if ((Domination.isEqual(A, B, criteria, epsilon))
                    && (Domination.isDominating(specimen.getAlternative(), a.getAlternative(),criteria,epsilon)))
                return a;
        }

        return null;
    }

    private static boolean secondCondition(ArrayList<ISpecimen> archive,
                                           ISpecimen specimen,
                                           ArrayList<ICriterion> criteria,
                                           double t[],
                                           double epsilon)
    {
        IAlternative A = Box.getDummyAlternativeBox(specimen.getAlternative(), criteria, t);

        for (ISpecimen a : archive)
        {
            IAlternative B = Box.getDummyAlternativeBox(a.getAlternative(), criteria,  t);
            if ((Domination.isEqual(A, B, criteria, epsilon))
            || (Domination.isDominating(B, A,criteria,epsilon)))
                return false;
        }

        return true;
    }
}
