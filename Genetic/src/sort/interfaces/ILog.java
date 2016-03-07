package sort.interfaces;

/**
 * Created by Michał on 2015-03-06.
 *
 */
public interface ILog
{
    void addLog(String key, Object value);
    Object getLog(String key);
}
