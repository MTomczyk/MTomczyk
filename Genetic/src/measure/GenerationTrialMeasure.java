package measure;

import interfaces.IGenetic;
import measure.population.DummyExtractor;
import measure.population.interfaces.IPopulationComprehensive;
import measure.population.interfaces.IStatisticExtractor;
import measure.population.specimen.interfaces.IFeatureExtractor;
import standard.Common;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Michał on 2015-02-23.
 */
public class GenerationTrialMeasure
{
    public static class Monotonic
    {
        public boolean _lessPreferable = true;
        public String _referenceKey = null;

        public Monotonic(String referenceKey)
        {
            this(referenceKey, true);
        }

        public Monotonic(String referenceKey, boolean lessPreferable)
        {
            this._referenceKey = referenceKey;
            this._lessPreferable = lessPreferable;
        }
    }

    public static class Params
    {
        public ArrayList<IGenetic> _genetic = null;
        public ArrayList<IFeatureExtractor> _featureExtractor = null;
        public ArrayList<IStatisticExtractor> _statisticExtractor = null;
        public ArrayList<IStatisticExtractor> _internalStatisticExtractor = null;
        public ArrayList<IPopulationComprehensive> _populationComprehensive = null;
        public HashMap<String, HashMap<String, Monotonic>> _monotonic = null;

        public int _generations = 1;
        public int _trials = 1;
        public boolean _performAUC = true;
        public boolean _performAUCTime = true;
    }


    // IN
    // GENETIC / TRIAL
    private HashMap<IGenetic, ArrayList<Measure>> _data = null;

    // OUT
    // GENETIC / GENERATION / FEATURE / STATISTIC / INTERNAL STATISTIC
    public HashMap<IGenetic, ArrayList<HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>>>> _statistic = null;

    private ArrayList<IGenetic> _genetic = null;
    private ArrayList<IFeatureExtractor> _featureExtractor = null;
    private ArrayList<IStatisticExtractor> _statisticExtractor = null;
    private ArrayList<IStatisticExtractor> _internalStatisticExtractor = null;

    private HashMap<String, IStatisticExtractor> _dummyAUCTimeExtractor = null;
    private HashMap<String, IStatisticExtractor> _dummyAUCExtractor = null;

    private HashMap<String, HashMap<String, GenerationTrialMeasure.Monotonic>> _monotonic = null;


    public boolean _performAUCTime = true;
    public boolean _performAUC = true;
    private int _generations = 1;
    private int _trials = 1;

    public GenerationTrialMeasure(Params p)
    {
        if (p._populationComprehensive != null)
        {
            for (IPopulationComprehensive pc : p._populationComprehensive)
            {
                p._statisticExtractor.add(new DummyExtractor(pc.getKey()));
                p._featureExtractor.add(new measure.population.specimen.DummyExtractor(pc.getKey()));
            }
        }

        this._genetic = p._genetic;
        this._featureExtractor = p._featureExtractor;
        this._statisticExtractor = p._statisticExtractor;
        this._internalStatisticExtractor = p._internalStatisticExtractor;

        this._monotonic = p._monotonic;
        this._generations = p._generations;
        this._trials = p._trials;
        this._performAUC = p._performAUC;
        this._performAUCTime = p._performAUCTime;

        this._dummyAUCExtractor = new HashMap<String, IStatisticExtractor>();
        for (IStatisticExtractor se : _statisticExtractor)
            _dummyAUCExtractor.put(se.getKey(), new DummyExtractor(se.getKey() + " AUC"));

        this._dummyAUCTimeExtractor = new HashMap<String, IStatisticExtractor>();
        for (IStatisticExtractor se : _statisticExtractor)
            _dummyAUCTimeExtractor.put(se.getKey(), new DummyExtractor(se.getKey() + " AUC Time"));

        this._data = new HashMap<IGenetic, ArrayList<Measure>>();
        for (IGenetic g : _genetic)
            this._data.put(g, new ArrayList<Measure>(this._trials));

        _statistic = initStatistics();
    }


    public void addData(IGenetic genetic, Measure measure)
    {
        this._data.get(genetic).add(measure);
    }

