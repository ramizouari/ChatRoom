package commun;
import java.io.*;
import java.util.*;

/*
    This class stores the aliases of commands
    the commandList is set of commands
    the file in parameter is containing the commands as function of aliases 
    (see ClientCommands.cfg and ServerCommands.cfg for more details)
*/
public class CommandsAlias
{
    protected Map<String,String> aliases;
    protected boolean fileReadSuccessfully=true;
    public CommandsAlias(File f,Set<String> commandsList)
    {   
        aliases=new HashMap<String,String>();
        //list of commands that are common between server and client
        aliases.put("/help","/help");
        aliases.put("/rooms","/rooms");
        aliases.put("/users","/users");
        aliases.put("/whisper","/whisper");
        aliases.put("/quit","/quit");
        //add specialized commands 
        commandsList.forEach(command->aliases.put(command,command));
        try
        {   
            String line;
            String T[];
            BufferedReader buff_reader=new BufferedReader(new FileReader(f));
            int lineNumber=0;
            do
            {
                lineNumber++;
                line=buff_reader.readLine();
                if(line==null)
                    break;
                else  if(line.isBlank()||line.charAt(0)=='#')//# for comments and also blank line is ignored
                    continue;
                T=line.split("\t| +");//seperator is a TAB
                try
                {
                    aliases.put(T[0],T[1]);//first column of T for alias, second one for the command
                }
                catch(ArrayIndexOutOfBoundsException e)//a line is corrupted
                {
                    System.err.println("Error while reading ClientCommands.cfg at line "+lineNumber);
                }
            }while(true);
            buff_reader.close();
        }
        catch(IOException e)//if unable to read file
        {
            System.err.println("Unable to read alias file "+f.getName());
            System.err.println("Commands aliases won't be supported");
            fileReadSuccessfully=false;
        }
    }
    public String getCommand(String a)//return the command as function of a
    {
        //if is the alias is a is not found as a key in the aliases map, it will be returned as it is
        String lowerCaseAlias=a.toLowerCase();
        return aliases.getOrDefault(lowerCaseAlias, lowerCaseAlias);
    }
    public boolean isFileSuccessfullyRead()
    {
        return fileReadSuccessfully;
    }
    public static boolean isCommand(String msg)
	{
		return (msg!=null)&&(msg.charAt(0)=='/');
	}
}