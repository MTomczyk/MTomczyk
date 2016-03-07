package killer.interfaces;

import interfaces.ISpecimen;

import java.util.ArrayList;

public interface IKiller
{
	void kill(ArrayList<ISpecimen> specimen, int number);
}
