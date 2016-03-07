package measure.population;

import measure.Record;
import measure.population.interfaces.IStatisticExtractor;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;
// TODO JAVADOC TEST
/**
 * Created by Michał on 2015-02-14.
 *
 */
public class SDExtractor implements IStatisticExtractor
{
    @Override
    public String getKey()
    {
        return "SD";
    }

    @Override
    public Object getValue(ArrayList<Record> record, String innerKey, double params[])
    {
        double v[] = new double[record.size()];
        for (int r = 0; r < record.size(); r++)
            v[r] = (Double) record.get(r).getObject(innerKey);


        return Math.sqrt(StatUtils.variance(v));
    }
}