package decision.elicitation.rules;

import decision.elicitation.rules.interfaces.IRule;
import standard.Common;

/**
 * Created by Michał on 2015-07-13.
 */
public class BaseRule implements IRule
{
    private int _interval = 1;
    private int _limit = Common.MAX_INT;
    private int _start = 0;

    private int _numberOfElicitations = 0;

    public BaseRule(int interval)
    {
        this(interval, 0);
    }

    public BaseRule(int interval, int start)
    {
        this(interval, start, Common.MAX_INT);
    }

    public BaseRule(int interval, int start, int limit)
    {
        this._interval = interval;
        this._start = start;
        this._limit = limit;
        this._numberOfElicitations = 0;
    }

    @Override
    public boolean isElicitationTime(int time)
    {
        return time >= _start && _numberOfElicitations < _limit && (time - _start) % _interval == 0;
    }

    @Override
    public int getElicitationTime(int time)
    {
        if (time >= _start && _numberOfElicitations < _limit && (time - _start) % _interval == 0) return 1;
        else return 0;
    }

    @Override
    public void increaseElicitation()
    {
        _numberOfElicitations++;
    }

    @Override
    public boolean isElicitationBegin()
    {
        return _numberOfElicitations > 0;
    }

    @Override
    public boolean isElicitationDone(int time)
    {
        return _numberOfElicitations >= _limit;
    }

    @Override
    public int getNumberOfElicitations()
    {
        return _numberOfElicitations;
    }

    @Override
    public double getEstimatedNumberOfElicitations(int generation)
    {
        if (isElicitationDone(generation)) return _numberOfElicitations;
        if (generation < _start) return 0.0d;
        double prop = 1.0d / _interval;
        return 1 + ((generation - 1) - _start) * prop;
    }

}
