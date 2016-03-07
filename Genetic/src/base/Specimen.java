package base;

import java.util.ArrayList;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import interfaces.IGene;
import interfaces.ISpecimen;
import shared.SC;

public class Specimen implements ISpecimen
{
    private IGene _gene;
    private IAlternative _alternative = null;
    private ArrayList<ICriterion> _criterion = null;
    private String _name = null;

    public Specimen(String name, ArrayList<ICriterion> criterion)
    {
        this._alternative = new Alternative(name, criterion);
        this._criterion = criterion;
        this._name = name;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public ISpecimen clone()
    {
        ISpecimen specimen = new Specimen(_name, _criterion);
        specimen.setAlternative(_alternative.clone());
        specimen.setGene(this._gene.clone());
        return specimen;
    }

    @Override
    public boolean isEqual(ISpecimen s, Double epsilon)
    {
        return this._gene.isEqual(s.getGene(), epsilon);
    }

    @Override
    public void print()
    {
        SC.getInstance().log("base.Specimen: \n");
        this._gene.print(1);
    }

    @Override
    public void printEvaluation()
    {
        SC.getInstance().setDebug(true);
        SC.getInstance().log(_name + " ");
        for (ICriterion c : _criterion)
            SC.getInstance().log(String.format("%f ", _alternative.getEvaluationAt(c)));
        SC.getInstance().log("\n");
        SC.getInstance().setDebug(false);
    }

    @Override
    public void setGene(IGene gene)
    {
        this._gene = gene;
    }

    @Override
    public IGene getGene()
    {
        return _gene;
    }

    @Override
    public void setAlternative(IAlternative alternative)
    {
        this._alternative = alternative;
    }

    @Override
    public IAlternative getAlternative()
    {
        return this._alternative;
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public void setName(String name)
    {
        this._name = name;
        this.getAlternative().setName(name);
    }


}
