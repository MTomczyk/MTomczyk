package year.y2014.greenlogistics.B;

import java.util.ArrayList;
import java.util.LinkedList;

import criterion.interfaces.ICriterion;
import interfaces.IEvaluator;
import interfaces.ISpecimen;
import standard.Common;

public class EvaluatorB implements IEvaluator
{
	@SuppressWarnings({"ConstantConditions", "UnusedParameters"})
	private double[][] updateSecond(int cityDC, int criterion, int tr, double demand, double citiesEP[],
			double tStorage[][], DataB p)
	{
		double result[] = { 9999999, 9999999, 9999999, 9999999, 9999999, 9999999, 9999999, 9999999,
				9999999 };

		double storageMap[] = new double[citiesEP.length];

		for (int i = 0; i < citiesEP.length; i++)
		{
			int cityEP = (int) citiesEP[i];
			double cost = 0.0d;
			double co2 = 0.0d;
			double pm = 0.0d;

			for (int j = 0; j < 5; j++)
			{
				if (tr < 5)
				{
					if (j != tr) continue;
				}
				
				if (j == 4)
				{
					if (cityEP == 3)
					{
						if (demand >= 14.9d)
							cost = demand * p.coRailBlockCost[cityDC];
						else
							cost = demand * p.coRailCost[cityDC];
						co2 = p.coRailCO2[cityDC] * demand;
						pm = p.coRailPM[cityDC] * demand;
					}
					else if (cityEP == 11)
					{
						if (demand >= 14.9d)
							cost = demand * p.vaRailBlockCost[cityDC];
						else
							cost = demand * p.vaRailCost[cityDC];
						co2 = p.vaRailCO2[cityDC] * demand;
						pm = p.vaRailPM[cityDC] * demand;
					}
					else if (cityEP == 15)
					{
						if (demand >= 14.9d)
							cost = demand * p.thRailBlockCost[cityDC];
						else
							cost = demand * p.thRailCost[cityDC];
						co2 = p.thRailCO2[cityDC] * demand;
						pm = p.thRailPM[cityDC] * demand;
					}

					if (cost < 0.0d)
					{
						continue;
					}
				}
				else
				{
					double distance = p.distances[cityDC][cityEP];
					if (cityDC == cityEP)
						cost += p.transport[j].free * demand;
					else
						cost += demand * (p.transport[j].oCostA * distance + p.transport[j].oCostB);
					co2 += demand * p.transport[j].oCo2 * distance;
					pm += demand * p.transport[j].oPm * distance;
				}

				if (((criterion == 0) && (cost < result[0])) || ((criterion == 1) && (co2 < result[1]))
						|| ((criterion == 2) && (pm < result[2])))
				{
					result[0] = cost;
					result[1] = co2;
					result[2] = pm;
					result[7] = cityEP;
					result[8] = j;
					for (int m = 0; m < storageMap.length; m++)
						storageMap[m] = 0.0d;
					storageMap[i] = demand;
				}
			}
		}

		return new double[][]{ result, storageMap };
	}

