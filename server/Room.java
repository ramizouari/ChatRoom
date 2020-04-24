package server;
import java.util.*;
//Class for storing a number of users in a room
public class Room
{
	private Set<User> users;
	public Room(Set<User> u)
	{
		users=u;
	}
	public Room()
	{
		this(Collections.synchronizedSet(new HashSet<User>()));
	}
	public void add(User s)
	{
		users.add(s);
	}
	public void remove(User s)
	{
		users.remove(s);
	}
	public Set<User> getUsers()
	{
		return users;
	}
	public int usersNumber()
	{
		return users.size();
	}
	public void broadcast(String message,String senderName)
	{
		for(User s:users)
			Commands.sendAsRoomMessage(message,senderName,s.getSocket());
	}
}