    public void calculateStatistics()
    {
        // FOR EACH GENETIC
        for (IGenetic genetic : _genetic)
        {
            // FOR EACH GENERATION
            for (int g = 0; g < _generations; g++)
            {
                // FOR EACH FEATURE EXTRACTOR
                for (IFeatureExtractor fe : _featureExtractor)
                {
                    //System.out.println("- " + fe.getKey());
                    //System.out.println("- " + g);
                    //if (_data.get(genetic).size() == 0) System.out.println("UPS");

                    // IF EXISTS
                    if (_data.get(genetic).get(0).getData().get(g)._populationMeasure.get(fe.getKey()) == null)
                        continue;

                    // COLLECT DATA
                    ArrayList<Record> r = new ArrayList<Record>(_trials);
                    for (int t = 0; t < _trials; t++)
                    {
                        Record record = _data.get(genetic).get(t).getData().get(g)._populationMeasure.get(fe.getKey());

                        // ADD AUC
                        if (_performAUC) for (IStatisticExtractor se : _statisticExtractor)
                        {
                            Double v = _data.get(genetic).get(t).calculateAUCFor(fe, se, g);

                            if (v == null) continue;
                            record.putObject((se.getKey() + " AUC"), v);
                        }

                        if (_performAUCTime) for (IStatisticExtractor se : _statisticExtractor)
                        {
                            Double v = _data.get(genetic).get(t).calculateAUCTimeFor(fe, se, g);

                            if (v == null) continue;
                            record.putObject((se.getKey() + " AUC Time"), v);
                        }

                        r.add(record);
                    }

                    // FOR EACH STATISTIC
                    for (IStatisticExtractor se : _statisticExtractor)
                    {
                        // IF EXISTS
                        if (r.get(0).getObject(se.getKey()) == null) continue;

                        // SINGLE
                        // CALCULATE VALUE FOR EACH INTERNAL STATISTIC
                        for (IStatisticExtractor ise : _internalStatisticExtractor)
                        {
                            Object v = ise.getValue(r, se.getKey(), null);
                            _statistic.get(genetic).get(g).get(fe).get(se).put(ise, v);

                        }

                        // AUC
                        if (_performAUC) for (IStatisticExtractor ise : _internalStatisticExtractor)
                        {
                            if (_statistic.get(genetic).get(g).get(fe).get(_dummyAUCExtractor.get(se.getKey())) == null)
                                continue;

                            Object v = ise.getValue(r, (se.getKey() + " AUC"), null);
                            _statistic.get(genetic).get(g).get(fe).get(_dummyAUCExtractor.get(se.getKey())).put(ise, v);
                        }

                        // AUC
                        if (_performAUCTime) for (IStatisticExtractor ise : _internalStatisticExtractor)
                        {
                            if (_statistic.get(genetic).get(g).get(fe).get(_dummyAUCTimeExtractor.get(se.getKey())) == null)
                                continue;

                            Object v = ise.getValue(r, (se.getKey() + " AUC Time"), null);
                            _statistic.get(genetic).get(g).get(fe).get(_dummyAUCTimeExtractor.get(se.getKey())).put(ise, v);
                        }
                    }
                }
            }
        }
    }

    public double[] getVectorPopulThroughGeneration(IGenetic genetic, IFeatureExtractor fe, IStatisticExtractor se,
                                                    IStatisticExtractor ise)
    {
        double result[] = new double[_generations];

        for (int g = 0; g < _generations; g++)
            result[g] = (Double) _statistic.get(genetic).get(g).get(fe).get(se).get(ise);

        return result;
    }

    public double[] getVectorAUCThroughGeneration(IGenetic genetic, IFeatureExtractor fe, IStatisticExtractor se,
                                                  IStatisticExtractor ise)
    {
        double result[] = new double[_generations];

        for (int g = 0; g < _generations; g++)
            result[g] = (Double) _statistic.get(genetic).get(g).get(fe).get(_dummyAUCExtractor.get(se.getKey())).get(
                    ise);

        return result;
    }

    public double[] getVectorAUCTimeThroughGeneration(IGenetic genetic, IFeatureExtractor fe, IStatisticExtractor se,
                                                      IStatisticExtractor ise)
    {
        double result[] = new double[_generations];

        for (int g = 0; g < _generations; g++)
            result[g] = (Double) _statistic.get(genetic).get(g).get(fe).get(_dummyAUCTimeExtractor.get(se.getKey())).get(
                    ise);

        return result;
    }

