package decision.maker.ordering;

import alternative.interfaces.IAlternative;
import decision.maker.ordering.interfaces.IOrderingDM;
import decision.model.interfaces.IModel;
import extractor.interfaces.IAlternativeExtractor;
import standard.Common;
import utils.Rank;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-07-12.
 *
 */
public class OrderingDM implements IOrderingDM
{
    private IModel _model = null;
    private double _epsilon = 0.0d;

    public OrderingDM(IModel model)
    {
        this(model, Common.EPSILON);
    }

    public OrderingDM(IModel model, double epsilon)
    {
        this._model = model;
        this._epsilon = epsilon;
    }

    /**
     * It constructs ordering objects from array of alternatives. Alternatives are sorted from the best to the worst.
     *
     * @param alternatives Array of alternatives to compare.
     * @return Order object.
     */
    @Override
    public Order order(ArrayList<IAlternative> alternatives)
    {
        // CALCULATE UTILITY
        double util[] = new double[alternatives.size()];

        util[0] = evaluate(alternatives.get(0));

        for (int i = 1; i < alternatives.size(); i++)
        {
            double u = evaluate(alternatives.get(i));
            util[i] = u;

            for (int j = i; j > 0; j--)
            {
                if ((u > util[j - 1] - _epsilon) && (u < util[j - 1] + _epsilon))
                {
                    break;
                } else if (u > util[j - 1])
                {
                    IAlternative tmp = alternatives.get(j);
                    alternatives.set(j, alternatives.get(j - 1));
                    alternatives.set(j - 1, tmp);
                    util[j] = util[j - 1];
                    util[j - 1] = u;
                } else break;
            }

        }

        return Order.getFromArrayAndUtility(alternatives, util, _epsilon);
    }


    @Override
    public Order order(Object arrayOfObjects, IAlternativeExtractor extractor)
    {
        @SuppressWarnings("all") ArrayList<Object> alternatives = (ArrayList<Object>) arrayOfObjects;
        // CALCULATE UTILITY
        double util[] = new double[alternatives.size()];

        util[0] = evaluate(extractor.getValue(alternatives.get(0)));

        for (int i = 1; i < alternatives.size(); i++)
        {
            double u = evaluate(extractor.getValue(alternatives.get(i)));
            util[i] = u;

            for (int j = i; j > 0; j--)
            {
                if ((u > util[j - 1] - _epsilon) && (u < util[j - 1] + _epsilon))
                {
                    break;
                } else if (u > util[j - 1])
                {
                    Object tmp = alternatives.get(j);
                    alternatives.set(j, alternatives.get(j - 1));
                    alternatives.set(j - 1, tmp);
                    util[j] = util[j - 1];
                    util[j - 1] = u;
                } else break;
            }

        }

        return Order.getFromArrayAndUtility(alternatives, extractor, util, _epsilon);
    }

    /**
     * It constructs array with position values for each alternative. F.i. if 4th value in received array is 0 it means
     * that 4th alternative in input array is the best for decision maker.
     *
     * @param alternatives Array of alternatives to compare.
     * @return Array of position values.
     */
    @Override
    public int[] getOrderIndex(ArrayList<IAlternative> alternatives)
    {
        double e[] = this.evaluate(alternatives);
        boolean m[][] = new boolean[e.length][e.length];

        for (int i = 0; i < e.length; i++)
            for (int j = 0; j < e.length; j++)
                if (e[i] > e[j] + _epsilon) m[i][j] = true;

        return Rank.rankOutrankingMatrix(m);
    }

    /**
     * It constructs array with position values for each alternative. F.i. if 4th value in received array is 0 it means
     * that 4th alternative in input array is the best for decision maker.
     *
     * @param arrayOfObjects ArrayList of IAlternatives
     * @param extractor      IAlternative object extractor
     * @return Array of position values.
     */
    @Override
    public int[] getOrderIndex(Object arrayOfObjects, IAlternativeExtractor extractor)
    {
        double e[] = this.evaluate(arrayOfObjects, extractor);
        boolean m[][] = new boolean[e.length][e.length];

        for (int i = 0; i < e.length; i++)
            for (int j = 0; j < e.length; j++)
                if (e[i] > e[j] + _epsilon) m[i][j] = true;

        return Rank.rankOutrankingMatrix(m);
    }

    /**
     * Get array of evaluation values for each given alternative.
     *
     * @param alternatives Array of alternatives.
     * @return Array of evaluation values.
     */
    @Override
    public double[] evaluate(ArrayList<IAlternative> alternatives)
    {
        double u[] = new double[alternatives.size()];
        for (int i = 0; i < alternatives.size(); i++)
            u[i] = this.evaluate(alternatives.get(i));
        return u;
    }

    /**
     * Get array of evaluation values for each given alternative.
     *
     * @param arrayOfObjects ArrayList of IAlternatives
     * @param extractor      IAlternative object extractor
     * @return Array of evaluation values.
     */
    @Override
    public double[] evaluate(Object arrayOfObjects, IAlternativeExtractor extractor)
    {
        @SuppressWarnings("all") ArrayList<Object> alternative = (ArrayList<Object>) arrayOfObjects;
        double u[] = new double[alternative.size()];
        for (int i = 0; i < alternative.size(); i++)
            u[i] = this.evaluate(extractor.getValue(alternative.get(i)));
        return u;
    }

    /**
     * Get utility function value for alternative.
     *
     * @param alternative Alternative to evaluate.
     * @return Evaluation value.
     */
    @Override
    public double evaluate(IAlternative alternative)
    {
        return _model.rateCandidate(alternative);
    }

    @Override
    public IModel getModel()
    {
        return _model;
    }

    @Override
    public void setModel(IModel model)
    {
        this._model = model;
    }


}
