package year.y2014.greenlogistics.exp_c_full.interactiveA;

import base.Genetic;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.RandomAlternatives;
import decision.elicitation.choice.ordering.iterfaces.IChoice;
import decision.elicitation.historymaintain.ordering.BaseMaintain;
import decision.elicitation.historymaintain.ordering.interfaces.IMaintain;
import decision.elicitation.rules.BaseRule;
import decision.elicitation.rules.interfaces.IRule;
import decision.maker.ordering.OrderingDM;
import decision.maker.ordering.interfaces.IOrderingDM;
import decision.model.utilityfunction.PartialSumUtility;
import extractor.interfaces.IValueExtractor;
import interfaces.IEvaluator;
import interfaces.IGenetic;
import interfaces.IInitializer;
import killer.Killer;
import killer.interfaces.IKiller;
import measure.GenerationTrialMeasure;
import measure.Measure;
import measure.population.MaxExtractor;
import measure.population.MeanExtractor;
import measure.population.SDExtractor;
import measure.population.comprehensive.ElapsedTime;
import measure.population.comprehensive.OrderingBestDistanceUtility;
import measure.population.interfaces.IPopulationComprehensive;
import measure.population.interfaces.IStatisticExtractor;
import measure.population.specimen.DMOrderingExtractor;
import measure.population.specimen.DummyExtractor;
import measure.population.specimen.interfaces.IFeatureExtractor;
import normalization.MinMaxReverseNormalization;
import org.apache.commons.math3.random.MersenneTwister;
import patterns.genetic.aglorithms.linear.NEMO0_SPEA2;
import patterns.genetic.aglorithms.nemo0.NEMO0_PATTERN;
import patterns.weights.weights_3d;
import reproducer.interfaces.IReproducer;
import select.Tournament;
import select.interfaces.ISelector;
import sort.CriterionExtractor;
import standard.Point;
import standard.Range;
import utils.ParamsGenerator;
import utils.UtilityFunction;
import year.y2014.greenlogistics.A.DataA;
import year.y2014.greenlogistics.A.EvaluatorA;
import year.y2014.greenlogistics.Initializer;
import year.y2014.greenlogistics.Reproducer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTomczyk on 23.08.2015.
 */
public class MainDataGetter
{
    // --- STATIC
    public static ArrayList<String> _keys = null;
    public static HashMap<String, ArrayList<Object>> _separateParams = null;
    public static ArrayList<ArrayList<Object>> _params = null;

    public static String _basePath = "Results/year/y2015/greenlogistics/11_2015/full";
    public static String _problemName = "full";
    // --- NON-STATIC

    public int _populationSize = 200;
    public int _populationResize = 400;
    public int _trials = 100;
    public int _generations = 1000;

    public String _names[] = {
            //"NSGA-II",
            //"SPEA2",
            //"TP-NSGA-II",
            "NEMO-NSGA-II",
            "NEMO-SPEA2"
    };

    public HashMap<IGenetic, String> _constantResultPaths = null;

    public ArrayList<IGenetic> _dummyGenetic = null;
    public ArrayList<IFeatureExtractor> _featureExtractor = null;
    public ArrayList<IStatisticExtractor> _statisticExtractor = null;
    public ArrayList<IStatisticExtractor> _internalStatisticExtractor = null;
    public ArrayList<IPopulationComprehensive> _populationComprehensive = null;
    public ArrayList<ArrayList<ChartInput>> _cInput = null;
    public ArrayList<ICriterion> _criteria = null;

    public IReproducer _reproducer = new Reproducer();
    public IEvaluator _evaluator = new EvaluatorA();
    public IInitializer _initializer = new Initializer();
    public Object problem = new DataA();
    public IKiller _killer = new Killer();
    public ISelector _selector = null;
    public IChoice _choose = new RandomAlternatives(new MersenneTwister(System.currentTimeMillis()), 2);
    public int _historySize = 50;
    public IMaintain _historyMaintain = new BaseMaintain(_historySize);

