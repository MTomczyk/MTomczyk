package criterion;

import alternative.CriterionExtractor;
import criterion.interfaces.ICriterion;
import extractor.interfaces.IValueExtractor;
import standard.Range;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Michal on 2014-12-28.
 *
 * @author Michal Tomczyk
 *         <p/>
 *         Class which represents Criterion.
 */
public class Criterion implements ICriterion
{
    private String _name = null;
    private boolean _gain = true;

    private IValueExtractor _extractor = null;
    private HashMap<String, Range> _range = null;

    /**
     * Simple constructor.
     *
     * @param name Name of criterion.
     * @param gain TRUE = is gain. FALSE = is cost.
     */
    public Criterion(String name, boolean gain)
    {
        this(name, gain, null, null);
    }


    /**
     * Constructor
     *
     * @param name      Name of criterion
     * @param gain      TRUE = is gain. FALSE = is cost.
     * @param extractor Extractor object which extracts evaluation on this criterion from IAlternative.
     * @param range     Container where you can keep different range values for this criterion for different purposes. F.I if you use MDVF (linear programming)
     *                  you can specify constant range values by putting here range with "MDVF" key.
     */
    public Criterion(String name, boolean gain, IValueExtractor extractor, HashMap<String, Range> range)
    {
        this.setName(String.format("Criterion %s", name));
        this._name = name;
        this._gain = gain;
        this._extractor = extractor;
        this._range = range;
    }


    /**
     * Produce array of dummy criteria. For tests purpose.
     *
     * @param baseName Base name of criteria. F.I. if baseName = "C" it will produce criteria C0, C1,..., Cn.
     * @param n        Number of criteria to return.
     * @param gain     Set all criteria gain value to FALSE or TRUE.
     * @return Array of dummy criteria.
     */
    public static ArrayList<ICriterion> getCriterionArray(String baseName, int n, boolean gain)
    {
        ArrayList<ICriterion> result = new ArrayList<ICriterion>(n);
        for (int i = 0; i < n; i++)
        {
            Criterion c = new Criterion(String.format("%s%d", baseName, i), gain);
            c.setExtractor(new CriterionExtractor(c));
            result.add(c);
        }
        return result;
    }

    /**
     * Get name of criterion.
     *
     * @return name of criterion.
     */
    @Override
    public String getName()
    {
        return _name;
    }

    /**
     * Set name of criterion.
     *
     * @param name Name of criterion.
     */
    @Override
    public void setName(String name)
    {
        this._name = name;
    }

    /**
     * Get extractor object which extracts evaluation value on this criterion from IAlternative.
     *
     * @return Extractor.
     */
    @Override
    public IValueExtractor getExtractor()
    {
        return _extractor;
    }

    /**
     * Set extractor object which extracts evaluation value on this criterion from IAlternative.
     *
     * @param extractor Extractor object.
     */
    @Override
    public void setExtractor(IValueExtractor extractor)
    {
        this._extractor = extractor;
    }

    /**
     * Check if this criterion is gain.
     *
     * @return Gain = FALSE or TRUE.
     */
    @Override
    public boolean isGain()
    {
        return _gain;
    }

    /**
     * Set gain value for this criterion.
     *
     * @param gain Gain = FALSE or TRUE.
     */
    @Override
    public void setGain(boolean gain)
    {
        this._gain = gain;
    }

    /**
     * Set map of ranges.
     *
     * @param ranges Map object.
     */
    @Override
    public void setRange(HashMap<String, Range> ranges)
    {
        this._range = ranges;
    }

    /**
     * Get map which keeps range values.
     *
     * @return Map with range values.
     */
    @Override
    public HashMap<String, Range> getRange()
    {
        return _range;
    }
}