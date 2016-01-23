package year.y2014.greenlogistics.exp_c_full.non_interactiveA;

import interfaces.IGenetic;
import measure.GenerationTrialMeasure;
import measure.population.interfaces.IPopulationComprehensive;
import measure.population.interfaces.IStatisticExtractor;
import measure.population.specimen.interfaces.IFeatureExtractor;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import standard.FileUtil;
import utils.FileManager;
import utils.ParamsGenerator;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by MTomczyk on 25.08.2015.
 */
public class CrossSavePattern
{
    private static int _bx = 1;
    private static int _by = 1;

    private static int start = 0;
    private static int interval = 1;


    public static void main(String args[])
    {
        new MainDataGetter();
        MainDataGetter data = new MainDataGetter();

        ArrayList<ArrayList<Object>> params = MainDataGetter._params;
        String basePath = MainDataGetter._basePath + "/CROSS";
        String readPath = MainDataGetter._basePath;

        FileManager.createDir(basePath, "/");

        for (String s : MainDataGetter._keys)
        {
            FileManager.createDir(basePath + "/" + s, "/");

            ArrayList<String> iKeys = new ArrayList<String>(MainDataGetter._keys.size());
            HashMap<String, ArrayList<Object>> iSeparateParams = new HashMap<String, ArrayList<Object>>();

            for (String iS : MainDataGetter._keys)
            {
                if (!s.equals(iS))
                {
                    iKeys.add(iS);
                    iSeparateParams.put(iS, MainDataGetter._separateParams.get(iS));
                }
            }

            ArrayList<ArrayList<Object>> iParams = ParamsGenerator.generateParams(iKeys, iSeparateParams);

            for (ArrayList<Object> p : iParams)
            {
                HashMap<String, Object> constantValues = new HashMap<String, Object>();
                String addPath = "";

                for (int i = 0; i < p.size(); i++)
                {
                    addPath += iKeys.get(i) + "_" + p.get(i).toString();
                    if (i < p.size() - 1) addPath += "_";
                    constantValues.put(iKeys.get(i), p.get(i));
                }
                FileManager.createDir(basePath + "/" + s + "/" + addPath, "/");
                System.out.println(addPath);

                // CREATE ALL NECESSARY FILES PATHS

                Object cp[][] = new Object[MainDataGetter._separateParams.get(s).size()][MainDataGetter._keys.size()];

                for (int i = 0; i < MainDataGetter._separateParams.get(s).size(); i++)
                {
                    for (int j = 0; j < MainDataGetter._keys.size(); j++)
                    {
                        if (MainDataGetter._keys.get(j).equals(s))
                            cp[i][j] = MainDataGetter._separateParams.get(s).get(i);
                        else
                            cp[i][j] = constantValues.get(MainDataGetter._keys.get(j));
                    }
                }

                for (int i = 0; i < p.size(); i++)
                {
                    if (iKeys.get(i).equals("START"))
                        start = Integer.parseInt(p.get(i).toString());
                    if (iKeys.get(i).equals("INTERVAL"))
                        interval = Integer.parseInt(p.get(i).toString());
                }

                createDataFile(data, basePath + "/" + s + "/" + addPath,
                        readPath, s, addPath, cp);

                createChartFile(data, basePath + "/" + s + "/" + addPath,
                        readPath, s, addPath, cp, false);

                createChartFile(data, basePath + "/" + s + "/" + addPath,
                        readPath, s, addPath, cp, true);
            }

        }
    }

