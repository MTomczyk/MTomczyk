package year.y2014.greenlogistics.exp_b_domination;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import criterion.Criterion;
import criterion.interfaces.ICriterion;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import standard.Common;
import standard.FileUtil;
import utils.Domination;
import year.y2014.greenlogistics.exp_visualization.Separate;

import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * Created by MTomczyk on 17.11.2015.
 */
public class ComputeDominance_B
{
    public static void main(String args[])
    {
        ArrayList<ICriterion> criteria = Criterion.getCriterionArray("C", 3, false);

        String path = "Results/year/y2015/greenlogistics/11_2015/fullB/pareto_B";
        int trials = 100;
        String algs[] = {"WSM", "ECM", "ADAPT-WSM", "ADAPT-ECM", "NSGA-II", "SPEA2", "TP-NSGA-II"};

        ArrayList<ArrayList<ArrayList<IAlternative>>> data = new ArrayList<ArrayList<ArrayList<IAlternative>>>(3);

        double avgPareto[] = {0.0d ,0.0d, 0.0d, 0.0d ,0.0d, 0.0d, 0.0d};


        // PARSE -- FROM FILES ----------------------------------
        int alg = -1;
        for (String s: algs)
        {
            alg++;
            ArrayList<ArrayList<IAlternative>> algData = new ArrayList<ArrayList<IAlternative>>(100);
            data.add(algData);

            int toRead = trials;
            if (s.equals("WSM") || (s.equals("ECM"))|| (s.equals("ADAPT-WSM"))|| (s.equals("ADAPT-ECM")))
                toRead = 1;

            System.out.println("READ: " + s);

            for (int t = 0; t < toRead; t++)
            {
                ArrayList<IAlternative> trialData = new ArrayList<IAlternative>();
                algData.add(trialData);

                if (toRead != 1)
                {
                    String pth = String.format(path + "/" + s + "_%d.txt", t);
                    BufferedReader br = FileUtil.getBufferReader(pth);

                    while (true)
                    {
                        String l = FileUtil.readLine(br);
                        if (l == null) break;
                        String part[] = l.split(" ");

                        IAlternative a = new Alternative("A", criteria);
                        a.setEvaluationAt(criteria.get(0), Double.parseDouble(part[0].replace(',','.')));
                        a.setEvaluationAt(criteria.get(1), Double.parseDouble(part[1].replace(',','.')));
                        a.setEvaluationAt(criteria.get(2), Double.parseDouble(part[2].replace(',','.')));
                        trialData.add(a);

                        if ((alg==4) && (trialData.size() == 200)) break;
                    }

                    FileUtil.closeReader(br);
                    avgPareto[alg] += trialData.size();
                }
                else
                {
                    double cData[][] = Separate.dataWSM_B;
                    if (s.equals("ECM")) cData = Separate.dataECM_B;
                    else if (s.equals("ADAPT-WSM")) cData = Separate.dataWSM_ADAPT_B;
                    else if (s.equals("ADAPT-ECM")) cData = Separate.dataECM_ADAPT_B;

                    for (double l[] : cData)
                    {
                        IAlternative a = new Alternative("A", criteria);
                        a.setEvaluationAt(criteria.get(0), l[0]);
                        a.setEvaluationAt(criteria.get(1), l[1]);
                        a.setEvaluationAt(criteria.get(2), l[2]);
                        trialData.add(a);
                    }

                    avgPareto[alg] += cData.length;
                }
            }

            if (toRead == 1)
            {
                for (int i = 1; i < 100; i++)
                {
                    algData.add(algData.get(i - 1));
                }
            }
        }

        avgPareto[4] /= (double) trials;
        avgPareto[5] /= (double) trials;
        avgPareto[6] /= (double) trials;

        // -----------------------------------------------

        System.out.println("Compute...");

        double dom[][] = new double[data.size()][data.size()];
        double dom_sd[][] = new double[data.size()][data.size()];

        double non_dom[][] = new double[data.size()][data.size()];
        double non_dom_sd[][] = new double[data.size()][data.size()];


        for (int a = 0; a < data.size(); a++)
        {
            for (int b = 0; b < data.size(); b++)
            {
                if (a == b) continue;

                double resDom[] = new double[trials];
                double resNonDom[] = new double[trials];

                for (int t = 0; t < trials; t++)
                {
                    ArrayList<IAlternative> cA = data.get(a).get(t);
                    ArrayList<IAlternative> cB = data.get(b).get(t);

                    for (IAlternative aB: cB)
                    {
                        for (IAlternative aA: cA)
                        {
                            if (Domination.isDominating(aA, aB, criteria, Common.EPSILON))
                            {
                                resDom[t] += 1.0d;
                                break;
                            }
                        }
                    }

                    for (IAlternative aA: cA)
                    {
                        boolean pass = true;
                        for (IAlternative aB: cB)
                        {
                            if (Domination.isDominating(aB, aA, criteria, Common.EPSILON))
                            {
                                pass = false;
                                break;
                            }
                        }
                        if (pass) resNonDom[t] += 1.0d;
                    }
                }

                Mean mean = new Mean();
                dom[a][b] = mean.evaluate(resDom);
                StandardDeviation sd = new StandardDeviation();
                dom_sd[a][b] = sd.evaluate(resDom);

                non_dom[a][b] = mean.evaluate(resNonDom);
                non_dom_sd[a][b] = sd.evaluate(resNonDom);
            }
        }

        System.out.println("DOMINATE");
        for (int i = 0; i < dom.length; i++)
        {
            String ln = String.format("%.2f ", avgPareto[i]);
            System.out.print(ln.replace('.',','));

            for (int j = 0; j < dom.length; j++)
            {
                ln = String.format("%.2f %.2f   ", dom[i][j], dom_sd[i][j]);
                System.out.print(ln.replace('.',','));
            }
            System.out.println("");
        }
        System.out.println("NON-DOMINATE");
        for (int i = 0; i < dom.length; i++)
        {
            String ln = String.format("%.2f ", avgPareto[i]);
            System.out.print(ln.replace('.',','));

            for (int j = 0; j < dom.length; j++)
            {
                ln = String.format("%.2f %.2f   ", non_dom[i][j], non_dom_sd[i][j]);
                System.out.print(ln.replace('.', ','));
            }
            System.out.println("");
        }
        System.out.println("Done...");
    }
}