	@SuppressWarnings("ConstantConditions")
	private double[][] updateFirst(int cityRM, int criterion, int tr, double demand, double citiesDC[],
			double criterionDC[], double trDC[], double citiesEP[], double tStorage[][], DataB p)
	{
		double tmpMap[] = new double[citiesDC.length];
		double storageMap[] = null;

		// WYZNACZ MOZLIWE KOMBINACJE POLACZEN
		LinkedList<Integer> connection = new LinkedList<>();
		for (int i = 0; i < citiesDC.length; i++)
		{
			for (int j = 0; j < citiesDC.length; j++)
			{
				if (i == j)
					connection.add(i * 100 + j);
				else
				{
					if ((tStorage[1][(int) citiesDC[j]] > 600.0d)
							&& (tStorage[1][(int) citiesDC[j]] < 15.0d)
							&& (demand + tStorage[1][(int) citiesDC[j]] > 15.0d))
					{
						// System.out.printf("LOL\n");
						connection.add(i * 100 + j);
					}

				}
			}
		}

		// DLA KAZDEJ KOMBINACJI ZNAJDZ NAJLEPSZA
		double result[] = { 9999999, 9999999, 9999999, 9999999, 9999999, 9999999, 9999999, 9999999,
				9999999 };

		for (Integer v : connection)
		{
			ArrayList<Integer> DC = new ArrayList<>(2);
			ArrayList<Integer> POINT = new ArrayList<>(2);
			ArrayList<Double> DEMAND = new ArrayList<>(2);

			int nd = v % 100;
			int st = (v - (v % 100)) / 100;

			for (int i = 0; i < tmpMap.length; i++)
				tmpMap[i] = 0.0d;

			if (nd != st)
			{
				double demandND = 15 - tStorage[1][(int) citiesDC[nd]];
				double demandST = demand - demandND;
				if (demandND < 0.0d)
					System.out.printf("MIN ERROR ND\n");
				if (demandST < 0.0d)
					System.out.printf("MIN ERROR ST\n");

				tmpMap[nd] += demandND;
				tmpMap[st] += demandST;

				DC.add((int) citiesDC[nd]);
				DEMAND.add(demandND);

				DC.add((int) citiesDC[st]);
				DEMAND.add(demandST);
			}
			else
			{
				tmpMap[st] = demand;
				DC.add((int) citiesDC[st]);
				POINT.add(st);
				DEMAND.add(demand);
			}

			double cost[][][] = new double[DC.size()][4][2];
			double co2[][][] = new double[DC.size()][4][2];
			double pm[][][] = new double[DC.size()][4][2];
			double storageCost[] = new double[DC.size()];

			for (int i = 0; i < DC.size(); i++)
			{
				int cityDC = DC.get(i);
				double dmd = DEMAND.get(i);
				storageCost[i] = this.calcStorageOut(dmd, 0, cityDC, p);

				for (int j = 0; j < 4; j++)
				{
					if (tr < 4)
					{
						if (j != tr)
						{
							cost[i][j][0] = -1;
							co2[i][j][0] = -1;
							pm[i][j][0] = -1;
							
							cost[i][j][1] = -1;
							co2[i][j][1] = -1;
							pm[i][j][1] = -1;
							continue;
						}
					}
					
					
					double distance = p.distances[cityRM][cityDC];
					if (cityRM == cityDC)
						cost[i][j][0] = p.transport[j].free * dmd;
					else
						cost[i][j][0] = dmd
								* (p.transport[j].oCostA * distance + p.transport[j].oCostB);
					co2[i][j][0] = dmd * p.transport[j].oCo2 * distance;
					pm[i][j][0] = dmd * p.transport[j].oPm * distance;

					double addResult[][] = this.updateSecond(cityDC, (int) criterionDC[POINT.get(i)], (int) trDC[POINT.get(i)],
							tStorage[1][cityDC] + dmd, citiesEP, tStorage, p);

					cost[i][j][1] = addResult[0][0];
					co2[i][j][1] = addResult[0][1];
					pm[i][j][1] = addResult[0][2];
				}
			}

			// TRY UPDATE
			if (DC.size() == 1)
				for (int i = 0; i < 4; i++)
				{
					double rCost = cost[0][i][0] + cost[0][i][1];
					
					if (rCost < 0.0d) continue;
					
					double rCO2 = co2[0][i][0] + co2[0][i][1];
					double rPM = pm[0][i][0] + pm[0][i][1];
					double rStorage = storageCost[0];
					// SECOND LEVEL???
					if (((criterion == 0) && (rCost + rStorage < result[0] + result[3] + result[4]))
							|| ((criterion == 1) && (rCO2 < result[1] + result[5]))
							|| ((criterion == 2) && (rPM < result[2] + result[6])))
					{
						result[0] = cost[0][i][0];
						result[1] = co2[0][i][0];
						result[2] = pm[0][i][0];
						result[3] = rStorage;
						result[4] = cost[0][i][1];
						result[5] = co2[0][i][1];
						result[6] = pm[0][i][1];
						result[7] = DC.get(0);
						result[8] = i;
						storageMap = tmpMap.clone();
					}
				}
			else
			{
				for (int i = 0; i < 4; i++)
				{
					if (cost[0][i][0] + cost[0][i][1] > result[0] + result[3] + result[4])
						continue;
					if (co2[0][i][0] + co2[0][i][1] > result[1] + result[5])
						continue;
					if (pm[0][i][0] + pm[0][i][1] > result[2] + result[6])
						continue;

					for (int j = 0; j < 4; j++)
					{
						double rCost = cost[0][i][0] + cost[0][i][1] + cost[1][j][0]
								+ cost[1][j][1];
						double rCO2 = co2[0][i][0] + co2[0][i][1] + co2[1][j][0] + co2[1][j][1];
						double rPM = pm[0][i][0] + pm[0][i][1] + pm[1][j][0] + pm[1][j][1];
						double rStorage = storageCost[0];
						// SECOND LEVEL???
						if (((criterion == 0) && (rCost + rStorage < result[0] + result[3] + result[4]))
								|| ((criterion == 1) && (rCO2 < result[1] + result[5]))
								|| ((criterion == 2) && (rPM < result[2] + result[6])))
						{
							// System.out.printf("USED\n");
							result[0] = cost[0][i][0] + cost[1][j][0];
							result[1] = co2[0][i][0] + co2[1][j][0];
							result[2] = pm[0][i][0] + pm[1][j][0];
							result[3] = rStorage;
							result[4] = cost[0][i][1] + cost[1][j][1];
							result[5] = co2[0][i][1] + co2[1][j][1];
							result[6] = pm[0][i][1] + pm[1][j][1];
							storageMap = tmpMap.clone();
						}
					}
				}
			}

		}

		return new double[][]{ result, storageMap };
	}

