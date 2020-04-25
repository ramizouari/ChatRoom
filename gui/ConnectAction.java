package gui;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

import commun.NameState;
import commun.RoomsInfo;

/*
	An action which is invoked when the connect button is clicked, The executed action will:
	- Verify whether the user filled the form or not, then try to find the correspending server..
	- If the server has been found, it will verify the uniqueness and valideness of the username, if not it will ask 
	for giving another username until giving a unique one
	- Then a RoomSelector dialog will be created which will gives you information about each room and its
	users
	- Finally, if the user choosed a room, a Chatter window will be created and the user will have the
	ability to exchange messages with others
*/
public class ConnectAction implements ActionListener {
    JComponent parent;//the parent component
    JTextField hostName, pseudoname;
    JSpinner portNumber;

    public ConnectAction(JTextField h, JSpinner p, JTextField pseudo, JComponent par) {
        hostName = h;
        pseudoname = pseudo;
        portNumber = p;
        parent = par;
    }

	public void actionPerformed(ActionEvent event)
	{
		if(hostName.getText().isEmpty()||pseudoname.getText().isEmpty())//if the form is not filled
		{
			JOptionPane.showMessageDialog(parent, "Fill all fields");
			return;
		}
		Socket sock = null;//user's socket
		// trying to establish connection with the correspending server
		try 
		{
			sock = new Socket(hostName.getText(),
					(Integer)portNumber.getValue());
		}
		catch(IOException e)//if the connection is unsuccessfull
		{
			System.err.println(e.getMessage());
			JOptionPane.showMessageDialog(parent,e.getMessage());
			return;
		}
		PrintStream sout=null;
		ObjectInputStream sin =null;
		String name = pseudoname.getText();
		try
		{
			sout = new PrintStream(sock.getOutputStream());
			sin = new ObjectInputStream(sock.getInputStream());
			int nameState;
			do
			{
				/*
				The user will send the candidate username to the server
				then the server will respong whether it is valid or not
				Valid means it is unique and not reserved
				Reserved names are "","Server" and any blank string
				 */
				sout.println(name);
				nameState=sin.readInt();//the response of the server 
				String error_msg="";
				switch(nameState)
				{
					case NameState.INVALID:
					error_msg="Not a valid username, use only alphanumeric characters and underscore\n";
					error_msg+="A username must contains at least 3 characters";
						break;
					case NameState.EXISTS:
					error_msg="This name already exists, please type another name";
						break;
					case NameState.RESERVED:
					error_msg="This name is reserved, try another";
						break;
				}
				if(nameState!=NameState.VALID)
					name= JOptionPane.showInputDialog(parent,error_msg);
				if(name==null)//if user clicked on cancel
				{
					sout.println(name);//Don't know why I sent a null
					sock.close();//close the connection
					return;
				}
			}while(nameState!=NameState.VALID);//while the username is not valid asks the user again for another one
		}
		catch(IOException e)// if the communication with the server has been interrupted
		{
			System.err.println(e.getMessage());
			return;
        }
		RoomsInfo roomsInfo=null;//rooms info is a class containing the ID's of rooms and each room's users
		RoomSelector roomSelect=null;
        try
        {
			roomsInfo=(RoomsInfo)sin.readObject();
			roomSelect = new RoomSelector(roomsInfo);//call "default" constructor
		}
		catch(InvalidClassException | ClassNotFoundException e )//error while deserializing
		{
			JOptionPane.showMessageDialog(parent,"Error while loading list of rooms");
			System.err.println(e.getMessage());
			/*
				This constructor will be sent when reading roomsInfo has been unsuccessful
				It will Create a room selector without the list of rooms
				this exception maybe raised if the class won't be found, or the two versions of the class
				between the server and the client differ
			*/
			roomSelect = new RoomSelector();
		}
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(parent, "Unable communicte with the server");
            return;
		}
		roomSelect.setSize(300,300);
		roomSelect.setVisible(true);
		roomSelect.setTitle("Room Selector");
		int value=roomSelect.getSelectedRoom();//returns the value of the selected room
		if(roomSelect.getState()==RoomSelector.CANCELED)//if the user canceled the selection of the room
		{
			try
			{
				sock.close();//close connection with the server
			}
			catch(IOException e)
			{
				System.err.println("Unable to close socket");
			}
			return;
		}
		sout.println(value);//send the room number to the server
		Chatter room= new Chatter(name,sock);//creates a Chatter window
		room.setTitle("ChatRoom GUI");
		room.setVisible(true);
		room.setSize(300,300);
    }
}