package sort;

import sort.interfaces.ILog;

import java.util.HashMap;

/**
 * Created by Michał on 2015-03-06.
 */
public class Log implements ILog
{
    private HashMap<String, Object> log = null;

    public Log()
    {
        this.log = new HashMap<>();
    }

    @Override
    public void addLog(String key, Object value)
    {
        log.put(key, value);
    }

    @Override
    public Object getLog(String key)
    {
        return log.get(key);
    }
}
