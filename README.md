## Getting Started ##

The amplafai flow client is a tool for sending http requests to a farreach.es API server.
During development it is normal to use a local server running on port 8080 and to edit your hosts file so that sandbox.farreach.es is mapped to 127.0.0.1.

To make any API calls you will need an API key. This will be generated the first time the wordpress plugin connects to the server. So you will need to install wordpress and the farreaches-wp-plugin to do this. 
To do that should follow the instructions at https://github.com/farreaches/farreaches-wp-plugin

When this is finished, the key that you need will be visible in the diagnostics screen for the plugin (It is a long hex string like this: ampcb_0cf02bdcd1218c9f5556b632640749a53946923cb26d0e90d2b9cf2300280497).

## Running the client manually ##

Run: 

    mvn package

This will create a jar file:
target/amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar

### To list available flows ###

     java -jar amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar -key <api_key>  -host http://sandbox.farreach.es -port 8080 -apiv apiv1 -desc

### To describe a flow ###

     java -jar amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar -key <api_key>  -host http://sandbox.farreach.es -port 8080 -apiv apiv1 -desc -flow <flowname>

### To call a flow ###

     java -jar amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar -key <api_key>  -host http://sandbox.farreach.es -port 8080 -   apiv apiv1 -flow <flowname> -D<param_name>=<value> D<param_name>=<value> ...
 
## Running Tests ##
 
Tests need to be run against a live instance of the farreaches API server.
This can be run from the farreaches-wp-plugin project (see above) with the ant target server-run
 
To run tests simply run: 
 
    mvn test
 
However if no tests are run you should check for test exclusions in the pom.xml under:

    <plugin>
	    <artifactId>maven-surefire-plugin</artifactId>
	         <configuration>
					    <excludes>
     ...
 
 
 To run the tests against the local sandbox server you will need to configure the sanbox details in the test configuration section of the pom.xml, here:
 
     <systemPropertyVariables>
        <!-- place your API key here -->		                    
        <key>ampcb_e1446aa0e3e46427b591fa044c5f51c57989e393b66269140af68709e1da228e</key>
        <host>http://sandbox.farreach.es</host>
        <port>8080</port>                         
     </systemPropertyVariables>

## Ingored Flows ##

Once we have identified that a flow is broken, it is no longer valuable to include it in the tests, except for the developer who is fixing it.
So it is possible to configure the tests to ignore certain flows.

Simply list them in comma separated form in the ignoreFlows system property which can be configured for tests in the pom.xml like this.

    <systemPropertyVariables>
        <!-- List the flows that you want to ignore here  -->		                                     
        <ignoreFlows>PermanentApiKey,CategoriesList,GetWordpressPlugin,GetWordpressPluginInfo</ignoreFlows>


## Testing DSL ###
A testing Domain Specific Language (DSL) has been added to the project. This is designed to be a highly concise and efficient way to write a simple test script for the server.  Scripts may be written in any folder below src/test/resources/testscripts. Here is an example of what a script looks like:

__________________________________________________________________________

    request('HelloFlow',['param1':'dog','param2':'cat']); 
    expect("""
    {	"validationErrors":{
         "flow-result":{
            "flowValidationTracking":[
               {
                  "key":"MissingRequiredTracking",
                  "parameters":[
                     "HelloFlow"
                  ]
               },
               {
                  "key":"flow.definition-not-found",
                  "parameters":[
                  ]
               }
            ]
         }
      }
    }
    """);
__________________________________________________________________________

That is it!

request() sends an http request immediately to the specified flow with the specified parameters. 

expect() verifies the logical content of the last response. i.e. object comparison not just string comparison.

Many flow responses have random elements. There is currently a method in the DSL checkReturnedValidJson() Which fails if the return is not valid JSON. 

