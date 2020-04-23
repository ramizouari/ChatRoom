package server;
import java.util.*;
import java.net.*;
import java.io.*;

//class for executing commands
public abstract class Commands
{
	public static final String SERVER_NAME="Server";
	public static void globalBroadcast(Map<Integer,Room>rooms,String msg)
	{
		for(Room room:rooms.values())
			room.broadcast(msg,SERVER_NAME);
	}

	public static void closeServer(Map<Integer,Room> rooms,
			Map<String,Socket> users_list)
	{
		for(Room room:rooms.values())
			room.broadcast("Closing Server..",SERVER_NAME);
		for(Socket s:users_list.values())
			try
			{
				s.close();
			}
			catch(IOException e)
			{
				System.err.println("Unable to close connection");
			}
	}

	//send a message to specific user
	public static void send(String message,String sender,Socket dest)
	{
			try
			{
				PrintStream sout = new PrintStream(dest.getOutputStream());
				sout.println("["+sender+"]:  "+message);
			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
			}
	}

	public static void send(String message,Socket dest)
	{
			try
			{
				PrintStream sout = new PrintStream(dest.getOutputStream());
				sout.println(message);
			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
			}
	}

	public static void showRoomsInfo(Map<Integer,Room> rooms,PrintStream print_stream)
	{
		if(rooms.isEmpty())
		{
			print_stream.println("No room registered");
			print_stream.println();
			return;
		}
		print_stream.println("Rooms list:");
				for(Integer roomID:rooms.keySet())
				{
					print_stream.println("\tRoom "+roomID+":");
					for(User u:rooms.get(roomID).getUsers())
						print_stream.println("\t\t"+u.getUserName());
				}
				print_stream.println();//this empty line is used as EOF for the client socket
	}
	public static void showUsers(Map<String,Socket> users_list,PrintStream print_stream)
	{
		if(users_list.isEmpty())
		{
			print_stream.println("No user registered");
			return;
		}
		print_stream.println("UserName\tIP Address");
		users_list.forEach((u,sock)->print_stream.println(u+"\t"+sock.getInetAddress()));
	}

	/*public static void whisper(String msg,String senderName,String destName,Map<String,Socket> users_list)
	{
		Socket sock=users_list.get(destName);
		if(sock==null)
		{
			if(senderName.equals(Commands.SERVER_NAME))
				System.err.println("Username doesn't exist");
			else Commands.send("Username doesn't exist",SERVER_NAME,)
			return;
		}	
		Commands.send(msg,senderName,sock);
	}*/
}

