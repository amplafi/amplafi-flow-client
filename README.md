## Getting Started ##

The amplafai flow client is a tool for sending http requests to a farreach.es API server.
During development it is normal to use a local server running on port 8080 and to edit your hosts file so that sandbox.farreach.es is mapped to 127.0.0.1.

To make any API calls you will need an API key. This will be generated the first time the wordpress plugin connects to the server. So you will need to install wordpress and the farreaches-wp-plugin to do this. 
To do that should follow the instructions at https://github.com/farreaches/farreaches-wp-plugin

When this is finished, the key that you need will be visible in the diagnostics screen for the plugin (It is a long hex string like this: ampcb_0cf02bdcd1218c9f5556b632640749a53946923cb26d0e90d2b9cf2300280497).

## Running the client manually ##

Run: mvn package

This will create a jar file:
target/amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar

### To list available flows ###

 java -jar amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar -key <api_key>  -host http://sandbox.farreach.es -port 8080 -apiv apiv1 -desc

### To describe a flow ###

 java -jar amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar -key <api_key>  -host http://sandbox.farreach.es -port 8080 -apiv apiv1 -desc -flow <flowname>

### To call a flow ###

 java -jar amplafi-flow-client-0.9.4-SNAPSHOT-jar-with-dependencies.jar -key <api_key>  -host http://sandbox.farreach.es -port 8080 -apiv apiv1 -flow <flowname> -D<param_name>=<value> D<param_name>=<value> ...
 
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
 
## Plan ##

My goal is to split the test tool into 2 modules. A fuzz test generator and a fuzz test runner. The test generator would create tests in a simple human readable DSL and store them to the disk. interesting test cases that showed faults in the system or were simply considered valuable could be checked into git and bugs reported would be able to refer to the script that highlighted the error. Having a DSL would also allow us to clone scripts and modify them for specific boundary cases. 

This work has not started yet.
 
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
