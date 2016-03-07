package interfaces;

import java.util.HashMap;

public interface IGene
{
    void setValues(double values[]);
    double[] getValues();

    IGene clone();

    void print(int level);

    void setGene(HashMap<String, IGene> gene);
    HashMap<String, IGene> getGene();

    boolean isEqual(IGene g, Double epsilon);
}
