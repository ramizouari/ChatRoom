package gui;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import commun.RoomsInfo;

//an action which is invoked when the connect button is clicked
public class ConnectAction implements ActionListener {
    JComponent parent;
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
		if(hostName.getText().equals("")||pseudoname.getText().equals(""))
		{
			JOptionPane.showMessageDialog(parent, "Fill all fields");
			return;
		}
		Socket sock = null;
		try
		{
			sock = new Socket(hostName.getText(),
					(Integer)portNumber.getValue());
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
			JOptionPane.showMessageDialog(parent,e.getMessage());
			return;
		}
		PrintStream sout=null;
		ObjectInputStream sin =null;
		try
		{
			sout = new PrintStream(sock.getOutputStream());
			String name = pseudoname.getText();
			sin = new ObjectInputStream(sock.getInputStream());
			boolean nameExists;
			do
			{
				sout.println(name);
				nameExists=sin.readBoolean();
				if(nameExists)
					name= JOptionPane.showInputDialog
						("pseudoname already exists, please type another name");
				if(name==null)//if user clicked on cancel
				{
					sout.println(name);
					sock.close();
					return;
				}
			}while(nameExists);
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
			return;
        }
		RoomsInfo roomsInfo=null;
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
			roomSelect = new RoomSelector();
		}
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(parent, "Unable to get rooms list");
            return;
		}
		roomSelect.setSize(300,300);
		roomSelect.setVisible(true);
		roomSelect.setTitle("Room Selector");
		int value=roomSelect.getSelectedRoom();
		if(roomSelect.getState()==RoomSelector.CANCELED)
		{
			try
			{
				sock.close();
			}
			catch(IOException e)
			{
				System.err.println("Unable to close socket");
			}
			return;
		}
		sout.println(value);
		MainWindow room= new MainWindow(sock);
		room.setTitle("ChatRoom GUI");
		room.setVisible(true);
		room.setSize(300,300);
    }
}