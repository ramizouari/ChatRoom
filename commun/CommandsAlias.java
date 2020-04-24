package commun;
import java.io.*;
import java.util.*;

abstract public class CommandsAlias
{
    protected Map<String,String> aliases;
    protected boolean fileReadSuccessfully=true;
   /* public CommandsAlias(File f) throws IOException,ArrayIndexOutOfBoundsException
    {  
        aliases=new HashMap<String,String>();
         String line;
        String T[];
        BufferedReader buff_reader=new BufferedReader(new FileReader(f));
        do
        {
            line=buff_reader.readLine();
            if(line==null)
                break;
            T=line.split("\t");
            aliases.put(T[0],T[1]);
        }while(true);
        buff_reader.close();
    }*/
    public CommandsAlias(File f,Set<String> commandsList)
    {   
        aliases=new HashMap<String,String>();
        //list of common commands
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
                else  if(line.isBlank()||line.charAt(0)=='#')
                    continue;
                T=line.split("\t");
                try
                {
                    aliases.put(T[0],T[1]);
                }
                catch(ArrayIndexOutOfBoundsException e)
                {
                    System.err.println("Error while reading ClientCommands.cfg at line "+lineNumber);
                    System.err.println("Line content: "+T[0]);
                }
            }while(true);
            buff_reader.close();
        }
        catch(IOException e)
        {
            System.err.println("Unable to read alias file "+f.getName());
            System.err.println("Commands aliases won't be supported");
            fileReadSuccessfully=false;
        }
    }
    public String getCommand(String alias)
    {
        return aliases.getOrDefault(alias, "/unknown");
    }
    public boolean isFileSuccessfullyRead()
    {
        return fileReadSuccessfully;
    }
}