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
		SpinnerNumberModel portModel=new SpinnerNumberModel();
		portModel.setMaximum(65535);//largest port number
		portModel.setValue(7500);
		portModel.setMinimum(0);
		SpinnerNumberModel roomNumberModel=new SpinnerNumberModel();
		roomNumberModel.setMinimum(0);
		portInput=new JSpinner(portModel);
		nameInput=new JTextField();
		exitButton=new JButton("Exit");
		connectButton=new JButton("Connect");
		JPanel pane=new JPanel(new GridLayout(5,1));
		JPanel buttonsPane=new JPanel();
		buttonsPane.setLayout(new BoxLayout(buttonsPane,BoxLayout.X_AXIS));
		buttonsPane.add(connectButton);
		buttonsPane.add(exitButton);
		hostLabel.setLabelFor(hostInput);
		portLabel.setLabelFor(portInput);
		nameLabel.setLabelFor(nameInput);
		pane.add(hostLabel);
		pane.add(hostInput);
		pane.add(portLabel);
		pane.add(portInput);
		pane.add(nameLabel);
		pane.add(nameInput);
		pane.add(buttonsPane);
		setContentPane(pane);
		exitButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.exit(0);
					}
				});
		connectButton.addActionListener(new ConnectAction(hostInput,
				portInput,nameInput ,pane));
		
	}
}
