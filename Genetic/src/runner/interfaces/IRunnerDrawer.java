package runner.interfaces;

import criterion.interfaces.ICriterion;
import interfaces.IGenetic;
import interfaces.ISpecimen;

import java.util.ArrayList;

public interface IRunnerDrawer
{
    void init(ArrayList<IGenetic> genetic, ArrayList<ICriterion> criterion);
    void update(ArrayList<IGenetic> genetic, ArrayList<ICriterion> criterion);
    @SuppressWarnings("unused")
    void updateDirect(ArrayList<IGenetic> genetic, ArrayList<ArrayList<ISpecimen>> specimen,
                      ArrayList<ICriterion> criterion);
    void start();
    @SuppressWarnings("unused")
    void stop();
}
