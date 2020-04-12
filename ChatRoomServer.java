import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import commun.*;
import server.*;
//for Server commands

public class ChatRoomServer
{
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
			return;
		}
		ServerSocket serv_sock=null;
		try
		{
			serv_sock = new ServerSocket(port);
			serv_sock.setSoTimeout(10000);
		}
		catch(Exception e)
		{
			System.err.println("Unable to create Server");
			return;
		}
		Map<String,Socket> users= 
				new ConcurrentHashMap<String,Socket>();
		Map<Integer,Room> rooms=
				new ConcurrentHashMap<Integer,Room>();
		InputReader in_reader= new ServerInputReader(rooms,users);
		in_reader.start();
		while(!in_reader.getExit())
		{	
			Socket sock=null;
			try
			{
				sock=serv_sock.accept();
			}
			//expected to happen, it lets server closes if /q command is invoked
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
			RegisterUser register= new RegisterUser(rooms,users,sock);
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
	public static void send(String message,String sender,Socket dest)
	{
			try
			{
				PrintStream sout = new PrintStream(dest.getOutputStream());
				sout.println("["+sender+"]:  "+message);
				sout.flush();
			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
			}
	}
}
