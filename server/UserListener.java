package server;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Stream;
import java.net.*;
import java.io.*;
import commun.*;
//class for adding and listening to users
public class UserListener extends Thread
{
	private Map<Integer,Room> rooms;//list of all rooms each one identified by its unique number
	private Map<String,Socket> users;//list of all users each one is identified by his unique name
	private Socket sock;//socket of current user
	private User user;
	private Room currentRoom;
	private CommandsAlias commandsAlias;
	public UserListener(Map<Integer,Room> rooms, 
			Map<String,Socket> users,Socket sock,CommandsAlias cmdAlias)
	{
		commandsAlias=cmdAlias;
		this.users=users;
		this.rooms=rooms;
		this.sock=sock;
		user=null;
	}

	private void affectToRoom(int room_number)//change the room of the user
	{
		if(rooms.containsKey(room_number))//if room exists
		{
			currentRoom=rooms.get(room_number);
			currentRoom.add(user);
		}
	else //create room
		{
			Set<User>set=Collections.synchronizedSet( new HashSet<User>());
			set.add(user);
			currentRoom=new Room(set);
			rooms.put(room_number,currentRoom);
		}
		currentRoom.broadcast(user.getUserName()+" has joined the room", Commands.SERVER_NAME);
	}

	//register username
	private void registerName(ObjectOutputStream obj_out,
			BufferedReader buff_reader) throws IOException  
	{
		int nameState;
		String userName="";
		//Only characters,numbers and _, the name should contains at least 2 characters
		Pattern pat=Pattern.compile("^[a-zA-Z0-9_]{3,}$");
		do
		{
			userName = buff_reader.readLine();
			//if disconnected or asked to quit
			if(userName==null || commandsAlias.getCommand(userName).equals("/quit"))
			{
				sock.close();
				return;
			}
			Matcher matcher=pat.matcher(userName);

			if(!matcher.find())
				nameState=NameState.INVALID;
			else if(users.containsKey(userName))
				nameState=NameState.EXISTS;
			else if(userName.equals("Server"))
				nameState=NameState.RESERVED;
			else nameState=NameState.VALID;
				obj_out.writeInt(nameState);
				obj_out.flush();
			
		}while(nameState!=NameState.VALID);//name already exists or reserved	
		user=new User(userName,sock);
		users.put(userName,sock);//add user to the list
	}

	public void run()
	{
		int room_number=-1;
		BufferedReader buff_reader=null;
		try
		{
			ObjectOutputStream obj_out=new ObjectOutputStream(sock.getOutputStream());
			buff_reader=new BufferedReader
			(new InputStreamReader(sock.getInputStream()));
			registerName(obj_out,buff_reader);
			try
			{
				Commands.sendRoomsInfoObject(rooms, obj_out);//send to the user information about rooms
				room_number= Integer.parseInt(buff_reader.readLine());//get the response of user
			}
			catch(NumberFormatException e)
			{
				room_number=0;
				Commands.sendAsPrivateMessage("not a valid room number\n"+
			"you have been affected to room 0" ,Commands.SERVER_NAME, sock);
			}
			affectToRoom(room_number);//affect user to room_number
		}
		catch(IOException e)// I/O exception, connection is impossible
		{
			if(user!=null)
				users.remove(user.getUserName());//remove user from the list of users
			System.err.println(e.getMessage());
			return;//close the thread
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
				if(CommandsAlias.isCommand(str))
				{
					String cmdSplit[]=str.split(" ");
					//if you want the meaning of all user methods, check UserCommands.txt
					switch (cmdSplit[0]) 
					{
						case "/q":
						case "/quit":
							exit=true;
							break;
						case "/whisper":// /whisper [user_name] [msg]
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
							Socket destSock=users.get(cmdSplit[1]);//get the socket of the dest user
							if(destSock==null)//if the socket is not found (if and only if the user does not exists)
							{
								Commands.sendAsPrivateMessage("Username doesn't exist",
									Commands.SERVER_NAME,sock);
								break;
							}
								String msg=stream.reduce("", (u,t)->u+' '+t);//compose the message
								Commands.sendAsPrivateMessage(msg,
									user.getUserName(),destSock);//send it to the destination
								//if the message is not reflexive, sent it back to the sender
								if(sock!=destSock)
									Commands.sendAsPrivateMessage(msg,
										user.getUserName(),sock);
								break;
						}
						case "/rooms":
							Commands.showRoomsInfo(rooms, 
								new PrintStream(user.getSocket().getOutputStream()));
								break;
						case "/users":
							Commands.showUsers(users, 
								new PrintStream(user.getSocket().getOutputStream()));
								break;
						case "/user":
							Commands.sendAsPrivateMessage("Your username is "+user.getUserName(),
								Commands.SERVER_NAME,user.getSocket());
							break;
						case "/room":
						{
							int newRoom=0;
							if(cmdSplit.length==1)// /room with no arguments will give the current room
							{
								Commands.sendAsPrivateMessage("Your current room is "+room_number,
								Commands.SERVER_NAME, sock);
								break;
							}
							// /room [room_number] will change the room of user to room_number
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
								currentRoom.remove(user);//remove user from the room
							if(currentRoom.usersNumber()==0)//removing empty room
								rooms.remove(room_number);
							else currentRoom.broadcast(user.getUserName()+" has left the room",
								Commands.SERVER_NAME);
							room_number=newRoom;
							affectToRoom(room_number);//affecting user to the new room
							break;
						}
						case "/r":
						Commands.sendAsPrivateMessage("No one sent you a private message", 
							Commands.SERVER_NAME, sock);
							break;
						default:
						Commands.sendAsPrivateMessage("Unknown command",Commands.SERVER_NAME,sock);
						break;
					}
				}
				//if it is not a command, then it is a message, sent it to all users of the room
				else rooms.get(room_number).broadcast(str, user.getUserName());
			}
			catch(IOException e)//unexpected rupture of connection
			{
				System.err.println(e.getMessage());
				break;
			}
		}
		try
		{
			buff_reader.close();//closing connection with the user
			users.remove(user.getUserName());//removing user
			Room room=rooms.get(room_number);
			if(room!=null)
				room.remove(user);//remove user from the room
			if(rooms.get(room_number).usersNumber()==0)//if room is now empty
				rooms.remove(room_number);//remove the room
			else rooms.get(room_number).broadcast("connection to "+user.getUserName()+" is lost",
			Commands.SERVER_NAME);
			
		}
		catch(IOException err)
		{
			System.err.println(err.getMessage());
		}

	}
}
