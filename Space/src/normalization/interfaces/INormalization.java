package normalization.interfaces;


/**
 * Created by Michał on 2015-02-09.
 */
public interface INormalization
{
    double getNormalized(double value);

    void reset();
    void update(double value);
    void perform();
}
