package year.y2014.greenlogistics.visualize;

/**
 * Created by MTomczyk on 19.02.2016.
 */
public class Runner2_A_console
{
    public static void main(String args[])
    {
        String p[][] =
                {
                        {"200", "0.010"},
                        //{"200", "0.012"},
                };

        for (String l[] : p)
        {
            System.out.println(":::: " + l[0] + " " + l[1]);
            Runner2_A_param.main(l);
        }
    }
}
