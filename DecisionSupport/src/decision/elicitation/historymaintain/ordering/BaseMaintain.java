package decision.elicitation.historymaintain.ordering;

import decision.elicitation.historymaintain.ordering.interfaces.IMaintain;
import decision.maker.ordering.Order;

import java.util.LinkedList;

/**
 * Created by Michał on 2015-07-13.
 *
 */
public class BaseMaintain implements IMaintain
{
    int _historySize = 50;

    public BaseMaintain(int historySize)
    {
        _historySize = historySize;
    }

    @Override
    public void maintainHistory(LinkedList<Order> history)
    {
        if (history.size() > _historySize) history.removeFirst();
    }
}
