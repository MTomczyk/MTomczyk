package decision.elicitation.historymaintain.ordering.interfaces;

import decision.maker.ordering.Order;

import java.util.LinkedList;


/**
 * Created by Michał on 2015-07-13.
 *
 */
public interface IMaintain
{
    void maintainHistory(LinkedList<Order> history);
}
