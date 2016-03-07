package reproducer;


import org.apache.commons.math3.random.MersenneTwister;

/**
 * Method for standard cross.
 */
public class StandardCross
{
    /**
     * Standard cross father and mother genotype and receive children genotype. One cut in random place.
     * First part of children genotype is taken from father genotype. Second from mother.
     * @param child Result will be put here.
     * @param father Father genotype.
     * @param mother Mother genotype.
     * @param generator Random number generator.
     * @param side if = 1 then replace father and mother genotype.
     */
    public static void cross(double child[], double father[], double mother[], MersenneTwister generator, int side)
    {
        if (side == 1)
        {
            double tmp[] = father;
            father = mother;
            mother = tmp;
        }

        int halfF = generator.nextInt(father.length + 1);

        int pnt = 0;

        for (int i = 0; i < halfF; i++)
        {
            child[i] = father[i];
            pnt++;
        }

        for (int i = halfF; i < mother.length; i++)
        {
            child[pnt] = mother[i];
            pnt++;
        }

        if (pnt < child.length)
        {
            for (int i = halfF; i < father.length; i++)
            {
                child[pnt] = father[i];
                pnt++;
                if (pnt == child.length) break;
            }
        }

        if (pnt < child.length)
        {
            for (int i = 0; i < halfF; i++)
            {
                child[pnt] = mother[i];
                pnt++;
                if (pnt == child.length) break;
            }
        }

    }
}
