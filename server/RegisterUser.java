package server;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
//class for adding and listening to users
public class RegisterUser extends Thread
{
	Map<Integer,Room> rooms;//list of all rooms
	Map<String,Socket> users;//list of all users identified by their names
	Socket sock;//socket of current user
	public RegisterUser(Map<Integer,Room> rooms, 
			Map<String,Socket> users,Socket sock)
	{
		this.users=users;
		this.rooms=rooms;
		this.sock=sock;
	}
	public void run()
	{
		int room_number=0;
		String userName="";
		BufferedReader buff_reader=null;
		try
		{
			boolean nameExists;
			do
			{
				buff_reader=new BufferedReader
				(new InputStreamReader(sock.getInputStream()));
				userName = buff_reader.readLine();
				if(userName==null || userName.equals("/q"))
				{
					sock.close();
					return;
				}
				nameExists=userName.isEmpty()||users.containsKey(userName);
				DataOutputStream out = new DataOutputStream(sock.getOutputStream());
					out.writeBoolean(nameExists);
				
			}while(nameExists);
			try
			{
				room_number= Integer.parseInt(buff_reader.readLine());
			}
			catch(NumberFormatException e)
			{
				Commands.send("not a valid room number\n"+
			"you have been affected to room 0", "Server", sock);
			}
			users.put(userName,sock);
			if(rooms.containsKey(room_number))//if room exists
				rooms.get(room_number).add(sock);
			else //create room
				{
					Set<Socket>set=Collections.synchronizedSet( new HashSet<Socket>());
					set.add(sock);
					rooms.put(room_number,new Room(set));
				}
		}
		catch(IOException e)// I/O exception, connection is impossible
		{
			System.err.println(e.getMessage());
			return;
		}
		catch(Exception e)//minor exception
		{
			System.err.println("other Exception");
			System.err.println(e.getMessage());
		}
		String S="";
		while(sock.isConnected())
		{
			try	
			{
				String str=buff_reader.readLine();
				if(str!=null)
					rooms.get(room_number).broadcast(str, userName);
			}
			catch(IOException e)//unexpected rupture of connection
			{
				System.err.println(e.getMessage());
				break;
			}
		}
		try
		{
			buff_reader.close();
			users.remove(userName);//removing user
			rooms.get(room_number).remove(sock);
			if(rooms.get(room_number).usersNumber()==0)//removing empty room
				rooms.remove(room_number);
			else rooms.get(room_number).broadcast("connection to "+userName+" is lost","Server");
			
		}
		catch(IOException err)
		{
			System.err.println(err.getMessage());
		}

	}
}
