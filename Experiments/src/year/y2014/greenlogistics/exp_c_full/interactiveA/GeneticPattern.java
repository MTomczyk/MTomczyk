package year.y2014.greenlogistics.exp_c_full.interactiveA;

import alternative.Alternative;
import alternative.interfaces.IAlternative;
import base.Specimen;
import decision.maker.ordering.OrderingDM;
import decision.model.utilityfunction.PartialSumUtility;
import interfaces.IGenetic;
import interfaces.ISpecimen;
import measure.GenerationTrialMeasure;
import measure.Measure;
import measure.population.specimen.DMOrderingExtractor;
import runner.Runner;
import runner.drawer.CubePareto;
import runner.interfaces.IRunner;
import standard.Common;
import utils.Domination;
import utils.FileManager;

import java.util.ArrayList;

/**
 * Created by MTomczyk on 23.08.2015.
 */
public class GeneticPattern
{
    public static void main(String args[])
    {
        new MainDataGetter();
        MainDataGetter data;

        // --- SET DIRECTORIES ---
        String basePath = MainDataGetter._basePath;
        FileManager.createDir(basePath, "/");

        for (ArrayList<Object> param : MainDataGetter._params)
        {
            data = new MainDataGetter();
            GenerationTrialMeasure gtm = new GenerationTrialMeasure(data.getGenerationTrialMeasureParams());

            long beginTime = System.currentTimeMillis();

            // --- CREATE SUB DIR ---
            String sd = "";
            for (int i = 0; i < param.size(); i++)
            {
                sd += MainDataGetter._keys.get(i) + "_" + param.get(i).toString();
                if (i < param.size() - 1) sd += "_";
            }
            // --- CREATE SUB DIR ---
            FileManager.createDir(basePath + "/" + sd);

            System.out.println(sd);

            int same[] = {0, 0};
            int sameFromAll[] = {0, 0};

            for (int t = 0; t < data._trials; t++)
            {
                ArrayList<IGenetic> genetic = data.getGenetic(param, t);

                // --- UPDATE STATISTIC EXTRACTOR
                DMOrderingExtractor e = (DMOrderingExtractor) data._featureExtractor.get(0);
                e._dm = new OrderingDM(new PartialSumUtility(data.getUtilityFunctions(t), data._criteria));
                ArrayList<Measure> m = data.getMeasure(genetic, data._generations);

                //System.out.println(ReferenceSolutions._data[t][0] + " " + ReferenceSolutions._data[t][1] + " " +
                //        ReferenceSolutions._data[t][2] + " ");

                // --- RUN ALGORITHM ---
                IRunner r = new Runner(genetic, data._criteria, null);
                r.init();

                System.out.println(t);
                for (int g = 0; g < data._generations; g++)
                {
                    r.step(g);
                    for (int i = 0; i < genetic.size(); i++)
                    {
                        if (genetic.get(i).getPareto().size() == 0) System.out.println("ERROR!");
                        m.get(i).updateData(genetic.get(i).getPareto());
                    }
                }

                for (int i = 0; i < genetic.size(); i++)
                {
                    double rs[] = ReferenceSolutions._data[t];
                    IAlternative a = new Alternative("A", data._criteria);
                    a.setEvaluationVector(rs, data._criteria);

                    for (ISpecimen s: genetic.get(i).getPareto())
                    {
                        if (Domination.isGoodAtLeastAs(s.getAlternative(),
                                a,data._criteria, 1.1d))
                        {
                            sameFromAll[i]++;
                            break;
                        }
                    }
                    if (Domination.isGoodAtLeastAs(genetic.get(i).getPareto().get(0).getAlternative(),
                            a, data._criteria, 1.1d))
                        same[i]++;

                    gtm.addData(data._dummyGenetic.get(i), m.get(i));
                }


                long endTime = System.currentTimeMillis();
                //System.out.println(t + " " + (endTime - beginTime) / 1000.0d);
            }

            System.out.print("PARAMS ");
            for (Object o: param)
                System.out.print(o.toString() + " " );
            System.out.println("");
            System.out.println(same[0] + " " + same[1]);
            System.out.println(sameFromAll[0] + " " + sameFromAll[1]);

            gtm.calculateStatistics();
            SaveRawPattern.saveRaw(basePath + "/" + sd, data._dummyGenetic, gtm, data._featureExtractor,
                    data._statisticExtractor, data._internalStatisticExtractor, data._populationComprehensive,
                    data._cInput, data._generations, data._trials);
        }
    }
}
