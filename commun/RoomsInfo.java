package commun;
import java.io.Serializable;
import java.util.*;

/*
    this class is especially made for storing the list of rooms ids each with its users' names
 */
public class RoomsInfo implements Serializable{
    static final long serialVersionUID=1;
    //contains a map from room number to its users
    private Map<Integer,Set<String>> rooms;
    public RoomsInfo(Map<Integer,Set<String>> rooms)
    {
        this.rooms=rooms;
    }
    public Map<Integer,Set<String>> getInfo()
    {
        return rooms;
    }
    public String toString()
    {
        if(rooms.isEmpty())
            return "no room registered\n";
        StringBuilder R=new StringBuilder("Rooms: \n");
        rooms.forEach((roomName,users)->
        {
            R.append("\t"+"Room "+roomName+"\n");
            users.forEach(username->R.append("\t\t"+username+"\n"));
        });
        return R.toString();
    }
}