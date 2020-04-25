import java.io.*;
import java.net.*;
import java.util.*;
import client.*;
import commun.*;

public class ChatRoom
{

	/*
		the main function accepts up to 2 arguments (optional)
		the first argument= localhost name
		the second argument= port number
	*/
	public static void main(String args[])
	{
		Scanner scn = new Scanner(System.in);
		String hostName;
		int port=0;
		if(args.length>0)//if first argument is given: localhost name
			hostName=args[0];
		else//else read from stdin
		{
			System.out.print("Host name:");
			hostName = scn.nextLine();
		}
		
		//readPortNumber asks whether to get the port number from stdin or not
		boolean readPortNumber=args.length<=1;//is the port number not given as an argument?
		if(!readPortNumber)//if it is given as an argument
		{
			try
			{
				port = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException e)//if args[1] is not a number
			{
				System.err.println("Not a valid port number");
				readPortNumber=true;
			}
		}
		if(readPortNumber)
		{
			//get port number from stdin
			System.out.print("Port number");
			port=scn.nextInt();
			scn.nextLine();//to remove the caracter \n from buffer
		}
		Socket sock=null;
		try
		{
			//establish connection with server
			sock=new Socket(hostName,port);
		}
		catch(IOException e)
		{
			System.err.println("Unable to connect to server");
			System.exit(1);
		}	
		System.out.println("Connection success");
		PrintStream sout=null;
		String name="";
		try
		{
			sout=new PrintStream(sock.getOutputStream());//socket out
			ObjectInputStream sock_in = new ObjectInputStream(sock.getInputStream());//socket in
			int nameState;
			do
			{
				System.out.print("Username: ");
				name=scn.nextLine();
				sout.println(name);
				nameState=sock_in.readInt();
				switch(nameState)
				{
					case NameState.INVALID:
						System.out.print("This username is invalid, you can only use alphanumeric characters: ");
						System.out.println(" a-z,A-Z,0-9 and the underscore");
						System.out.println("the username must contains at least 3 characters");
						System.out.println("Example: -fofo64\n\t- fofo_here\n\t- gg_izi_20");
						break;
					case NameState.EXISTS:
						System.out.println("This name already exists or it is reserved, try another one");
						break;
					case NameState.RESERVED:
						System.out.println("This name is reserved, try another");
				}
			}while(nameState!=NameState.VALID);
			/*BufferedReader buff_reader=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String roomsInfo;
			do
			{
				roomsInfo=buff_reader.readLine();
				System.out.println(roomsInfo);
			}while(!roomsInfo.equals(""));*/
			RoomsInfo roomsInfo=(RoomsInfo)sock_in.readObject();
			System.out.print(roomsInfo.toString());
			System.out.print("Room number: ");
			sout.println(scn.nextInt());
		}
		catch(IOException e)
		{
			System.err.println("Unable to reach server");
			System.err.println(e.getMessage());
		}
		catch(Exception e)
		{
			System.err.println("Other exception: ");
			System.err.println(e.getMessage());	
		}

		/*
			ClientInputReader is a thread for accepting input from stdin
		*/
		PrivateConversationTracker convTracker=new PrivateConversationTracker(name);
		ClientInputReader in_reader=new ClientInputReader(sock,sout,convTracker);
		in_reader.start();
		BufferedReader buff_reader=null;
		try
		{
			buff_reader=new BufferedReader(new InputStreamReader
					(sock.getInputStream())); 
			String str="";
			while(!in_reader.getExit())//while the client is not asking to exit
				{
					str=buff_reader.readLine();//read input from server
					if(str!=null)
						System.out.println(str);
					convTracker.track(str);
				}
			
		}
		catch(IOException e)
		{
			System.err.println("Unable to receive message");
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				scn.close();
				if(buff_reader!=null)
					buff_reader.close();
				sock.close();
			}
			catch(IOException err)
			{
				System.err.println("Error: unable to close connection");
				System.exit(1);
			}
		}
	}
}
