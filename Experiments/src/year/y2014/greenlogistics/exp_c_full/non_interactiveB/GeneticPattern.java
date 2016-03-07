package year.y2014.greenlogistics.exp_c_full.non_interactiveB;

import criterion.interfaces.ICriterion;
import decision.maker.ordering.OrderingDM;
import decision.model.utilityfunction.PartialSumUtility;
import interfaces.IGenetic;
import interfaces.ISpecimen;
import measure.GenerationTrialMeasure;
import measure.Measure;
import measure.population.specimen.DMOrderingExtractor;
import runner.Runner;
import runner.interfaces.IRunner;
import standard.FileUtil;
import utils.FileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

            for (int t = 0; t < data._trials; t++)
            {
                ArrayList<IGenetic> genetic = data.getGenetic(param, t);

                // --- UPDATE STATISTIC EXTRACTOR
                DMOrderingExtractor e = (DMOrderingExtractor) data._featureExtractor.get(0);
                e._dm = new OrderingDM(new PartialSumUtility(data.getUtilityFunctions(t), data._criteria));
                ArrayList<Measure> m = data.getMeasure(genetic, data._generations);

                // --- RUN ALGORITHM ---
                int repeat[] = {1,1,200};
                IRunner r = new Runner(genetic, data._criteria, null, repeat);
                r.init();

                System.out.println(t);
                for (int g = 0; g < data._generations; g++)
                {
                    //System.out.println(g);
                    r.step(g);
                    for (int i = 0; i < genetic.size(); i++)
                    {
                        if (genetic.get(i).getPareto().size() == 0) System.out.println("ERROR!");
                        m.get(i).updateData(genetic.get(i).getPareto());
                    }
                }

                for (int i = 0; i < genetic.size(); i++)
                    gtm.addData(data._dummyGenetic.get(i), m.get(i));

                long endTime = System.currentTimeMillis();
                System.out.println(t + " " + (endTime - beginTime) / 1000.0d);

                // SAFE PARETO-FRONTS
                safePareto(MainDataGetter._basePath, genetic.get(0), t, data._criteria);
                safePareto(MainDataGetter._basePath, genetic.get(1), t, data._criteria);
                safePareto(MainDataGetter._basePath, genetic.get(2), t, data._criteria);
            }

            gtm.calculateStatistics();
            SaveRawPattern.saveRaw(basePath + "/" + sd, data._dummyGenetic, gtm, data._featureExtractor,
                    data._statisticExtractor, data._internalStatisticExtractor, data._populationComprehensive,
                    data._cInput, data._generations, data._trials);
        }
    }

    private static void safePareto(String path, IGenetic g, int t, ArrayList<ICriterion>criteria)
    {
        String name = String.format("%s_%d", g.getName(), t);

        FileUtil.createDir(path + "/pareto_B/");

        File file = FileUtil.getFile(path + "/pareto_B/" + name + ".txt");
        FileWriter fw = FileUtil.getFileWriter(file);
        assert fw != null;
        BufferedWriter bw = new BufferedWriter(fw);


        for (ISpecimen s: g.getPareto())
        {
            double v1 = s.getAlternative().getEvaluationAt(criteria.get(0));
            double v2 = s.getAlternative().getEvaluationAt(criteria.get(1));
            double v3 = s.getAlternative().getEvaluationAt(criteria.get(2));
            String l = String.format("%.2f %.2f %.2f", v1, v2, v3);
            FileUtil.writeToFile(bw, l);
        }

        FileUtil.closeWriter(bw);

    }
}
