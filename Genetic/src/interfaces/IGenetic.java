package interfaces;

import sort.interfaces.ISorter;

import java.util.ArrayList;

public interface IGenetic
{

    void init();
    void evaluate(int generation, boolean log);
    void sort(int generation);
    void select();
    void reproduce(Integer generation);
    void kill(int generation);
    void stepPreparation(int generation);

    ISorter getSorter();

    ArrayList<ISpecimen> getSpecimens();
    void setSpecimens(ArrayList<ISpecimen> specimens);
    ArrayList<ISpecimen> getPareto();
    ArrayList<ISpecimen> getToDraw();
    ISpecimen getRepresentativeSpecimen();

    @SuppressWarnings("unused")
    void printPopulation();
    void printEvaluation(int specimens);

    double getElapsedTime();

    String getName();
}