    public HashMap<String, HashMap<String, GenerationTrialMeasure.Monotonic>> _monotonic;


    public MainDataGetter()
    {
        // --- PARAMS ------------------------------------------
        // -----------------------------------------------------
        if (_keys == null)
        {
            _keys = new ArrayList<>(2);
            _keys.add("INTERVAL");
            _keys.add("START");

            _separateParams = new HashMap<>();
            {
                ArrayList<Object> interval = new ArrayList<>(1);
                interval.add(10);
                interval.add(20);
                interval.add(30);
                //interval.add(40);
                //interval.add(50);
                //interval.add(100);
                _separateParams.put(_keys.get(0), interval);
            }
            {
                ArrayList<Object> start = new ArrayList<>(5);
                start.add(100);
                //start.add(100);
                //start.add(200);
                //start.add(300);
                _separateParams.put(_keys.get(1), start);
            }

            _params = ParamsGenerator.generateParams(_keys, _separateParams);
        }


        // --- DUMMY GENETIC ------------------------------------------
        // -----------------------------------------------------

        _dummyGenetic = new ArrayList<>(_keys.size());
        for (String _name : _names)
        {
            Genetic.Params p = new Genetic.Params();
            p.name = _name;
            _dummyGenetic.add(new Genetic(p));
        }

        // --- CONSTANT RESULTS PATHS ---------------------------
        // -----------------------------------------------------
        _constantResultPaths = new HashMap<>();
        //_constantResultPaths.put(_dummyGenetic.get(0), "Results/year/y2015/greenlogistics/11_2015/full/NSGA-II");
        // _constantResultPaths.put(_dummyGenetic.get(1), "Results/year/y2015/greenlogistics/11_2015/full/SPEA2");
        //_constantResultPaths.put(_dummyGenetic.get(2), "Results/year/y2015/greenlogistics/11_2015/full/TP-NSGA-II");

        // --- STATISTICS ------------------------------------------
        // -----------------------------------------------------
        // --- FEATURES EXTRACTOR ---
        _featureExtractor = new ArrayList<>(1);
        _featureExtractor.add(new DMOrderingExtractor(new OrderingDM(new PartialSumUtility(null, null)), "DM"));

        // --- STATISTICS EXTRACTOR ---
        _statisticExtractor = new ArrayList<>(2);
        _statisticExtractor.add(new MaxExtractor());
        _statisticExtractor.add(new MeanExtractor());

        // --- INTERNAL STATISTICS EXTRACTOR ---
        _internalStatisticExtractor = new ArrayList<>(2);
        _internalStatisticExtractor.add(new MeanExtractor());
        _internalStatisticExtractor.add(new SDExtractor());

        // --- UWAGA
        _populationComprehensive = new ArrayList<>(2);
        _populationComprehensive.add(new ElapsedTime());
        _populationComprehensive.add(new OrderingBestDistanceUtility(null, ReferenceSolutions._data[0], _criteria));
        // --- CHART INPUT
        _cInput = new ArrayList<>(3);
        {
            ArrayList<ChartInput> ci = new ArrayList<>(1);
            ci.add(new ChartInput(_featureExtractor.get(0), _statisticExtractor.get(0), _internalStatisticExtractor.get(0)));
            ci.add(new ChartInput(_featureExtractor.get(0), _statisticExtractor.get(1), _internalStatisticExtractor.get(0)));
            _cInput.add(ci);
        }
        {
            ArrayList<ChartInput> ci = new ArrayList<>(1);
            ci.add(new ChartInput(new DummyExtractor("ElapsedTime"), new measure.population.DummyExtractor("ElapsedTime"),
                    _internalStatisticExtractor.get(0)));
            _cInput.add(ci);
        }
        {
            ArrayList<ChartInput> ci = new ArrayList<>(1);
            ci.add(new ChartInput(new DummyExtractor("OrderingBestDistanceUtility"), new measure.population.DummyExtractor("OrderingBestDistanceUtility"),
                    _internalStatisticExtractor.get(0)));
            _cInput.add(ci);
        }

        _monotonic = new HashMap<>();
        _monotonic.put(_featureExtractor.get(0).getKey(), new HashMap<>());
        _monotonic.get(_featureExtractor.get(0).getKey()).put(_statisticExtractor.get(0).getKey(), new GenerationTrialMeasure.Monotonic("Mean"));
        _monotonic.get(_featureExtractor.get(0).getKey()).put(_statisticExtractor.get(1).getKey(), new GenerationTrialMeasure.Monotonic("Mean"));
        _monotonic.put("OrderingBestDistanceUtility", new HashMap<>());
        _monotonic.get("OrderingBestDistanceUtility").put("OrderingBestDistanceUtility", new GenerationTrialMeasure.Monotonic("OrderingBestDistanceUtility"));

        // --- CRITERIA ------------------------------------------
        // -----------------------------------------------------


        HashMap<String, Range> costMap = new HashMap<>(3);
        costMap.put("space", new Range(843633.68, 1047988.73));
        costMap.put("display", new Range(843633.68, 1047988.73));
        costMap.put("mdvf", new Range(843633.68, 1047988.73));
        costMap.put("nsgaii", new Range(843633.68, 1047988.73));
        costMap.put("spea2_nemo0", new Range(843633.68, 1047988.73));


        HashMap<String, Range> co2Map = new HashMap<>(3);
        co2Map.put("space", new Range(535039.184, 571223.026));
        co2Map.put("display", new Range(535039.184, 571223.026));
        co2Map.put("mdvf", new Range(535039.184, 571223.026));
        co2Map.put("nsgaii", new Range(535039.184, 571223.026));
        co2Map.put("spea2_nemo0", new Range(535039.184, 571223.026));


        HashMap<String, Range> pmMap = new HashMap<>(3);
        pmMap.put("space", new Range(2712.256, 14803.36));
        pmMap.put("display", new Range(2712.256, 14803.36));
        pmMap.put("mdvf", new Range(2712.256, 14803.36));
        pmMap.put("nsgaii", new Range(2712.256, 14803.36));
        pmMap.put("spea2_nemo0", new Range(2712.256, 14803.36));

        // NORMALIZATION


        _criteria = new ArrayList<>(3);

        _criteria.add(new Criterion("Cost", false, null, costMap));
        IValueExtractor e1 = new CriterionExtractor(_criteria.get(0));
        _criteria.get(0).setExtractor(e1);

        _criteria.add(new Criterion("CO2", false, null, co2Map));
        IValueExtractor e2 = new CriterionExtractor(_criteria.get(1));
        _criteria.get(1).setExtractor(e2);

        _criteria.add(new Criterion("PM", false, null, pmMap));
        IValueExtractor e3 = new CriterionExtractor(_criteria.get(2));
        _criteria.get(2).setExtractor(e3);


        // --- SELECTOR ------------------------------------------
        // -------------------------------------------------------
        Tournament.Params tournamentParams = new Tournament.Params();
        tournamentParams._k = 2;
        tournamentParams._pickLimit = 5;
        tournamentParams._probability = 0.9d;
        _selector = new Tournament(tournamentParams);

    }

