package server;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import commun.*;
//for reading server input
public class ServerInputReader extends InputReader
{
	private Map<Integer,Room> rooms;
	private Map<String,Socket> users_list;
	public ServerInputReader(Map<Integer,Room> r,Map<String,Socket> users)
	{
		rooms =r;
		users_list=users;
	}
	public void run()
	{
		Scanner scn = new Scanner(System.in);
		String cmd="",ins="";
		String cmdSplit[];
		while(!cmd.equals("/q"))
		{
			cmd=scn.nextLine();
			cmdSplit=cmd.split(" ");
			ins=cmdSplit[0];
			switch(ins)//requires java 7
			{
			case "/all": //format : "/all [message]"
			case "/post":
				Commands.globalBroadcast(rooms, cmd.substring(4));
				break;
			
			case "/postroom":
			{
				if(cmd.length()<2)
				{
					System.err.println("invalid command format");
					System.out.println("/postroom [room_number] [message]");
					continue;
				}
				Room room =null;
				try
				{
					room = rooms.get(Integer.parseInt(cmdSplit[1]));
				}
				catch(Exception e) 
				{
					System.err.println("not a valid room number");
					continue;
				}
				int first_space_pos=cmd.indexOf(" ");
				int second_space_rel_pos=cmd.substring(first_space_pos+1)
					.indexOf(" ");
				int pos=first_space_pos+2+second_space_rel_pos;
				room.broadcast(cmd.substring(pos),"Server");
			}
			break;
			
			case "/send":// format: "/send [username] [message]"
			case "/chat":
			{
				if(cmdSplit.length<2)
				{
					System.err.println("invalid command format");
					System.out.println("/send [username] [message]");
					continue;
				}
				Socket sock=users_list.get(cmdSplit[1]);
				if(sock==null)
				{
					System.err.println("Username doesn't exist");
				}	
				int first_space_pos=cmd.indexOf(" ");
				int second_space_rel_pos=cmd.substring(first_space_pos+1)
					.indexOf(" ");
				int pos=first_space_pos+2+second_space_rel_pos;
					Commands.send(cmd.substring(pos),"Server",sock);
			}
				break;
				
			case "/q":
			case "/quit":
			case "/exit":
				scn.close();
				exit=true;
				Commands.closeServer(rooms, users_list);
				break;
			}	
		}
	}
}

