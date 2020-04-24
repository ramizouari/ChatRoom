package client;
import commun.*;
import java.io.*;
import java.util.*;
import java.net.*;

//class for reading client input from stdin
public class ClientInputReader extends InputReader 
{
	private Socket sock;//client socket
	private PrintStream sout;
	private ClientCommandsAlias clientAlias;

	public ClientInputReader(Socket sock,PrintStream out)
	{
		this.sock=sock;
		sout=out;
		clientAlias=new ClientCommandsAlias(new File("ClientCommands.cfg"));
	}

	public void run()
	{
		Scanner scn=new Scanner(System.in);
		String msg="";
		while(!msg.equals("/quit"))// /q for exiting
		{
			try
			{
				msg=scn.nextLine();
				if(msg.isBlank())
					continue;
				if(msg.charAt(0)=='/')
				{
					String alias=msg.split(" ")[0];
					String command=clientAlias.getCommand(alias);
					if(command!=null)
						msg=msg.replaceAll("^/[a-zA-Z]+", command);
					
				}	
			}
			catch(NoSuchElementException e)
			{
				System.err.println("unable to read");
				System.err.println("Exiting...");
				break;
			}
			sout.println(msg);//write the received message
		}
		//exiting..
		scn.close();
		exit=true;
		try
		{
			sout.close();
			sock.close();
		}
		catch(IOException err)
		{
			System.err.println("error: unable to close connection");
		}
	}
}