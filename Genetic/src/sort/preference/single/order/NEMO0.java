package sort.preference.single.order;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.filter.interfaces.IFilter;
import decision.manager.ordering.OrderDMRegressionManager;
import interfaces.ISpecimen;
import sort.Log;
import sort.functions.CrowdingDistance;
import sort.functions.Duplication;
import sort.functions.Front;
import sort.functions.Sort;
import sort.interfaces.ILog;
import sort.interfaces.ISorter;
import standard.Common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Michał on 2014-11-13.
 *
 */
public class NEMO0 implements ISorter
{
    public static class Params
    {
        public OrderDMRegressionManager _dm = null;
        public int _populSize = 0;
        public double _epsilon = Common.EPSILON;
        public IFilter _filter = null;
    }

    // -- DATA
    private ArrayList<ISpecimen> _pareto = null;
    private ArrayList<ISpecimen> _reproductionPool = null;
    private IFilter _filter = null;

    OrderDMRegressionManager _dm = null;

    private int _populSize = 0;

    public double _epsilon = Common.EPSILON;
    public double _duplicatesEpsilon = Common.EPSILON;

    public NEMO0(Params p)
    {
        this._dm = p._dm;
        this._epsilon = p._epsilon;
        this._populSize = p._populSize;
        this._filter = p._filter;
    }


    @Override
    public ILog sort(ArrayList<ISpecimen> specimens, ArrayList<ICriterion> criteria, int generation)
    {
        // IGNORE DUPLICATES
        ArrayList<ISpecimen> duplicates = Duplication.extractDuplicates(specimens, criteria, _duplicatesEpsilon, 2);

        // GET FRONTS AND PARETO
        LinkedList<LinkedList<Integer>> front = Front.getDominationFrontList(specimens, criteria, _epsilon);
        ArrayList<IAlternative> paretoAlternatives = new ArrayList<>(front.getFirst().size());

        LinkedList<Integer> pFront = front.getFirst();
        paretoAlternatives.addAll(pFront.stream().map(i -> specimens.get(i).getAlternative()).collect(Collectors.toList()));

        // UPDATE PREFERENCES AND ESTIMATED DM
        // TODO NIE MA INCREASE ELICITATION
        if (_dm.getElicitationRule().isElicitationTime(generation))
        {
            paretoAlternatives = sort.common.Common.applyChoiceFilter(_filter, paretoAlternatives, criteria);

            if (paretoAlternatives.size() < _dm.getChoiceModel().get(0).getRequiredAlternatives())
            {
                ArrayList<IAlternative> sortedByDomination = new ArrayList<>(specimens.size());
                for (LinkedList<Integer> f: front)
                    sortedByDomination.addAll(f.stream().map(i -> specimens.get(i).getAlternative()).collect(Collectors.toList()));

                ArrayList<IAlternative> tmp = new ArrayList<>(_dm.getChoiceModel().get(0).getRequiredAlternatives());
                for (int i = 0; i < _dm.getChoiceModel().get(0).getRequiredAlternatives(); i++)
                {
                    if (i < paretoAlternatives.size()) tmp.add(paretoAlternatives.get(i));
                    else tmp.add(sortedByDomination.get(i));
                }
                _dm.updatePreferences(tmp, generation);
                _dm.updateEstimatedDM(tmp, criteria);
            }
            else
            {
                _dm.updatePreferences(paretoAlternatives, generation);
                _dm.updateEstimatedDM(paretoAlternatives, criteria);
            }
        }

        double crowding[] = null;
        if ((!_dm.getElicitationRule().isElicitationBegin())
            && (!_dm.getEstimatedDMs().get(0).getModel().hasModel()))
            crowding = CrowdingDistance.getDistanceFrontWithNormalization(front, specimens, criteria);


        // EVALUATE
        int frontNo = 0;
        for (LinkedList<Integer> f : front)
        {
            for (Integer s : f)
            {
                IAlternative a = specimens.get(s).getAlternative();
                Double eval[] = _dm.evaluateAlternative(a);
                if (crowding != null) eval[0] = crowding[s];

                double v = (double) frontNo + _epsilon;
                if ((eval != null) && (eval[0] != null))
                {

                    v = (double) frontNo + (1.0d - _epsilon - (eval[0] * 0.5d));
                }

                a.setAggregatedEvaluation(v);
            }
            frontNo++;
        }

        // SORT
        Sort.sortByAggregatedValue(specimens);

        // CREATE PARETO / AFTER KILL
        {
            ArrayList<ISpecimen> toConsider = new ArrayList<>(_populSize);
            for (int i = 0; i < _populSize; i++)
            {
                if (specimens.size() <= i) break;
                toConsider.add(specimens.get(i));
            }
            this._pareto = Front.getPareto(toConsider, criteria, _epsilon);
        }


        // CREATE REPRODUCTION POOL
        this._reproductionPool = specimens;

        // AT DUPLICATES AT THE END
        // TODO IGNORE AND DEAL INSIDE GENETIC/RUNNER
        specimens.addAll(duplicates.stream().collect(Collectors.toList()));

        int shifted = duplicates.size();
        ILog log = new Log();
        log.addLog("shifted", shifted);
        return log;
    }

    @Override
    public ArrayList<ISpecimen> getPareto()
    {
        return this._pareto;
    }

    @Override
    public ArrayList<ISpecimen> getReproductionPool()
    {
        return this._reproductionPool;
    }
}
