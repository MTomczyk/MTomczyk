package alternative.interfaces;


import criterion.interfaces.ICriterion;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface of alternative.
 * Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *
 */
public interface IAlternative
{
    /**
     * Initialize the alternative object with given criteria
     * @param criteria Array of considered criteria.
     */
    void initWithCriteria(ArrayList<ICriterion> criteria);

    /**
     * Return the map of evaluations.
     * @return Map of evaluations.
     */
    HashMap<ICriterion, Double> getEvaluations();

    /**
     * Return the evaluation on a particular criterion .
     * @param criterion Considered criterion.
     * @return Evaluation value.
     */
    Double getEvaluationAt(ICriterion criterion);

    /**
     * Set the map of evaluations.
     * @param evaluations Map of evaluations.
     */
    void setEvaluations(HashMap<ICriterion, Double> evaluations);

    /**
     * Set the evaluation on a particular criterion
     * @param criterion Considered criterion.
     * @param value Evaluation value.
     */
    void setEvaluationAt(ICriterion criterion, Double value);

    /**
     * Set the vector of evaluations. Values have to be ordered according to the given criteria order.
     * @param eval Array of evaluations.
     * @param criteria Array of considered criteria.
     */
    void setEvaluationVector(double eval[], ArrayList<ICriterion> criteria);

    /**
     * Return the vector of evaluations. The values are ordered according to the given criteria order.
     * @param criteria Considered criteria.
     * @return Vector of evaluations.
     */
    double[] getEvaluationVector(ArrayList<ICriterion> criteria);

    /**
     * Return the aggregated evaluation.
     * @return Aggregated evaluation.
     */
    Double getAggregatedEvaluation();

    /**
     * Set the aggregated evaluation value.
     * @param aggregatedEvaluation Aggregated evaluation value.
     */
    void setAggregatedEvaluation(Double aggregatedEvaluation);

    /**
     * Clone object.
     * @return Clone of this object.
     */
    IAlternative clone();

    /**
     * Print evaluation values.
     */
    void printEvaluation();

    /**
     * Print evaluation values with respect to the given criteria.
     * @param criteria Considered criteria.
     */
    void printEvaluation(ArrayList<ICriterion> criteria);


    /**
     * Return the name of the object.
     * @return The name of the object.
     */
    String getName();

    /**
     * Set the new name of the object.
     * @param name New name of the object.
     */
    void setName(String name);
}
