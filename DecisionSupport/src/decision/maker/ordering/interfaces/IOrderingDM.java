package decision.maker.ordering.interfaces;

import alternative.interfaces.IAlternative;
import decision.maker.ordering.Order;
import decision.model.interfaces.IModel;
import extractor.interfaces.IAlternativeExtractor;

import java.util.ArrayList;


public interface IOrderingDM
{
    Order order(ArrayList<IAlternative> alternatives);
    Order order(Object arrayOfObjects, IAlternativeExtractor extractor);

    double[] evaluate(ArrayList<IAlternative> alternatives);
    double[] evaluate(Object arrayOfObjects, IAlternativeExtractor extractor);

    int[] getOrderIndex(ArrayList<IAlternative> alternatives);
    int[] getOrderIndex(Object arrayOfObjects, IAlternativeExtractor extractor);

    double evaluate(IAlternative alternative);

    IModel getModel();
    void setModel(IModel model);
}
