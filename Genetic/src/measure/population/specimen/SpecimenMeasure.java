package measure.population.specimen;

import interfaces.ISpecimen;
import measure.Record;
import measure.population.specimen.interfaces.IFeatureExtractor;

import java.util.ArrayList;

// TODO JAVADOC TEST

/**
 * Created by Michał on 2015-02-14.
 *
 */
public class SpecimenMeasure
{
    public Record getRecord(ISpecimen specimen, ArrayList<IFeatureExtractor> featureExtractor)
    {
        Record r = new Record(specimen.getName());

        for (IFeatureExtractor e : featureExtractor)
        {
            String key = e.getKey();
            Object value = e.getValue(specimen);
            r.putObject(key, value);
        }

        return r;
    }
}
