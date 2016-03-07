package base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import criterion.interfaces.ICriterion;
import interfaces.IGenetic;
import interfaces.ISpecimen;
import org.apache.commons.math3.random.MersenneTwister;

import shared.SC;
import sort.interfaces.ISorter;

public class DummyGenetic implements IGenetic
{
    // DATA
    private String _name = null;
    private ArrayList<ISpecimen> _specimen = null;
    private ArrayList<ICriterion> _criterion = null;

    // RANDOM
    @SuppressWarnings("unused")
    MersenneTwister generator = new MersenneTwister(System.currentTimeMillis());

    public DummyGenetic(ArrayList<ICriterion> criterion, LinkedList<double[]> p, String name)
    {
        this._criterion = criterion;
        this._name = name;
        this.init(p);
    }

    public DummyGenetic(ArrayList<ICriterion> criterion, String path, String delim, String name)
    {
        this._name = name;
        this._criterion = criterion;

        LinkedList<double[]> p = new LinkedList<>();

        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader(path));

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                String v[] = sCurrentLine.split(delim);
                double r[] = new double[v.length];
                for (int i = 0; i < v.length; i++)
                {
                    String t = v[i].replaceAll(",", ".");
                    r[i] = Double.parseDouble(t);
                }
                p.add(r);
            }

            this.init(p);

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void init(LinkedList<double[]> p)
    {
        if (p != null)
        {
            this._specimen = new ArrayList<>(p.size());
            for (double[] d : p)
            {
                ISpecimen s = new Specimen("DUMMY", _criterion);

                for (int i = 0; i < d.length; i++)
                    s.getAlternative().setEvaluationAt(_criterion.get(i), d[i]);
                _specimen.add(s);
            }
        }
        else
        {
            this._specimen = new ArrayList<>(1);
        }

    }

    @Override
    public void stepPreparation(int generation)
    {

    }


    @Override
    public void init()
    {

    }

    @Override
    public void evaluate(int generation, boolean log)
    {

    }

    @Override
    public void sort(int generation)
    {

    }

    @Override
    public void select()
    {

    }

    @Override
    public void reproduce(Integer generation)
    {

    }

    @Override
    public void kill(int generation)
    {

    }

    @Override
    public ISorter getSorter()
    {
        return null;
    }

    @Override
    public ArrayList<ISpecimen> getSpecimens()
    {
        return this._specimen;
    }

    @Override
    public void setSpecimens(ArrayList<ISpecimen> specimens)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void printPopulation()
    {

    }

    @SuppressWarnings("all")
    @Override
    public void printEvaluation(int specimens)
    {
        if (specimens < 0) specimens = this._specimen.size();
        /*for (int i = 0; i < specimens; i++)
        {
            this._specimen.get(i).printEvaluation();
        }*/
        SC.getInstance().log("\n");
    }

    @Override
    public double getElapsedTime()
    {
        return 0;
    }

    @Override
    public ArrayList<ISpecimen> getPareto()
    {
        return this._specimen;
    }

    @Override
    public ArrayList<ISpecimen> getToDraw()
    {
        return this._specimen;
    }

    @Override
    public ISpecimen getRepresentativeSpecimen()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return _name;
    }


}
