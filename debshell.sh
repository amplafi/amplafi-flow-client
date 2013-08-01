#!/bin/bash

CLASS_PATH=target/amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar 

java  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1043 -cp $CLASS_PATH  org.amplafi.flow.ui.InteractiveShell
 