    private static void createChartFile(MainDataGetter mdg, String basePath, String readPath, String changeKey, String constantKey, Object files[][], boolean monotonic)
    {
        HashMap<IGenetic, String[][][]> data = captureData(mdg, readPath, changeKey, files);

        for (int f = 0; f < mdg._featureExtractor.size() + mdg._populationComprehensive.size(); f++)
        {
            SXSSFWorkbook wb = new SXSSFWorkbook();

            ArrayList<Sheet> sheets = new ArrayList<Sheet>();
            ArrayList<String> labels = new ArrayList<String>();

            for (int i = 0; i < MainDataGetter._separateParams.get(changeKey).size(); i++)
            {
                String name = MainDataGetter._separateParams.get(changeKey).get(i).toString();
                labels.add(name);
                int value = 0;
                if (changeKey.equals("START"))
                    value = Integer.parseInt(name);
                else if (changeKey.equals("INTERVAL"))
                    value = Integer.parseInt(name);

                Sheet sh = wb.createSheet(Integer.toString(value));
                createParamSheet(2, 2, mdg, wb, sh, 0, changeKey, i, f, data, monotonic);
                sheets.add(sh);
            }

            for (int i = 0; i < mdg._dummyGenetic.size(); i++)
            {
                Sheet sh = wb.createSheet(mdg._dummyGenetic.get(i).getName());
                createAlgorithmSheet(2, 2, mdg, wb, sh, sheets, labels, i, 0, changeKey, i, f, data, monotonic);
            }

            FileOutputStream out;
            try
            {
                String runName;
                if (f < mdg._featureExtractor.size()) runName = mdg._featureExtractor.get(f).getKey();
                else runName = mdg._populationComprehensive.get(f - mdg._featureExtractor.size()).getKey();

                if (!monotonic) out = new FileOutputStream(basePath + "/" + "Przebiegi_" + runName + "_" + constantKey + ".xlsx");
                else out = new FileOutputStream(basePath + "/" + "Przebiegi_" + runName + "_" + constantKey + "_MONOTONIC.xlsx");

                wb.write(out);
                out.close();
                wb.dispose();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }


    }

    private static void createAlgorithmSheet(int by, int bx, MainDataGetter mdg, Workbook wb, Sheet sh, ArrayList<Sheet> sheets,
                                             ArrayList<String> labels, int genetic, int mode,
                                             String changeKey, int param, int feature, HashMap<IGenetic, String[][][]> data, boolean monotonic)
    {
        int chartWidth = 8;
        int chartHeight = 18;

        int byp = chartHeight + by + 2;

        int statisticWidth = mdg._statisticExtractor.size();
        if (feature >= mdg._featureExtractor.size()) statisticWidth = 1;

        int shiftX = chartWidth + 2;
        for (int i = 0; i < statisticWidth; i++)
        {
            Drawing drawing = sh.createDrawingPatriarch();
            ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, bx + i * shiftX, by, bx + i * shiftX + chartWidth, by + chartHeight);

            Chart chart = drawing.createChart(anchor);
            ChartLegend legend = chart.getOrCreateLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);

            LineChartData dataChart = chart.getChartDataFactory().createLineChartData();

            ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
            ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            for (int j = 0; j < sheets.size(); j++)
            {
                ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheets.get(j), new CellRangeAddress(byp + 2, byp + 2 + mdg._generations, bx, bx));
                int c = bx + 1 + (genetic * statisticWidth) + i;
                ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheets.get(j), new CellRangeAddress(byp + 2, byp + 2 + mdg._generations, c, c));
                String title = mdg._dummyGenetic.get(j).getName();
                LineChartSeries chartSeries = dataChart.addSeries(xs, ys);
                chartSeries.setTitle(labels.get(j));
            }

            chart.plot(dataChart, bottomAxis, leftAxis);

        }

    }

    private static void createParamSheet(int by, int bx, MainDataGetter mdg, Workbook wb, Sheet sh, int mode,
                                         String changeKey, int param, int feature,
                                         HashMap<IGenetic, String[][][]> data, boolean monotonic)
    {
        int chartWidth = 8;
        int chartHeight = 18;
        int byp = chartHeight + by + 2;

        int statisticWidth = mdg._statisticExtractor.size();
        if (feature >= mdg._featureExtractor.size()) statisticWidth = 1;

        int ise = 0;
        for (int i = 0; i < mdg._internalStatisticExtractor.size(); i++)
            if (mdg._internalStatisticExtractor.get(i).getKey().equals("Mean"))
            {
                ise = i;
                break;
            }

        int readRow = 4;
        int column[] = new int[mdg._dummyGenetic.size() * statisticWidth];
        for (int g = 0; g < mdg._dummyGenetic.size(); g++)
        {
            for (int s = 0; s < statisticWidth; s++)
            {
                if (feature < mdg._featureExtractor.size())
                {
                    column[g * statisticWidth + s] = 1 + getColumnNumber(mdg._featureExtractor.get(feature),
                            mdg._statisticExtractor.get(s),
                            mdg._internalStatisticExtractor.get(ise),
                            mdg._featureExtractor,
                            mdg._statisticExtractor,
                            mdg._internalStatisticExtractor);
                }
                else
                {
                    int over = (feature - mdg._featureExtractor.size()) + 1;
                    int columns = data.get(mdg._dummyGenetic.get(g))[param][4].length;
                    column[g * statisticWidth + s] = getColumnNumber(mdg, over, ise,
                            mdg._internalStatisticExtractor.size(), columns);
                }
            }
        }

        double prevData[] = new double[ mdg._dummyGenetic.size() * statisticWidth];

        for (int i = 0; i < mdg._generations + 2; i++)
        {
            Row row = sh.getRow(byp + i);
            if (row == null) row = sh.createRow(byp + i);

            Cell cell = row.getCell(bx);
            if (cell == null) cell = row.createCell(bx);
            if (i == 0) cell.setCellValue("GEN");
            else if (i == 1) cell.setCellValue("-");
            else cell.setCellValue(i - 1);

            double newData[] = new double[ mdg._dummyGenetic.size() * statisticWidth];

            for (int j = 0; j < mdg._dummyGenetic.size(); j++)
            {
                for (int k = 0; k < statisticWidth; k++)
                {
                    GenerationTrialMeasure.Monotonic m;
                    if (feature < mdg._featureExtractor.size())
                        m = getMonotonic(mdg, feature, k);
                    else
                        m = getMonotonic(mdg, feature, mdg._statisticExtractor.size() + k);


                    cell = row.getCell(bx + 1 + (j * statisticWidth) + k);
                    if (cell == null) cell = row.createCell(bx + 1 + (j * statisticWidth) + k);
                    if ((i == 0) && (k == 0)) cell.setCellValue(mdg._dummyGenetic.get(j).getName());
                    else if (i == 1)
                    {
                        if (feature < mdg._featureExtractor.size()) cell.setCellValue(mdg._statisticExtractor.get(k).getKey());
                        else cell.setCellValue(mdg._populationComprehensive.get(feature - mdg._featureExtractor.size()).getKey());
                    }
                    else if (i >= 2)
                    {
                        if ((monotonic) && (i > 2) && (m != null) && ((mdg._internalStatisticExtractor.get(ise).getKey().equals(m._referenceKey))
                        || ( m._referenceKey.equals(mdg._populationComprehensive.get(feature - mdg._featureExtractor.size()).getKey())   ))
                        )
                        {
                            String mat[][] = data.get(mdg._dummyGenetic.get(j))[param];
                            double n = Double.parseDouble(mat[readRow + i - 2][column[(j * statisticWidth) + k]].replace(',', '.'));
                            double p = prevData[(j * statisticWidth) + k];

                            if ((n > p) && (m._lessPreferable))
                                n = p;
                            else
                            if ((n < p) && (!m._lessPreferable))
                                n = p;

                            cell.setCellValue(n);
                            newData[(j * statisticWidth) + k] = n;
                        }
                        else
                        {
                            String mat[][] = data.get(mdg._dummyGenetic.get(j))[param];
                            double n = Double.parseDouble(mat[readRow + i - 2][column[(j * statisticWidth) + k]].replace(',', '.'));
                            cell.setCellValue(n);
                            newData[(j * statisticWidth) + k] = n;
                        }
                    }
                }
            }

            prevData = newData;
        }

        int shiftX = chartWidth + 2;
        for (int i = 0; i < statisticWidth; i++)
        {
            Drawing drawing = sh.createDrawingPatriarch();
            ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, bx + i * shiftX, by, bx + i * shiftX + chartWidth, by + chartHeight);

            Chart chart = drawing.createChart(anchor);
            ChartLegend legend = chart.getOrCreateLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);

            LineChartData dataChart = chart.getChartDataFactory().createLineChartData();

            ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
            ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            for (int j = 0; j < mdg._dummyGenetic.size(); j++)
            {
                ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sh, new CellRangeAddress(byp + 2, byp + 2 + mdg._generations, bx, bx));
                int c = bx + 1 + (j * statisticWidth) + i;
                ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sh, new CellRangeAddress(byp + 2, byp + 2 + mdg._generations, c, c));
                String title = mdg._dummyGenetic.get(j).getName();
                LineChartSeries chartSeries = dataChart.addSeries(xs, ys);
                chartSeries.setTitle(title);
            }
            chart.plot(dataChart, bottomAxis, leftAxis);

        }
    }


    private static GenerationTrialMeasure.Monotonic getMonotonic(MainDataGetter mdg, int feature, int statistic)
    {
        HashMap<String, GenerationTrialMeasure.Monotonic> m;
        if (feature < mdg._featureExtractor.size())
            m = mdg._monotonic.get(mdg._featureExtractor.get(feature).getKey());
        else
            m = mdg._monotonic.get(mdg._populationComprehensive.get(feature - mdg._featureExtractor.size()).getKey());

        if (m != null)
        {
            if (statistic < mdg._statisticExtractor.size())
                return m.get(mdg._statisticExtractor.get(statistic).getKey());
            else
                return m.get(mdg._populationComprehensive.get(feature - mdg._featureExtractor.size()).getKey());
        }
        return null;
    }

    private static void createDataFile(MainDataGetter mdg, String basePath, String readPath, String changeKey, String constantKey, Object files[][])
    {
        // CAPTURE DATA
        HashMap<IGenetic, String[][][]> data = captureData(mdg, readPath, changeKey, files);

        // CREATE NUMERICAL
        {
            SXSSFWorkbook wb = new SXSSFWorkbook();

            int f = data.get(mdg._dummyGenetic.get(0)).length;
            int se = mdg._statisticExtractor.size();
            int ise = mdg._statisticExtractor.size();
            int totalWidth = (se) * (1 + ((f + 1) * ise));
            int totalWidthTest = (1 + mdg._dummyGenetic.size()) * f;

            Sheet sh = wb.createSheet("Najlepsza generacja");
            createHeader(1, 1, totalWidth, 1, true, mdg, wb, sh,
                    MainDataGetter._problemName + " Najlepszy z " + mdg._generations + " generacji (to nie musi byc ostatnia gen!)", data,
                    IndexedColors.GREY_50_PERCENT.getIndex());
            fillNumericalSheet(2, 1, mdg, wb, sh, 0, changeKey, data);

            sh = wb.createSheet("Najlepsza generacja TEST");
            createHeader(1, 1, totalWidthTest, 1, true, mdg, wb, sh,
                    MainDataGetter._problemName + " Test Wilcoxona", data,
                    IndexedColors.GREY_50_PERCENT.getIndex());
            fillNumericalSheetTest(2, 1, mdg, wb, sh, 0, changeKey, data);

            sh = wb.createSheet("Usrednione generacje");
            createHeader(1, 1, totalWidth, 1, true, mdg, wb, sh,
                    MainDataGetter._problemName + " Usrednione wyniki z " + mdg._generations + " generacji", data,
                    IndexedColors.GREY_50_PERCENT.getIndex());
            fillNumericalSheet(2, 1, mdg, wb, sh, 1, changeKey, data);

            sh = wb.createSheet("Usrednione generacje TEST");
            createHeader(1, 1, totalWidthTest, 1, true, mdg, wb, sh,
                    MainDataGetter._problemName + " Test Wilcoxona", data,
                    IndexedColors.GREY_50_PERCENT.getIndex());
            fillNumericalSheetTest(2, 1, mdg, wb, sh, 1, changeKey, data);

            sh = wb.createSheet("Usrednione generacje X Czas");
            createHeader(1, 1, totalWidth, 1, true, mdg, wb, sh,
                    MainDataGetter._problemName + " Usrednione wyniki z " + mdg._generations + " generacji (Czas na osi X)",
                    data, IndexedColors.GREY_50_PERCENT.getIndex());
            fillNumericalSheet(2, 1, mdg, wb, sh, 2, changeKey, data);

            sh = wb.createSheet("Usrednione generacje X Czas TEST");
            createHeader(1, 1, totalWidthTest, 1, true, mdg, wb, sh,
                    MainDataGetter._problemName + " Test Wilcoxona",
                    data, IndexedColors.GREY_50_PERCENT.getIndex());
            fillNumericalSheetTest(2, 1, mdg, wb, sh, 2, changeKey, data);

            FileOutputStream out;
            try
            {
                out = new FileOutputStream(basePath + "/" + "Liczby_" + constantKey + ".xlsx");
                wb.write(out);
                out.close();
                wb.dispose();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void fillNumericalSheetTest(int by, int bx, MainDataGetter mdg, Workbook wb, Sheet sh, int mode,
                                               String changeKey, HashMap<IGenetic, String[][][]> data)
    {
        int statisticSize = 2 + mdg._dummyGenetic.size();
        int featureSize = (mdg._statisticExtractor.size()) * statisticSize;
        int files = data.get(mdg._dummyGenetic.get(0)).length;

        int totalWidth = (1 + mdg._dummyGenetic.size()) * files;

        // STANDARD FEATURE EXTRACTORS
        for (int i = 0; i < mdg._featureExtractor.size(); i++)
        {
            int x = bx;
            int y = by + i * (featureSize + 1);
            createHeader(y, x, totalWidth, 1, true, mdg, wb, sh, mdg._featureExtractor.get(i).getKey(), data,
                    IndexedColors.GREY_40_PERCENT.getIndex());

            for (int j = 0; j < mdg._statisticExtractor.size(); j++)
            {
                x = bx;
                y = by + i * (featureSize + 1) + 1 + j * statisticSize;

                createHeader(y, x, totalWidth, 1, true, mdg, wb, sh, mdg._statisticExtractor.get(j).getKey(), data,
                        IndexedColors.GREY_25_PERCENT.getIndex());

                for (int k = 0; k < MainDataGetter._separateParams.get(changeKey).size(); k++)
                {
                    x = bx + k * (mdg._dummyGenetic.size() + 1);
                    createStatisticTest(y + 1, x, i, j, mdg, wb, sh, mode, changeKey, k, data);
                }

            }
        }

        int baseY = by + (mdg._featureExtractor.size()) * (featureSize + 1);

        int i = -1;
        for (IPopulationComprehensive pc : mdg._populationComprehensive)
        {
            i++;
            int x = bx;
            int y = baseY + i * (statisticSize);
            createHeader(y, x, totalWidth, 1, true, mdg, wb, sh, pc.getKey(), data,
                    IndexedColors.GREY_40_PERCENT.getIndex());

            for (int k = 0; k < MainDataGetter._separateParams.get(changeKey).size(); k++)
            {
                x = bx + k * (mdg._dummyGenetic.size() + 1);
                createStatisticTest(y + 1, x, mdg._featureExtractor.size() + i, mdg._featureExtractor.size() + i,
                        mdg, wb, sh, mode, changeKey, k, data);
            }

        }

    }

    private static void fillNumericalSheet(int by, int bx, MainDataGetter mdg, Workbook wb, Sheet sh, int mode,
                                           String changeKey, HashMap<IGenetic, String[][][]> data)
    {
        int featureSize = mdg._dummyGenetic.size() + 4;
        int files = data.get(mdg._dummyGenetic.get(0)).length;
        int statisticSize = 1 + mdg._internalStatisticExtractor.size() * (files + 1);

        int se = mdg._statisticExtractor.size();
        int ise = mdg._statisticExtractor.size();
        int totalWidth = (se) * (1 + ((files + 1) * ise));

        // STANDARD FEATURE EXTRACTORS
        for (int i = 0; i < mdg._featureExtractor.size(); i++)
        {
            int x = bx;
            int y = by + i * (featureSize + 1);
            createHeader(y, x, totalWidth, 1, true, mdg, wb, sh, mdg._featureExtractor.get(i).getKey(), data,
                    IndexedColors.GREY_40_PERCENT.getIndex());

            for (int j = 0; j < mdg._statisticExtractor.size(); j++)
            {
                x = bx + j * statisticSize;
                createStatistic(y + 1, x, i, j, mdg, wb, sh, mode, changeKey, data);
            }
        }

        totalWidth = (1 + (files * ise));

        int y = by + (mdg._featureExtractor.size() - 1) * (featureSize + 1);
        int i = -1;
        for (IPopulationComprehensive pc : mdg._populationComprehensive)
        {
            i++;
            int x = bx;
            y += (featureSize + 1);
            createHeader(y, x, totalWidth, 1, true, mdg, wb, sh, pc.getKey(), data,
                    IndexedColors.GREY_40_PERCENT.getIndex());
            createStatistic(y + 1, x, mdg._featureExtractor.size() + i, mdg._featureExtractor.size() + i, mdg, wb, sh, mode, changeKey, data);

            if ((mode == 0) && (pc.getKey().equals("OrderingBestDistanceUtility")))
            {
                double th[] = {0.03d, 0.05d, 0.1d, 0.15d, 0.2d};
                for (double t : th)
                {
                    y += (featureSize + 1);
                    createHeader(y, x, totalWidth, 1, true, mdg, wb, sh, pc.getKey() + String.format("GEN and PC - %.3f", t), data,
                            IndexedColors.GREY_40_PERCENT.getIndex());

                    createStatisticGenAndPC(y + 1, x, mdg._featureExtractor.size() + i, mdg._featureExtractor.size() + i, mdg, wb, sh,
                            changeKey, data, t);
                }

            }
        }
    }

    private static void createStatisticGenAndPC(int by, int bx, int feature, int statistic, MainDataGetter mdg, Workbook wb, Sheet sh,
                                                String changeKey, HashMap<IGenetic, String[][][]> data, double threshold)
    {
        // CREATE LEFT INFO BAR
        int featureSize = mdg._dummyGenetic.size() + 3;
        for (int i = 0; i < featureSize; i++)
        {
            Row row = sh.getRow(by + i);
            if (row == null) row = sh.createRow(by + i);
            Cell cell = row.getCell(bx);
            if (cell == null) cell = row.createCell(bx);

            if (i == 1)
            {
                cell.setCellValue(changeKey);
            } else if (i > 2)
            {
                cell.setCellValue(mdg._dummyGenetic.get(i - 3).getName());
            }

            CellStyle cs = wb.createCellStyle();
            if ((i == 0) || (i == 3)) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
            if ((i == featureSize - 1) || (i == 2)) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
            cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            cs.setBorderRight(XSSFCellStyle.BORDER_THIN);

            cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
            if (i < 3) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            else cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            cell.setCellStyle(cs);
        }

        int files = data.get(mdg._dummyGenetic.get(0)).length;
        int ise = mdg._statisticExtractor.size();
        int totalWidth = files * ise;

        createHeader(by, bx + 1, totalWidth, 1, true, mdg, wb, sh, "",
                data, IndexedColors.WHITE.getIndex());

        for (int i = 0; i < files; i++)
        {
            String name = MainDataGetter._separateParams.get(changeKey).get(i).toString();

            if (changeKey.equals("START"))
                start = Integer.parseInt(name);
            else if (changeKey.equals("INTERVAL"))
                interval = Integer.parseInt(name);

            fillDataFromFileGenAndPC(by + 1, bx + ise * i, feature, statistic, i, mdg, wb, sh, changeKey, data, threshold);
        }

    }

    private static void createStatistic(int by, int bx, int feature, int statistic, MainDataGetter mdg, Workbook wb, Sheet sh, int mode,
                                        String changeKey, HashMap<IGenetic, String[][][]> data)
    {
        // CREATE LEFT INFO BAR
        int featureSize = mdg._dummyGenetic.size() + 3;
        for (int i = 0; i < featureSize; i++)
        {
            Row row = sh.getRow(by + i);
            if (row == null) row = sh.createRow(by + i);
            Cell cell = row.getCell(bx);
            if (cell == null) cell = row.createCell(bx);

            if (i == 1)
            {
                cell.setCellValue(changeKey);
            } else if (i > 2)
            {
                cell.setCellValue(mdg._dummyGenetic.get(i - 3).getName());
            }

            CellStyle cs = wb.createCellStyle();
            if ((i == 0) || (i == 3)) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
            if ((i == featureSize - 1) || (i == 2)) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
            cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            cs.setBorderRight(XSSFCellStyle.BORDER_THIN);

            cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
            if (i < 3) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            else cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            cell.setCellStyle(cs);
        }

        int files = data.get(mdg._dummyGenetic.get(0)).length;
        int ise = mdg._statisticExtractor.size();
        int totalWidth = (files + 1) * ise;

        if (feature >= mdg._featureExtractor.size())
            createHeader(by, bx + 1, totalWidth, 1, true, mdg, wb, sh, "",
                    data, IndexedColors.WHITE.getIndex());
        else
            createHeader(by, bx + 1, totalWidth, 1, true, mdg, wb, sh, mdg._statisticExtractor.get(statistic).getKey(),
                    data, IndexedColors.GREY_25_PERCENT.getIndex());

        for (int i = 0; i < files; i++)
            fillDataFromFile(by + 1, bx + ise * i, feature, statistic, i, mdg, wb, sh, mode, changeKey, data);

        // COMPUTE AVERAGED
        {
            String name = "AVERAGED";
            createHeader(by + 1, bx + files * ise + 1, ise, 0, true, mdg, wb, sh, name, data, IndexedColors.WHITE.getIndex());

            for (int i = 0; i < ise; i++)
            {
                short color = IndexedColors.GREY_25_PERCENT.getIndex();
                if (i % 2 == 0) color = IndexedColors.WHITE.getIndex();
                createHeader(by + 2, bx + files * ise + i + 1, 1, 0, true, mdg, wb, sh, mdg._internalStatisticExtractor.get(i).getKey(),
                        data, color);

                for (int g = 0; g < mdg._dummyGenetic.size(); g++)
                {
                    Row row = sh.getRow(by + 3 + g);
                    if (row == null) row = sh.createRow(by + 3 + g);
                    Cell cell = row.getCell(bx + files * ise + i + 1);
                    if (cell == null) cell = row.createCell(bx + files * ise + i + 1);

                    String strFormula= "AVERAGE(";
                    for (int f = 0; f < files; f++)
                    {
                        int X = bx + ise * f + 1 + i;
                        int Y = by + 3 + g;
                        CellReference cr = new CellReference(Y, X);
                        strFormula += cr.formatAsString();
                        if (f < files - 1) strFormula += ",";
                    }
                    strFormula += ")";

                    //cell.setCellValue(strFormula);

                    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                    cell.setCellFormula(strFormula);

                    CellStyle cs = wb.createCellStyle();
                    if (i ==  (ise - 1)) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);
                    if (g == (mdg._dummyGenetic.size() - 1))
                        cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);

                    cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                    cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                    cs.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("0.0000"));
                    cell.setCellStyle(cs);
                }
            }
        }
    }

    private static void createStatisticTest(int by, int bx, int feature, int statistic, MainDataGetter mdg, Workbook wb, Sheet sh, int mode,
                                            String changeKey, int changeNumber, HashMap<IGenetic, String[][][]> data)
    {
        Object o = MainDataGetter._separateParams.get(changeKey).get(changeNumber);
        createHeader(by, bx, 1, 0, false, mdg, wb, sh, o.toString(), data, IndexedColors.WHITE.getIndex());

        for (int i = 0; i < mdg._dummyGenetic.size(); i++)
        {
            String l = mdg._dummyGenetic.get(i).getName();
            {
                Row row = sh.getRow(by + 1 + i);
                if (row == null) row = sh.createRow(by + 1 + i);
                Cell cell = row.getCell(bx);
                if (cell == null) cell = row.createCell(bx);

                cell.setCellValue(l);

                CellStyle cs = wb.createCellStyle();
                if (i == 0) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                if (i == mdg._dummyGenetic.size() - 1) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                cs.setBorderRight(XSSFCellStyle.BORDER_THIN);

                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                cell.setCellStyle(cs);
            }
            {
                Row row = sh.getRow(by);
                if (row == null) row = sh.createRow(by);
                Cell cell = row.getCell(bx + 1 + i);
                if (cell == null) cell = row.createCell(bx + 1 + i);

                cell.setCellValue(l);

                CellStyle cs = wb.createCellStyle();
                if (i == 0) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (i == mdg._dummyGenetic.size() - 1) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);
                cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);

                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                cell.setCellStyle(cs);
            }
        }

        int x = bx + 1;
        int y = by + 1;

        double v[][] = new double[mdg._dummyGenetic.size()][mdg._dummyGenetic.size()];

        for (int i = 0; i < mdg._dummyGenetic.size(); i++)
        {
            for (int j = i + 1; j < mdg._dummyGenetic.size(); j++)
            {
                IGenetic rG = mdg._dummyGenetic.get(i);
                IGenetic cG = mdg._dummyGenetic.get(j);

                double rV[] = getTestData(rG, feature, statistic, mdg, mode, changeKey, changeNumber, data);
                double cV[] = getTestData(cG, feature, statistic, mdg, mode, changeKey, changeNumber, data);

                WilcoxonSignedRankTest w = new WilcoxonSignedRankTest();
                double p = w.wilcoxonSignedRankTest(rV, cV, false);

                v[i][j] = p;
                v[j][i] = p;
            }
        }

        for (int i = 0; i < mdg._dummyGenetic.size(); i++)
        {
            Row row = sh.getRow(y + i);
            if (row == null) row = sh.createRow(y + i);

            for (int j = 0; j < mdg._dummyGenetic.size(); j++)
            {
                Cell cell = row.getCell(x + j);
                if (cell == null) cell = row.createCell(x + j);

                CellStyle cs = wb.createCellStyle();
                if (i == 0) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                if (i == mdg._dummyGenetic.size() - 1) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                if (j == 0) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (j == mdg._dummyGenetic.size() - 1) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);

                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);

                if (i == j)
                {
                    cs.setAlignment(XSSFCellStyle.ALIGN_CENTER);
                    cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                    cell.setCellValue("-");
                } else
                {
                    cs.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("0.0000"));
                    cs.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
                    cell.setCellValue(v[i][j]);

                    cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                    if (v[i][j] <= 0.05)
                        cs.setFillForegroundColor(IndexedColors.LIME.getIndex());
                }

                cell.setCellStyle(cs);
            }
        }
    }

    private static double[] getTestData(IGenetic genetic, int feature, int statistic, MainDataGetter mdg, int mode,
                                        String changeKey, int changeNumber, HashMap<IGenetic, String[][][]> data)
    {
        String file[][] = data.get(genetic)[changeNumber];
        double result[] = new double[mdg._trials];

        int mainHeaderRow = 4;
        int mainRow = mdg._generations;
        int testHeaderRow = 3;
        int testRow = mdg._trials;
        int complete = mainHeaderRow + mainRow + testHeaderRow + testRow;

        int rowBegin = mainHeaderRow + mainRow + testHeaderRow;
        if (mode > 0) rowBegin += mode * complete;

        int column;

        if (feature >= mdg._featureExtractor.size())
        {
            int over = (feature - mdg._featureExtractor.size()) + 1;
            int columns = file[rowBegin].length;
            column = getColumnNumberTestOver(mdg, over, columns);

        } else column = getColumnNumberTest(mdg._featureExtractor.get(feature), mdg._statisticExtractor.get(statistic),
                mdg._featureExtractor, mdg._statisticExtractor);

        for (int i = 0; i < testRow; i++)
            result[i] = Double.parseDouble(file[i + rowBegin][column].replace(',', '.'));

        return result;
    }

    private static void fillDataFromFileGenAndPC(int by, int bx, int feature, int statistic, int file, MainDataGetter mdg,
                                                 Workbook wb, Sheet sh, String changeKey, HashMap<IGenetic, String[][][]> data, double threshold)
    {
        String name = MainDataGetter._separateParams.get(changeKey).get(file).toString();
        createHeader(by, bx + 1, 2, 0, false, mdg, wb, sh, name, data, IndexedColors.WHITE.getIndex());

        String names[] = {"Gen", "PC"};

        for (int i = 0; i < names.length; i++)
        {
            short color = IndexedColors.GREY_25_PERCENT.getIndex();
            if (i % 2 == 0) color = IndexedColors.WHITE.getIndex();
            createHeader(by + 1, bx + 1 + i, 1, 0, true, mdg, wb, sh, names[i],
                    data, color);

            Double geneticResults[] = new Double[mdg._dummyGenetic.size()];


            for (int g = 0; g < mdg._dummyGenetic.size(); g++)
            {
                Integer value = getValueGenAndPC(mdg._dummyGenetic.get(g), mdg, feature, statistic, file, data, threshold);
                if (value != null) geneticResults[g] = (double) value;

                if ((value != null) && (i == 1))
                    geneticResults[g] = getEstimatedNumberOfElicitations(value, start, interval);

            }

            int position[] = new int[geneticResults.length];
            for (int g = 0; g < mdg._dummyGenetic.size(); g++)
            {
                if (geneticResults[g] == null)
                {
                    position[g] = mdg._dummyGenetic.size();
                    continue;
                }
                for (int j = 0; j < mdg._dummyGenetic.size(); j++)
                {
                    if (geneticResults[j] == null) continue;
                    if (g == j) continue;
                    if (geneticResults[j] < geneticResults[g]) position[g]++;
                }
            }

            for (int g = 0; g < mdg._dummyGenetic.size(); g++)
            {
                Row row = sh.getRow(by + 2 + g);
                if (row == null) row = sh.createRow(by + 2 + g);
                Cell cell = row.getCell(bx + 1 + i);
                if (cell == null) cell = row.createCell(bx + 1 + i);

                if (geneticResults[g] != null)
                    cell.setCellValue(geneticResults[g]);
                else cell.setCellValue("-");

                CellStyle cs = wb.createCellStyle();
                if (g == 0) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                if (g == mdg._dummyGenetic.size() - 1) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                if (i == 0) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (i == 2 - 1) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);

                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                if (position != null)
                {
                    if (position[g] == 0) cs.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
                    else if (position[g] == 1) cs.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                    else if (position[g] == 2) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    else if (position[g] == 3) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    else cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                } else
                    cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());

                if (i == 0) cs.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("0"));
                else cs.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("0.00"));

                cs.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
                cell.setCellStyle(cs);
            }
        }
    }

    private static void fillDataFromFile(int by, int bx, int feature, int statistic, int file, MainDataGetter mdg,
                                         Workbook wb, Sheet sh, int mode, String changeKey, HashMap<IGenetic, String[][][]> data)
    {
        int ise = mdg._internalStatisticExtractor.size();
        String name = MainDataGetter._separateParams.get(changeKey).get(file).toString();
        createHeader(by, bx + 1, ise, 0, false, mdg, wb, sh, name, data, IndexedColors.WHITE.getIndex());

        for (int i = 0; i < ise; i++)
        {
            short color = IndexedColors.GREY_25_PERCENT.getIndex();
            if (i % 2 == 0) color = IndexedColors.WHITE.getIndex();
            createHeader(by + 1, bx + 1 + i, 1, 0, true, mdg, wb, sh, mdg._internalStatisticExtractor.get(i).getKey(),
                    data, color);

            double geneticResults[] = new double[mdg._dummyGenetic.size()];

            for (int g = 0; g < mdg._dummyGenetic.size(); g++)
            {
                double value = getValue(mdg._dummyGenetic.get(g), mdg, mode, feature, statistic, i, file, data);
                geneticResults[g] = value;
            }

            int position[] = null;
            GenerationTrialMeasure.Monotonic m = getMonotonic(mdg, feature, statistic);
            if ((m != null) && (mdg._internalStatisticExtractor.get(i).getKey().equals(m._referenceKey)))
            {
                position = new int[geneticResults.length];
                for (int g = 0; g < mdg._dummyGenetic.size(); g++)
                {
                    for (int j = 0; j < mdg._dummyGenetic.size(); j++)
                    {
                        if (g == j) continue;
                        if ((m._lessPreferable) && (geneticResults[j] < geneticResults[g])) position[g]++;
                        else if ((!m._lessPreferable) && (geneticResults[j] > geneticResults[g])) position[g]++;
                    }
                }
            }

            for (int g = 0; g < mdg._dummyGenetic.size(); g++)
            {
                Row row = sh.getRow(by + 2 + g);
                if (row == null) row = sh.createRow(by + 2 + g);
                Cell cell = row.getCell(bx + 1 + i);
                if (cell == null) cell = row.createCell(bx + 1 + i);
                cell.setCellValue(geneticResults[g]);

                CellStyle cs = wb.createCellStyle();
                if (g == 0) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                if (g == mdg._dummyGenetic.size() - 1) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                if (i == 0) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (i == ise - 1) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);

                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());

                if (position != null)
                {
                    if (position[g] == 0) cs.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
                    else if (position[g] == 1) cs.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                    else if (position[g] == 2) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    else if (position[g] == 3) cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    else cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                } else
                    cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());

                cs.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("0.00000"));

                cs.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
                cell.setCellStyle(cs);
            }
        }
    }


    private static double getValue(IGenetic g, MainDataGetter mdg, int mode, int feature, int statistic,
                                   int internalStatistic, int file, HashMap<IGenetic, String[][][]> data)
    {
        //System.out.println(g.getName());
        String mat[][] = data.get(g)[file];

        int mainHeaderRow = 4;
        int mainRow = mdg._generations;
        int testHeaderRow = 3;
        int testRow = mdg._trials;
        int complete = mainHeaderRow + mainRow + testHeaderRow + testRow;

        int column;

        // CHECK IF MONOTONIC
        GenerationTrialMeasure.Monotonic monotonic = getMonotonic(mdg, feature, statistic);

        // FIND REFERENCE COLUMN
        int monotonicRow = 0;
        if ((monotonic != null) && (mode != 2))
        {
            int referenceIse = 0;
            int referenceColumn;
            for (int i = 0; i < mdg._internalStatisticExtractor.size(); i++)
                if (mdg._internalStatisticExtractor.get(i).getKey().equals(monotonic._referenceKey))
                {
                    referenceIse = i;
                    break;
                }

            if (feature >= mdg._featureExtractor.size())
            {
                int over = (feature - mdg._featureExtractor.size()) + 1;
                int columns = mat[mainHeaderRow].length;
                referenceColumn = getColumnNumber(mdg, over, referenceIse,
                        mdg._internalStatisticExtractor.size(), columns);

            } else referenceColumn = 1 + getColumnNumber(mdg._featureExtractor.get(feature),
                    mdg._statisticExtractor.get(statistic),
                    mdg._internalStatisticExtractor.get(referenceIse),
                    mdg._featureExtractor,
                    mdg._statisticExtractor,
                    mdg._internalStatisticExtractor);

            int row = mainHeaderRow + mainRow - 1;
            if (mode > 0) row += mode * complete;
            monotonicRow = row;

            double v = Double.parseDouble(mat[row][referenceColumn].replace(',', '.'));
            for (int i = 0; i < mdg._generations; i++)
            {
                double vp = Double.parseDouble(mat[row - i][referenceColumn].replace(',', '.'));
                if ((monotonic._lessPreferable) && (vp < v))
                {
                    v = vp;
                    monotonicRow = row - i;
                } else if ((!monotonic._lessPreferable) && (vp > v))
                {
                    v = vp;
                    monotonicRow = row - i;
                }
            }
        }

        if (feature >= mdg._featureExtractor.size())
        {
            int over = (feature - mdg._featureExtractor.size()) + 1;
            int columns = mat[mainHeaderRow].length;
            column = getColumnNumber(mdg, over, internalStatistic,
                    mdg._internalStatisticExtractor.size(), columns);

        } else column = 1 + getColumnNumber(mdg._featureExtractor.get(feature),
                mdg._statisticExtractor.get(statistic),
                mdg._internalStatisticExtractor.get(internalStatistic),
                mdg._featureExtractor,
                mdg._statisticExtractor,
                mdg._internalStatisticExtractor);

        int row = mainHeaderRow + mainRow - 1;
        if (mode > 0) row += mode * complete;
        if ((monotonic != null) && (mode != 2))
        {
            row = monotonicRow;
        }
        return Double.parseDouble(mat[row][column].replace(',', '.'));
    }


    private static Integer getValueGenAndPC(IGenetic g, MainDataGetter mdg, int feature, int statistic,
                                            int file, HashMap<IGenetic, String[][][]> data, double threshold)
    {
        String mat[][] = data.get(g)[file];

        int mainHeaderRow = 4;
        int mainRow = mdg._generations;
        int testHeaderRow = 3;
        int testRow = mdg._trials;
        int complete = mainHeaderRow + mainRow + testHeaderRow + testRow;

        int column;

        // CHECK IF MONOTONIC
        GenerationTrialMeasure.Monotonic monotonic = getMonotonic(mdg, feature, statistic);

        // FIND REFERENCE COLUMN
        int monotonicRow = 0;
        if (monotonic != null)
        {
            int referenceIse = 0;
            int referenceColumn;
            for (int i = 0; i < mdg._internalStatisticExtractor.size(); i++)
                if (mdg._internalStatisticExtractor.get(i).getKey().equals(monotonic._referenceKey))
                {
                    referenceIse = i;
                    break;
                }

            if (feature >= mdg._featureExtractor.size())
            {
                int over = (feature - mdg._featureExtractor.size()) + 1;
                int columns = mat[mainHeaderRow].length;
                referenceColumn = getColumnNumber(mdg, over, referenceIse,
                        mdg._internalStatisticExtractor.size(), columns);

            } else referenceColumn = 1 + getColumnNumber(mdg._featureExtractor.get(feature),
                    mdg._statisticExtractor.get(statistic),
                    mdg._internalStatisticExtractor.get(referenceIse),
                    mdg._featureExtractor,
                    mdg._statisticExtractor,
                    mdg._internalStatisticExtractor);

            int row = mainHeaderRow;
            monotonicRow = row;

            boolean found = false;

            for (int i = 1; i < mdg._generations - 1; i++)
            {
                double vA = Double.parseDouble(mat[row + i - 1][referenceColumn].replace(',', '.'));
                double vB = Double.parseDouble(mat[row + i][referenceColumn].replace(',', '.'));

                if ((monotonic._lessPreferable) && (vA >= threshold) && (vB <= threshold))
                {
                    monotonicRow = row + i;
                    found = true;
                    break;
                } else if ((!monotonic._lessPreferable) && (vA <= threshold) && (vB >= threshold))
                {
                    monotonicRow = row + i;
                    found = true;
                    break;
                }
            }

            if (found == false) monotonicRow = -1;
        }

        int row = mainHeaderRow + mainRow - 1;
        if (monotonic != null)
        {
            row = monotonicRow;
        }
        if (monotonicRow == -1) return null;
        return row;
    }

    private static int getColumnNumberTestOver(MainDataGetter mdg, int over, int columns)
    {
        return columns - (mdg._populationComprehensive.size() - over + 1);
    }

    private static int getColumnNumberTest(IFeatureExtractor cFe, IStatisticExtractor cSe, ArrayList<IFeatureExtractor> fe,
                                           ArrayList<IStatisticExtractor> se)
    {
        int result = 1;

        for (IFeatureExtractor featureExtractor : fe)
        {
            if (!featureExtractor.getKey().equals(cFe.getKey()))
            {
                result += se.size() * (se.size());
                continue;
            }

            for (IStatisticExtractor statisticExtractor : se)
            {
                if (!statisticExtractor.getKey().equals(cSe.getKey()))
                    result++;
                else return result;
            }
        }
        return 0;
    }

    private static double getEstimatedNumberOfElicitations(int generation, int start, int interval)
    {
        if (generation < start) return 0.0d;
        double prop = 1.0d / interval;
        return 1 + ((generation - 1) - start) * prop;
    }

    private static int getColumnNumber(MainDataGetter mdg, int over, int ise, int iseSize, int columns)
    {
        int begin = columns - (mdg._populationComprehensive.size() - over + 1) * iseSize;
        return begin + ise;
    }

    private static int getColumnNumber(IFeatureExtractor cFe, IStatisticExtractor cSe, IStatisticExtractor cIse, ArrayList<IFeatureExtractor> fe,
                                       ArrayList<IStatisticExtractor> se, ArrayList<IStatisticExtractor> ise)
    {
        int result = 0;

        for (IFeatureExtractor featureExtractor : fe)
        {
            if (!featureExtractor.getKey().equals(cFe.getKey()))
            {
                result += se.size() * (ise.size());
                continue;
            }

            for (IStatisticExtractor statisticExtractor : se)
            {
                if (!statisticExtractor.getKey().equals(cSe.getKey()))
                {
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

    private static void createHeader(int by, int bx, int width, int shift, boolean text, MainDataGetter mdg, Workbook wb, Sheet sh, String name,
                                     HashMap<IGenetic, String[][][]> data)
    {
        createHeader(by, bx, width, shift, text, mdg, wb, sh, name, data, IndexedColors.GREY_50_PERCENT.getIndex());
    }

    private static void createHeader(int by, int bx, int width, int shift, boolean text, MainDataGetter mdg, Workbook wb, Sheet sh, String name,
                                     HashMap<IGenetic, String[][][]> data, short color)
    {

        for (int i = 0; i < 1; i++)
        {
            boolean leftBorder = false;
            boolean rightBorder = false;
            boolean bottomBorder = false;
            boolean topBorder = false;

            Row row = sh.getRow(by + i);
            if (row == null) row = sh.createRow(by + i);

            for (int j = 0; j < width; j++)
            {
                Cell cell = row.getCell(bx + j);
                if (cell == null) cell = row.createCell(bx + j);
                if ((i == 0) && (j == shift))
                {
                    if (text) cell.setCellValue(name);
                    else cell.setCellValue(Double.parseDouble(name));
                }
                CellStyle cs = wb.createCellStyle();
                if (i == 0) cs.setBorderTop(XSSFCellStyle.BORDER_THIN);
                if (i == 0) cs.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                if (j == 0) cs.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                if (j == width - 1) cs.setBorderRight(XSSFCellStyle.BORDER_THIN);
                cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cs.setFillForegroundColor(color);
                cell.setCellStyle(cs);
            }
        }
    }

    private static HashMap<IGenetic, String[][][]> captureData(MainDataGetter mdg, String readPath, String changeKey, Object files[][])
    {
        HashMap<IGenetic, String[][][]> data = new HashMap<IGenetic, String[][][]>();
        for (IGenetic g : mdg._dummyGenetic)
        {
            String[][][] gData = new String[files.length][][];
            int place = 0;
            for (Object file[] : files)
            {
                String dataPath = "";
                for (int i = 0; i < MainDataGetter._keys.size(); i++)
                {
                    dataPath += MainDataGetter._keys.get(i) + "_" + file[i];
                    if (i < MainDataGetter._keys.size() - 1) dataPath += "_";
                }

                if (mdg._constantResultPaths.get(g) != null)
                    gData[place++] = readDataFromFile(mdg._constantResultPaths.get(g) + "/" + g.getName() + ".txt");
                else
                    gData[place++] = readDataFromFile(readPath + "/" + dataPath + "/" + g.getName() + ".txt");
            }
            data.put(g, gData);
        }


        return data;
    }

    private static String[][] readDataFromFile(String path)
    {
        LinkedList<String> data = new LinkedList<String>();
        BufferedReader br = FileUtil.getBufferReader(path);

        while (true)
        {
            String l = FileUtil.readLine(br);
            if (l == null) break;
            data.add(l);
        }

        String result[][] = new String[data.size()][];

        int i = 0;
        for (String s : data)
        {
            result[i++] = s.split(";");
        }
        return result;
    }
}