    // ---------------------------------------------------------------------------------
    // --- NON-CONSTANT DATA -----------------------------------------------------------

    public GenerationTrialMeasure.Params getGenerationTrialMeasureParams()
    {
        GenerationTrialMeasure.Params gtmParams = new GenerationTrialMeasure.Params();
        gtmParams._featureExtractor = _featureExtractor;
        gtmParams._generations = _generations;
        gtmParams._genetic = _dummyGenetic;
        gtmParams._internalStatisticExtractor = _internalStatisticExtractor;
        gtmParams._statisticExtractor = _statisticExtractor;
        gtmParams._trials = _trials;
        gtmParams._populationComprehensive = _populationComprehensive;
        gtmParams._monotonic = _monotonic;
        return gtmParams;
    }

    public ArrayList<UtilityFunction> getUtilityFunctions(int t)
    {
        ArrayList<UtilityFunction> uf = new ArrayList<>(3);

        double w1 = weights_3d.data[t][0];
        double w2 = weights_3d.data[t][1];
        double w3 = weights_3d.data[t][2];


        uf.add(new UtilityFunction(2));
        uf.get(0).add(new Point(843633.68, w1));
        uf.get(0).add(new Point(1047988.73, 0.000000d));

        uf.add(new UtilityFunction(2));
        uf.get(1).add(new Point(535039.184, w2));
        uf.get(1).add(new Point(571223.026, 0.000000d));

        uf.add(new UtilityFunction(2));
        uf.get(2).add(new Point(2712.256, w3));
        uf.get(2).add(new Point(14803.36, 0.000000d));

        return uf;
    }