The scripts are technically just a snippet of Groovy, so any groovy code and almost any Java code can also be included if needed. For example you are welcome to import other classes and use them in your test script. Sticking to Java syntax is normally fine, but groovy offers some shortcuts; for example the three double quote delimiter (""") , used above, is a multi-line string which can be very convenient for containing formatted JSON data etc.


## Test Generator ##
A test generator class has been added. This is a command line tool that will interrogate the server for available flows and generate DSL tests according to specific strategies. You can run it like this.

    java -DignoreFlows="PermanentApiKey,CategoriesList" -cp amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies org.amplafi.flow.utils.DSLTestGenerator  -key ampcb_e1446aa0e3e46427b591fa044c5f51c57989e393b66269140af68709e1da228e -host http://sandbox.farreach.es -port 8080 -apiv apiv1 -strategy BogusString -out ./out/
    
Options are:

     -apiv <arg>           API version
     -D <property=value>   Specify query parameter name and value.
     -flow <arg>           Flow name - If no know is specified tests will be generated for all flows.
     -help                    Prints this message.
    -host <arg>            Host address
    -key <arg>             API key
    -port <arg>             Service port
    -strategy <arg>       One of [BogusString,] Others to follow soon .


When tests are generated you should copy the ones you want to somewhere below src/test/resources/testscripts/** I put them in a sub folder called "gen" to distinguish them from ones I will create manually. These can be edited, checked in to git. etc.

## Testing Strategies ##
The concept of a testing strategy has been added. Testing strategies are subclasses of org.amplafi.flow.strategies.AbstractTestingStrategy. These classes define how the DSLTestGenerator will create the test scripts for each flow. In this way new approaches to testing can be created easily. For example the BogusStringTestingStrategy is very concise and just looks like this:

    /**
    * This strategy produces tests that simply send in bogus String data.
    * @author paul
    */
    public class BogusStringDataStrategy extends AbstractTestingStrategy {
         
        private static final String NAME = "BogusStringData";
        /**
         * @return the name of this strategy
         */
         @Override
         public String getName(){
          return NAME;
         }
        @Override
        public Collection<NameValuePair> generateParameters(String flow, Collection<String> parameterNames){
          String bogusData = "bogusData";
               List<NameValuePair> bogusDataList = new ArrayList<NameValuePair>();
               for (String parameterName : parameterNames) {
                   bogusDataList.add(new BasicNameValuePair(parameterName,bogusData));
               }
               return bogusDataList;
        }
         
        @Override
        public void addVerification(String typicalResponse){
         writeToFileBuffer("checkReturnedValidJson()");
        }
    }
New testing strategies must be registered in the TestingStrategiesEnum.java so they can be called from the command line.

It is easy to create more strategies to test different forms of corrupt data and more intelligent tests.

Test file names are generated with the name format number_Strategy_FlowName.groovy 

e.g. 0062BogusStringData_BroadcastTopicMessageEndPoints.groovy

5) A TestRunner has been added. This will run all tests under src/test/resources/testscripts/**

This is run from a TestNG unit test called (TestDSLScriptsBatchRunner.java) that locates the test scripts in that path and returns them from a DataProvider. This allows each test to be run individually with separate test reports. Tests are run in name sort order which is why they are created with a numerical prefix. 
 
## Plan ##

I plan to implement wildcards in the expect string so that unpredictable return fields can be tested gracefully.

I also plan to implement the following testing strategies:

CorruptParamsNameTestingStrategy,
RandomMissingParamsTestingStrategy, 
IncorrectEncodingTestingStrategy, 
RandomDataTestingStrategy

========================================================================================
 
## Suspected Errors ##
    
Flow  PermanentApiKey returns :
(org.amplafi.flow.TestFlowTypesSandBox): Flow definition not valid JSON, JSON Error: Expected a ',' or '}' but was '{' at character 88 of {errorMessage: 'Failed to render flow state. Cause: A JSONObject text must begin with '{' but was 'b' at character 1 of bogusData'}

Probably because the securityLevel was passed a string "bogusData" and not a SecurityLevel object.

--------------------

Flow CategoriesList returns :


Called with:
http://sandbox.farreach.es:8080/c/ampcb_e1446aa0e3e46427b591fa044c5f51c57989e393b66269140af68709e1da228e/apiv1/CategoriesList?configuration#bogusData&deleteCategories#bogusData&originalDeleteCategories#bogusData&defaultCategory#bogusData&fsRenderResult#json

Note valid API key used.

Returns:

javax.servlet.ServletException: java.lang.reflect.UndeclaredThrowableException
	com.amplafi.web.services.AmplafiWebRequestServicerPipelineBridge.service(AmplafiWebRequestServicerPipelineBridge.java:44)
	$ServletRequestServicer_13b2f97e4e8.service($ServletRequestServicer_13b2f97e4e8.java)
	org.apache.tapestry.request.DecodedRequestInjector.service(DecodedRequestInjector.java:55)
...
	com.amplafi.web.servlet.DisableSessionIdsInUrlFilter.doFilter(DisableSessionIdsInUrlFilter.java:61)
	com.amplafi.web.servlet.LogoutFilter.doFilter(LogoutFilter.java:34)
	com.amplafi.web.servlet.PerformanceLoggingFilter.doFilter(PerformanceLoggingFilter.java:59)
root cause
...
...
root cause

org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: com.amplafi.core.messagehandling.BroadcastTopic
	org.hibernate.engine.ForeignKeys.getEntityIdentifierIfNotUnsaved(ForeignKeys.java:242)
	org.hibernate.type.EntityType.getIdentifier(EntityType.java:430)
	org.hibernate.type.ManyToOneType.isDirty(ManyToOneType.java:265)
...
...
	nUrlFilter.java:61)
	com.amplafi.web.servlet.LogoutFilter.doFilter(LogoutFilter.java:34)
	com.amplafi.web.servlet.PerformanceLoggingFilter.doFilter(PerformanceLoggingFilter.java:59)
 
 
---------------------------------
### Finally ###

Neither flows GetWordpressPlugin nor GetWordpressPluginInfo have any activities defined.
