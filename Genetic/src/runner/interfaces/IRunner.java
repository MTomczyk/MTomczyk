package runner.interfaces;


public interface IRunner
{
	void run(int generations);

	void init();
	void step(int generation);

}
