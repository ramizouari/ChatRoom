package server;
import java.net.*;


//This class contains the information of a user: his username and the socket
//A one to one correspendence between each socket and each username is assumed
public class User {
    private Socket sock;
    private String username;
    public User(String name,Socket s)
    {
        sock=s;
        username=name;
    }
    public String getUserName()
    {
        return username;
    }
    public Socket getSocket()
    {
        return sock;
    }

    //this method will assume the one to one correspendence
    public boolean equals(Object o)
    {   
        if(o instanceof User)
            return ((User)o).username.equals(username);
        else if(o instanceof String)
            return o.equals(username);
        else if(o instanceof Socket)
            return o.equals(sock);
        return false;
    }
    
    //this method will also assume the one to one correspendence
    public int hashCode()
    {
        return username.hashCode();
    }
}