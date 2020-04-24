package server;

import java.util.*;
//import java.util.function.*;
import java.util.stream.Stream;
import java.net.*;
import java.io.*;
//class for adding and listening to users
public class RegisterUser extends Thread
{
	Map<Integer,Room> rooms;//list of all rooms
	Map<String,Socket> users;//list of all users identified by their names
	Socket sock;//socket of current user
	User currentUser;
	Room currentRoom;
	public RegisterUser(Map<Integer,Room> rooms, 
			Map<String,Socket> users,Socket sock)
	{
		this.users=users;
		this.rooms=rooms;
		this.sock=sock;
		currentUser=null;
	}

	private void affectToRoom(int room_number)
	{
		if(rooms.containsKey(room_number))//if room exists
		{
			currentRoom=rooms.get(room_number);
			currentRoom.add(currentUser);
		}
	else //create room
		{
			Set<User>set=Collections.synchronizedSet( new HashSet<User>());
			set.add(currentUser);
			currentRoom=new Room(set);
			rooms.put(room_number,currentRoom);
		}
		currentRoom.broadcast(currentUser.getUserName()+" has joined the room", Commands.SERVER_NAME);
	}

/*	public static final Map<String,Consumer<RegisterUser>> CLIENT_COMMANDS;
	static
	{
		CLIENT_COMMANDS = new HashMap<String,Consumer<RegisterUser> >();

	}*/

	//register username
	private void registerName(ObjectOutputStream obj_out,
			BufferedReader buff_reader) throws IOException  
	{
		Boolean invalidName;
		String userName="";
		do
		{
			userName = buff_reader.readLine();
			if(userName==null || userName.equals("/q"))
			{
				sock.close();
				return;
			}
			invalidName=userName.isEmpty()||users.containsKey(userName);
				obj_out.writeBoolean(invalidName);
				obj_out.flush();
			
		}while(invalidName);//name already exists or reserved	
		currentUser=new User(userName,sock);
		users.put(userName,sock);
	}

	public void run()
	{
		int room_number=0;
		BufferedReader buff_reader=null;
		try
		{
			ObjectOutputStream obj_out=new ObjectOutputStream(sock.getOutputStream());
			buff_reader=new BufferedReader
			(new InputStreamReader(sock.getInputStream()));
			registerName(obj_out,buff_reader);
			try
			{
				Commands.sendRoomsInfoObject(rooms, obj_out);
				//Commands.showRoomsInfo(rooms, new PrintStream(sock.getOutputStream()));
				room_number= Integer.parseInt(buff_reader.readLine());
			}
			catch(NumberFormatException e)
			{
				Commands.sendAsPrivateMessage("not a valid room number\n"+
			"you have been affected to room 0" ,Commands.SERVER_NAME, sock);
			}
			affectToRoom(room_number);
		}
		catch(IOException e)// I/O exception, connection is impossible
		{
			users.remove(currentUser.getUserName());
			System.err.println(e.getMessage());
			return;
		}
		catch(Exception e)//minor exception
		{
			System.err.println("other Exception");
			System.err.println(e.getMessage());
		}
		boolean exit=false;
		while(!exit)
		{
			try	
			{
				String str=buff_reader.readLine();
				if(str==null)
					break;
				if(str.charAt(0)=='/')
				{
					String cmdSplit[]=str.split(" ");
					switch (cmdSplit[0]) 
					{
						case "/q":
						case "/quit":
							exit=true;
							break;
						case "/whisper":
						{
							if(cmdSplit.length<2)
							{
								Commands.sendAsPrivateMessage("Invalid command format",
									Commands.SERVER_NAME,sock);
								Commands.sendAsPrivateMessage("/send [username] [message]",
									Commands.SERVER_NAME,sock);
								break;
							}
							Stream<String> stream = Arrays.stream(cmdSplit).skip(2);//requires java 8
							Socket destSock=users.get(cmdSplit[1]);
							if(destSock==null)
							{
								Commands.sendAsPrivateMessage("Username doesn't exist",
									Commands.SERVER_NAME,sock);
								break;
							}
								String msg=stream.reduce("", (u,t)->u+' '+t);
								Commands.sendAsPrivateMessage(msg,
									currentUser.getUserName(),destSock);
								if(sock!=destSock)
									Commands.sendAsPrivateMessage(msg,
										currentUser.getUserName(),sock);
								break;
						}
						case "/rooms":
							Commands.showRoomsInfo(rooms, 
								new PrintStream(currentUser.getSocket().getOutputStream()));
								break;
						case "/users":
							Commands.showUsers(users, 
								new PrintStream(currentUser.getSocket().getOutputStream()));
								break;
						case "/user":
							Commands.sendAsPrivateMessage("Your username is "+currentUser.getUserName(),
								Commands.SERVER_NAME,currentUser.getSocket());
							break;
						case "/room":
						{
							int newRoom=0;
							if(cmdSplit.length<2)
							{
								Commands.sendAsPrivateMessage("Your current room is "+room_number,
								Commands.SERVER_NAME, sock);
								break;
							}
							try
							{
								newRoom =Integer.parseInt(cmdSplit[1]);
							}
							catch(NumberFormatException e)
							{
								Commands.sendAsPrivateMessage("not a valid room number",
								Commands.SERVER_NAME, sock);
								break;
							}
								currentRoom.remove(currentUser);
							if(currentRoom.usersNumber()==0)//removing empty room
								rooms.remove(room_number);
							else currentRoom.broadcast(currentUser.getUserName()+" has left the room",
								Commands.SERVER_NAME);
							room_number=newRoom;
							affectToRoom(room_number);
							break;
						}

						default:
						Commands.sendAsPrivateMessage("Unknown command",Commands.SERVER_NAME,sock);
						break;
					}
				}
				else rooms.get(room_number).broadcast(str, currentUser.getUserName());
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
			users.remove(currentUser.getUserName());//removing user
			rooms.get(room_number).remove(currentUser);
			if(rooms.get(room_number).usersNumber()==0)//removing empty room
				rooms.remove(room_number);
			else rooms.get(room_number).broadcast("connection to "+currentUser.getUserName()+" is lost",
			Commands.SERVER_NAME);
			
		}
		catch(IOException err)
		{
			System.err.println(err.getMessage());
		}

	}
}
