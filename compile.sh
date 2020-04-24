#!/bin/bash
#this script will compile Client and Server files place each to its target location 
#specified by the variables below:
#if you add files to the project that are dependant to ChatRoomServer.java,ChatRoom.java or ChatRoomGUI.java
#you must add them manually to the script or compile them then  move them manually to the target directory
target_server_location="../ChatRoomBin/Server"
target_client_location="../ChatRoomBin/Client"
javac ChatRoomServer.java -d $target_server_location
javac ChatRoom.java ChatRoomGUI.java -d $target_client_location
cp ClientCommands.cfg $target_client_location
cp ServerCommands.cfg $target_server_location
