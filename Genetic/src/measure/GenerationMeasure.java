package measure;


import java.util.ArrayList;
import java.util.HashMap;

// TODO JAVA DOC TEST

/**
 * Created by Michał on 2015-02-14.
 *
 */
public class GenerationMeasure
{
    public ArrayList<Record> _specimenMeasure = null;
    public HashMap<String, Record> _populationMeasure = null;

    public GenerationMeasure(int size)
    {
        if (size > 0) this._specimenMeasure = new ArrayList<>(size);
        else this._specimenMeasure = null;

        this._populationMeasure = new HashMap<>();

    }
}
