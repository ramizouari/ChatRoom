package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import commun.RoomsInfo;

/*
    RoomSelector is a dialog that gives the list of rooms with their respective users and waits for
    the user to give whatever room he wants to join
 */
public class RoomSelector extends JDialog {
    public static final int SUCCESS=0;//if user joined a room
    public static final int CANCELED=1;//if user canceled
    final private JSpinner roomInput;
    final private JButton joinButton;
    final private JButton cancelButton;
    final private JLabel numberOfRoomsLabel;
    private JTree tree;
    private int state;

    //generate a tree list of each room
    private DefaultMutableTreeNode generateRoomsTree(RoomsInfo roomsInfo)
    {
        DefaultMutableTreeNode root=new DefaultMutableTreeNode("root");
		roomsInfo.getInfo().forEach((id,users)->
		{
			DefaultMutableTreeNode node= new DefaultMutableTreeNode("Room "+id);
			users.forEach(username->node.add(new DefaultMutableTreeNode(username)));
			root.add(node);
		});
        return root;
    }

    //the "default" constructor that is invoked if the deserialization of RoomsInfo has been successful
    public RoomSelector(RoomsInfo roomsInfo)
    {
        /*unless the button join is clicked, any other scenario that causes the dialog to exit will 
         will cancel the connection*/
        state=CANCELED;
        //Creating elements
        JPanel panel =new JPanel();
        JPanel southPanel=new JPanel();
        roomInput=new JSpinner();
        joinButton =new JButton("Join");
        cancelButton=new JButton("Cancel");
        TreeNode root = generateRoomsTree(roomsInfo);
        numberOfRoomsLabel=new JLabel("Number of rooms: "+root.getChildCount());
       tree=new JTree(root);
       tree.setRootVisible(false);
        //editing spinner model
        SpinnerNumberModel spinnerModel=new SpinnerNumberModel();
        spinnerModel.setMinimum(0);
        spinnerModel.setValue(0);
        roomInput.setModel(spinnerModel);
        //creating boxlayout for the button and spinner
       BoxLayout southLayout=new BoxLayout(southPanel,BoxLayout.X_AXIS);
       southPanel.setLayout(southLayout);
        southPanel.add(roomInput);
        southPanel.add(joinButton);
        southPanel.add(cancelButton);
        //editing dialog layout
        panel.setLayout(new BorderLayout());
        panel.add(tree,BorderLayout.CENTER);
        panel.add(numberOfRoomsLabel,BorderLayout.NORTH);
        panel.add(southPanel,BorderLayout.SOUTH);
        setContentPane(panel);
        //setting dialog modal (it blocks other windows)
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        joinButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state=SUCCESS;
                RoomSelector.this.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                RoomSelector.this.dispose();
            }
        });
    }


    //this constructor invoked if an exception occured while deserializing RoomsInfo
    public RoomSelector()
    {
        /*unless the button join is clicked, any other scenario that causes the dialog to exit will 
         will cancel the connection*/
        state=CANCELED;
        //Creating elements
        JPanel panel =new JPanel();
        JPanel southPanel=new JPanel();
        roomInput=new JSpinner();
        joinButton =new JButton("Join");
        cancelButton=new JButton("Cancel");
        numberOfRoomsLabel=new JLabel("Error getting informations about rooms");
        JLabel errorLabel=new JLabel("An error occured while loading the list of rooms");
        //editing spinner model
        SpinnerNumberModel spinnerModel=new SpinnerNumberModel();
        spinnerModel.setMinimum(0);
        spinnerModel.setValue(0);
        roomInput.setModel(spinnerModel);
        //creating boxlayout for the button and spinner
       BoxLayout southLayout=new BoxLayout(southPanel,BoxLayout.X_AXIS);
       southPanel.setLayout(southLayout);
        southPanel.add(roomInput);
        southPanel.add(joinButton);
        southPanel.add(cancelButton);
        //editing dialog layout
        panel.setLayout(new BorderLayout());
        panel.add(numberOfRoomsLabel,BorderLayout.NORTH);
        panel.add(errorLabel,BorderLayout.CENTER);
        panel.add(southPanel,BorderLayout.SOUTH);
        setContentPane(panel);
        //setting dialog modal (it blocks other windows)
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        joinButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state=SUCCESS;
                RoomSelector.this.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                RoomSelector.this.dispose();
            }
        });
    }
    public int getState()
    {
        return state;
    }
    public int getSelectedRoom()
    {
        return (Integer)roomInput.getValue(); 
    }
}