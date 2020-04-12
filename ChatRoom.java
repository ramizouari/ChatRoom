import java.io.*;
import java.net.*;
import java.util.*;
import commun.*;
import client.*;

//class extending Thread for reading input


public class ChatRoom
{
	public static void main(String args[])
	{
		Scanner scn = new Scanner(System.in);
		String hostName;
		int port=0;
		if(args.length>0)
			hostName=args[0];
		else
		{
			System.out.print("Host name:");
			hostName = scn.nextLine();
		}
		boolean readPortNumber=args.length<=1;
		if(!readPortNumber)
		{
			try
			{
				port = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException e)
			{
				System.err.println("Not a valid port number");
				readPortNumber=true;
			}
		}
		if(readPortNumber)
		{
			System.out.print("Port number");
			port=scn.nextInt();
		}
		Socket sock=null;
		try
		{
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
			scn.nextLine();
			sout=new PrintStream(sock.getOutputStream());
			DataInputStream sin = new DataInputStream(sock.getInputStream());
			boolean nameExists;
			do
			{
				System.out.print("Username: ");
				String str=scn.nextLine();
				sout.println(str);
				nameExists=sin.readBoolean();
				if(nameExists)
					System.out.println("name already exists, try another");
			}while(nameExists);
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
		ClientInputReader in_reader=new ClientInputReader(sock,sout);//stdin reader
		in_reader.start();
		BufferedReader buff_reader=null;
		try
		{
			buff_reader=new BufferedReader(new InputStreamReader
					(sock.getInputStream())); 
			String str="";
			while(!in_reader.getExit())
				{
					str=buff_reader.readLine();
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
