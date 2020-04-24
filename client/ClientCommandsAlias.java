package client;
import commun.CommandsAlias;
import java.util.Set;
import java.util.HashSet;
import java.io.File;

public class ClientCommandsAlias extends CommandsAlias{
    static private Set<String> clientCommands;
    static
    {
        clientCommands=new HashSet<String>();
        //adding commands specific to Client
        clientCommands.add("/room");
        clientCommands.add("/user");
    }
    public ClientCommandsAlias(File f)
    {
        super(f,clientCommands);
    }
}