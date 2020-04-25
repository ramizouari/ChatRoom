# ChatRoom

A little chatroom,that comes with three applications:

1. ChatRoomServer is the Server
2. ChatRoom is the console client
3. ChatRoomGUI is the gui client

- This chatroom divides users to rooms, every users in the same room can talk to each other.
- Before connecting, each user must choose a username that must be unique in the scope of the whole server.
- If a connection with the server has been established and the user has chosen a valid username, a list of rooms each with its users witll be presented to the user, and he will choose to join one or create another room.
- Valid usernames are those who contains only alphanumeric characters and the character _
- The user can get some other functionalities using commands,these commands in the form "/command" like /quit for exiting application, for more information about commands, see the file [COMMANDS.md](./COMMANDS.md)
- The server is launched with a port number as an argument, if -i argument is also given, then the server will accept input from STDIN (The server will act like an administrator)

To compile the 3 applications, just compile the 3 files: ChatRoomServer,ChatRoom and ChatRoomGUI

compile is a bash script to compile project files and copy them with other configuration files to target folders (which are by default set to ../ChatRoomBin/Client and ../ChatRoomBin/Server)

install is a bash script to compile project files, archive the in a JAR archive, then copy them with the other
configuration files to target folders
(which are by default set to ../ChatRoomBin/Client and ../ChatRoomBin/Server)

Tested with JDK-12
Should work down to JDK-8
