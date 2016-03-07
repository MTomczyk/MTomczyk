package reproducer.interfaces;

import interfaces.ISpecimen;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-02-16.
 *
 */

public interface IParents
{
    ArrayList<ISpecimen> getParents();
    ISpecimen getParent(int id);
    @SuppressWarnings("unused")
    void shuffleParents();
}
