package base;

import interfaces.IGene;
import shared.SC;

import java.util.HashMap;
import java.util.Map;


// TODO WRITE TEST JAVADOC

public class Gene implements IGene
{
    private double _value[];
    private HashMap<String, IGene> _gene;

    public Gene()
    {
        this._gene = null;
        this._value = null;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public IGene clone()
    {
        IGene result = new Gene();
        if (this._value != null) result.setValues(this._value.clone());

        if (this._gene != null)
        {
            HashMap<String, IGene> gene = new HashMap<>(this._gene.size());
            for (Map.Entry<String, IGene> entry : this._gene.entrySet())
            {
                gene.put(entry.getKey(), entry.getValue().clone());
            }
            result.setGene(gene);
        }

        return result;
    }


    public boolean isEqual(IGene g, Double epsilon)
    {
        if ((this._value == null) && (g.getValues() != null)) return false;
        if ((this._value != null) && (g.getValues() == null)) return false;
        if ((this._value != null))
        {
            if (this._value.length != g.getValues().length) return false;
            else
            {
                for (int i = 0; i < this.getValues().length; i++)
                {
                    if (epsilon != null)
                    {
                        if (!this.isEqual(this._value[i], g.getValues()[i], epsilon)) return false;
                    } else
                    {
                        if (Double.compare(this._value[i], g.getValues()[i]) != 0) return false;
                    }
                }
            }
        }

        if ((this._gene == null) && (g.getGene() != null)) return false;
        if ((this._gene != null) && (g.getGene() == null)) return false;
        if ((this._gene != null))
        {
            // CHECK EXISTS
            for (Map.Entry<String, IGene> entry : this._gene.entrySet())
                if (g.getGene().get(entry.getKey()) == null) return false;

            for (Map.Entry<String, IGene> entry : g.getGene().entrySet())
                if (this.getGene().get(entry.getKey()) == null) return false;

            for (Map.Entry<String, IGene> entry : this._gene.entrySet())
            {
                if (!this.getGene().get(entry.getKey()).isEqual(g.getGene().get(entry.getKey()), epsilon))
                    return false;
            }
        }

        return true;
    }

    public boolean isEqual(double a, double b, double epsilon)
    {
        return (a >= b - epsilon) && (a <= b + epsilon);
    }

    @Override
    public void print(int level)
    {
        if (this._value != null)
        {
            SC.getInstance().log("Value: ");
            for (double a_value : this._value) SC.getInstance().log(String.format("%.2f ", a_value));

            SC.getInstance().log("\n");
        }
        if (this._gene != null) for (Map.Entry<String, IGene> entry : _gene.entrySet())
        {
            for (int i = 0; i < level; i++)
                SC.getInstance().log("   ");
            SC.getInstance().log(String.format("%s\n", entry.getKey()));

            double value[] = entry.getValue().getValues();
            if (value != null)
            {
                for (int i = 0; i < level + 1; i++)
                    SC.getInstance().log("   ");

                for (double aValue : value)
                {
                    SC.getInstance().log(String.format("%.3f ", aValue));
                }
                SC.getInstance().log("\n");
            }

            if (entry.getValue().getGene() != null)
            {
                entry.getValue().print(level + 1);
            }
        }
    }

    @Override
    public void setValues(double[] values)
    {
        this._value = values;
    }

    @Override
    public double[] getValues()
    {
        return this._value;
    }

    @Override
    public void setGene(HashMap<String, IGene> gene)
    {
        this._gene = gene;
    }

    @Override
    public HashMap<String, IGene> getGene()
    {
        return this._gene;
    }
}
