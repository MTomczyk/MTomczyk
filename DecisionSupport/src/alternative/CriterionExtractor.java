package alternative;

import alternative.interfaces.IAlternative;
import criterion.interfaces.ICriterion;
import extractor.interfaces.IValueExtractor;

/**
 *
 * This class extracts evaluation value on specific criterion from IAlternative.
 * See: {@See Graph.tree.binary.BinaryTree}. Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *
 */

public class CriterionExtractor implements IValueExtractor
{
    private ICriterion _criterion = null;

    /**
     * Constructor requires criterion object.
     *
     * @param criterion criterion object.
     */
    public CriterionExtractor(ICriterion criterion)
    {
        this._criterion = criterion;
    }

    /**
     * Extract evaluation on specific criterion from IAlternative.
     *
     * @param o here it needs to be an {@See IAlternative} object.
     * @return Extracted value on specific criterion.
     */
    @Override
    public Double getValue(Object o)
    {
        return ((IAlternative) o).getEvaluationAt(_criterion);
    }
}