    public ArrayList<Measure> getMeasure(ArrayList<IGenetic> genetic, int generations)
    {
        ArrayList<Measure> result = new ArrayList<>(genetic.size());
        for (IGenetic aGenetic : genetic)
        {
            Measure.Params p = new Measure.Params();
            p._featureExtractor = _featureExtractor;
            p._statisticExtractor = _statisticExtractor;
            p._generations = generations;
            p._keepSpecimenStatistics = false;
            p._genetic = aGenetic;

            p._normalization = new HashMap<>();
            p._normalization.put(_featureExtractor.get(0), new HashMap<>());
            p._normalization.get(_featureExtractor.get(0)).put(_statisticExtractor.get(0), new MinMaxReverseNormalization(0.0d, 1.0d, 1.0d));
            p._normalization.get(_featureExtractor.get(0)).put(_statisticExtractor.get(1), new MinMaxReverseNormalization(0.0d, 1.0d, 1.0d));

            p._populationComprehensive = _populationComprehensive;

            result.add(new Measure(p));
        }
        return result;
    }

    public ArrayList<IGenetic> getGenetic(ArrayList<Object> params, int t)
    {
        ArrayList<IGenetic> genetic = new ArrayList<>(2);

        /**/

        // NEMO0_LINEAR-NSGA-II
        {
            int interval = (Integer) params.get(0);
            int start = (Integer) params.get(1);
            IRule rule = new BaseRule(interval, start, 1000000);
            IGenetic g = NEMO0_PATTERN.getNEMO0(
                    "NEMO-NSGA-II", _populationSize, _populationResize, _criteria,
                    new OrderingDM(new PartialSumUtility(getUtilityFunctions(t), _criteria)),
                    _reproducer, _evaluator, _initializer, _killer, _selector, _choose, rule,
                    _historyMaintain, 10000, problem, null, NEMO0_PATTERN.INT_LINEAR);
            genetic.add(g);
        }

        // SPEA2
        {
            int interval = (Integer) params.get(0);
            int start = (Integer) params.get(1);
            IRule rule = new BaseRule(interval, start, 1000000);

            IGenetic g = NEMO0_SPEA2.getNEMO0_SPEA2("NEMO-SPEA2", _populationSize, _populationResize, _populationSize, _criteria, getUtilityFunctions(t),
                    _reproducer, _evaluator, _initializer, _killer, _selector, _choose, rule, _historyMaintain, 10000, problem, null);
            genetic.add(g);
        }

        // UWAGA!
        _populationComprehensive = new ArrayList<>(2);
        _populationComprehensive.add(new ElapsedTime());

        IOrderingDM dm = new OrderingDM(new PartialSumUtility(getUtilityFunctions(t), _criteria));
        _populationComprehensive.add(new OrderingBestDistanceUtility(dm, ReferenceSolutions._data[t], _criteria));

        return genetic;
    }
}

