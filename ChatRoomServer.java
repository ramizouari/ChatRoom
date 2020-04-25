import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import commun.*;
import server.*;

public class ChatRoomServer
{
	//ChatRoomServer expects at least one argument which is the port number
	//the option -i will five the server the ability read commands from your inputs
	public static void main(String args[])
	{
		int port=0;
		boolean isInteractive=false;//-i option
		String portStr=null;
		for(String U:args)//search for the -i option and the port number in the args
		{
			if(U.equals("-i"))
				isInteractive=true;
			else if(!U.isEmpty()&&U.charAt(0)!='-')
				portStr=U;
		}
		if(portStr==null)//if no port number found
		{
			System.err.println("no arguments");
			System.out.println("java ChatRoomServer [port_number]");
			return;
		}
		try
		{
			port=Integer.parseInt(portStr);//try to convert the candidate port number to int
		}
		catch(Exception e)	
		{
			System.err.println("invalid port number format");
			return;//exit application
		}
		ServerSocket serv_sock=null;
		try
		{
			serv_sock = new ServerSocket(port);
			serv_sock.setSoTimeout(10000);//a time out of 10 seconds
		}
		catch(Exception e)
		{
			System.err.println("Unable to create Server");
			return;
		}
		ServerCommandsAlias commandsAlias=new ServerCommandsAlias(new File("ServerCommands.cfg"));
		//a map from pseudoname to the associated user (pseudonames are unique)
		Map<String,Socket> users= 
				new ConcurrentHashMap<String,Socket>();

		//a map from the room number to the associated room
		Map<Integer,Room> rooms=
				new ConcurrentHashMap<Integer,Room>();

		//a thread for reading server input from stdin
		InputReader in_reader= new ServerInputReader(rooms,users,commandsAlias);
		if(isInteractive)
			in_reader.start();
		while(!in_reader.getExit())//while the server is not asking to exit (via the /q command)
		{	
			Socket sock=null;
			try
			{
				/*
				accept here is not blocking indefinitely
				 it waits for a maximum of 10 seconds
				*/
				sock=serv_sock.accept();
			}
			catch(SocketTimeoutException exc)
			{
				continue;//waits for the next connection or possibly exits if the server is shutting down
			}
			catch(IOException e)
			{
				System.err.println("Problem while accepting connection");
				System.err.println(e.getMessage());
				continue;
			}
			UserListener listener= new UserListener(rooms,users,sock,commandsAlias);//add user
			listener.start();//start thread of user
		}
		try
		{
			serv_sock.close();//closing server
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}
	}
}
