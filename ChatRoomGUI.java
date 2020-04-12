import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

//launcher dialog that asks for hostname, portnumber, pseudoname and room number
class Launcher extends JDialog
{
	private JLabel hostLabel,portLabel,nameLabel,roomLabel;
	private JTextField hostInput,nameInput;
	private JSpinner portInput,roomInput;
	private JButton connectButton,cancelButton;
	public Launcher()
	{
		hostLabel=new JLabel("Host name: ");
		portLabel=new JLabel("Port number: ");
		nameLabel=new JLabel("Pseudoname: ");
		roomLabel=new JLabel("Room number: ");
		hostInput=new JTextField();
		SpinnerNumberModel portModel=new SpinnerNumberModel();
		portModel.setMaximum(65535);//largest port number
		portModel.setValue(7500);
		portModel.setMinimum(0);
		SpinnerNumberModel roomNumberModel=new SpinnerNumberModel();
		roomNumberModel.setMinimum(0);
		portInput=new JSpinner(portModel);
		nameInput=new JTextField();
		roomInput=new JSpinner(roomNumberModel);
		cancelButton=new JButton("Cancel");
		connectButton=new JButton("Connect");
		JPanel pane=new JPanel(new GridLayout(5,1));
		JPanel buttonsPane=new JPanel();
		buttonsPane.setLayout(new BoxLayout(buttonsPane,BoxLayout.X_AXIS));
		buttonsPane.add(connectButton);
		buttonsPane.add(cancelButton);
		hostLabel.setLabelFor(hostInput);
		portLabel.setLabelFor(portInput);
		nameLabel.setLabelFor(nameInput);
		roomLabel.setLabelFor(roomInput);
		pane.add(hostLabel);
		pane.add(hostInput);
		pane.add(portLabel);
		pane.add(portInput);
		pane.add(nameLabel);
		pane.add(nameInput);
		pane.add(roomLabel);
		pane.add(roomInput);
		pane.add(buttonsPane);
		setContentPane(pane);
		cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.exit(0);
					}
				});
		connectButton.addActionListener(new ConnectAction(hostInput,
				portInput,nameInput,
				roomInput
				,pane
				));
		
	}
}

//an action which is invoked when the connect button is clicked
class ConnectAction implements ActionListener
{
	JComponent parent;
	JTextField hostName,pseudoname;
	JSpinner roomNumber,portNumber;
	public ConnectAction(JTextField h,JSpinner p,
			JTextField pseudo, JSpinner room,JComponent par)
	{
		hostName=h;
		pseudoname=pseudo;
		portNumber=p;
		roomNumber=room;
		parent=par;
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
		try
		{
			PrintStream sout = new PrintStream(sock.getOutputStream());
			String name = pseudoname.getText();
			DataInputStream sin = new DataInputStream(sock.getInputStream());
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
			
			sout.println((Integer)roomNumber.getValue());
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
			return;
		}
		ChatRoomGUI room= new ChatRoomGUI(sock);
		room.setVisible(true);
		room.setSize(300,300);
		
	}
}


public class ChatRoomGUI extends JFrame {

	final private JTextArea messageArea;
	final private JTextField inputField;
	final private Socket sock;
	public ChatRoomGUI(Socket s)
	{
		sock=s;
		inputField=new JTextField();
		messageArea=new JTextArea();
		messageArea.setRows(3);
		JScrollPane scrollable = new JScrollPane(messageArea);
		JPanel pane = new JPanel();
		messageArea.setEditable(false);
		pane.setLayout(new BorderLayout());
		pane.add(scrollable,BorderLayout.CENTER);
		pane.add(inputField,BorderLayout.SOUTH);
		inputField.addKeyListener(new KeyAdapter()
				{
					public void keyTyped(KeyEvent event)
					{
						if(event.getKeyChar()==KeyEvent.VK_ENTER)//if the user clicked on enter after typing message
						{
							if(inputField.getText().equals(""))//if no input return
								return;
							try
							{
								PrintStream sout = new PrintStream(sock.getOutputStream());
								sout.println(inputField.getText());
							}
							catch(IOException exc)//connection issue
							{
								System.err.println("Unable to send message");
								JOptionPane.showMessageDialog(ChatRoomGUI.this,
										exc.getMessage());
								ChatRoomGUI.this.dispose();
								return;
							}
						}
					}
				});
		setContentPane(pane);
		//Thread for writing received messages
		Thread messageWriter=new Thread(
				new Runnable()
				{
					public void run()
					{
						BufferedReader buff_reader=null;
						try
						{
							buff_reader=new BufferedReader(new 
									InputStreamReader(sock.getInputStream()));
						}
						catch(IOException e)
						{
							JOptionPane.showMessageDialog(ChatRoomGUI.this,
									e.getMessage());
							return;
						}
						while(true)
						{
							try
							{
								messageArea.setText(messageArea.getText()+"\n"
									+buff_reader.readLine());
							}
							catch(IOException exc)//connection issue
							{
								System.err.println("Unable to receive message");
								JOptionPane.showMessageDialog(ChatRoomGUI.this,
										exc.getMessage());
								ChatRoomGUI.this.dispose();//close window
								return;
							}
								inputField.setText("");
						}
					}
				});
		messageWriter.start();
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Launcher launcher=new Launcher();
		launcher.setSize(360,150);
		launcher.setVisible(true);
	}
	

}
