## Shell ##

## Compile ## 

mvn package

## Run ##

To run as an ant target:

> ant FAdmin -Dargs="shell"

Note: if the src code for the amplafi-flow-client is present in the ./src folder this will be used for additional documentation on the DSL language



## Core Concepts ##

At its core the admin tool shell is an interactive interface to the Admin tool itself. 
Therefore it is able to run the admin tool scripts in the same way as if you had run them from the command line. So 

> ant FAdmin -Dargs="scriptName arg1=<arg1> arg2=<arg2>"

within the shell becomes.

> call scriptName arg1=<arg1> arg2=<arg2>

The admin tool scripts are written in a groovy DSL so and since the shell is also an admin tool script it is also able to call methods in that dsl directly. 

The shell provides a function to run groovy statements interactively including all the functions in the DSL language using the 'g or rungroovy' command. In fact most of the 
commands in the shell are simply aliases for this. 

so 

> call scriptName arg1=<arg1> arg2=<arg2>

could also be achieved using the inbuilt callScript() method as 

> rungroovy callScript("scriptName",["arg1":"arg1","arg2:"arg2"])


## Session ##

The shell REPL is evaluated with the context of the same. FlowTestDSL object, so it will remember any changes to the local api key, api version, server details. etc between commands,
since these are properties in the FlowTestDSL object.

It would also be possible to run valid groovy such as 

> g String a = getKey()

However although this would define a temporary variable "a" whilest evaluating that statement, the temporary variable would be immediately discarded. 
If you watch to store a value between shell commands you can use the "stash" map.

e.g.

> g stash["real_key"] = getKey()

> g setKey("some new key")

// Now a new key has been set

> g setKey(stash["real_key"])

// Now the original key has been restored.

To view the current contents of the stash map it is enough to enter:

> g stash

All returned values from admin scripts are stored in the field stash["LAST_RETURN"] by default. Returned values from groovy statements are not stored automatically.


## Documentation ##

The help function or ? alias provides a lot of information on the command scripts and the groovy DSL. It will grab the DSL javadoc comments if it can find the source code. 


## Recommended Usage ## 

Generally do not try to do anything complicated with direct groovy statements. They only support one line of code and will be lost when the shell closes. 
If there is a complicated sequence of commands place them in a groovy file in 

\farreaches-customer-service-client\src\main\resources\commandScripts

Add a description line at the top 

e.g.

description "myScript", "This is my script", [];

reopen the shell and this will be available in "la" list. No compilation is needed.


## Example Session ##






