## Admin Tool ##

The FAdmin.sh/FAdmin.bat offers a simple and extensible framework for running scripts to query the server flows.
Features include, a simple and clear DSL based on groovy which can be mixed with Java and groovy code to create clear and versitile scripts.

Scripts do not need re-compilation of the tool, just edit them and run the tool, they will be re-loaded on-the-fly.

Scripts do not need registering with the tool, just drop a valid script into the scripts folder and it will be found and added to the available commands.

Scripts much include a self documentation line at the top, with script name and description, this is displayed automatically when enumerating scripts.
This prevents collecting a large number of script that noone knows how to use.

APIKEY, Host, Port, version etc. Can be passed in from command line.
Otherwise framework will prompt for them on first run and store them in your system preferences.
These can be overridden anytime or discarded with the -x option.

APIKEY, Host, Port, version may also be overridden in the command scripts themselves, in case of commands that must only be run on a particular host
or "su" commands where the API key needs to change programatically.

DSL offers easy programmatic access to the return JSON data from the server.
DSL offers pretty print and other commands.

### Setup ###

mvn  -Dmaven.test.skip=true package

### Command Line Options ###
-l                    List Scripts

-apiv <arg>           API version

-host <arg>           Host address

-key <arg>            API key

-port <arg>           Service port

-x                    Don't use cached server credentials

### Simple Usage ###

Running ./FAdmin.sh without parameters will display the usage.

To list the currently available commands enter :

    ./FAdmin.sh -l
    
To list In-built Scripts and show all scripts path :

    ./FAdmin.sh -L

To run one of the commands enter the command name :

    ./FAdmin.sh example <param1Name>=<param1Value> <param2Name>=<param2Value> ...

To reset host, port, api version and key :

    ./FAdmin.sh -x

### Scripting Reference ###

Scripts are placed in:

/src/main/resources/commandScripts

Any script placed here will be loaded on startup.

#### Example Script ####
    // Java comments are ignored.
    // The description line must be the first line in the script.
    
    description "example", "Just an example script"

    // You can set certain globals in the script if you want.
    // These will affect all susequent requests
    // Don't normally do this though. As the default ones are good.

    // setHost("http://www.google.com");
    // setPort("90");
    // setApiVersion("apiv12");
    // setKey("newkey");


    //Access to command line parameters
    if (params&&params["param2"]){

        println "------------"+ params["param2"]
    } 

    if (params&&params["param1"]){
        println "------------"+params["param1"]

    }


    // Make a request to the server
    request("EligibleExternalServiceInstancesFlow", ["eligibleExternalServiceInstanceMap":"bogusData","eligibleExternalServiceInstances":"bogusData","fsRenderResult":"json"]);


    // Pretty Print The Response
    prettyPrintResponse();


    // Access the response data
    def data = getResponseData();

    // You can define your own variables if you need to.
    def row1LookupKey = null;

    // In this case the response data should be a JSONArray but if an error is returned then it would be a JSONObject
    if (data instanceof JSONArray){
       // Access the first row in the array and get its "lookupKey" property
       row1LookupKey = data.get(0).get("lookupKey")
    }


    // print the result
    println "The lookup key in the first row of the response is: <<" + row1LookupKey + ">>"



### DSL Function Reference ###

description (String name, String description )       - Sets the name of the script. This is how it is invoked so it cannot have spaces etc. Description also set for list command.

request(String flowName, Map<String,String> params); - calls the flow with the parameter map, valuses may be JSON strings.

prettyPrintResponse()                                - pretty prints the last response JSON

getResponseData()                                    - Gets response data as a org.amplafi.json.JSONArray or org.amplafi.json.JSONObject


#### Advanced Functions ###

These are generally not needed by normal scripts as these are set from the command line or will be prompted for

setHost(String host)                    - sets the host for all subsequent requests
setPort(String port)                    - sets the port for all subsequent requests
setApiVersion(String apiVersion)        - sets the api for all subsequent requests
setKey(String apiKey)                   - sets the key for all subsequent requests


### open port and listens request ###

openPort method : The method is "blocking" until a request is received or the timeout occurs
/**
 * The method is to open a port and listens request.
 * @param portNo is port number
 * @param timeOutSeconds is time out seconds
 * @param doNow is the request in script
 * @param handleRequest is the handle method when recieved a request
 */
 public void openPort(int portNo, int timeOutSeconds, Closure doNow, Closure handleRequest)


openPort script like blow:
// This line must be the first line in the script
description "openPort", "Open port"

println("Call open port");
openPort(9090, 20,{ println("In do now") },
{ request ->

        println("In handle " + request.getParameter("param1")) 

});


Run command "FAdmin <openPort script>"


## TODO ##

1. Document Command Line Options
2. Document Access to command line parameters
3. Remove un-needed parameters from AdminToolCommandLineOptions (order)
4. Add a -L option that will also list the script file location
5. In the scripts error report change file path from absolute to relative(If script is not under the project file)
6. Add -f <filename> option so that an adhoc script can be run without putting it in the scripts folder.
7. Add ability for one script to call another. 


### automatic parameter validation and default value setup from parameter specification ###

description method in dsl:
/**
 * The description of the name.
 *@param name - the name to description
 *@param description - the name 's description
 *@param usage is the usage of the script
 *@throw EarlyExitException  if it just need description
 */
public void description (String name, String description, String usage)

Add params usage in script like below:
description "example", "Just an example script", [paramDef("param1","test param1",true,"100"),
                                                    paramDef("param2","test param2",false,"100")]

paramDef method in dsl:
/**
 * This method returns a ParameterUsge.
 * @param name is name of the param
 * @param description is description of the param
 * @param optional is optional of the param
 * @param defaultValue is default value of the param
 * @return a ParameterUsge
 */                                                 
ParameterUsge paramDef(String name,String description,boolean optional,Object defaultValue)

Run command line "FAdmin example param1=dog param2=cat" will generate script line:
def param1 = dog;def param2 = cat;
Then user can use param1 and param2 in script.Just like 'println param1'.

Run command line "FAdmin example param1=dog" will throw an exception:
Parameter param2 must be supplied.
Because param2 is not optional.

Run command line "FAdmin example param2=cat" will generate script line:
def param1 = 100;def param2 = cat;
Then user can use param1 and param2 in script.
When param optional is true and user does not set the param value, script use the default value.


### automatic generation of parameter usage doc from description ###

description method in dsl:
/**
 * The description of the name.
 *@param name - the name to description
 *@param description - the name 's description
 *@param usage is the usage of the script
 *@throw EarlyExitException  if it just need description
 */
public void description (String name, String description, String usage)

Add params usage in script like below:
description "example", "Just an example script", [paramDef("param1","test param1",true,"100"),
                                                    paramDef("param2","test param2",false,"200")]

paramDef method in dsl:
/**
 * This method returns a ParameterUsge.
 * @param name is name of the param
 * @param description is description of the param
 * @param optional is optional of the param
 * @param defaultValue is default value of the param
 * @return a ParameterUsge
 */                                                 
ParameterUsge paramDef(String name,String description,boolean optional,Object defaultValue)

Run command line "FAdmin -help example" will print usage:
Script Usage:
param1 = <test param1> , optional , defaultValue = 100
param2 = <test param2> , required , defaultValue = 200
                                                    

