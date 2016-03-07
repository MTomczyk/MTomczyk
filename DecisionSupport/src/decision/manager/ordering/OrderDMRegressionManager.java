package decision.manager.ordering;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import decision.elicitation.choice.ordering.iterfaces.IChoice;
import decision.elicitation.historymaintain.ordering.BaseMaintain;
import decision.elicitation.historymaintain.ordering.interfaces.IMaintain;
import decision.elicitation.rules.BaseRule;
import decision.elicitation.rules.interfaces.IRule;
import decision.maker.ordering.interfaces.IOrderingDM;
import decision.maker.ordering.Order;
import decision.model.utilityfunction.estimator.ordering.interfaces.IModelOrderEstimator;
import extractor.alternative.FromAlternative;
import linearprogramming.or.MaximumSet;
import linearprogramming.or.NegativeEpsilon;
import linearprogramming.or.PotentiallyOptimal;
import utils.UtilityFunction;
import linearprogramming.or.interfaces.IOrdinalRegression;
import standard.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Michał on 2015-02-13.
 */

public class OrderDMRegressionManager
{
    public static class Params
    {
        // --- OLD VERSION ------
        public int _elicitationStart = -1;
        public int _elicitationInterval = 1;
        public int _elicitationLimit = Common.MAX_INT;
        // --- NEW VERSION ------
        public IRule _elicitationRule = null;

        // --- OLD VERSION ------
        public int _historySize = 1;
        // --- NEW VERSION ------
        public IMaintain _historyMaintain = null;

        // --- OLD VERSION ------
        public IOrdinalRegression _or = null;
        // --- NEW VERSION ------
        public ArrayList<IModelOrderEstimator> _estimators = null;

        public ArrayList<IOrderingDM> _artificialDMs = null;
        public ArrayList<IOrderingDM> _estimatedDMs = null;
        public ArrayList<IChoice> _choice = null;
        public IChoice _singleChoice = null;

        public boolean _acceptEqualOrder = true;
    }

    private HashMap<IOrderingDM, LinkedList<Order>> _history = null;

    private ArrayList<IOrderingDM> _artificialDMs = null;
    private ArrayList<IOrderingDM> _estimatedDMs = null;

    private ArrayList<IModelOrderEstimator> _estimators = null;
    private IOrdinalRegression _or = null;

    private IRule _elicitationRule = null;
    private IMaintain _historyMaintain = null;

    private ArrayList<IChoice> _choice = null;

    private boolean _acceptEqualOrder = true;

    public OrderDMRegressionManager(Params p)
    {
        if (p._elicitationRule != null) this._elicitationRule = p._elicitationRule;
        else this._elicitationRule = new BaseRule(p._elicitationInterval, p._elicitationStart, p._elicitationLimit);

        if (p._historyMaintain != null) this._historyMaintain = p._historyMaintain;
        else this._historyMaintain = new BaseMaintain(p._historySize);

        this._artificialDMs = p._artificialDMs;
        this._estimatedDMs = p._estimatedDMs;

        System.out.println(p._artificialDMs.size());

        this._choice = p._choice;
        if (this._choice == null)
        {
            this._choice = new ArrayList<>();
            this._choice.add(p._singleChoice);
        }

        this._estimators = p._estimators;
        this._or = p._or;

        this._history = new HashMap<>();
        for (IOrderingDM dm : _artificialDMs)
            this._history.put(dm, new LinkedList<>());


        this._acceptEqualOrder = p._acceptEqualOrder;
    }

    public void updateElicitatedPreferences(ArrayList<IAlternative> alternativesToCompare, int time)
    {
        for (int i = 0; i < _artificialDMs.size(); i++)
        {
            IOrderingDM aDM = _artificialDMs.get(i);
            _elicitationRule.increaseElicitation();

            Order o = aDM.order(alternativesToCompare);

            if ((_acceptEqualOrder) || (!o.isSingleEqualOrder()))
            {
                if ((_choice != null) && (_choice.get(i) != null))
                    _choice.get(i).addFeedback(o);
                _history.get(aDM).add(o);
                _historyMaintain.maintainHistory(_history.get(aDM));
            }
        }
    }

