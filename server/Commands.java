package server;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.net.*;
import java.io.*;
import commun.RoomsInfo;
//class for executing commands
public abstract class Commands
{
	public static final String SERVER_NAME="Server";
	public static void globalBroadcast(Map<Integer,Room>rooms,String msg)
	{
		for(Room room:rooms.values())
			for(User u:room.getUsers())
				sendAsGlobalMessage(msg, u.getSocket());
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

	public static void sendMessage(String message,String sender,String firstDelimiter,
		String secondDelimiter,Socket dest)
	{
			try
			{
				PrintStream sout = new PrintStream(dest.getOutputStream());
				sout.println(firstDelimiter+sender+secondDelimiter  +message);
			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
			}
	}

	public static void sendAsGlobalMessage(String message,Socket dest)
	{
		sendMessage(message,SERVER_NAME,"#","#: ",dest);
	}
	

	public static void sendAsRoomMessage(String message,String sender,Socket dest)
	{
		sendMessage(message,sender,"[","]: ",dest);
	}

	public static void send(String message,Socket dest)//send plain message without specifying sender
	{
			sendMessage(message,"","","",dest);
	}

	public static void sendAsPrivateMessage(String message,String sender,Socket dest)
	{
		sendMessage(message,sender,"<<",">>: ",dest);
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
	public static void sendRoomsInfoObject(Map<Integer,Room> rooms,ObjectOutputStream obj_stream) throws IOException
	{
		Map<Integer,Set<String>> roomInfo=new HashMap<Integer,Set<String>>();
		for(Integer id:rooms.keySet())
		{
			Stream<String> stream=rooms.get(id).getUsers().stream().map(user->user.getUserName());
			roomInfo.put(id,stream.collect(Collectors.toSet()));
		}
		obj_stream.writeObject(new RoomsInfo(roomInfo));
		
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

