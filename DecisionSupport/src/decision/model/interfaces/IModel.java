package decision.model.interfaces;

import alternative.interfaces.IAlternative;
import extractor.interfaces.IAlternativeExtractor;

import java.util.ArrayList;

/**
 * Created by Michał on 2015-07-12.
 *
 */
public interface IModel
{
    double rateCandidate(IAlternative alternative);
    double[] rateCandidate(ArrayList<IAlternative> alternatives);
    double[] rateCandidate(Object arrayOfObjects, IAlternativeExtractor extractor);

    // TODO RELATIONAL MODEL ZROBIC, JAKAS ELECTRA COS, POBIERAC W FORMIE MACIERZY, GRAFU

    boolean hasModel();
    void setModel(Object model);
    Object getModel();
}