    public boolean updatePreferences(ArrayList<IAlternative> alternatives, int time)
    {
        if (!_elicitationRule.isElicitationTime(time)) return false;

        for (int i = 0; i < _artificialDMs.size(); i++)
        {
            IOrderingDM aDM = _artificialDMs.get(i);
            IOrderingDM eDM;
            if (_estimatedDMs.size() <= i) eDM = null;
            else eDM = _estimatedDMs.get(i);

            int times = _elicitationRule.getElicitationTime(time);
            for (int j = 0; j < times; j++)
            {
                _elicitationRule.increaseElicitation();
                ArrayList<IAlternative> toCompare = _choice.get(i).getAlternativesToCompare(alternatives, new FromAlternative(), eDM);
                Order o = aDM.order(toCompare);

                if ((_acceptEqualOrder) || (!o.isSingleEqualOrder()))
                {
                    _choice.get(i).addFeedback(o);
                    _history.get(aDM).add(o);
                    _historyMaintain.maintainHistory(_history.get(aDM));
                }
            }
        }

        return true;
    }

    public boolean updateEstimatedDM(ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria)
    {
        for (int i = 0; i < _artificialDMs.size(); i++)
        {
            if ((_history.get(_artificialDMs.get(i)) == null) || (_history.get(_artificialDMs.get(i)).size() == 0))
                continue;

            if (_estimators != null)
            {
                IModelOrderEstimator e = _estimators.get(i);
                Object model = e.getEstimatedModel(alternatives, _history.get(_artificialDMs.get(i)));
                this._estimatedDMs.get(i).getModel().setModel(model);
            } else
            {
                ArrayList<UtilityFunction> uf = _or.getUtility(alternatives, criteria, _history.get(_artificialDMs.get(i)));
                if (uf == null) return false;
                this._estimatedDMs.get(i).getModel().setModel(uf);
            }
        }
        return true;
    }

    public Double[] evaluateAlternative(IAlternative alternative)
    {
        Double result[] = new Double[_estimatedDMs.size()];
        for (int i = 0; i < _estimatedDMs.size(); i++)
        {
            if (_estimatedDMs.get(i).getModel().hasModel()) result[i] = this._estimatedDMs.get(i).evaluate(alternative);
            else result[i] = null;
        }

        return result;
    }

    @SuppressWarnings("all")
    public Double[][] evaluateAlternatives(ArrayList<IAlternative> alternatives)
    {
        Double result[][] = new Double[_estimatedDMs.size()][alternatives.size()];

        for (int i = 0; i < _estimatedDMs.size(); i++)
        {
            if (_estimatedDMs.get(i).getModel().hasModel())
            {
                double ar[] = this._estimatedDMs.get(i).evaluate(alternatives);
                for (int j = 0; j < ar.length; j++)
                    result[i][j] = ar[j];
            } else for (int j = 0; j < alternatives.size(); j++)
                result[i][j] = null;
        }
        return result;
    }

    public IOrderingDM getFirstOrderingDM()
    {
        return _artificialDMs.get(0);
    }

    public IOrderingDM getFirstOrderingEstimatedDM()
    {
        return _estimatedDMs.get(0);
    }

    @SuppressWarnings("unused")
    public void setChoiceModel(IChoice choice)
    {
        this._choice.set(0, choice);
    }

    @SuppressWarnings("unused")
    public void setChoiceModel(ArrayList<IChoice> choice)
    {
        this._choice = choice;
    }

    public HashMap<IOrderingDM, LinkedList<Order>> getHistory()
    {
        return this._history;
    }


    public boolean updateEstimatedNegativeEpsilon(ArrayList<IAlternative> alternatives,
                                                  ArrayList<ICriterion> criteria)
    {
        NegativeEpsilon ne = new NegativeEpsilon();
        ArrayList<UtilityFunction> uf = ne.getUtility(_or, alternatives, criteria, _history, _artificialDMs);
        this._estimatedDMs.get(0).getModel().setModel(uf);
        return true;
    }

    public double[][] getPotentiallyOptimalMatrix(ArrayList <IAlternative> alternatives, boolean mask[][], ArrayList<ICriterion> criteria,
                                                  PotentiallyOptimal potentiallyOptimal)
    {

        double result[][] = new double[alternatives.size()][_artificialDMs.size()];

        for (int i = 0; i < _artificialDMs.size(); i++)
        {
            LinkedList<Order> dummyHistory = new LinkedList<>();
            HashMap<IAlternative, Integer> index = new HashMap<>();
            HashMap<IAlternative, Integer> ignoredIndex = new HashMap<>();
            ArrayList<IAlternative> dummyAlternatives = new ArrayList<>(alternatives.size());

            for (int j = 0; j < alternatives.size(); j++)
            {
                if ((mask == null) || (!mask[j][i]))
                {
                    dummyAlternatives.add(alternatives.get(j));
                    index.put(alternatives.get(j), j);
                }
                else ignoredIndex.put(alternatives.get(j),j);
            }

            for (Order o: _history.get(_artificialDMs.get(i)))
            {
                ArrayList<IAlternative> orderedAlternatives = o.getSortedArray();
                boolean pass = true;
                for (IAlternative pa: orderedAlternatives)
                {
                    if (ignoredIndex.get(pa) != null)
                    {
                        pass = false;
                        break;
                    }
                }
                if (pass) dummyHistory.add(o);
            }

            double partial[][] = potentiallyOptimal.getPotentiallyOptimalSeparateMatrix(dummyAlternatives, criteria,dummyHistory,_artificialDMs.get(i));

            if (partial == null)
            {
                System.out.println("Potentially optimal null");
                return null;
            }

            double na[][] = new double[alternatives.size()][1];

            int j = -1;
            for (IAlternative da: dummyAlternatives)
            {
                j++;
                int row = index.get(da);
                System.arraycopy(partial[j], 0, na[row], 0, 1);
            }

            for (j = 0; j < alternatives.size(); j++)
            {
                result[j][i] = na[j][0];
            }
        }

        return result;
    }


