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
public class CoevolutionaryGeneticWitchBaseSwitch implements IGenetic
{
    public static int EXTRACTION_REPRESENTATIVE = 0;
    public static int EXTRACTION_LEAD = 1;

    private String _name = null;
    private ArrayList<IGenetic> _genetic = null;
    private IGenetic _baseGenetic = null;
    private int _SGE = 100;
    private double _elapsedTime = 0.0d;
    private ICoevolutionaryAction _action = null;

    private int _paretoExtraction = 0;

    private boolean _switched = false;

    public CoevolutionaryGeneticWitchBaseSwitch(String name,
                                                IGenetic baseGenetic,
                                                ArrayList<IGenetic> genetic,
                                                int SGE,
                                                ICoevolutionaryAction action,
                                                int paretoExtraction)
    {
        this._name = name;
        this._genetic = genetic;
        this._baseGenetic = baseGenetic;
        this._SGE = SGE;
        this._elapsedTime = 0.0d;
        this._action = action;
        this._paretoExtraction = paretoExtraction;
    }

    private void doSwitch(int generation)
    {
        _switched = true;
        ArrayList<ISpecimen> specimens = _baseGenetic.getSpecimens();
        int div = specimens.size() / _genetic.size();

        for (int g = 0; g < _genetic.size(); g++)
        {
            int left = g * div;
            int right = (g + 1) * div;
            if (right > specimens.size())
                right = specimens.size();

            ArrayList<ISpecimen> partition = new ArrayList<>(div);
            for (int i = left; i < right; i++)
                partition.add(specimens.get(i));
            _genetic.get(g).setSpecimens(partition);
            _genetic.get(g).sort(generation);

        }
        for (IGenetic g: _genetic)
            g.evaluate(generation, false);
    }

    @Override
    public void stepPreparation(int generation)
    {
        if (generation == _SGE)
            doSwitch(generation);

    }


    @Override
    public void init()
    {
        long startTime = System.nanoTime();
        if (_switched) _genetic.forEach(interfaces.IGenetic::init);
        else _baseGenetic.init();
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void evaluate(int generation, boolean log)
    {
        long startTime = System.nanoTime();
        if (_switched)
        {
            for (IGenetic g: _genetic)
                g.evaluate(generation, false);
            if (_action != null)
                _action.doAfterEvaluation(generation);
        }
        else
        {
            _baseGenetic.evaluate(generation, log);
        }
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void sort(int generation)
    {
        long startTime = System.nanoTime();
        if (_switched)
            for (IGenetic g: _genetic) g.sort(generation);
        else
            _baseGenetic.sort(generation);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void select()
    {
        long startTime = System.nanoTime();
        if (_switched)
            _genetic.forEach(interfaces.IGenetic::select);
        else
            _baseGenetic.select();
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void reproduce(Integer generation)
    {
        long startTime = System.nanoTime();
        if (_switched)
        {
            for (IGenetic g: _genetic) g.reproduce(generation);
            if (_action != null)
                _action.doAfterReproduction(generation);
        }
        else
        {
            _baseGenetic.reproduce(generation);
        }

        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void kill(int generation)
    {
        long startTime = System.nanoTime();
        if (_switched)
        {
            for (IGenetic g: _genetic)
                g.kill(generation);
        }
        else
        {
            _baseGenetic.kill(generation);
        }

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

        if (_switched)
        {
            for (IGenetic g: _genetic)
            {
                ArrayList<ISpecimen> gs = g.getSpecimens();
                specimen.addAll(gs.stream().collect(Collectors.toList()));
            }
        }
        else
        {
            return _baseGenetic.getSpecimens();
        }

        return specimen;
    }

    @SuppressWarnings("Convert2streamapi")
    @Override
    public ArrayList<ISpecimen> getPareto()
    {

        ArrayList<ISpecimen> pareto = new ArrayList<>();

        if (_switched)
        {
            if (_paretoExtraction == EXTRACTION_REPRESENTATIVE)
            {
                for (IGenetic g: _genetic)
                    pareto.add(g.getRepresentativeSpecimen());
            }
            else if (_paretoExtraction == EXTRACTION_LEAD)
            {
                ArrayList<ISpecimen> pr = _genetic.get(0).getPareto();
                for (ISpecimen spec: pr) pareto.add(spec);
            }

        }
        else
        {
            return _baseGenetic.getPareto();
        }


        return pareto;
    }

    @Override
    public ArrayList<ISpecimen> getToDraw()
    {
        ArrayList<ISpecimen> pareto = new ArrayList<>();

        if (_switched)
        {
            for (IGenetic g: _genetic)
            {
                ArrayList<ISpecimen> pg = g.getPareto();
                pareto.addAll(pg.stream().collect(Collectors.toList()));
            }
        }
        else
        {
            return _baseGenetic.getToDraw();
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
        if (_switched)
        {
            _genetic.forEach(interfaces.IGenetic::printPopulation);
        }
        else
        {
            _baseGenetic.printPopulation();
        }

    }

    @Override
    public void printEvaluation(int specimens)
    {
        if (_switched)
        {
            for (IGenetic g: _genetic)
                g.printEvaluation(specimens);
        }
        else
        {
            _baseGenetic.printPopulation();
        }

    }

    @Override
    public void setSpecimens(ArrayList<ISpecimen> specimens)
    {
        throw new UnsupportedOperationException();
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
