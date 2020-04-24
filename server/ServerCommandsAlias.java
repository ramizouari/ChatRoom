package server;
import java.util.Set;
import java.util.HashSet;
import java.io.File;

import commun.CommandsAlias;

public class ServerCommandsAlias extends CommandsAlias{
    static private Set<String> commands;
    static
    {
        commands=new HashSet<String>();
        commands.add("/broadcast");//send a global message to all connected users
        commands.add("/postroom");//send a message to all users in a specific rooms
    }
    public ServerCommandsAlias(File f)
    {
        super(f,commands);
    }

}