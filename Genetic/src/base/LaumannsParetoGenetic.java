package base;

import criterion.interfaces.ICriterion;
import interfaces.IEvaluator;
import interfaces.IGenetic;
import interfaces.IInitializer;
import interfaces.ISpecimen;
import killer.Killer;
import killer.interfaces.IKiller;
import org.apache.commons.math3.random.MersenneTwister;
import reproducer.interfaces.IParents;
import reproducer.interfaces.IReproducer;
import select.Tournament;
import select.interfaces.ISelector;
import shared.SC;
import sort.Sorter;
import sort.functions.Front;
import sort.functions.TParetoUpdateFunction;
import sort.interfaces.ILog;
import sort.interfaces.ISorter;
import standard.Common;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LaumannsParetoGenetic implements IGenetic
{
    public static class Params
    {
        public int _populationSize = 50;
        public double _t[] = null;
        public IInitializer _initializer = null;
        public IEvaluator _evaluator = null;
        public ISorter _sorter = null;
        public ISelector _selector = null;
        public IReproducer _reproducer = null;
        public IKiller _killer = null;
        public Object _problem = null;
        public ArrayList<ICriterion> _criteria = null;
        public String name = null;
        public double _epsilon = Common.EPSILON;

        @SuppressWarnings("CloneDoesntCallSuperClone")
        public Params clone()
        {
            Params p = new Params();
            p._t = this._t;
            p._populationSize = this._populationSize;
            p._initializer = this._initializer;
            p._evaluator = this._evaluator;
            p._sorter = this._sorter;
            p._selector = this._selector;
            p._reproducer = this._reproducer;
            p._killer = this._killer;
            p._problem = this._problem;
            p.name = this.name;
            p._epsilon = this._epsilon;
            p._criteria = new ArrayList<>(this._criteria.size());
            p._criteria.addAll(this._criteria.stream().collect(Collectors.toList()));
            return p;
        }

    }

    // DATA
    private String _name = null;
    private int _populationSize = 0;
    public double _t[] = null;
    public double _epsilon = Common.EPSILON;
    private ArrayList<ISpecimen> _specimen = null;
    private ArrayList<IParents> _selection = null;
    private ArrayList<ICriterion> _criterion = null;
    private Object problem = null;

    private ArrayList<ISpecimen> _archive = null;

    // DEFAULT
    private IInitializer _initializer = null;
    private IEvaluator _evaluator = null;
    private ISorter _sorter = null;
    private ISelector _selector = new Tournament(new Tournament.Params());
    private IReproducer _reproducer = null;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private IKiller _killer = new Killer();

    // RANDOM
    MersenneTwister generator = new MersenneTwister(System.currentTimeMillis());

    public double _elapsedTime = 0.0d;

    private ILog _lastLog = null;

    public LaumannsParetoGenetic(Params params)
    {
        this._populationSize = params._populationSize;
        this._specimen = new ArrayList<>(this._populationSize + 1);
        this._archive = new ArrayList<>(this._populationSize + 1);

        this.problem = params._problem;
        this._criterion = params._criteria;

        this._name = params.name;
        this._t = params._t;

        if (params._initializer != null) this._initializer = params._initializer;
        if (params._evaluator != null) this._evaluator = params._evaluator;
        if (params._sorter != null) this._sorter = params._sorter;
        else this._sorter = new Sorter();
        if (params._selector != null) this._selector = params._selector;
        if (params._reproducer != null) this._reproducer = params._reproducer;
        if (params._killer != null) this._killer = params._killer;

        this._elapsedTime = 0.0d;
        this._epsilon = params._epsilon;

        generator = SC.getInstance().getRandomNumberGenerator();
    }

    @Override
    public void stepPreparation(int generation)
    {

    }


    @Override
    public void init()
    {
        long startTime = System.nanoTime();
        this._initializer.createInitialPopulation(this._criterion, this._specimen, this._populationSize, generator,
                problem);
        this._archive = this._specimen;
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void evaluate(int generation, boolean log)
    {
        long startTime = System.nanoTime();
        this._evaluator.evaluate(_criterion, _specimen, problem, log);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }


    @Override
    public void select()
    {
        long startTime = System.nanoTime();
        int number = 1;

        if ((_lastLog != null) && (_lastLog.getLog("shifted") != null))
        {
            int shifted = (Integer) _lastLog.getLog("shifted");
            if (shifted > number) number = shifted;
        }

        _selection = this._selector.select(this._sorter.getReproductionPool(), number, generator);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void reproduce(Integer generation)
    {
        //printEvaluation(-1);

        long startTime = System.nanoTime();
        @SuppressWarnings("unused") int number = 1;

        ArrayList<ISpecimen> reproduction = this._reproducer.reproduce(_selection, problem, generation, generator);

        // Add and evaluate rest
        this._evaluator.evaluate(_criterion, reproduction, problem, false);

        // LAUMANNS
        //System.out.println(this._specimen.size() + " AAA " + this._archive.size());

        this._archive = TParetoUpdateFunction.getSet(this._archive, reproduction.get(0), _criterion, _t, _epsilon);

        /*if (this._archive.size() < this._populationSize)
            for (int i = 0; i < this._populationSize - this._archive.size(); i++)
                this._archive.add(this._specimen.get(i));*/

        this._specimen = this._archive;

        //System.out.println(this._specimen.size() + " BBB " + this._archive.size());


        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void sort(int generation)
    {
        long startTime = System.nanoTime();
        _lastLog = this._sorter.sort(_specimen, _criterion, generation);
        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public void kill(int generation)
    {
        long startTime = System.nanoTime();
        @SuppressWarnings("unused") int number = 1;
        if (_specimen.size() <= _populationSize) //noinspection UnusedAssignment
            number = 0;

        //this._killer.kill(_specimen, number);

        /*if (_killer != null)
        {
            if ((_lastLog != null) && (_lastLog.getLog("shifted") != null))
            {
                int shifted = (Integer) _lastLog.getLog("shifted");
                if (shifted > number) number = shifted;
                System.out.println("SH  " + shifted);
            }

            //System.out.println("BEFORE "  +_specimen.size());
            this._killer.kill(_specimen, number);
            //System.out.println("AFTER " + _specimen.size());
        }*/


        long endTime = System.nanoTime();
        this._elapsedTime += ((double)(endTime - startTime) / 1000000000.0);
    }

    @Override
    public ArrayList<ISpecimen> getSpecimens()
    {
        return this._specimen;
    }

    @Override
    public void printPopulation()
    {
        for (int i = 0; i < _specimen.size(); i++)
        {
            SC.getInstance().log(String.format("base.Specimen %d\n", i));
            this._specimen.get(i).print();
        }
    }

    @Override
    public void printEvaluation(int specimens)
    {
        _specimen.forEach(interfaces.ISpecimen::printEvaluation);
    }

    @Override
    public ArrayList<ISpecimen> getPareto()
    {
        return Front.getPareto(_archive, _criterion, _epsilon);
    }

    @Override
    public ISpecimen getRepresentativeSpecimen()
    {
        return null;
    }

    @Override
    public ISorter getSorter()
    {
        return _sorter;
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public double getElapsedTime()
    {
        return this._elapsedTime;
    }

    @Override
    public void setSpecimens(ArrayList<ISpecimen> specimens)
    {
        this._specimen = specimens;
    }

    @Override
    public ArrayList<ISpecimen> getToDraw()
    {
        return _sorter.getPareto();
    }
}
