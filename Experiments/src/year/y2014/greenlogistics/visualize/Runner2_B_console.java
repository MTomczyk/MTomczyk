package year.y2014.greenlogistics.visualize;

/**
 * Created by MTomczyk on 19.02.2016.
 */
public class Runner2_B_console
{
    public static void main(String args[])
    {
        String p[][] =
                {
                        {"50", "0.00075"},
                        {"50", "0.00075"},
                        {"50", "0.00075"},
                        {"50", "0.00075"},
                        {"50", "0.00075"},
                };
        for (String l[] : p)
        {
            System.out.println(":::: " + l[0] + " " + l[1]);
            Runner2_B_param.main(l);
        }
    }
}
