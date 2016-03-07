package reproducer;

import org.apache.commons.math3.random.MersenneTwister;
import reproducer.boundcorrect.interfaces.IBoundCorrect;
import standard.Range;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-02-16.
 *
 */

// TODO JAVADOC TEST

public class SBX
{
    public static void cross(double child[], double father[], double mother[], MersenneTwister generator,
                             double distributionIndex, IBoundCorrect boundCorrect, Range r)
    {
        ArrayList<Range> ar = new ArrayList<>();
        ar.add(r);
        double di[] = {distributionIndex};
        cross(child, null, father, mother, generator, di, boundCorrect, ar);
    }



    public static void cross(double child[], double father[], double mother[], MersenneTwister generator,
                             double distributionIndex[], IBoundCorrect boundCorrect, ArrayList<Range> ar)
    {
        cross(child, null, father, mother, generator, distributionIndex, boundCorrect, ar);
    }

    public static void cross(double childA[], double childB[], double father[], double mother[],
                             MersenneTwister generator, double distributionIndex[], IBoundCorrect boundCorrect, ArrayList<Range> ar)
    {
        if (childB == null)
        {
            double tmp[] = father;
            father = mother;
            mother = tmp;
        }

        for (int i = 0; i < childA.length; i++)
        {
            double u = generator.nextDouble();
            double beta = 0;

            double index = 5.0d;
            if (distributionIndex.length == childA.length)
                index = distributionIndex[i];
            else
                index = distributionIndex[0];

            if (Double.compare(u, 0.5d) < 0) beta = Math.pow(2.0d * u, 1.0d / (index + 1.0d));
            else if (Double.compare(u, 0.5d) > 0) beta = Math.pow(0.5d / (1.0d - u), 1.0d / (index + 1.0d));
            else if (u == 0.5) beta = 1.0d;

            double c1 = (father[i] + mother[i]) / 2.0d - beta * 0.5d * Math.abs(father[i] - mother[i]);
            double c2 = (father[i] + mother[i]) / 2.0d + beta * 0.5d * Math.abs(father[i] - mother[i]);

            if (ar != null)
            {
                Range r = null;
                if (ar.size() == childA.length)
                    r = ar.get(i);
                else if (ar.size() > 0)
                    r = ar.get(0);
                if (r != null)
                {
                    if (!r.isInRange(c1))
                        c1 = boundCorrect.correct(r, c1);

                    if (!r.isInRange(c2))
                        c2 = boundCorrect.correct(r, c2);
                }
            }

            if (generator.nextDouble() < 0.5d)
            {
                childA[i] = c1;
                if (childB != null) childB[i] = c2;
            } else
            {
                childA[i] = c2;
                if (childB != null) childB[i] = c1;
            }
        }
    }

}