    /*
    public double[][] getPotentiallyOptimalMatrix(ArrayList <IAlternative> alternatives, boolean mask[], ArrayList<ICriterion> criteria,
                                                  PotentiallyOptimal potentiallyOptimal)
    {
        HashMap<IOrderingDM, LinkedList<Order>> dummyHistory = new HashMap<IOrderingDM, LinkedList<Order>>();
        HashMap<IAlternative, Integer> index = new HashMap<IAlternative, Integer>();
        HashMap<IAlternative, Integer> ignoredIndex = new HashMap<IAlternative, Integer>();
        ArrayList<IAlternative> dummyAlternatives = new ArrayList<IAlternative>(alternatives.size());

        for (int i = 0; i < alternatives.size(); i++)
        {
            if ((mask == null) || (!mask[i]))
            {
                dummyAlternatives.add(alternatives.get(i));
                index.put(alternatives.get(i), i);
            }
            else ignoredIndex.put(alternatives.get(i),i);
        }

        for (IOrderingDM dm: _artificialDMs)
        {
            LinkedList<Order> dummyOrder = new LinkedList<Order>();
            dummyHistory.put(dm, dummyOrder);

            for (Order o: _history.get(dm))
            {
                ArrayList<IAlternative> orderedAlternatives = o.getSortedArray();
                boolean pass = true;
                for (IAlternative pa: orderedAlternatives)
                {
                    if (ignoredIndex.get(pa) != null)
                    {
                        pass = false;
                        break;
                    }
                }
                if (pass) dummyOrder.add(o);
            }
        }

        double a[][] = potentiallyOptimal.getPotentiallyOptimalSeparateMatrix(dummyAlternatives, criteria, dummyHistory, _artificialDMs);
        if (mask == null) return a;

        double na[][] = new double[alternatives.size()][_artificialDMs.size()];

        int i = -1;
        for (IAlternative da: dummyAlternatives)
        {
            i++;
            int row = index.get(da);
            System.arraycopy(a[i], 0, na[row], 0, _artificialDMs.size());
        }
        return a;
    }
     */


    public void performMaxComparisonSet(ArrayList<IAlternative> alternatives, ArrayList<ICriterion> criteria,
                                        MaximumSet.Params p)
    {
        for (IOrderingDM dm : _artificialDMs)
        {
            if ((_history.get(dm) == null) || (_history.get(dm).size() == 0))
                return;
        }

        HashMap<IOrderingDM, LinkedList<Order>> output = MaximumSet.getMaximumSet(alternatives, _or,
                _history, _artificialDMs, criteria, p);

        if (output != null)
            this._history = output;
    }


    public boolean updateEstimatedDMMaxComparisonSet(ArrayList<IAlternative> alternatives,
                                                     ArrayList<ICriterion> criteria)
    {
        ArrayList<UtilityFunction> uf = _or.getUtility(alternatives,criteria,_history,_artificialDMs);
        if (uf != null)
        {
            this._estimatedDMs.get(0).getModel().setModel(uf);
            return true;
        }
        return false;
    }

    public ArrayList<IOrderingDM> getArtificialDMs()
    {
        return this._artificialDMs;
    }

    public IRule getElicitationRule()
    {
        return _elicitationRule;
    }

    public ArrayList<IChoice> getChoiceModel()
    {
        return _choice;
    }

    public void setEstimatedDMs(ArrayList<IOrderingDM> eDMs)
    {
        this._estimatedDMs = eDMs;
    }

    public ArrayList<IOrderingDM> getEstimatedDMs()
    {
        return this._estimatedDMs;
    }

    public void setElicitationRule(IRule rule)
    {
        this._elicitationRule = rule;
    }
}



