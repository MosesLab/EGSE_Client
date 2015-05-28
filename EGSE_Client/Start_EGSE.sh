#!/bin/sh -e
#
#Run ground station Server and Client modules

server="/home/moses/NetBeansProjects/EGSE_Server/EGSE_Server/dist/EGSE_Server.jar"
x_term="/home/moses/NetBeansProjects/EGSE_Client/EGSE_launch_xterm/dist/Debug/GNU-Linux-x86/egse_launch_xterm"
client="/home/moses/NetBeansProjects/EGSE_Client/EGSE_Client/dist/EGSE_Client.jar"


java -jar $server &
#xterm -e "java -jar $server; bash" &
#$x_term &
java -jar $client &
#xterm -e "java -jar $client; bash" &

exit 0
