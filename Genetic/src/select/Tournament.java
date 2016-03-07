package select;

import java.util.ArrayList;

import reproducer.Parents;
import reproducer.interfaces.IParents;
import select.interfaces.ISelector;
import org.apache.commons.math3.random.MersenneTwister;

import interfaces.ISpecimen;

/**
 * Implementation of tournament selection.
 * In one tournament always chooses different solutions.
 */
public class Tournament implements ISelector
{
    private double _probability = 0.5d;
    private int _k = 0;
    private int _pickLimit = 1;

    /**
     * Params to tune tournament selection.
     */
    public static class Params
    {
        /**
         * Probability of loose better solution.
         */
        public double _probability = 0.5d;
        /**
         * Number of solutions in one tournament.
         */
        public int _k = 2;
        /**
         * Max of better solutions to loose.
         */
        public int _pickLimit = 1;
    }

    public Tournament(Params p)
    {
        this._probability = p._probability;
        this._k = p._k;
        this._pickLimit = p._pickLimit;
    }

    @Override
    public ArrayList<IParents> select(ArrayList<ISpecimen> specimens, int number, MersenneTwister generator)
    {
        ArrayList<IParents> result = new ArrayList<>(number);
        ArrayList<ISpecimen> chosen = new ArrayList<>(2 * number);

        int size = specimens.size();
        // DLA KAZDEGO TOURNAMENTA...
        for (int i = 0; i < number * 2; i++)
        {
            if ((specimens.size() < _k) || (_k == 1))
            {
                chosen.add(specimens.get(generator.nextInt(specimens.size())));
                continue;
            }
            // WYBIERZ k OSOBNIKOW...
            // PRZYGOTUJ MASKE
            // OD LEWEJ SA NAJLEPSI BO INPUT:SPECIMEN JEST POSORTOWANY
            boolean mask[] = new boolean[size];
            for (int j = 0; j < _k; j++)
            {
                int pick = generator.nextInt(size);
                while (mask[pick]) pick = (size + pick - 1) % size;
                mask[pick] = true;
            }
            // WYBIERZ NAJLEPSZEGO
            int fail = 0;

            for (int j = 0; j < size; j++)
            {
                if (!mask[j]) continue;

                if (generator.nextDouble() < _probability)
                {
                    chosen.add(specimens.get(j));
                    break;
                } else fail++;

                if ((fail > _pickLimit) ||(fail == _k))
                {
                    chosen.add(specimens.get(j));
                    break;
                }

            }

        }

        for (int i = 0; i < chosen.size(); i += 2)
            result.add(new Parents(chosen.get(i), chosen.get(i + 1)));

        return result;
    }

}
