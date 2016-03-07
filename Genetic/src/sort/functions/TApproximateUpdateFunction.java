package sort.functions;

import criterion.interfaces.ICriterion;
import interfaces.ISpecimen;
import utils.Domination;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by MTomczyk on 13.11.2015.
 */
public class TApproximateUpdateFunction
{
    public static ArrayList<ISpecimen> getSet(ArrayList<ISpecimen> archive, ISpecimen specimen,
                                              ArrayList<ICriterion> criteria, double t[], double epsilon)
    {
        //System.out.println("----- " + archive.size());

        boolean pass = false;
        for (ISpecimen s: archive)
        {
            if (Domination.isTMultipleDominating(s.getAlternative(), specimen.getAlternative(),criteria, t, epsilon))
            {
                pass = true;
                break;
            }
        }
        if (pass)
        {
            return archive;
        }
        else
        {
            ArrayList <ISpecimen> eD = new ArrayList<>(archive.size());
            eD.addAll(archive.stream().filter(s -> !Domination.isDominating(specimen.getAlternative(), s.getAlternative(), criteria, epsilon)).collect(Collectors.toList()));

            ArrayList<ISpecimen> result = new ArrayList<>(archive.size());

            result.addAll(eD.stream().collect(Collectors.toList()));
            result.add(specimen);

            return result;
        }
    }
}
