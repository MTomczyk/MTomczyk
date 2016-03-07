package reproducer;

import utils.Shuffle;
import interfaces.ISpecimen;
import reproducer.interfaces.IParents;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-02-16.
 *
 * It represents parents object. It is just a container for ISpecimen array.
 *
 */
public class Parents implements IParents
{
    private ArrayList<ISpecimen> _parents = null;

    /**
     * Simple constructor.
     * @param parents Specimens which are selected parents for reproduction purpose.
     */
    @SuppressWarnings("unused")
    public Parents(ArrayList<ISpecimen> parents)
    {
        this._parents = parents;
    }

    /**
     * Simple constructor.
     * @param father Selected father.
     * @param mother Selected mother.
     */
    public Parents(ISpecimen father, ISpecimen mother)
    {
        this._parents = new ArrayList<>(2);
        this._parents.add(father);
        this._parents.add(mother);
    }

    /**
     * Return array of parents.
     * @return Array of parents.
     */
    @Override
    public ArrayList<ISpecimen> getParents()
    {
        return this._parents;
    }

    /**
     * Return nth parent.
     * @param n n-number.
     * @return Specimen
     */
    @Override
    public ISpecimen getParent(int n)
    {
        return this._parents.get(n);
    }

    /**
     * Shuffle array of parents.
     */
    @Override
    public void shuffleParents()
    {
        Shuffle<ISpecimen> shuffle = new Shuffle<>();
        shuffle.shuffle(_parents);
    }
}
