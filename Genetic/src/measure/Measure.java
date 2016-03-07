package measure;

import interfaces.IGenetic;
import interfaces.ISpecimen;
import measure.population.PopulationMeasure;
import measure.population.interfaces.IPopulationComprehensive;
import measure.population.interfaces.IStatisticExtractor;
import measure.population.specimen.interfaces.IFeatureExtractor;
import normalization.MinMaxNormalization;
import normalization.interfaces.INormalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// TODO JAVADOC TEST

/**
 * Created by Michał on 2015-02-14.
 */
public class Measure
{

    public static class Params
    {
        public ArrayList<IStatisticExtractor> _statisticExtractor = null;
        public ArrayList<IFeatureExtractor> _featureExtractor = null;
        public ArrayList<IPopulationComprehensive> _populationComprehensive = null;
        public HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, INormalization>> _normalization = null;
        public IGenetic _genetic = null;
        public int _generations = 1;
        public boolean _keepSpecimenStatistics = false;
    }

    private ArrayList<GenerationMeasure> _data = null;
    private PopulationMeasure _populationMeasure = null;
    private ArrayList<IStatisticExtractor> _statisticExtractor = null;
    private ArrayList<IFeatureExtractor> _featureExtractor = null;
    private ArrayList<IPopulationComprehensive> _populationComprehensive = null;
    private HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, INormalization>> _normalization = null;
    private IGenetic _genetic = null;


    public Measure(Params p)
    {
        this._data = new ArrayList<>(p._generations);
        this._populationMeasure = new PopulationMeasure(p._keepSpecimenStatistics);
        this._populationComprehensive = p._populationComprehensive;
        this._statisticExtractor = p._statisticExtractor;
        this._featureExtractor = p._featureExtractor;
        this._normalization = p._normalization;
        this._genetic = p._genetic;
    }


    public GenerationMeasure updateData(ArrayList<ISpecimen> specimen)
    {
        GenerationMeasure data = _populationMeasure.getRecord(specimen, _featureExtractor, _statisticExtractor, _normalization);
        this._data.add(data);

        if (_genetic != null)
        {
            if (_populationComprehensive != null)
            {
                for (IPopulationComprehensive pc : _populationComprehensive)
                {
                    if (data._populationMeasure.get(pc.getKey()) == null)
                        data._populationMeasure.put(pc.getKey(), new Record(pc.getKey()));

                    Object v = pc.getValue(_genetic, this._data.size(), specimen);
                    data._populationMeasure.get(pc.getKey()).putObject(pc.getKey(), v);
                }
            }
        }

        return data;
    }


    public ArrayList<GenerationMeasure> getData()
    {
        return _data;
    }

    public Double calculateAUCTimeFor(IFeatureExtractor fe, IStatisticExtractor se, int generation)
    {
        return calculateAUCTimeFor(fe, se, generation, new MinMaxNormalization(0.0d, 1.0d));
    }

    public Double calculateAUCTimeFor(IFeatureExtractor fe, IStatisticExtractor se, int generation,
                                      INormalization normalization)
    {

        double v = 0.0d;
        for (int g = 0; g <= generation; g++)
        {
            if (_data.get(g)._populationMeasure.get("ElapsedTime") == null) return null;
            if (_data.get(g)._populationMeasure.get(fe.getKey()).getObject(se.getKey()) == null) return null;
            double i = (Double) _data.get(g)._populationMeasure.get(fe.getKey()).getObject(se.getKey());
            v += normalization.getNormalized(i);
        }
        v /= (double) (generation + 1);
        double time = (Double) _data.get(generation)._populationMeasure.get("ElapsedTime").getObject("ElapsedTime");

        return v * time;
    }


    public Double calculateAUCFor(IFeatureExtractor fe, IStatisticExtractor se, int generation)
    {
        return calculateAUCFor(fe, se, generation, new MinMaxNormalization(0.0d, 1.0d));
    }

    public Double calculateAUCFor(IFeatureExtractor fe, IStatisticExtractor se, int generation,
                                  INormalization normalization)
    {
        double v = 0.0d;
        for (int g = 0; g <= generation; g++)
        {
            if (_data.get(g)._populationMeasure.get(fe.getKey()).getObject(se.getKey()) == null) return null;
            double i = (Double) _data.get(g)._populationMeasure.get(fe.getKey()).getObject(se.getKey());
            v += normalization.getNormalized(i);
        }
        v /= (double) (generation + 1);
        return v;
    }


}
