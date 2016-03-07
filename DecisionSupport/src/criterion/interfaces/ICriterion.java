package criterion.interfaces;

import extractor.interfaces.IValueExtractor;
import standard.Range;

import java.util.HashMap;

/**
 * Created by Michal on 2014-12-28.
 * @author Michal Tomczyk
 *
 */

public interface ICriterion
{
    String getName();
    void setName(String name);

    boolean isGain();
    void setGain(boolean gain);

    IValueExtractor getExtractor();
    void setExtractor(IValueExtractor extractor);

    HashMap<String, Range> getRange();
    void setRange(HashMap<String, Range> ranges);
}
