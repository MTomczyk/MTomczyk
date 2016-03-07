package decision.elicitation.rules.interfaces;

/**
 * Created by Michał on 2015-07-13.
 *
 */
public interface IRule
{
    boolean isElicitationTime(int time);
    int getElicitationTime(int time);

    void increaseElicitation();

    boolean isElicitationBegin();
    boolean isElicitationDone(int time);

    int getNumberOfElicitations();
    double getEstimatedNumberOfElicitations(int generation);
}