    public Integer getMonotonicGeneration(IGenetic genetic, IFeatureExtractor fe, IStatisticExtractor se, int mode)
    {
        // FIND ISE
        if ((_monotonic.get(fe.getKey()) == null) || (_monotonic.get(fe.getKey()).get(se.getKey()) == null))
            return null;

        Monotonic m = _monotonic.get(fe.getKey()).get(se.getKey());
        String s = m._referenceKey;
        IStatisticExtractor ise = null;
        for (IStatisticExtractor ie : _internalStatisticExtractor)
            if (ie.getKey().equals(s))
            {
                ise = ie;
                break;
            }
        if (ise == null) return null;
        double tGeneration[] =  getVectorPopulThroughGeneration(genetic, fe, se, ise);
        if (mode == 1) getVectorAUCThroughGeneration(genetic, fe, se, ise);
        else if (mode == 2) getVectorAUCTimeThroughGeneration(genetic, fe, se, ise);

        double best = Common.MAX_DOUBLE;
        if (!m._lessPreferable) best = Common.MIN_DOUBLE;
        int gen = 0;

        for (int i = 0; i < tGeneration.length; i++)
        {
            if ((m._lessPreferable) && (tGeneration[i] < best))
            {
                best = tGeneration[i];
                gen = i;
            } else if ((!m._lessPreferable) && (tGeneration[i] > best))
            {
                best = tGeneration[i];
                gen = i;
            }
        }

        return gen;
    }



    public double[] getVectorPopulBestTest(IGenetic genetic, IFeatureExtractor fe, IStatisticExtractor se)
    {
        Integer gen = getMonotonicGeneration(genetic, fe, se, 0);
        if (gen == null) gen = _generations - 1;
        return getVectorPopulTest(genetic, gen, fe, se);
    }

    public double[] getVectorPopulTest(IGenetic genetic, int generation, IFeatureExtractor fe, IStatisticExtractor se)
    {
        double result[] = new double[_trials];
        for (int t = 0; t < _trials; t++)
        {
            Measure m = _data.get(genetic).get(t);

            Object o;
            o = m.getData().get(generation)._populationMeasure.get(fe.getKey()).getObject(se.getKey());
            double v = (Double) o;
            result[t] = v;
        }
        return result;
    }

    public double[] getVectorAUCBestTest(IGenetic genetic, IFeatureExtractor fe, IStatisticExtractor se)
    {
        Integer gen = getMonotonicGeneration(genetic, fe, se, 1);
        if (gen == null) gen = _generations - 1;
        return getVectorAUCTest(genetic, gen, fe, se);
    }


    public double[] getVectorAUCTest(IGenetic genetic, int generation, IFeatureExtractor fe, IStatisticExtractor se)
    {
        double result[] = new double[_trials];
        for (int t = 0; t < _trials; t++)
        {
            Measure m = _data.get(genetic).get(t);

            Object o = m.calculateAUCFor(fe, se, generation);
            result[t] = (Double) o;
        }
        return result;
    }

    public double[] getVectorAUCTimeBestTest(IGenetic genetic, IFeatureExtractor fe, IStatisticExtractor se)
    {
        Integer gen = getMonotonicGeneration(genetic, fe, se, 2);
        if (gen == null) gen = _generations - 1;
        return getVectorAUCTimeTest(genetic, gen, fe, se);
    }

    public double[] getVectorAUCTimeTest(IGenetic genetic, int generation, IFeatureExtractor fe, IStatisticExtractor se)
    {
        double result[] = new double[_trials];
        for (int t = 0; t < _trials; t++)
        {
            Measure m = _data.get(genetic).get(t);

            Object o = m.calculateAUCTimeFor(fe, se, generation);
            result[t] = (Double) o;
        }
        return result;
    }

    public Double getValue(IGenetic genetic, int generation, IFeatureExtractor fe, IStatisticExtractor se,
                           IStatisticExtractor ise)
    {
        if (_statistic.get(genetic) == null) return null;
        if (_statistic.get(genetic).get(generation) == null) return null;
        if (_statistic.get(genetic).get(generation).get(fe) == null) return null;
        if (_statistic.get(genetic).get(generation).get(fe).get(se) == null) return null;
        return (Double) _statistic.get(genetic).get(generation).get(fe).get(se).get(ise);
    }

