import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import commun.*;
import server.*;

public class ChatRoomServer
{
	//ChatRoomServer expects exactly one argument which is the port number
	public static void main(String args[])
	{
		if(args.length==0)
		{
			System.err.println("no arguments");
			System.out.println("java ChatRoomServer [port_number]");
			return;
		}
		int port=0;
		try
		{
			port=Integer.parseInt(args[0]);
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
		//a map from pseudoname to the associated user (pseudonames are unique)
		Map<String,Socket> users= 
				new ConcurrentHashMap<String,Socket>();

		//a map from the room number to the associated room
		Map<Integer,Room> rooms=
				new ConcurrentHashMap<Integer,Room>();

		//a thread for reading server input from stdin
		InputReader in_reader= new ServerInputReader(rooms,users);
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
				continue;
			}
			catch(IOException e)
			{
				System.err.println("Problem while accepting connection");
				System.err.println(e.getMessage());
				continue;
			}
			RegisterUser register= new RegisterUser(rooms,users,sock);//add user
			register.start();
		}
		try
		{
			serv_sock.close();
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}
	}
}
