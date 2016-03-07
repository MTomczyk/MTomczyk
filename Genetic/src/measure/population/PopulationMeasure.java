package measure.population;

import interfaces.ISpecimen;
import measure.GenerationMeasure;
import measure.Record;
import measure.population.interfaces.IStatisticExtractor;
import measure.population.specimen.SpecimenMeasure;
import measure.population.specimen.interfaces.IFeatureExtractor;
import normalization.interfaces.INormalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
// TODO JAVADOC TEST

/**
 * Created by Michał on 2015-02-14.
 *
 */
public class PopulationMeasure
{
    private SpecimenMeasure _specimenMeasure = null;
    private boolean _keepSpecimenStatistics = false;

    @SuppressWarnings("unused")
    public PopulationMeasure()
    {
        this._specimenMeasure = new SpecimenMeasure();
    }

    public PopulationMeasure(boolean keepSpecimenStatistics)
    {
        this._keepSpecimenStatistics = keepSpecimenStatistics;
        this._specimenMeasure = new SpecimenMeasure();
    }


    public GenerationMeasure getRecord(ArrayList<ISpecimen> specimen, ArrayList<IFeatureExtractor> featureExtractor,
                                       ArrayList<IStatisticExtractor> statisticExtractor, HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, INormalization>> normalization)
    {
        GenerationMeasure result;
        if (_keepSpecimenStatistics) result = new GenerationMeasure(specimen.size());
        else result = new GenerationMeasure(0);

        // INDIVIDUAL
        ArrayList<Record> _sMeasure = new ArrayList<>(specimen.size());
        for (ISpecimen s : specimen)
            _sMeasure.add(_specimenMeasure.getRecord(s, featureExtractor));

        if (_keepSpecimenStatistics)
            result._specimenMeasure.addAll(specimen.stream().map(s -> _specimenMeasure.getRecord(s, featureExtractor)).collect(Collectors.toList()));

        // POPULATION
        if (statisticExtractor != null)
        {
            for (IFeatureExtractor fe : featureExtractor)
            {
                String feKey = fe.getKey();
                Record r = new Record(feKey);
                for (IStatisticExtractor se : statisticExtractor)
                {
                    String seKey = se.getKey();
                    Object v = null;

                    if (_sMeasure.get(0).getObject(feKey) != null)
                        //noinspection ConstantConditions
                        v = se.getValue(_sMeasure, feKey, null);

                    if ((normalization != null) && (v != null))
                    {
                        if (normalization.get(fe) != null)
                        {
                            if (normalization.get(fe).get(se) != null)
                            {
                                double e = (Double) v;
                                v = normalization.get(fe).get(se).getNormalized(e);
                            }
                        }
                    }

                    r.putObject(seKey, v);
                }
                result._populationMeasure.put(fe.getKey(), r);
            }

        }

        _sMeasure.clear();
        //noinspection UnusedAssignment
        _sMeasure = null;

        return result;
    }

}
