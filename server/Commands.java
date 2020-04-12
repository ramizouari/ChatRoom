package server;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
public abstract class Commands
{
	public static void globalBroadcast(Map<Integer,Room>rooms,String msg)
	{
		for(Room room:rooms.values())
			room.broadcast(msg,"Server");
	}
	public static void closeServer(Map<Integer,Room> rooms,
			Map<String,Socket> users_list)
	{
		for(Room room:rooms.values())
			room.broadcast("Closing Server..","Server");
		for(Socket s:users_list.values())
			try
			{
				s.close();
			}
			catch(IOException e)
			{
				System.err.println("unable to close connection");
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

