package year.y2014.greenlogistics.lp;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import standard.Common;
import standard.Range;
import year.y2014.greenlogistics.A.DataA;

/**
 * Created by MTomczyk on 09.11.2015.
 */
public class Constraints_A
{

    public static void addECMObjective(Problem problem, DataA d, int criterion,
                                       double firstConstraint, double secondConstraint, double eps[],
                                       Double minCost, Double maxCost,
                                       Double minCO2, Double maxCO2, Double minPM, Double maxPM,
                                       boolean slack, Range r[]) {
        // COST OBJECTIVE
        Linear linear = new Linear();
        Linear linearCO2 = new Linear();
        Linear linearPM = new Linear();
        // EMISJE

        // KOSZT Z MJP DO EP
        linear.add((d.shipCoCost), "x_0_3");
        linear.add((d.shipVaCost), "x_0_11");
        linear.add((d.shipThCost), "x_0_15");

        linearCO2.add((d.shipCoCO2), "x_0_3");
        linearCO2.add((d.shipVaCO2), "x_0_11");
        linearCO2.add((d.shipThCO2), "x_0_15");

        linearPM.add((d.shipCoPM), "x_0_3");
        linearPM.add((d.shipVaPM), "x_0_11");
        linearPM.add((d.shipThPM), "x_0_15");

        // KOSZT Z EP DO DC
        for (int i = 0; i < 16; i++) {
            if ((i != 3) && (i != 11) && (i != 15)) continue;

            for (int j = 0; j < 16; j++) {
                for (int m = 0; m < 6; m++) {
                    if (m == 5) {
                        // BLOCK TRAIN
                        double cost = d.coRailBlockCost[j];
                        double co2 = d.coRailCO2[j];
                        double pm = d.coRailPM[j];

                        if (i == 11) {
                            cost = d.vaRailBlockCost[j];
                            co2 = d.vaRailCO2[j];
                            pm = d.vaRailPM[j];
                        } else if (i == 15) {
                            cost = d.thRailBlockCost[j];
                            co2 = d.thRailCO2[j];
                            pm = d.thRailPM[j];
                        }
                        String var = String.format("x_EPDC_%d_%d_5", i, j);
                        //problem.setVarType(var, Integer.class);
                        if (cost > 1.0d) {
                            cost += d.storageCostOutsource[j];
                            linear.add(cost, var);
                            linearCO2.add(co2, var);
                            linearPM.add(pm, var);
                        } else {
                            linear.add(1000000, var);
                            linearCO2.add(1000000, var);
                            linearPM.add(1000000, var);
                        }

                    } else if (m == 4) {
                        // TRAIN
                        double cost = d.coRailCost[j];
                        double co2 = d.coRailCO2[j];
                        double pm = d.coRailPM[j];

                        if (i == 11) {
                            cost = d.vaRailCost[j];
                            co2 = d.vaRailCO2[j];
                            pm = d.vaRailPM[j];
                        } else if (i == 15) {
                            cost = d.thRailCost[j];
                            co2 = d.thRailCO2[j];
                            pm = d.thRailPM[j];
                        }

                        String var = String.format("x_EPDC_%d_%d_4", i, j);
                        //problem.setVarType(var, Integer.class);
                        if (cost > 1.0d) {
                            cost += d.storageCostOutsource[j];
                            linear.add(cost, var);
                            linearCO2.add(co2, var);
                            linearPM.add(pm, var);
                        } else {
                            linear.add(1000000, var);
                            linearCO2.add(1000000, var);
                            linearPM.add(1000000, var);
                        }

                    } else {
                        double distance = d.distances[j][i];
                        double cost = d.transport[m].oCostA * distance + d.transport[m].oCostB;
                        if (i == j) cost = d.transport[m].free;

                        double co2 = d.transport[m].oCo2 * distance;
                        double pm = d.transport[m].oPm * distance;

                        cost += d.storageCostOutsource[j];

                        String var = String.format("x_EPDC_%d_%d_%d", i, j, m);
                        //problem.setVarType(var, Integer.class);
                        linear.add(cost, var);
                        linearCO2.add(co2, var);
                        linearPM.add(pm, var);

                    }
                }
            }
        }

        // KOSZT Z DC DO RM
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 15; j++) {
                for (int m = 0; m < 4; m++) {
                    double distance = d.distances[j][i];
                    double cost = d.transport[m].oCostA * distance + d.transport[m].oCostB;
                    if (i == j) cost = d.transport[m].free;
                    double co2 = d.transport[m].oCo2 * distance;
                    double pm = d.transport[m].oPm * distance;

                    String var = String.format("x_DCRM_%d_%d_%d", i, j, m);
                    //problem.setVarType(var, Integer.class);
                    linear.add(cost, var);
                    linearCO2.add(co2, var);
                    linearPM.add(pm, var);
                }
            }
        }

        if (criterion == 0) {

            if (slack)
            {
                problem.setObjective(linear, OptType.MIN);
                linear.add(-1.0d/r[1].getRange(), "s1");
                linear.add(-1.0d/r[2].getRange(), "s2");

                linearCO2.add(1.0d, "s1");
                linearPM.add(1.0d, "s2");
                problem.add(linearCO2, "=", firstConstraint - eps[1]);
                problem.add(linearPM, "=", secondConstraint - eps[2]);
            }
            else
            {
                problem.setObjective(linear, OptType.MIN);
                problem.add(linearCO2, "<=", firstConstraint - eps[1]);
                problem.add(linearPM, "<=", secondConstraint - eps[2]);
            }

        } else if (criterion == 1) {

            if (slack)
            {
                problem.setObjective(linearCO2, OptType.MIN);
                linear.add(-1.0d/r[0].getRange(), "s0");
                linear.add(-1.0d/r[2].getRange(), "s2");

                linear.add(1.0d, "s0");
                linearPM.add(1.0d, "s2");
                problem.add(linear, "=", firstConstraint - eps[0]);
                problem.add(linearPM, "=", secondConstraint - eps[2]);
            }
            else
            {
                problem.setObjective(linearCO2, OptType.MIN);
                problem.add(linear, "<=", firstConstraint - eps[0]);
                problem.add(linearPM, "<=", secondConstraint - eps[2]);
            }

        } else if (criterion == 2) {

            if (slack)
            {
                problem.setObjective(linearPM, OptType.MIN);
                linear.add(-1.0d/r[0].getRange(), "s0");
                linear.add(-1.0d/r[1].getRange(), "s1");

                linear.add(1.0d, "s0");
                linearCO2.add(1.0d, "s1");
                problem.add(linear, "=", firstConstraint - eps[0]);
                problem.add(linearCO2, "=", secondConstraint - eps[1]);
            }
            else
            {
                problem.setObjective(linearPM, OptType.MIN);
                problem.add(linear, "<=", firstConstraint - eps[0]);
                problem.add(linearCO2, "<=", secondConstraint - eps[1]);
            }
        }

        if (minCost != null) problem.add(linear, ">=", minCost);
        if (maxCost != null) problem.add(linear, "<=", maxCost);

        if (minCO2 != null) problem.add(linearCO2, ">=", minCO2);
        if (maxCO2 != null) problem.add(linearCO2, "<=", maxCO2);

        if (minPM != null) problem.add(linearPM, ">=", minPM);
        if (maxPM != null) problem.add(linearPM, "<=", maxPM);


    }

    public static void addWSMObjective(Problem problem, DataA d, double w1, double w2, double w3,
                                       double n1, double n2, double n3,
                                       Double minCost, Double maxCost,
                                       Double minCO2, Double maxCO2, Double minPM, Double maxPM,
                                       Double eqCost, Double eqCO2, Double eqPM) {
        // COST OBJECTIVE
        Linear linear = new Linear();

        Linear costObj = new Linear();
        Linear co2Obj = new Linear();
        Linear pmObj = new Linear();


        // COST MJP-EP
        linear.add(w1 * (d.shipCoCost - n1) + w2 * (d.shipCoCO2 - n2) + w3 * (d.shipCoPM - n3), "x_0_3");
        linear.add(w1 * (d.shipVaCost - n1) + w2 * (d.shipVaCO2 - n2) + w3 * (d.shipVaPM - n3), "x_0_11");
        linear.add(w1 * (d.shipThCost - n1) + w2 * (d.shipThCO2 - n2) + w3 * (d.shipThPM - n3), "x_0_15");

        costObj.add(d.shipCoCost,"x_0_3");
        costObj.add(d.shipVaCost,"x_0_11");
        costObj.add(d.shipThCost,"x_0_15");

        co2Obj.add(d.shipCoCO2,"x_0_3");
        co2Obj.add(d.shipVaCO2,"x_0_11");
        co2Obj.add(d.shipThCO2,"x_0_15");

        pmObj.add(d.shipCoPM,"x_0_3");
        pmObj.add(d.shipVaPM,"x_0_11");
        pmObj.add(d.shipThPM,"x_0_15");

        // COST EP-DC
        for (int i = 0; i < 16; i++) {
            if ((i != 3) && (i != 11) && (i != 15)) continue;

            for (int j = 0; j < 16; j++) {
                for (int m = 0; m < 6; m++) {
                    if (m == 5) {
                        // BLOCK TRAIN
                        double cost = d.coRailBlockCost[j];
                        double co2 = d.coRailCO2[j];
                        double pm = d.coRailPM[j];

                        if (i == 11) {
                            cost = d.vaRailBlockCost[j];
                            co2 = d.vaRailCO2[j];
                            pm = d.vaRailPM[j];
                        } else if (i == 15) {
                            cost = d.thRailBlockCost[j];
                            co2 = d.thRailCO2[j];
                            pm = d.thRailPM[j];
                        }
                        String var = String.format("x_EPDC_%d_%d_5", i, j);
                        if (cost > 1.0d) {
                            cost += d.storageCostOutsource[j];
                            linear.add(w1 * (cost - n1) + w2 * (co2 - n2) + w3 * (pm - n3), var);

                            costObj.add(cost, var);
                            co2Obj.add(co2, var);
                            pmObj.add(pm, var);

                        } else {
                            linear.add(10000000, var);

                            costObj.add(10000000, var);
                            co2Obj.add(10000000, var);
                            pmObj.add(10000000, var);
                        }

                    } else if (m == 4) {
                        // TRAIN
                        double cost = d.coRailCost[j];
                        double co2 = d.coRailCO2[j];
                        double pm = d.coRailPM[j];

                        if (i == 11) {
                            cost = d.vaRailCost[j];
                            co2 = d.vaRailCO2[j];
                            pm = d.vaRailPM[j];
                        } else if (i == 15) {
                            cost = d.thRailCost[j];
                            co2 = d.thRailCO2[j];
                            pm = d.thRailPM[j];
                        }

                        // OUTSOURCE COST :D
                        String var = String.format("x_EPDC_%d_%d_4", i, j);
                        if (cost > 1.0d) {
                            cost += d.storageCostOutsource[j];
                            linear.add(w1 * (cost - n1) + w2 * (co2 - n2) + w3 * (pm - n3), var);

                            costObj.add(cost, var);
                            co2Obj.add(co2, var);
                            pmObj.add(pm, var);

                        } else{

                            linear.add(1000000, var);
                            costObj.add(1000000, var);
                            co2Obj.add(1000000, var);
                            pmObj.add(1000000, var);
                        }

                    } else {
                        double distance = d.distances[j][i];
                        double cost = d.transport[m].oCostA * distance + d.transport[m].oCostB;
                        if (i == j) cost = d.transport[m].free;

                        double co2 = d.transport[m].oCo2 * distance;
                        double pm = d.transport[m].oPm * distance;

                        cost += d.storageCostOutsource[j];

                        String var = String.format("x_EPDC_%d_%d_%d", i, j, m);
                        linear.add(w1 * (cost - n1) + w2 * (co2 - n2) + w3 * (pm - n3), var);

                        costObj.add(cost, var);
                        co2Obj.add(co2, var);
                        pmObj.add(pm, var);

                    }
                }
            }
        }

        // KOSZT Z DC DO RM
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 15; j++) {
                for (int m = 0; m < 4; m++) {
                    double distance = d.distances[j][i];
                    double cost = d.transport[m].oCostA * distance + d.transport[m].oCostB;
                    if (i == j) cost = d.transport[m].free;
                    double co2 = d.transport[m].oCo2 * distance;
                    double pm = d.transport[m].oPm * distance;

                    String var = String.format("x_DCRM_%d_%d_%d", i, j, m);
                    linear.add(w1 * (cost - n1) + w2 * (co2 - n2) + w3 * (pm - n3), var);

                    costObj.add(cost, var);
                    co2Obj.add(co2, var);
                    pmObj.add(pm, var);
                }
            }
        }

        problem.setObjective(linear, OptType.MIN);

        if (minCost != null) problem.add(costObj, ">=", minCost);
        if (maxCost != null) problem.add(costObj, "<=", maxCost);

        if (minCO2 != null) problem.add(co2Obj, ">=", minCO2);
        if (maxCO2 != null) problem.add(co2Obj, "<=", maxCO2);

        if (minPM != null) problem.add(pmObj, ">=", minPM);
        if (maxPM != null) problem.add(pmObj, "<=", maxPM);

        if (eqCost != null) problem.add(costObj, "=", eqCost);
        if (eqCO2 != null) problem.add(costObj, "=", eqCO2);
        if (eqPM != null) problem.add(costObj, "=", eqPM);


    }

    public static void addConstraints(Problem problem, DataA d ) {
        Linear linear;
        // FLOW CONSTR.

        for (int i = 0; i < 16; i++) {
            if ((i != 3) && (i != 11) && (i != 15)) continue;

            linear = new Linear();
            linear.add(1, String.format("x_0_%d", i));

            for (int j = 0; j < 16; j++) {
                for (int m = 0; m < 6; m++) {
                    String var = String.format("x_EPDC_%d_%d_%d", i, j, m);
                    linear.add(-1, var);
                }
            }
            problem.add(linear, "=", 0);
        }

        for (int i = 0; i < 16; i++) {
            linear = new Linear();
            for (int m = 0; m < 6; m++) {
                String var = String.format("x_EPDC_%d_%d_%d", 3, i, m);
                linear.add(1, var);
                var = String.format("x_EPDC_%d_%d_%d", 11, i, m);
                linear.add(1, var);
                var = String.format("x_EPDC_%d_%d_%d", 15, i, m);
                linear.add(1, var);
            }

            for (int j = 0; j < 16; j++) {
                for (int m = 0; m < 4; m++) {
                    String var = String.format("x_DCRM_%d_%d_%d", i, j, m);
                    linear.add(-1, var);
                }
            }
            problem.add(linear, "=", 0);
        }

        for (int i = 0; i < 16; i++) {
            linear = new Linear();
            for (int j = 0; j < 15; j++) {
                for (int m = 0; m < 4; m++) {
                    String var = String.format("x_DCRM_%d_%d_%d", j, i, m);
                    linear.add(1, var);
                }
            }
            double demand = d.demand[i];
            problem.add(linear, "=", demand);
        }

        for (int i = 0; i < 16; i++) {
            if ((i != 3) && (i != 11) && (i != 15)) continue;

            for (int j = 0; j < 16; j++) {
                linear = new Linear();
                linear.add(1, String.format("x_BT_%d_%d", i, j));
                problem.setVarType(String.format("x_BT_%d_%d", i, j), Integer.class);
                problem.add(linear, "<=", 1);

                linear = new Linear();
                linear.add(1, String.format("x_EPDC_%d_%d_5", i, j));
                linear.add(-15, String.format("x_BT_%d_%d", i, j));
                problem.add(linear, ">=", 0);

                linear = new Linear();
                linear.add(1, String.format("x_EPDC_%d_%d_5", i, j));
                linear.add(-1000000, String.format("x_BT_%d_%d", i, j));
                problem.add(linear, "<=", 0);
            }
        }
    }


    public static double[] eval(Result r, DataA d) {
        boolean log = false;

        double res[] = {0.0d, 0.0d, 0.0d};

        // SHIP COST
        res[0] += d.shipCoCost * r.get("x_0_3").doubleValue();
        res[0] += d.shipVaCost * r.get("x_0_11").doubleValue();
        res[0] += d.shipThCost * r.get("x_0_15").doubleValue();

        res[1] += d.shipCoCO2 * r.get("x_0_3").doubleValue();
        res[1] += d.shipVaCO2 * r.get("x_0_11").doubleValue();
        res[1] += d.shipThCO2 * r.get("x_0_15").doubleValue();

        res[2] += d.shipCoPM * r.get("x_0_3").doubleValue();
        res[2] += d.shipVaPM * r.get("x_0_11").doubleValue();
        res[2] += d.shipThPM * r.get("x_0_15").doubleValue();

        //noinspection ConstantConditions
        if (log) {
            System.out.println(
                    "SHIP CONSTANTA: " + d.shipCoCost * r.get("x_0_3").doubleValue() + " " + d.shipCoCO2 * r.get(
                            "x_0_3").doubleValue() + " " + d.shipCoPM * r.get("x_0_3").doubleValue());
            System.out.println(
                    "SHIP VARNA: " + d.shipCoCost * r.get("x_0_11").doubleValue() + " " + d.shipCoCO2 * r.get(
                            "x_0_11").doubleValue() + " " + d.shipCoPM * r.get("x_0_11").doubleValue());
            System.out.println(
                    "SHIP THESSALONIKI: " + d.shipCoCost * r.get("x_0_15").doubleValue() + " " + d.shipCoCO2 * r.get(
                            "x_0_15").doubleValue() + " " + d.shipCoPM * r.get("x_0_15").doubleValue());
            System.out.println(r.get("x_0_3").doubleValue() + " " + r.get("x_0_11").doubleValue() + " " + r.get(
                    "x_0_15").doubleValue());
        }

        //noinspection ConstantConditions
        if (log) System.out.println("EP - DC");

        // DO DC COST
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if ((j != 3) && (j != 11) && (j != 15)) continue;

                for (int m = 0; m < 6; m++) {
                    if (m == 5) {
                        double cost = d.coRailBlockCost[i];
                        double co2 = d.coRailCO2[i];
                        double pm = d.coRailPM[i];

                        if (j == 11) {
                            cost = d.vaRailBlockCost[i];
                            co2 = d.vaRailCO2[i];
                            pm = d.vaRailPM[i];
                        } else if (j == 15) {
                            cost = d.thRailBlockCost[i];
                            co2 = d.thRailCO2[i];
                            pm = d.thRailPM[i];
                        }

                        res[0] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * cost;
                        res[1] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * co2;
                        res[2] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm;

                        //noinspection ConstantConditions
                        if (log) {
                            if (r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).intValue() > 0) {
                                System.out.println(d.names[j] + " - " + d.names[i] + " " + m + ":" +
                                        r.get(String.format("x_EPDC_%d_%d_%d", j, i,
                                                m)).doubleValue() * cost + " " + r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * co2 + " " + r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm);

                                System.out.println(r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue());
                            } else //noinspection StatementWithEmptyBody
                                if (r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm > Common.EPSILON)

                                {

                                }
                        }

                    } else if (m == 4) {
                        double cost = d.coRailCost[i];
                        double co2 = d.coRailCO2[i];
                        double pm = d.coRailPM[i];

                        if (j == 11) {
                            cost = d.vaRailCost[i];
                            co2 = d.vaRailCO2[i];
                            pm = d.vaRailPM[i];
                        } else if (j == 15) {
                            cost = d.thRailCost[i];
                            co2 = d.thRailCO2[i];
                            pm = d.thRailPM[i];
                        }

                        res[0] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * cost;
                        res[1] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * co2;
                        res[2] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm;

                        //noinspection ConstantConditions
                        if (log) {
                            if (r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).intValue() > 0) {
                                System.out.println(d.names[j] + " - " + d.names[i] + " " + m + ":" +
                                        r.get(String.format("x_EPDC_%d_%d_%d", j, i,
                                                m)).doubleValue() * cost + " " + r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * co2 + " " + r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm);

                                System.out.println(r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue());
                            } else //noinspection StatementWithEmptyBody
                                if (r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm > Common.EPSILON)

                                {

                                }


                        }
                    } else {
                        double cost = d.transport[m].oCostA * d.distances[i][j] + d.transport[m].oCostB;
                        if (i == j) cost = d.transport[m].free;
                        double co2 = d.transport[m].oCo2 * d.distances[i][j];
                        double pm = d.transport[m].oPm * d.distances[i][j];

                        res[0] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * cost;
                        res[1] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * co2;
                        res[2] += r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm;

                        //noinspection ConstantConditions
                        if (log) {
                            if (r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).intValue() > 0) {
                                System.out.println(d.names[j] + " - " + d.names[i] + " " + m + ":" +
                                        r.get(String.format("x_EPDC_%d_%d_%d", j, i,
                                                m)).doubleValue() * cost + " " + r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * co2 + " " + r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm);
                                System.out.println(r.get(String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue());
                            } else //noinspection StatementWithEmptyBody
                                if (r.get(
                                        String.format("x_EPDC_%d_%d_%d", j, i, m)).doubleValue() * pm > Common.EPSILON)

                                {

                                }


                        }
                    }
                }
            }
        }

        // CALC OUTSOURCE COST
        for (int i = 0; i < 16; i++) {
            if ((i != 3) && (i != 11) && (i != 15)) continue;

            for (int j = 0; j < 16; j++) {
                for (int m = 0; m < 6; m++) {
                    double base = r.get(String.format("x_EPDC_%d_%d_%d", i, j, m)).doubleValue();
                    res[0] += base * d.storageCostOutsource[j];
                }
            }
        }

        //noinspection ConstantConditions
        if (log) System.out.println("DC - RM");

        // DO EP COST
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 16; j++) {
                for (int m = 0; m < 4; m++) {
                    double cost = d.transport[m].oCostA * d.distances[i][j] + d.transport[m].oCostB;
                    if (i == j) cost = d.transport[m].free;
                    double co2 = d.transport[m].oCo2 * d.distances[i][j];
                    double pm = d.transport[m].oPm * d.distances[i][j];

                    res[0] += r.get(String.format("x_DCRM_%d_%d_%d", j, i, m)).doubleValue() * cost;
                    res[1] += r.get(String.format("x_DCRM_%d_%d_%d", j, i, m)).doubleValue() * co2;
                    res[2] += r.get(String.format("x_DCRM_%d_%d_%d", j, i, m)).doubleValue() * pm;

                    //noinspection ConstantConditions
                    if (log) {
                        if (r.get(String.format("x_DCRM_%d_%d_%d", j, i, m)).intValue() > 0) {
                            System.out.println(d.names[j] + " - " + d.names[i] + " " + m + ":" +
                                    r.get(String.format("x_DCRM_%d_%d_%d", j, i, m)).doubleValue() * cost + " " + r.get(
                                    String.format("x_DCRM_%d_%d_%d", j, i, m)).doubleValue() * co2 + " " + r.get(
                                    String.format("x_DCRM_%d_%d_%d", j, i, m)).doubleValue() * pm);

                            System.out.println(r.get(String.format("x_DCRM_%d_%d_%d", j, i, m)).doubleValue());
                        } else if (r.get(String.format("x_DCRM_%d_%d_%d", j, i, m)).doubleValue() * pm > 0.000001d)
                            System.out.println("UPS");


                    }
                }
            }
        }

        //noinspection ConstantConditions
        if (log) System.out.println("\n" + res[0] + " " + res[1] + " " + res[2]);

        return res;
    }

}
