# ChatRoom

A little chatroom,that comes with three applications:

1. ChatRoomServer is the Server
2. ChatRoom is the console client
3. ChatRoomGUI is the gui client

- This chatroom divides users to rooms, every users in the same room can talk to each other.
- Before connecting, each user must choose a username that must be unique in the scope of the whole server.
- If a connection with the server has been established and the user has chosen a valid username, a list of rooms each with its users witll be presented to the user, and he will choose to join one or create another room.
- The user can get some other functionalities using commands,these commands in the form "/command" like /quit for exiting application, for more information about commands, see the file [COMMANDS.md](./COMMANDS.md)
</a>
- The server is launched with a port number as an argument, if -i argument is also given, then the server will accept input from STDIN (The server will act like an administrator)

Tested with JDK-12
Should work down to JDK-8
