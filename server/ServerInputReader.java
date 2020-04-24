package server;
import java.util.*;
import java.util.stream.Stream;
import java.net.*;
import commun.*;
import java.io.File;
//for reading server input from stdin
public class ServerInputReader extends InputReader
{
	private Map<Integer,Room> rooms;
	private Map<String,Socket> users_list;
	private CommandsAlias commandsAlias;
	public ServerInputReader(Map<Integer,Room> r,Map<String,Socket> users)
	{
		rooms =r;
		users_list=users;
		commandsAlias=new ServerCommandsAlias(new File("ServerCommands.cfg"));
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
			switch(commandsAlias.getCommand(ins))//requires java 7 
			{
			case "/all": //format : "/all [message]"
			case "/post": //send a message as server to all users
			case "/broadcast":
				Commands.globalBroadcast(rooms, cmd.substring(4));
				break;
			
			case "/postroom": //send a message as server to all users of given room 
			{
				if(cmd.length()<2)
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
				int first_space_pos=cmd.indexOf(" ");
				int second_space_rel_pos=cmd.substring(first_space_pos+1)
					.indexOf(" ");
				int pos=first_space_pos+2+second_space_rel_pos;
				room.broadcast(cmd.substring(pos),"Server");
			}
			break;
			
			case "/whisper": //send a message as server to an exact user
			// format: "/whisper [username] [message]"
			{
				if(cmdSplit.length<2)
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
			default:
				System.err.println("Unknown command");
				break;
			}	
		}
	}
}

