package server;

import java.util.*;
import java.util.stream.Stream;
import java.io.IOException;
import java.net.*;
import commun.*;
//for reading server input from stdin
public class ServerInputReader extends InputReader
{
	private Map<Integer,Room> rooms;
	private Map<String,Socket> users_list;
	private ServerCommandsAlias commandsAlias;
	public ServerInputReader(Map<Integer,Room> r,Map<String,Socket> users,ServerCommandsAlias cmdAlias)
	{
		rooms =r;
		users_list=users;
		commandsAlias=cmdAlias;
	}
	public void run()
	{
		Scanner scn = new Scanner(System.in);
		String cmd="",ins="";
		String cmdSplit[];
		while(!exit)
		{
			cmd=scn.nextLine();
			cmdSplit=cmd.split(" ");
			ins=cmdSplit[0];//ins is the instruction: /q,/whisper...
			//for the definition of Server commands see README.md
			switch(commandsAlias.getCommand(ins))//requires java 7 
			{
			case "/all": //format : "/all [message]"
			case "/post": //send a message as server to all users
			case "/broadcast":
				Commands.globalBroadcast(rooms, cmd.substring(4));
				break;
			
			case "/postroom": //send a message as server to all users of given room 
			{
				if(cmd.length()==1)
				{
					System.err.println("invalid command format");
					System.out.println(ins + " [room_number] [message]");
					continue;
				}
				Room room =null;
				try
				{
					room = rooms.get(Integer.parseInt(cmdSplit[1]));
				}
				catch(Exception e) 
				{
					System.err.println("Not a valid room number");
					continue;
				}
				Stream<String> stream = Arrays.stream(cmdSplit).skip(2);//requires java 8
				room.broadcast(stream.reduce("", (u,t)->u+' '+t),Commands.SERVER_NAME);
			}
			break;
			
			case "/whisper": //send a message as server to an exact user
			// format: "/whisper [username] [message]"
			{
				if(cmdSplit.length==1)
				{
					System.err.println("invalid command format");
					System.out.println(ins+" [username] [message]");
					continue;
				}
				Stream<String> stream = Arrays.stream(cmdSplit).skip(2);//requires java 8
				Socket sock=users_list.get(cmdSplit[1]);
				if(sock==null)
				{
					System.err.println("Username doesn't exist");
					continue;
				}	
					Commands.sendAsPrivateMessage(stream.reduce("", (u,t)->u+' '+t),Commands.SERVER_NAME,sock);
			}
				break;
			case "/rooms"://print list of rooms with their users
				Commands.showRoomsInfo(rooms, System.out);
				break;		
			case "/users"://print list of users
				Commands.showUsers(users_list,System.out);
				break;
			case "/q": //close server
			case "/quit":
				exit=true;
				scn.close();
				Commands.closeServer(rooms, users_list);
				break;
			case "/ban":
				{
					String bannedUser=cmdSplit[1];
					Socket bannedUserSock=users_list.get(bannedUser);
					if(bannedUserSock==null)
					{
						System.err.println("User does not exists");
						continue;
					}
					Commands.sendAsPrivateMessage("You have been banned", Commands.SERVER_NAME,bannedUserSock);
					for(Integer room_id:rooms.keySet())
						if(rooms.get(room_id).getUsers().removeIf(user->user.getUserName().equals(bannedUser)))
						{	
							System.out.println("GJ");
							if(rooms.get(room_id).usersNumber()==0)
								rooms.remove(room_id);
							else
								rooms.get(room_id).broadcast("User "+bannedUser+" has been banned", 
									Commands.SERVER_NAME);	
							break;
							
						}
						users_list.remove(bannedUser);
						try
						{
							bannedUserSock.close();
						}
						catch(IOException e)
						{
							System.err.println("Unable to close socket");
						}
						break;
				}
			default:
				System.err.println("Unknown command");
				break;
			}	
		}
	}
}

