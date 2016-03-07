package distance.interfaces;

import normalization.interfaces.INormalization;
import standard.Point;


import java.util.ArrayList;

/**
 * Created by Michał on 2015-02-09.
 *
 */
public interface IDistance
{
    double getDistance(Point A, Point B);
    void setNormalizations(ArrayList <INormalization> normalizations);
}
