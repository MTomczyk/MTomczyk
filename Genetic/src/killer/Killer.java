package killer;

import java.util.ArrayList;

import interfaces.ISpecimen;
import killer.interfaces.IKiller;

/**
 * Simple killer. It removes last n specimens from population (must be sorted).
 */
public class Killer implements IKiller
{
    /**
     * Remove last n specimens from array.
     *
     * @param specimen Array of specimen.
     * @param number   Number of last specimen to remove.
     */
    @Override
    public void kill(ArrayList<ISpecimen> specimen, int number)
    {
        if (number < 0) throw new IllegalArgumentException();
        if (number > specimen.size()) throw new IllegalArgumentException();

        int size = specimen.size();
        for (int i = 1; i <= number; i++)
        {
            specimen.remove(size - i);
            // TODO !!!
            //if (specimen.size() < 5) break;
        }
    }
}
