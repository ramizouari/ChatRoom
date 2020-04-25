package gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
launcher dialog that asks for hostname, portnumber, pseudoname
it allows for more than one connection provided that the pseudoname is valid
*/
public class Launcher extends JDialog
{
	private JLabel hostLabel,portLabel,nameLabel;
	private JTextField hostInput,nameInput;
	private JSpinner portInput;
	private JButton connectButton,exitButton;
	public Launcher()
	{
		hostLabel=new JLabel("Host name: ");
		portLabel=new JLabel("Port number: ");
		nameLabel=new JLabel("Pseudoname: ");
		hostInput=new JTextField();
		//Setting model for port numbers
		SpinnerNumberModel portModel=new SpinnerNumberModel();
		portModel.setMaximum(65535);//largest port number
		portModel.setValue(7500);
		portModel.setMinimum(0);
		//Setting model for room numbers
		SpinnerNumberModel roomNumberModel=new SpinnerNumberModel();
		roomNumberModel.setMinimum(0);
		portInput=new JSpinner(portModel);
		nameInput=new JTextField();
		exitButton=new JButton("Exit");
		connectButton=new JButton("Connect");
		JPanel formPanel=new JPanel(new GridLayout(5,1));//panel that hold the form
		JPanel buttonsPanel=new JPanel();//panel that holds the two buttons
		//Setting layout for the buttonsPanel
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
		buttonsPanel.add(connectButton);
		buttonsPanel.add(exitButton);
		//Setting layout for formPanel
		hostLabel.setLabelFor(hostInput);
		portLabel.setLabelFor(portInput);
		nameLabel.setLabelFor(nameInput);
		formPanel.add(hostLabel);
		formPanel.add(hostInput);
		formPanel.add(portLabel);
		formPanel.add(portInput);
		formPanel.add(nameLabel);
		formPanel.add(nameInput);
		formPanel.add(buttonsPanel);
		setContentPane(formPanel);
		//Adding action listenerss
		exitButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.exit(0);
					}
				});//action invoked if exit button is clicked
		connectButton.addActionListener(new ConnectAction(hostInput,
				portInput,nameInput ,formPanel));//Action invoked if connect button is clicked
		
	}
}