	@Override
	public void evaluate(ArrayList<ICriterion> criteria, ISpecimen specimen, Object problem, boolean log)
	{
		DataB p = (DataB) problem;

		// --- GENOTYP
		double rm_p[] = specimen.getGene().getGene().get("RM_P").getValues();
		double dc_p[] = specimen.getGene().getGene().get("DC_P").getValues();
		double ep_p[] = specimen.getGene().getGene().get("EP_P").getValues();

		double rm_c[] = specimen.getGene().getGene().get("RM_C").getValues();
		double dc_c[] = specimen.getGene().getGene().get("DC_C").getValues();
		
		double rm_t[] = specimen.getGene().getGene().get("RM_T").getValues();
		double dc_t[] = specimen.getGene().getGene().get("DC_T").getValues();
		
		// --- WYNIKI
		double costTransport = 0.0d;
		double costWarehouse = 0.0d;
		double co2 = 0.0d;
		double pm = 0.0d;

		double tStorage[][] = new double[3][16];

		//System.out.printf("------------------\n");
		//System.out.printf(" RM -> DC\n");
		// OBLICZ RM - DC
		for (int i = 0; i < rm_p.length; i++)
		{
			int cityRM = (int) rm_p[i];

			// System.out.printf("\n%s\n", p.names[cityRM]);

			double demand = p.demand[cityRM];
			double result[][] = this.updateFirst(cityRM, (int) rm_c[i], (int)rm_t[i], demand, dc_p, dc_c, dc_t, ep_p,
					tStorage, p);

			costTransport += result[0][0];
			co2 += result[0][1];
			pm += result[0][2];
			
			//System.out.printf("%s -> %s: (%d) %.2f %.2f %.2f\n", p.names[cityRM],
			//		p.names[(int) result[0][7]], (int) result[0][8], result[0][0], result[0][1],
			//		result[0][2]);

			for (int j = 0; j < result[1].length; j++)
				tStorage[1][(int) dc_p[j]] += result[1][j];
		}

		// OBLICZ KOSZT STORAGE W DC
		for (double aDc_p : dc_p)
		{
			int cityB = (int) aDc_p;
			if (tStorage[1][cityB] < 1.0)
				continue;
			costWarehouse += this.calcStorageOut(tStorage[1][cityB], 0, cityB, p);
		}

		//System.out.printf(" DC -> EP\n");
		// OBLICZ DC - EP
		double war = 0;
		for (int i = 0; i < dc_p.length; i++)
		{
			int cityDC = (int) dc_p[i];
			double demand = tStorage[1][cityDC];
			war += demand;
			double result[][] = this.updateSecond(cityDC, (int) dc_c[i], (int)dc_t[i], demand, ep_p, tStorage, p);

			costTransport += result[0][0];
			co2 += result[0][1];
			pm += result[0][2];

			//System.out.printf("%s -> %s: (%d) %.2f %.2f %.2f\n", p.names[cityDC],
			//		p.names[(int) result[0][7]], (int) result[0][8], result[0][0], result[0][1],
			//		result[0][2]);

			for (int j = 0; j < result[1].length; j++)
				tStorage[2][(int) ep_p[j]] += result[1][j];
		}
		if ((war < 246) || (war > 248))
			System.out.printf("DC ERRORRR!\n");

		// --- OBLICZ STATKI
		double coStorage = tStorage[2][3];
		double vaStorage = tStorage[2][11];
		double thStorage = tStorage[2][15];

        war = (coStorage + vaStorage + thStorage);
        if ((war <= 246) || (war >= 248))
        {
            //specimen.print();
            //System.out.printf("--- %f\n", war);
            //System.out.printf("EVAL ERRORRR!\n");

            specimen.getAlternative().setEvaluationAt(criteria.get(0), Common.MAX_DOUBLE);
            specimen.getAlternative().setEvaluationAt(criteria.get(1), Common.MAX_DOUBLE);
            specimen.getAlternative().setEvaluationAt(criteria.get(2), Common.MAX_DOUBLE);
        }
        else
        {
            double coShipCost = coStorage * p.shipCoCost;
            double coCO2 = coStorage * p.shipCoCO2;
            double coPM = coStorage * p.shipCoPM;

            double vaShipCost = vaStorage * p.shipVaCost;
            double vaCO2 = vaStorage * p.shipVaCO2;
            double vaPM = vaStorage * p.shipVaPM;

            double thShipCost = thStorage * p.shipThCost;
            double thCO2 = thStorage * p.shipThCO2;
            double thPM = thStorage * p.shipThPM;

            double shipCost = coShipCost + vaShipCost + thShipCost;
            double shipCO2 = coCO2 + vaCO2 + thCO2;
            double shipPM = coPM + vaPM + thPM;

            // ZAPISZ WYNIKI
            specimen.getAlternative().setEvaluationAt(criteria.get(0), (costTransport + costWarehouse + shipCost));
            specimen.getAlternative().setEvaluationAt(criteria.get(1), (co2 + shipCO2));
            specimen.getAlternative().setEvaluationAt(criteria.get(2), (pm + shipPM));
        }
		
		/*System.out.printf("SHIP %f %f %f\n", shipCost,shipCO2,shipPM);
		
		System.out.printf("STORAGE %f \n", costWarehouse);
		System.out.printf("%f %f %f\n", specimen.getEvaluations()[1],specimen.getEvaluations()[2],specimen.getEvaluations()[3]);*/

		// --------- APPLY NEW DC

		ArrayList<Integer> DCp = new ArrayList<>(dc_p.length);
		ArrayList<Integer> DCc = new ArrayList<>(dc_p.length);

		for (int i = 0; i < dc_p.length; i++)
		{
			if (tStorage[1][(int) dc_p[i]] < 0.1d)
				continue;
			DCp.add((int) dc_p[i]);
			DCc.add((int) dc_c[i]);
		}

		dc_p = new double[DCp.size()];
		dc_c = new double[DCc.size()];

		for (int i = 0; i < dc_p.length; i++)
		{
			dc_p[i] = DCp.get(i);
			dc_c[i] = DCc.get(i);
		}

		specimen.getGene().getGene().get("DC_P").setValues(dc_p);
		specimen.getGene().getGene().get("DC_C").setValues(dc_c);

	}

	@Override
	public void evaluate(ArrayList<ICriterion> criteria, ArrayList<ISpecimen> specimens, Object problem, boolean log)
	{
		for (ISpecimen specimen : specimens)
		{
			this.evaluate(criteria, specimen, problem, log);
		}
	}

	private double calcStorageOut(double demand, double storage, int city, DataB p)
	{
		return (storage + demand) * p.storageCostOutsource[city];
	}
}
