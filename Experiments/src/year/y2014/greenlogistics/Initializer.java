package year.y2014.greenlogistics;

import java.util.ArrayList;
import java.util.HashMap;

import base.Gene;
import base.Specimen;
import criterion.interfaces.ICriterion;
import interfaces.IGene;
import interfaces.IInitializer;
import interfaces.ISpecimen;
import org.apache.commons.math3.random.MersenneTwister;

import utils.Shuffle;

public class Initializer implements IInitializer
{

	@Override
	public void createInitialPopulation(ArrayList<ICriterion> criteria, ArrayList<ISpecimen> specimens, int populationSize,
			MersenneTwister generator, Object problem)
	{
		for (int i = 0; i < populationSize; i++)
		{
            ISpecimen spec = this.createSpecimen(criteria, String.format("0-%d", i), generator, problem);
            specimens.add(spec);
		}
	}

    private ISpecimen createSpecimen(ArrayList<ICriterion> criterion, String name, MersenneTwister generator,
                                     Object problem)
    {
		// Data p = (Data) problem;
		ISpecimen specimen = new Specimen(name, criterion);

		// SECOND CODING APPROACH

		IGene gene = new Gene();
		HashMap<String, IGene> geneMap = new HashMap<String, IGene>(4);

		IGene epGene_P = new Gene();
		IGene epGene_C = new Gene();
		
		IGene dcGene_P = new Gene();
		IGene dcGene_C = new Gene();
		IGene dcGene_T = new Gene();
		
		IGene rmGene_P = new Gene();
		IGene rmGene_C = new Gene();
		IGene rmGene_T = new Gene();
		
		specimen.getAlternative().setAggregatedEvaluation(0.0d);

		gene.setGene(geneMap);
		specimen.setGene(gene);

		geneMap.put("EP_P", epGene_P);
		geneMap.put("EP_C", epGene_C);
		geneMap.put("DC_P", dcGene_P);
		geneMap.put("DC_C", dcGene_C);
		geneMap.put("DC_T", dcGene_T);
		geneMap.put("RM_P", rmGene_P);
		geneMap.put("RM_C", rmGene_C);
		geneMap.put("RM_T", rmGene_T);

        Shuffle<Integer> shuffle = new Shuffle<Integer>();

		// MAKE RM
		ArrayList<Integer> RM = new ArrayList<Integer>(15);
		for (int i = 0; i < 15; i++)
			RM.add(i);

        shuffle.shuffle(RM);

		double rmCode_P[] = new double[15];
		double rmCode_C[] = new double[15];
		double rmCode_T[] = new double[15];

		int criteria = 3;
		for (int i = 0; i < 15; i++)
		{
			rmCode_P[i] = RM.get(i);
			rmCode_C[i] = generator.nextInt(criteria);
			if (generator.nextBoolean()) rmCode_T[i] = 4;
			else rmCode_T[i] = generator.nextInt(4);
		}

		rmGene_P.setValues(rmCode_P);
		rmGene_C.setValues(rmCode_C);
		rmGene_T.setValues(rmCode_T);
		
		// MAKE EP + DC
		ArrayList<Integer> sEP = new ArrayList<Integer>(3);
		ArrayList<Integer> DC = new ArrayList<Integer>(16);

		sEP.add(3);
		sEP.add(11);
		sEP.add(15);
        shuffle.shuffle(sEP);

		ArrayList<Integer> EP = new ArrayList<Integer>(3);
		EP.add(sEP.get(0));

		if (generator.nextDouble() > 0.5d)
			EP.add(sEP.get(1));
		if (generator.nextDouble() > 0.5d)
			EP.add(sEP.get(2));

		DC.add(0);
		for (int i = 1; i < 16; i++)
		{
			// if (DC.size() > 8) break;
			if (generator.nextDouble() > 0.5d)
				DC.add(i);
		}

        shuffle.shuffle(DC);

		// MAKE DC

		double dcCode_P[] = new double[DC.size()];
		double dcCode_C[] = new double[DC.size()];
		double dcCode_T[] = new double[DC.size()];
		
		for (int i = 0; i < DC.size(); i++)
		{
			dcCode_P[i] = DC.get(i);
			dcCode_C[i] = generator.nextInt(criteria);
			if (generator.nextBoolean()) dcCode_T[i] = 5;
			else dcCode_T[i] = generator.nextInt(5);		
		}

		dcGene_P.setValues(dcCode_P);
		dcGene_C.setValues(dcCode_C);
		dcGene_T.setValues(dcCode_T);
		// MAKE EP

		double epCode_P[] = new double[EP.size()];
		double epCode_C[] = new double[EP.size()];
		
		for (int i = 0; i < EP.size(); i++)
		{
			epCode_P[i] = EP.get(i);
			epCode_C[i] = generator.nextInt(criteria);
		}

		epGene_P.setValues(epCode_P);
		epGene_C.setValues(epCode_C);
		
		return specimen;
	}



}
