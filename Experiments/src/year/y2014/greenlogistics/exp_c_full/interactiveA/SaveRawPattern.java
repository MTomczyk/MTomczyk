package year.y2014.greenlogistics.exp_c_full.interactiveA;

import interfaces.IGenetic;
import measure.GenerationTrialMeasure;
import measure.population.interfaces.IPopulationComprehensive;
import measure.population.interfaces.IStatisticExtractor;
import measure.population.specimen.interfaces.IFeatureExtractor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import standard.FileUtil;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by MTomczyk on 23.08.2015.
 * Created by MTomczyk on 23.08.2015.
 */
public class SaveRawPattern
{
    public static void saveRaw(String dir, ArrayList<IGenetic> genetic, GenerationTrialMeasure gtm, ArrayList<IFeatureExtractor> fe,
                               ArrayList<IStatisticExtractor> se, ArrayList<IStatisticExtractor> ise,
                               ArrayList<IPopulationComprehensive> pc,
                               ArrayList<ArrayList<ChartInput>> cInput, int generations, int trials)
    {
        String delim = ";";

        int pcSize = pc.size();

        int baseColumns = (se.size() - pcSize) * (ise.size());
        int columns = 1 + baseColumns * (fe.size() - pcSize) + (pcSize * ise.size());
        int columnsTest = 1 + (se.size() - pcSize) * (fe.size() - pcSize) + pcSize;

        String header[][] = getHeader(columns, baseColumns, gtm, fe, se, ise, pc);
        String headerTest[][] = getHeaderTest(gtm, fe, se, ise, pc);

        for (IGenetic g : genetic)
        {
            String name = g.getName();

            File file = FileUtil.getFile(dir + "/" + name + ".txt");
            FileWriter fw = FileUtil.getFileWriter(file);
            assert fw != null;
            BufferedWriter bw = new BufferedWriter(fw);

            SXSSFWorkbook wb = new SXSSFWorkbook();
            Sheet sh = wb.createSheet("Result");

            FileOutputStream out;
            try
            {
                out = new FileOutputStream(dir + "/" + name + ".xlsx");
                // ALGORITHMS RUN
                {
                    FileUtil.writeToFile(bw, "Algorithm (" + name + ") run (function of generation");
                    printHeader(header, delim, bw, sh, wb, fe.size() - pcSize, baseColumns, ise.size());

                    double viaGenerations[][] = getThroughGenerations(columns, generations, 0, g, gtm, fe, se, ise, pc);

                    printResults(viaGenerations, generations, delim, bw, sh, wb, fe.size() - pcSize, baseColumns, ise.size());

                    createCharts(sh, generations, columns, cInput, fe, se, ise, pc);
                }

                // ALGORITHMS RUN TEST
                {
                    sh = wb.createSheet("Result TEST");
                    FileUtil.writeToFile(bw, "Algorithm (" + name + ") last generation results");
                    printHeaderTest(headerTest, delim, bw, sh, wb, fe.size() - pcSize, se.size() - pcSize, 1);
                    double forTest[][] = getForTest(columnsTest, generations, trials, 0, g, gtm, fe, se, pc);
                    printResultsTest(forTest, trials, delim, bw, sh, wb, fe.size() - pcSize, (se.size() - 1), 1);
                }

                // AUC
                {
                    sh = wb.createSheet("AUC");
                    FileUtil.writeToFile(bw, "AUC (" + name + ")  (function of generation");
                    printHeader(header, delim, bw, sh, wb, fe.size() - pcSize, baseColumns, ise.size());

                    double viaGenerations[][] = getThroughGenerations(columns, generations, 1, g, gtm, fe, se, ise, pc);

                    printResults(viaGenerations, generations, delim, bw, sh, wb, fe.size() - pcSize, baseColumns, ise.size());

                    createCharts(sh, generations, columns, cInput, fe, se, ise, pc);
                }


                // AUC RUN TEST
                {
                    sh = wb.createSheet("AUC TEST");
                    FileUtil.writeToFile(bw, "AUC (" + name + ")  last generation results");
                    printHeaderTest(headerTest, delim, bw, sh, wb, fe.size() - pcSize, se.size() - pcSize, 1);
                    double forTest[][] = getForTest(columnsTest, generations, trials, 1, g, gtm, fe, se, pc);
                    printResultsTest(forTest, trials, delim, bw, sh, wb, fe.size() - pcSize, (se.size() - 1), 1);
                }

                // AUC TIME
                {
                    sh = wb.createSheet("AUC Time");
                    FileUtil.writeToFile(bw, "AUC TIME (" + name + ") (function of generation");
                    printHeader(header, delim, bw, sh, wb, fe.size() - pcSize, baseColumns, ise.size());

                    double viaGenerations[][] = getThroughGenerations(columns, generations, 2, g, gtm, fe, se, ise, pc);

                    printResults(viaGenerations, generations, delim, bw, sh, wb, fe.size() - pcSize, baseColumns, ise.size());

                    createCharts(sh, generations, columns, cInput, fe, se, ise, pc);
                }


                // AUC TIME TEST
                {
                    sh = wb.createSheet("AUC TIME TEST");
                    FileUtil.writeToFile(bw, "AUC Time (" + name + ")  last generation results");
                    printHeaderTest(headerTest, delim, bw, sh, wb, fe.size() - pcSize, se.size() - pcSize, 1);
                    double forTest[][] = getForTest(columnsTest, generations, trials, 2, g, gtm, fe, se, pc);
                    printResultsTest(forTest, trials, delim, bw, sh, wb, fe.size() - pcSize, (se.size() - 1), 1);
                }


                FileUtil.closeWriter(bw);

                wb.write(out);
                out.close();
                wb.dispose();

            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    private static void createCharts(Sheet sh, int generations, int columns, ArrayList<ArrayList<ChartInput>> cInput,
                                     ArrayList<IFeatureExtractor> fe, ArrayList<IStatisticExtractor> se,
                                     ArrayList<IStatisticExtractor> ise, ArrayList<IPopulationComprehensive> pc)
    {
        // --- CREATE CUSTOM
        int bx = columns + 3;
        int by = 3;

        int chartWidth = 8;
        int chartHeight = 18;

        for (ArrayList<ChartInput> ci : cInput)
        {
            Drawing drawing = sh.createDrawingPatriarch();
            ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, bx, by, bx + chartWidth, by + chartHeight);

            Chart chart = drawing.createChart(anchor);
            ChartLegend legend = chart.getOrCreateLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);

            LineChartData data = chart.getChartDataFactory().createLineChartData();

            ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
            ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            for (ChartInput i : ci)
            {
                ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sh, new CellRangeAddress(4, 4 + generations, 1, 1));
                int c = 2 + getColumnNumber(i.fe, i.se, i.ise, fe, se, ise, pc);
                //System.out.println(i.fe.getKey() + " " + i.se.getKey() + " " + i.ise.getKey() + " " + (c - 2));
                ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sh, new CellRangeAddress(4, 4 + generations, c, c));

                String title = i.fe.getKey() + " " + i.se.getKey() + " " + i.ise.getKey();

                LineChartSeries chartSeries = data.addSeries(xs, ys1);
                chartSeries.setTitle(title);
            }
            chart.plot(data, bottomAxis, leftAxis);
            // ---------------------------------
            by += chartHeight + 2;
        }
    }

    private static boolean isFeatureInList(IFeatureExtractor fe, ArrayList<IPopulationComprehensive> pc)
    {
        for (IPopulationComprehensive p : pc)
            if (p.getKey().equals(fe.getKey())) return true;
        return false;
    }

    private static boolean isFeatureNotInList(IFeatureExtractor fe, ArrayList<IPopulationComprehensive> pc)
    {
        for (IPopulationComprehensive p : pc)
            if (p.getKey().equals(fe.getKey())) return false;
        return true;
    }

    private static int getColumnNumber(IFeatureExtractor cFe, IStatisticExtractor cSe, IStatisticExtractor cIse, ArrayList<IFeatureExtractor> fe,
                                       ArrayList<IStatisticExtractor> se, ArrayList<IStatisticExtractor> ise,
                                       ArrayList<IPopulationComprehensive> pc)
    {
        int result = 0;

        for (IFeatureExtractor featureExtractor : fe)
        {
            if (!featureExtractor.getKey().equals(cFe.getKey()))
            {
                //if ((featureExtractor.getKey().equals("DataSize"))
                //        || (featureExtractor.getKey().equals("ElapsedTime")))
                if (isFeatureInList(featureExtractor, pc))
                    result += (ise.size());
                else
                    result += (se.size() - pc.size()) * (ise.size());

                continue;
            }

            for (IStatisticExtractor statisticExtractor : se)
            {
                if (!statisticExtractor.getKey().equals(cSe.getKey()))
                {
                    if (isFeatureNotInList(featureExtractor, pc))
                        result += (ise.size());
                    continue;
                }

                for (IStatisticExtractor internalStatisticExtractor : ise)
                {
                    if (!internalStatisticExtractor.getKey().equals(cIse.getKey()))
                        result++;
                    else return result;
                }
            }
        }

        return 0;
    }

    private static void printResultsTest(double[][] res, int trials, String delim, BufferedWriter bw,
                                         Sheet sh, Workbook wb, int baseFeatures, int baseColumns, int secondaryColumns)
    {
        int itR = 3;
        for (int i = 0; i < trials; i++)
        {
            Row row = sh.createRow(itR++);
            int itC = 1;
            String l = "";
            for (double[] viaGeneration : res)
            {
                l += Double.toString(viaGeneration[i]);
                l += delim;

                Cell cell = row.createCell(itC++);
                cell.setCellValue(viaGeneration[i]);

                boolean leftBorder = false;
                boolean rightBorder = false;
                boolean bottomBorder = false;

                if (i == trials - 1) bottomBorder = true;
                if ((itC == 2) || ((itC - 1) % secondaryColumns == 0)) leftBorder = true;
                if ((itC == 2) || (itC == res.length + 1)) rightBorder = true;

                /*CellStyle cs = wb.createCellStyle();
                if (bottomBorder) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                if (leftBorder) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (rightBorder) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);

                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                if (itC == 2) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

                cs.setAlignment(XSSFCellStyle.ALIGN_RIGHT);

                if (itC > 2)
                    cs.setDataFormat(
                            wb.getCreationHelper().createDataFormat().getFormat("0.0000"));
                else
                    cs.setDataFormat(
                            wb.getCreationHelper().createDataFormat().getFormat("#"));
                cell.setCellStyle(cs);*/

            }
            FileUtil.writeToFile(bw, l.replace('.', ','));
        }
    }


    private static void printResults(double[][] res, int generations, String delim, BufferedWriter bw,
                                     Sheet sh, Workbook wb, int baseFeatures, int baseColumns, int secondaryColumns)
    {
        int itR = 4;
        for (int i = 0; i < generations; i++)
        {
            Row row = sh.createRow(itR++);
            int itC = 1;
            String l = "";
            for (double[] viaGeneration : res)
            {
                l += Double.toString(viaGeneration[i]);
                l += delim;

                Cell cell = row.createCell(itC++);
                cell.setCellValue(viaGeneration[i]);

                boolean leftBorder = false;
                boolean rightBorder = false;
                boolean bottomBorder = false;

                if (i == generations - 1) bottomBorder = true;
                if ((itC == 2) || ((itC - 1) % secondaryColumns == 0)) leftBorder = true;
                if ((itC == 2) || (itC == res.length + 1)) rightBorder = true;


                /*CellStyle cs = wb.createCellStyle();
                if (bottomBorder) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                if (leftBorder) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (rightBorder) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);

                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                if (itC == 2) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

                cs.setAlignment(XSSFCellStyle.ALIGN_RIGHT);

                if (itC > 2)
                    cs.setDataFormat(
                            wb.getCreationHelper().createDataFormat().getFormat("0.0000"));
                else
                    cs.setDataFormat(
                            wb.getCreationHelper().createDataFormat().getFormat("#"));
                cell.setCellStyle(cs);*/

            }
            FileUtil.writeToFile(bw, l.replace('.', ','));
        }
    }

    private static void printHeader(String header[][], String delim, BufferedWriter bw, Sheet sh,
                                    Workbook wb, int baseFeatures, int baseColumns, int secondaryColumns)
    {
        int itR = 1;
        for (String[] r : header)
        {
            Row row = sh.createRow(itR++);
            int itC = 1;
            String l = "";
            for (String c : r)
            {
                l += (c + delim);
                Cell cell = row.createCell(itC++);
                cell.setCellValue(c);

                boolean leftBorder = false;
                boolean rightBorder = false;
                boolean topBorder = false;
                boolean bottomBorder = false;
                if ((itR == 2) || ((itR == 3) && (itC > 2))) topBorder = true;
                if ((itR == 4) || ((itR == 3) && (itC > 2))) bottomBorder = true;
                if (itC == 2) leftBorder = true;

                if (itC == 2) rightBorder = true;
                else if ((itC > 2) && (itC <= 2 + baseColumns * baseFeatures))
                {
                    if ((itR == 2) && ((itC - 2) % baseColumns == 0))
                        rightBorder = true;
                    if ((itR > 2) && ((itC - 2) % secondaryColumns == 0))
                        rightBorder = true;

                } else if (itC > 2 + baseColumns * baseFeatures)
                {
                    if ((itC - (2 + baseColumns * baseFeatures)) % secondaryColumns == 0) rightBorder = true;
                }

                CellStyle cs = wb.createCellStyle();
                if (topBorder) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                if (bottomBorder) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                if (leftBorder) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (rightBorder) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);


                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                if ((itC > 2) && (itR == 2)) cs.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                if ((itC > 2) && (itR == 3)) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                cell.setCellStyle(cs);
            }
            FileUtil.writeToFile(bw, l);
        }
    }

    private static void printHeaderTest(String header[][], String delim, BufferedWriter bw, Sheet sh,
                                        Workbook wb, int baseFeatures, int baseColumns, int secondaryColumns)
    {
        int itR = 1;
        for (String[] r : header)
        {
            Row row = sh.createRow(itR++);
            int itC = 1;
            String l = "";
            for (String c : r)
            {
                l += (c + delim);
                Cell cell = row.createCell(itC++);
                cell.setCellValue(c);

                boolean leftBorder = false;
                boolean rightBorder = false;
                boolean topBorder = false;
                boolean bottomBorder = false;
                if ((itR == 2) || ((itR == 3) && (itC > 2))) topBorder = true;
                if (itR == 3) bottomBorder = true;
                if (itC == 2) leftBorder = true;

                if (itC == 2) rightBorder = true;
                else if ((itC > 2) && (itC <= 2 + baseColumns * baseFeatures))
                {
                    if ((itR == 2) && ((itC - 2) % baseColumns == 0))
                        rightBorder = true;
                    if ((itR > 2) && ((itC - 2) % secondaryColumns == 0))
                        rightBorder = true;

                } else if (itC > 2 + baseColumns * baseFeatures)
                {
                    if ((itC - (2 + baseColumns * baseFeatures)) % secondaryColumns == 0) rightBorder = true;
                }

                /*CellStyle cs = wb.createCellStyle();
                if (topBorder) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                if (bottomBorder) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                if (leftBorder) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (rightBorder) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);


                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                if ((itC > 2) && (itR == 2)) cs.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                if ((itC > 2) && (itR == 3)) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                cell.setCellStyle(cs);*/
            }
            FileUtil.writeToFile(bw, l);
        }
    }

    private static double[][] getForTest(int columns, int generations, int trials, int mode,
                                         IGenetic g,
                                         GenerationTrialMeasure gtm,
                                         ArrayList<IFeatureExtractor> fe,
                                         ArrayList<IStatisticExtractor> se,
                                         ArrayList<IPopulationComprehensive> populationComprehensives)
    {
        double forTest[][] = new double[columns][trials];
        for (int i = 0; i < trials; i++) forTest[0][i] = i + 1;


        int c = 0;
        for (int i = 0; i < fe.size() - populationComprehensives.size(); i++)
        {
            for (int j = 0; j < se.size() - populationComprehensives.size(); j++)
            {
                c++;
                if (mode == 0)
                    forTest[c] = gtm.getVectorPopulBestTest(g, fe.get(i), se.get(j));
                else if (mode == 1)
                    forTest[c] = gtm.getVectorAUCBestTest(g, fe.get(i), se.get(j));
                else if (mode == 2)
                    forTest[c] = gtm.getVectorAUCTimeBestTest(g, fe.get(i), se.get(j));

            }
        }

        for (int i = populationComprehensives.size(); i > 0; i--)
        {
            c++;
            if (mode == 0) forTest[c] = gtm.getVectorPopulBestTest(g, fe.get(fe.size() - i),
                    se.get(se.size() - i));
            else if (mode == 1) forTest[c] = gtm.getVectorAUCBestTest(g,  fe.get(fe.size() - i),
                    se.get(se.size() - i));
            else if (mode == 2) forTest[c] = gtm.getVectorAUCTimeBestTest(g, fe.get(fe.size() - i),
                    se.get(se.size() - i));
        }

        return forTest;
    }


    private static double[][] getThroughGenerations(int columns, int generations, int mode,
                                                    IGenetic g,
                                                    GenerationTrialMeasure gtm,
                                                    ArrayList<IFeatureExtractor> fe,
                                                    ArrayList<IStatisticExtractor> se, ArrayList<IStatisticExtractor> ise,
                                                    ArrayList<IPopulationComprehensive> pc)
    {
        double viaGenerations[][] = new double[columns][generations];
        for (int i = 0; i < generations; i++) viaGenerations[0][i] = i + 1;

        int pcSize = pc.size();

        {
            int c = 0;
            for (int i = 0; i < fe.size() - pcSize; i++)
            {
                for (int j = 0; j < se.size() - pcSize; j++)
                {
                    for (IStatisticExtractor anIse : ise)
                    {
                        c++;
                        if (mode == 0)
                            viaGenerations[c] = gtm.getVectorPopulThroughGeneration(g, fe.get(i), se.get(j), anIse);
                        else if (mode == 1)
                            viaGenerations[c] = gtm.getVectorAUCThroughGeneration(g, fe.get(i), se.get(j), anIse);
                        else if (mode == 2)
                            viaGenerations[c] = gtm.getVectorAUCTimeThroughGeneration(g, fe.get(i), se.get(j), anIse);
                    }
                }
            }

            for (int i = pcSize; i > 0; i--)
            {
                for (IStatisticExtractor anIse : ise)
                {
                    c++;
                    if (mode == 0) viaGenerations[c] = gtm.getVectorPopulThroughGeneration(g, fe.get(fe.size() - i),
                            se.get(se.size() - i), anIse);
                    else if (mode == 1) viaGenerations[c] = gtm.getVectorAUCThroughGeneration(g, fe.get(fe.size() - i),
                            se.get(se.size() - i), anIse);
                    else if (mode == 2)
                        viaGenerations[c] = gtm.getVectorAUCTimeThroughGeneration(g, fe.get(fe.size() - i),
                                se.get(se.size() - i), anIse);
                }
            }
        }

        return viaGenerations;
    }


    private static String[][] getHeader(int columns, int baseColumns, GenerationTrialMeasure gtm, ArrayList<IFeatureExtractor> fe,
                                        ArrayList<IStatisticExtractor> se, ArrayList<IStatisticExtractor> ise,
                                        ArrayList<IPopulationComprehensive> populationComprehensives)
    {
        String header[][] = new String[3][columns];

        int c = 1;
        int r = 0;

        header[0][0] = "GEN";

        // 1st LINE
        {
            //String l = "GEN" + delim;
            for (int i = 0; i < fe.size() - populationComprehensives.size(); i++)
            {
                for (int j = 0; j < baseColumns; j++)
                {
                    if (j == 0) header[r][c++] = fe.get(i).getKey();
                    else header[r][c++] = " ";
                }
            }

            for (IPopulationComprehensive pc : populationComprehensives)
            {
                header[r][c++] = pc.getKey();
                for (int i = 0; i < ise.size() - 1; i++)
                    header[r][c++] = " ";
            }
        }
        header[1][0] = " ";
        c = 1;
        r = 1;
        // 2nd LINE
        {
            for (int i = 0; i < fe.size() - populationComprehensives.size(); i++)
            {
                for (int j = 0; j < se.size() - populationComprehensives.size(); j++)
                {
                    for (int k = 0; k < ise.size(); k++)
                    {
                        if (k == 0) header[r][c++] = se.get(j).getKey();
                        else header[r][c++] = " ";
                    }
                }
            }

            for (IPopulationComprehensive pc : populationComprehensives)
            {
                header[r][c++] = pc.getKey();
                for (int i = 0; i < ise.size() - 1; i++)
                    header[r][c++] = " ";
            }
        }
        header[2][0] = " ";
        c = 1;
        r = 2;
        {
            for (int i = 0; i < fe.size() - populationComprehensives.size(); i++)
            {
                for (int j = 0; j < se.size() - populationComprehensives.size(); j++)
                {
                    for (IStatisticExtractor anIse : ise)
                    {
                        header[r][c++] = anIse.getKey();
                    }
                }
            }

            for (IPopulationComprehensive pc : populationComprehensives)
            {
                for (IStatisticExtractor anIse : ise)
                    header[r][c++] = anIse.getKey();
            }
        }

        return header;
    }

    private static String[][] getHeaderTest(GenerationTrialMeasure gtm, ArrayList<IFeatureExtractor> fe,
                                            ArrayList<IStatisticExtractor> se, ArrayList<IStatisticExtractor> ise,
                                            ArrayList<IPopulationComprehensive> populationComprehensives)
    {
        int baseColumns = (se.size() - populationComprehensives.size());
        int columns = 1 + baseColumns * (fe.size() - populationComprehensives.size()) + populationComprehensives.size();
        String header[][] = new String[2][columns];

        int c = 1;
        int r = 0;

        header[0][0] = "GEN";

        // 1st LINE
        {
            //String l = "GEN" + delim;
            for (int i = 0; i < fe.size() - populationComprehensives.size(); i++)
            {
                for (int j = 0; j < baseColumns; j++)
                {
                    if (j == 0) header[r][c++] = fe.get(i).getKey();
                    else header[r][c++] = " ";
                }
            }

            for (IPopulationComprehensive pc : populationComprehensives)
                header[r][c++] = pc.getKey();
        }
        header[1][0] = " ";
        c = 1;
        r = 1;
        // 2nd LINE
        {
            for (int i = 0; i < fe.size() - populationComprehensives.size(); i++)
            {
                for (int j = 0; j < se.size() - populationComprehensives.size(); j++)
                {
                    header[r][c++] = se.get(j).getKey();
                }
            }

            for (IPopulationComprehensive pc : populationComprehensives)
                header[r][c++] = pc.getKey();
        }

        return header;
    }
}
