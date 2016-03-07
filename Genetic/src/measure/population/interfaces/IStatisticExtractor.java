package measure.population.interfaces;


import measure.Record;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-02-14.
 *
 */
public interface IStatisticExtractor
{
    String getKey();
    Object getValue(ArrayList<Record> record, String innerKey, double params[]);
}
