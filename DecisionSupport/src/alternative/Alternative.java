package alternative;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import shared.SC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This is an implementation of IAlternative. Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *
 */

public class Alternative implements IAlternative
{
    private String _name = "";

    private HashMap<ICriterion, Double> _evaluations = null;
    private Double _aggregatedEvaluation = 0.0d;

    /**
     * Simple constructor of alternative.
     *
     * @param name Name of alternative.
     */
    public Alternative(String name)
    {
        this(name, null);
    }

    /**
     * Simple constructor of alternative.
     *
     * @param name     Name of alternative.
     * @param criteria Array of criteria.
     */
    public Alternative(String name, ArrayList<ICriterion> criteria)
    {
        this.setName(name);
        if (criteria != null) this.initWithCriteria(criteria);
    }

    /**
     * @return Deep copy of this object.
     */
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public IAlternative clone()
    {
        IAlternative alternative = new Alternative(this.getName());
        alternative.setAggregatedEvaluation(this._aggregatedEvaluation);

        HashMap<ICriterion, Double> evaluation = new HashMap<>(this._evaluations);
        for (Entry<ICriterion, Double> entry : this._evaluations.entrySet())
            evaluation.put(entry.getKey(), entry.getValue());

        alternative.setEvaluations(evaluation);
        return alternative;
    }

    /**
     * Init object with criteria and set evaluation values to 0.
     *
     * @param criteria array of criteria.
     */
    @Override
    public void initWithCriteria(ArrayList<ICriterion> criteria)
    {
        this._evaluations = new HashMap<>(criteria.size());
        for (ICriterion c : criteria)
            this._evaluations.put(c, 0.0d);

    }

    /**
     * Produce array of dummy alternatives. For tests purpose. All evaluations on each criterion is set to 0.
     *
     * @param baseName  Base name of alternatives. F.I. if baseName = "A" it will produce alternatives A0, A1,..., An.
     * @param n         Number of alternatives to return.
     * @param criterion Array of criteria.
     * @return Array of dummy alternatives.
     */
    public static ArrayList<IAlternative> getAlternativeArray(String baseName, int n, ArrayList<ICriterion> criterion)
    {
        ArrayList<IAlternative> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
        {
            Alternative a = new Alternative(String.format("%s%d", baseName, i), criterion);
            result.add(a);
        }
        return result;
    }

    /**
     * Use if you want to obtain evaluation values.
     *
     * @return Map of evaluations where key if criterion.
     */
    @Override
    public HashMap<ICriterion, Double> getEvaluations()
    {
        return this._evaluations;
    }

    /**
     * Use if you want to obtain evaluation on specific criterion.
     *
     * @param criterion Specific criterion.
     * @return Value of evaluation on specific criterion.
     */
    @Override
    public Double getEvaluationAt(ICriterion criterion)
    {
        return this._evaluations.get(criterion);
    }

    /**
     * Set map of evaluations.
     *
     * @param evaluations Map of evaluations where key is criterion.
     */
    @Override
    public void setEvaluations(HashMap<ICriterion, Double> evaluations)
    {
        this._evaluations = evaluations;
    }

    /**
     * Set evaluation value on specific criterion.
     *
     * @param criterion Specific criterion.
     * @param value     Value on specific criterion.
     */
    @Override
    public void setEvaluationAt(ICriterion criterion, Double value)
    {
        this._evaluations.put(criterion, value);
    }

    /**
     * Return aggregated value (evaluation of this alternative).
     *
     * @return Aggregated value.
     */
    @Override
    public Double getAggregatedEvaluation()
    {
        return this._aggregatedEvaluation;
    }

    /**
     * Set aggregated value (evaluation of this alternative).
     *
     * @param aggregatedEvaluation Value of evaluation.
     */
    @Override
    public void setAggregatedEvaluation(Double aggregatedEvaluation)
    {
        this._aggregatedEvaluation = aggregatedEvaluation;
    }

    /**
     * Just print evaluation of this object.
     * <br>
     * NAME: aggregated value
     * Criterion_0_Name = value_0
     * ...
     * Criterion_N_Name = value_n;
     */
    @Override
    public void printEvaluation()
    {
        SC.getInstance().log(String.format("%s: %f\n", this.getName(), this._aggregatedEvaluation));

        for (Entry<ICriterion, Double> entry : this._evaluations.entrySet())
            SC.getInstance().log(String.format("%s = %f\n", entry.getKey().getName(), entry.getValue()));
    }

    /**
     * Just print evaluation of this object on selected criteria.
     * <br>
     * NAME: aggregated value
     * Criterion_0_Name = value_0
     * ...
     * Criterion_N_Name = value_n;
     *
     * @param criteria Array of criteria
     */
    @Override
    public void printEvaluation(ArrayList<ICriterion> criteria)
    {
        SC.getInstance().log(String.format("%s: %f\n", this.getName(), this._aggregatedEvaluation));
        for (ICriterion c : criteria)
            SC.getInstance().log(String.format("%s = %f\n", c.getName(), this.getEvaluations().get(c)));
        SC.getInstance().log("\n");
    }

    /**
     * Get name of alternative.
     *
     * @return Name of alternative.
     */
    @Override
    public String getName()
    {
        return _name;
    }

    /**
     * Set alternative name.
     *
     * @param name Name of alternative.
     */
    @Override
    public void setName(String name)
    {
        this._name = name;
    }

    /**
     * Set evaluation on each criterion where values are stored in double[].
     *
     * @param evaluations Array of values.
     * @param criteria    Array of criteria.
     */
    @Override
    public void setEvaluationVector(double[] evaluations, ArrayList<ICriterion> criteria)
    {
        for (int i = 0; i < criteria.size(); i++)
            this._evaluations.put(criteria.get(i), evaluations[i]);
    }

    /**
     * Parse evaluation values on each criterion to double[].
     *
     * @param criteria Array of criterion.
     * @return Array of evaluation values.
     */
    @Override
    public double[] getEvaluationVector(ArrayList<ICriterion> criteria)
    {
        double result[] = new double[criteria.size()];

        for (int i = 0; i < criteria.size(); i++)
            result[i] = this._evaluations.get(criteria.get(i));

        return result;
    }

}