    public Double getAUCValue(IGenetic genetic, int generation, IFeatureExtractor fe, IStatisticExtractor se,
                              IStatisticExtractor ise)
    {
        if (_statistic.get(genetic) == null) return null;
        if (_statistic.get(genetic).get(generation) == null) return null;
        if (_statistic.get(genetic).get(generation).get(fe) == null) return null;
        if (_statistic.get(genetic).get(generation).get(fe).get(_dummyAUCExtractor.get(se.getKey())) == null)
            return null;
        return (Double) _statistic.get(genetic).get(generation).get(fe).get(_dummyAUCExtractor.get(se.getKey())).get(
                ise);
    }

    public Double getAUCTimeValue(IGenetic genetic, int generation, IFeatureExtractor fe, IStatisticExtractor se,
                                  IStatisticExtractor ise)
    {
        if (_statistic.get(genetic) == null) return null;
        if (_statistic.get(genetic).get(generation) == null) return null;
        if (_statistic.get(genetic).get(generation).get(fe) == null) return null;
        if (_statistic.get(genetic).get(generation).get(fe).get(_dummyAUCTimeExtractor.get(se.getKey())) == null)
            return null;
        return (Double) _statistic.get(genetic).get(generation).get(fe).get(_dummyAUCTimeExtractor.get(se.getKey())).get(
                ise);
    }

    public double[] getVectorTimeTest(IGenetic genetic, int generation)
    {
        double result[] = new double[_trials];
        for (int t = 0; t < _trials; t++)
        {
            Measure m = _data.get(genetic).get(t);
            Object o = m.getData().get(generation)._populationMeasure.get("ElapsedTime").getObject("ElapsedTime");
            result[t] = (Double) o;
        }
        return result;
    }


    private HashMap<IGenetic, ArrayList<HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>>>> initStatistics()
    {

        HashMap<IGenetic, ArrayList<HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>>>> result = new HashMap<IGenetic, ArrayList<HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>>>>();
        for (IGenetic g : _genetic)
        {
            ArrayList<HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>>> l0 = new ArrayList<HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>>>(
                    _generations);
            result.put(g, l0);
            for (int i = 0; i < _generations; i++)
            {
                HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>> l1 = new HashMap<IFeatureExtractor, HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>>();
                l0.add(l1);
                for (IFeatureExtractor fe : _featureExtractor)
                {
                    HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>> l2 = new HashMap<IStatisticExtractor, HashMap<IStatisticExtractor, Object>>();
                    l1.put(fe, l2);
                    for (IStatisticExtractor se : _statisticExtractor)
                    {
                        HashMap<IStatisticExtractor, Object> l3 = new HashMap<IStatisticExtractor, Object>();
                        l2.put(se, l3);

                        for (IStatisticExtractor ise : _internalStatisticExtractor)
                        {
                            l3.put(ise, null);
                        }

                    }

                    if (_performAUC) for (IStatisticExtractor se : _statisticExtractor)
                    {
                        HashMap<IStatisticExtractor, Object> l3 = new HashMap<IStatisticExtractor, Object>();
                        l2.put(_dummyAUCExtractor.get(se.getKey()), l3);

                        for (IStatisticExtractor ise : _internalStatisticExtractor)
                        {
                            l3.put(ise, null);
                        }

                    }

                    if (_performAUCTime) for (IStatisticExtractor se : _statisticExtractor)
                    {
                        HashMap<IStatisticExtractor, Object> l3 = new HashMap<IStatisticExtractor, Object>();
                        l2.put(_dummyAUCTimeExtractor.get(se.getKey()), l3);

                        for (IStatisticExtractor ise : _internalStatisticExtractor)
                        {
                            l3.put(ise, null);
                        }

                    }
                }
            }
        }
        return result;
    }

    public ArrayList<IFeatureExtractor> getUpdatedFeatureExtractors()
    {
        return _featureExtractor;
    }

    public ArrayList<IStatisticExtractor> getUpdatedStatisticExtractors()
    {
        return _statisticExtractor;
    }


}
