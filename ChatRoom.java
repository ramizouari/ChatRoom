import java.io.*;
import java.net.*;
import java.util.*;
import client.*;


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
		try
		{
			sout=new PrintStream(sock.getOutputStream());//socket out
			DataInputStream sock_in = new DataInputStream(sock.getInputStream());//socket in
			boolean nameExists;
			do
			{
				System.out.print("Username: ");
				String str=scn.nextLine();
				sout.println(str);
				nameExists=sock_in.readBoolean();
				if(nameExists)
					System.out.println("This name already exists or it is reserved, try another one");
			}while(nameExists);
			BufferedReader buff_reader=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String roomsInfo;
			do
			{
				roomsInfo=buff_reader.readLine();
				System.out.println(roomsInfo);
			}while(!roomsInfo.equals(""));
			System.out.print("Room number: ");
			sout.println(scn.nextInt());
		}
		catch(IOException e)
		{
			System.err.println("Unable to reach server");
		}
		catch(Exception e)
		{
			System.err.println("Other exception: ");
			System.err.println(e.getMessage());	
		}

		/*
			ClientInputReader is a thread for accepting input from stdin
		*/
		ClientInputReader in_reader=new ClientInputReader(sock,sout);
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
