package gui;
import client.ClientCommandsAlias;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class MainWindow extends JFrame {

	final private JTextArea messageArea;
	final private JTextField inputField;
	final private Socket sock;
	private ClientCommandsAlias commandsAlias;
    private boolean exit=false;
	public MainWindow(Socket s)
	{
		sock=s;
		commandsAlias=new ClientCommandsAlias(new File("ClientCommands.txt"));
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
							if(inputField.getText().isBlank())//if no input return
								return;
							String msg=inputField.getText();
							if(msg.charAt(0)=='/')
							{
								String alias=msg.split(" ")[0];
								String command=commandsAlias.getCommand(alias);
								if(command!=null)
									msg=msg.replaceAll("^/[a-zA-Z]+", command);		
							}
                            if(msg.equals("/quit"))
                            {
                                dispose();
                                exit=true;
                                return;
                            }
							try
							{
								PrintStream sout = new PrintStream(sock.getOutputStream());
								sout.println(msg);
							}
							catch(IOException exc)//connection issue
							{
								System.err.println("Unable to send message");
								JOptionPane.showMessageDialog(MainWindow.this,
										exc.getMessage());
                                        MainWindow.this.dispose();
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
							JOptionPane.showMessageDialog(MainWindow.this,
									e.getMessage());
							return;
						}
						while(!exit)
						{
							try
							{
								messageArea.setText(messageArea.getText()+"\n"
									+buff_reader.readLine());
							}
							catch(IOException exc)//connection issue
							{
								System.err.println(exc.getMessage());
								JOptionPane.showMessageDialog(MainWindow.this,
										"Connection Terminated");
                                        MainWindow.this.dispose();//close window
								return;
							}
								inputField.setText("");
						}
					}
                });
                this.addWindowListener(
                    new WindowAdapter()
                {
                    public void windowClosing(WindowEvent winEvent)
                    {
                        MainWindow.this.dispose();
                    }
                });
		messageWriter.start();
	}
    public void dispose()
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
