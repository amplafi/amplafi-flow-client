#!/bin/bash

CLASS_PATH=target/amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar 

java -cp $CLASS_PATH -DignoreFlows=ResetApiKeysFlow,GetWordpressPluginInfo,GetWordpressPlugin org.amplafi.flow.utils.DSLTestGenerator $@
 
