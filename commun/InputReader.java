package commun;

//base class for stdin reading (from the console)
public abstract class InputReader extends Thread
{
	protected boolean exit=false;//if exiting is set to true, the reading will stop
	public boolean getExit()
	{
		return exit;
	}	
}