package shared;

import org.apache.commons.math3.random.MersenneTwister;

public class SC
{
    private static SC _instance = null;
    private boolean _debug = false;
    private MersenneTwister _generator = null;

    private SC()
    {
        _generator = new MersenneTwister(System.currentTimeMillis());
    }

    @SuppressWarnings("unused")
    public static void notify(String msg)
    {

    }

    public static SC getInstance()
    {
        if (SC._instance == null) _instance = new SC();
        return _instance;
    }

    public void log(String msg)
    {
        if (_debug) System.out.print(msg);
    }

    @SuppressWarnings("unused")
    public boolean isDebug()
    {
        return _debug;
    }

    public void setDebug(boolean debug)
    {
        this._debug = debug;
    }

    public MersenneTwister getRandomNumberGenerator()
    {
        return _generator;
    }
}
