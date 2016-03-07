package interfaces;

import alternative.interfaces.IAlternative;

public interface ISpecimen
{
	void print();
	
	void setAlternative(IAlternative alternative);
	IAlternative getAlternative();

	void setGene(IGene gene);
	IGene getGene();
	
	ISpecimen clone();

	boolean isEqual(ISpecimen s, Double epsilon);
	
	String getName();
	void setName(String name);

	void printEvaluation();
}
