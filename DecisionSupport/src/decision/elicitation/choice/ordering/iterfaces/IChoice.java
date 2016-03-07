package decision.elicitation.choice.ordering.iterfaces;

import alternative.interfaces.IAlternative;
import decision.maker.ordering.Order;
import decision.maker.ordering.interfaces.IOrderingDM;
import extractor.interfaces.IAlternativeExtractor;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-02-12.
 *
 *
 */

@SuppressWarnings("all")
public interface IChoice
{
    Order getOrder(ArrayList<IAlternative> alternatives, IOrderingDM dm, IOrderingDM estimatedDM);
    Order getOrder(Object arrayOfObjects, IAlternativeExtractor extractor, IOrderingDM dm, IOrderingDM estimatedDM);
    ArrayList<IAlternative> getAlternativesToCompare(Object arrayOfObjects, IAlternativeExtractor extractor, IOrderingDM estimatedDM);

    void addFeedback(Order o);

    int getRequiredAlternatives();
}
