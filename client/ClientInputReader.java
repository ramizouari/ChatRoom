package client;
import commun.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class ClientInputReader extends InputReader 
{
	Socket sock;
	PrintStream sout;
	public ClientInputReader(Socket sock,PrintStream out)
	{
		this.sock=sock;
		sout=out;
	}
	public void run()
	{
		Scanner scn=new Scanner(System.in);
		String msg="";
		while(!msg.equals("/q"))
		{
			try
			{
				msg=scn.nextLine();
			}
			catch(NoSuchElementException e)
			{
				System.err.println("unable to read");
				System.err.println("Exiting...");
				break;
			}
			sout.println(msg);
		}
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