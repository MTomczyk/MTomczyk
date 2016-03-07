package runner;

import java.util.ArrayList;

import criterion.interfaces.ICriterion;
import interfaces.IGenetic;
import runner.interfaces.IRunner;
import runner.interfaces.IRunnerDrawer;

/**
 * Class which take care of evolutionary algorithm run.
 */
public class Runner implements IRunner
{
    private ArrayList<IGenetic> genetic = null;
    private ArrayList<ICriterion> criterion = null;
    private IRunnerDrawer drawer = null;
    private int _repeat[] = null;

    /**
     * Simple constructor.
     * @param genetic Array of genetic algorithms.
     * @param criterion Array of criteria.
     * @param drawer Drawer object to visualize evolution. Can be null.
     */
    public Runner(ArrayList<IGenetic> genetic, ArrayList<ICriterion> criterion, IRunnerDrawer drawer)
    {
        this(genetic, criterion, drawer, null);
    }

    public Runner(ArrayList<IGenetic> genetic, ArrayList<ICriterion> criterion, IRunnerDrawer drawer, int repeat[])
    {
        if (repeat == null)
        {
            this._repeat = new int [genetic.size()];
            for (int i = 0; i < genetic.size(); i++)
                this._repeat[i] = 1;
        }
        else this._repeat = repeat;
        this.genetic = genetic;
        this.criterion = criterion;
        this.drawer = drawer;
        if (drawer != null) drawer.init(genetic, criterion);
    }


    /**
     * Run algorithm with params.
     * @param generations number of generations
     */
    @Override
    public void run(int generations)
    {
        // --- CUBE -----------------------
        if (drawer != null) drawer.start();

        // --- INIT
        @SuppressWarnings("unused") int gSize = genetic.size();
        genetic.forEach(interfaces.IGenetic::init);

        // --- EVALUATE
        for (IGenetic aGenetic : genetic) aGenetic.evaluate(-1, false);

        // --- SORT
        for (IGenetic aGenetic : genetic) aGenetic.sort(-1);


        for (int g = 0; g < generations; g++)
        {

            for (int a = 0; a < genetic.size(); a++)
            {
                if (_repeat != null)
                    for (int i = 0; i < _repeat[a]; i++)
                    {
                        genetic.get(a).stepPreparation(g);
                        genetic.get(a).select();
                        genetic.get(a).reproduce(g);
                        genetic.get(a).sort(g);
                        genetic.get(a).kill(g);
                    }
            }

            // --- CHARTS ---------------------------

            if (drawer != null) drawer.update(genetic, criterion);
        }

        // --- END LOOP
    }

    /**
     * Init runner if you want to iterate algorithms step by step.
     */
    @Override
    public void init()
    {
        // --- CUBE -----------------------
        if (drawer != null) drawer.start();

        // --- INIT
        @SuppressWarnings("unused") int gSize = genetic.size();
        genetic.forEach(interfaces.IGenetic::init);

        // --- EVALUATE
        for (IGenetic aGenetic : genetic) aGenetic.evaluate(-1, false);

        // --- SORT
        for (IGenetic aGenetic : genetic) aGenetic.sort(-1);
    }

    /**
     * Execute next step of algorithms.
     * @param generation Time
     */
    @Override
    public void step(int generation)
    {
        @SuppressWarnings("unused") int gSize = genetic.size();

        for (int a = 0; a < genetic.size(); a++)
        {
            //System.out.println(genetic.get(a).getName());
            if (_repeat != null)
                for (int i = 0; i < _repeat[a]; i++)
                {
                    genetic.get(a).stepPreparation(generation);
                    genetic.get(a).select();
                    genetic.get(a).reproduce(generation);
                    genetic.get(a).sort(generation);
                    genetic.get(a).kill(generation);
                }
        }

        // --- CHARTS ---------------------------

        if (drawer != null) drawer.update(genetic, criterion);
    }


}
