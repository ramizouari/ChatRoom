package server;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
//Class for storing a number of users in a room
public class Room
{
	private Set<Socket> users;
	public Room(Set<Socket> u)
	{
		users=u;
	}
	public Room()
	{
		this(Collections.synchronizedSet(new HashSet<Socket>()));
	}
	public void add(Socket s)
	{
		users.add(s);
	}
	public void remove(Socket s)
	{
		users.remove(s);
	}
	public int usersNumber()
	{
		return users.size();
	}
	public void broadcast(String message,String senderName)
	{
		for(Socket s:users)
			Commands.send(message,senderName,s);
	}
}

