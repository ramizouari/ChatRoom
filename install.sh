#!/bin/bash
#this script will compile Client and Server files then place each to its target location 
#specified by the variables below:
#It will then create 3 executable jar archives, one for each application
#if you add files to the project that are not dependant to ChatRoomServer.java,ChatRoom.java or ChatRoomGUI.java
#you must add them manually to the script or compile them then  move them manually to the target directory
target_server_location="../ChatRoomBin/Server"
target_client_location="../ChatRoomBin/Client"
old_loc=`pwd`
javac ChatRoomServer.java -d /tmp/ChatRoomBin/Server
javac ChatRoom.java ChatRoomGUI.java -d /tmp/ChatRoomBin
cp ClientCommands.cfg README.md $target_client_location
cp ServerCommands.cfg README.md $target_server_location
(cd /tmp/ChatRoomClient
 jar cvfe $old_loc/$target_client_location/ChatRoom.jar ChatRoom ChatRoom.class `find $target_client_location -maxdepth 1 -mindepth 1 -type d`
 jar cvfe $old_loc/$target_client_location/ChatRoomGUI.jar ChatRoomGUI ChatRoomGUI.class `find -maxdepth 1 -mindepth 1 -type d`
 rm -R ChatRoomBinClient
)
(cd /tmp/ChatRoomBinServer
 jar cvfe $old_loc/$target_server_location/ChatRoomServer.jar ChatRoomServer *.class `find $target_server_location -maxdepth 1 -mindepth 1 -type d`
 rm -R ChatRoomBinServer
)