package measure.population;

import measure.Record;
import measure.population.interfaces.IStatisticExtractor;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-02-23.
 *
 */
public class DummyExtractor implements IStatisticExtractor
{
    private String _name = "NaN";

    public DummyExtractor(String name)
    {
        this._name = name;
    }

    @Override
    public String getKey()
    {
        return _name;
    }

    @Override
    public Object getValue(ArrayList<Record> record, String innerKey, double[] params)
    {
        return null;
    }
}
