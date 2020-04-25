package client;

import java.util.regex.*;

/*
    Class for tracking private conversations,It stores the last user that the current user was privately
    talking to..
*/
public class PrivateConversationTracker {
    String lastSender=null;
    String userName;
    public PrivateConversationTracker(String user)
    {
        userName=user;
    }
    public void track(String msg)
    {
        if(msg==null)
            return;
        Pattern pat=Pattern.compile("^<<.*>>");
        Matcher match=pat.matcher(msg);
        if(!match.find())//if the message is not private
            return;
        String r=msg.substring(2,match.end()-2);//get the username of the other party
        if(!r.equals("Server")&&!r.equals(userName))
            lastSender=r;
    }
    public String getLastSender()
    {
        return lastSender;
    }
}