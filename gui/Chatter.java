package gui;

import client.ClientCommandsAlias;
import client.PrivateConversationTracker;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

//created after connecting and choosing room
//this is the window that gives the user the ability to talk with otherss
public class Chatter extends JFrame {

	final private JTextArea messageArea;
	final private JTextField inputField;
	final private Socket sock;
	final private PrivateConversationTracker convTracker;
	private ClientCommandsAlias commandsAlias;
	private boolean exit=false;
	final private InputTracker inTracker;
	public Chatter(String userName,Socket s)
	{
		inTracker = new InputTracker();//tracking user inputs (See InputTracker)
		sock=s;//the user's sockets
		convTracker=new PrivateConversationTracker(userName);//tracking private incoming messages
		commandsAlias=new ClientCommandsAlias(new File("ClientCommands.cfg"));//configuration file for aliases
		inputField=new JTextField();//where the user write his messages
		messageArea=new JTextArea();//where the user get incoming messages
		messageArea.setRows(3);
        final JScrollPane scrollable = new JScrollPane(messageArea);//Pattern Strategy: gives the ability to scroll
		DefaultCaret caret = (DefaultCaret)messageArea.getCaret();
  		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollable.setWheelScrollingEnabled(true);
		JPanel pane = new JPanel();
		messageArea.setEditable(false);//who wants to edit incoming messages?
		pane.setLayout(new BorderLayout());
		pane.add(scrollable,BorderLayout.CENTER);
		pane.add(inputField,BorderLayout.SOUTH);
		inputField.addKeyListener(new KeyAdapter()
				{
					public void keyPressed(KeyEvent event)
					{
						if(event.getKeyCode()==KeyEvent.VK_UP)
						{
							if(!inTracker.isEmpty())
								inputField.setText(inTracker.getCurrentElement());
							if(!inTracker.hasReachedLastElement())
								inTracker.incrementIndex();
						}
						else if(event.getKeyCode()==KeyEvent.VK_DOWN)
						{
							if(!inTracker.isEmpty())
								inputField.setText(inTracker.getCurrentElement());
							if(!inTracker.hasReachedFirstElement())
								inTracker.decrementIndex();
						}
					}
					public void keyTyped(KeyEvent event)
					{
						//if the user clicked (and released) enter after typing messages
						if(event.getKeyChar()==KeyEvent.VK_ENTER)
						{
								if(inputField.getText().matches("^[[:blank:]]*$"))//if there is no input, then return
								return;
							String msg=inputField.getText();//get the user's typed message
							inTracker.add(msg);//add this message to the tracker's history
							if(msg.charAt(0)=='/')//if this message is actually a command or an alias
							{
								String alias=msg.split(" ")[0];//we will suppose that it is an alias to a command
								/*
									- Get what does this command refer to
									- a command is an alias to itself
								 */
								String command=commandsAlias.getCommand(alias);
/*
- /r command a command processed by the client
- it searches if possible for the last private conversation and extracts the
userName of the other user, this command then will be converted to:
/whisper [user_name] [message]
- if no match is found, the command is passed to the server as it is, and
the server will respond by sending an error
 */
								if(command.equals("/r"))//if it is the /r command: the respond command
								{
									if(convTracker.getLastSender()!=null)
										command = "/whisper "+convTracker.getLastSender();
								}
								msg=msg.replaceAll("^/[a-zA-Z]+", command);//converts the alias to a command
							}
                            if(msg.equals("/quit"))// the /quit command for exiting
                            {
                                dispose();
                                exit=true;
                                return;
                            }
							try
							{
								PrintStream sout = new PrintStream(sock.getOutputStream());
								sout.println(msg);//sending message (or command) to the server
								inputField.setText("");//clear inputField
							}
							catch(IOException exc)//connection issue
							{
								System.err.println("Unable to send message");
								JOptionPane.showMessageDialog(Chatter.this,
										exc.getMessage());
                                        Chatter.this.dispose();
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
							JOptionPane.showMessageDialog(Chatter.this,
									e.getMessage());
							return;
						}
						while(!exit)//while the user has not asked to exit the window (or application)
						{
							try
							{
								String str=buff_reader.readLine();
								if(str==null)
									throw new IOException("Connection with Server lost");
/*
	the convTracker will read received message and try to find private messages to track them:
	the userName of the other user will be stored
 */		
								convTracker.track(str);
								messageArea.setText(messageArea.getText()+"\n"
									+str);
							}
							catch(IOException exc)//connection issue
							{
								System.err.println(exc.getMessage());
								JOptionPane.showMessageDialog(Chatter.this,
										"Connection Terminated");
                                        Chatter.this.dispose();//close window
								return;
							}
						}
					}
                });
                this.addWindowListener(
                    new WindowAdapter()
                {
					//invoked if the user asked to exit via the X button
                    public void windowClosing(WindowEvent winEvent)
                    {
                        Chatter.this.dispose();
                    }
                });
		messageWriter.start();//Starts the thread
	}
    public void dispose()//close the window
    {
        super.dispose();
        try
        {
            sock.close();
        }
        catch(IOException e)
        {
            System.err.println("Unable to close socket");
       }
    }
}
