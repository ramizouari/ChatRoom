package commun;
import java.io.*;

public abstract class InputReader extends Thread
{
	protected boolean exit=false;
	public boolean getExit()
	{
		return exit;
	}	
}