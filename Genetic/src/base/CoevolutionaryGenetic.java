package base;

import interfaces.ICoevolutionaryAction;
import interfaces.IGenetic;
import interfaces.ISpecimen;
import sort.interfaces.ISorter;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by MTomczyk on 13.02.2016.
 */
public class CoevolutionaryGenetic implements IGenetic
{
    private String _name = null;
    private ArrayList<IGenetic> _genetic = null;
    private double _elapsedTime = 0.0d;
    private ICoevolutionaryAction _action = null;

    public CoevolutionaryGenetic(String name, ArrayList<IGenetic> genetic, ICoevolutionaryAction action)
    {
        this._name = name;
        this._genetic = genetic;
        this._elapsedTime = 0.0d;
        this._action = action;
    }

    @Override
    public void stepPreparation(int generation)
    {

    }


    @Override
    public void init()
    {
        long startTime = System.nanoTime();
        _genetic.forEach(interfaces.IGenetic::init);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void evaluate(int generation, boolean log)
    {
        long startTime = System.nanoTime();
        for (IGenetic g: _genetic)
            g.evaluate(generation, false);
        if (_action != null)
            _action.doAfterEvaluation(generation);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void sort(int generation)
    {
        long startTime = System.nanoTime();
        for (IGenetic g: _genetic) g.sort(generation);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void select()
    {
        long startTime = System.nanoTime();
        _genetic.forEach(interfaces.IGenetic::select);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void reproduce(Integer generation)
    {
        long startTime = System.nanoTime();
        for (IGenetic g: _genetic) g.reproduce(generation);
        if (_action != null)
            _action.doAfterReproduction(generation);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void kill(int generation)
    {
        long startTime = System.nanoTime();
        for (IGenetic g: _genetic)
            g.kill(generation);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public ISorter getSorter()
    {
        return null;
    }

    @Override
    public ArrayList<ISpecimen> getSpecimens()
    {
        ArrayList<ISpecimen> specimen = new ArrayList<>();

        for (IGenetic g: _genetic)
        {
            ArrayList<ISpecimen> gs = g.getSpecimens();
            specimen.addAll(gs.stream().collect(Collectors.toList()));
        }

        return specimen;
    }

    @Override
    public void setSpecimens(ArrayList<ISpecimen> specimens)
    {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("Convert2streamapi")
    @Override
    public ArrayList<ISpecimen> getPareto()
    {
        ArrayList<ISpecimen> pareto = new ArrayList<>();

        for (IGenetic g: _genetic)
            pareto.add(g.getRepresentativeSpecimen());

        return pareto;
    }

    @Override
    public ArrayList<ISpecimen> getToDraw()
    {
        ArrayList<ISpecimen> pareto = new ArrayList<>();

        for (IGenetic g: _genetic)
        {
            ArrayList<ISpecimen> pg = g.getPareto();
            pareto.addAll(pg.stream().collect(Collectors.toList()));
        }
        return pareto;
    }

    @Override
    public ISpecimen getRepresentativeSpecimen()
    {
        return null;
    }

    @Override
    public void printPopulation()
    {
        _genetic.forEach(interfaces.IGenetic::printPopulation);
    }

    @Override
    public void printEvaluation(int specimens)
    {
        for (IGenetic g: _genetic)
            g.printEvaluation(specimens);
    }

    @Override
    public double getElapsedTime()
    {
        return _elapsedTime;
    }

    @Override
    public String getName()
    {
        return _name;
    }
}
