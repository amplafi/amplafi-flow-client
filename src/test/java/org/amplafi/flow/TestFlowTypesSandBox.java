package org.amplafi.flow;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *  TODO refactor TestFlowTypesSandBox and TestFlowTypes to either merge the two or remove duplicate code
 * It is quite possible that TestFlowTypes is now obsolete.
 *
 * The sand box server needs to be running for this test class to work.<br>
 * <br>
 * This test has been disabled by default, either run in eclipse or use the command <b>mvn
 * -Dtest=TestFlowTypesSandBox test</b> <br>
 * <br>
 * This test Class tests basic elements of interacting with the server using flows and JSON
 * responses. One of the major items covered is sending bogus data to the different flow types to
 * see if that breaks the server. What should happen when sending in bogus data is a JSON string
 * with an error message pointing out which of the parameters were incorrectly passed, should be
 * displayed. <br>
 * 
 * @author Paul Smout
 */
public class TestFlowTypesSandBox {
   
    /**
      * These static variables are overridden in the static initializer below. 
      */
    private static String requestUriString = "http://localhost:8080";
    private static String apiKey = "dummyKey";
    
    private static final String JSON_ACTIVITY_KEY = "activities";
    private static final String JSON_PARAMETER_KEY = "parameters";
    private static final String JSON_PARAMETER_NAME_KEY = "name";
    private static final String JSON_GENERIC_ERROR_MESSAGE_KEY = "errorMessage";
    private static final String JSON_GENERIC_ERROR_MESSAGE = "Exception while running flowState";
    private static final String HOST_PROPERTY_KEY = "host";
    private static final String PORT_PROPERTY_KEY = "port";
    private static final String API_PROPERTY_KEY = "key";    
    private static final String CONFIG_ERROR_MSG = "\n***************************** \n" + 
                                                                    " This Error can be caused by the API key \n" +
                                                                    " not being set in the pom.xml, or the\n" +
                                                                    " sandbox host not running or configured. \n" +
                                                                    " Please check these things first.\n" + 
                                                                    "***************************** \n";
    
    private static final NameValuePair renderAsJson = new BasicNameValuePair("fsRenderResult", "json");
    private static final NameValuePair keyParam = new BasicNameValuePair("fsRenderResult", "json");

    /** 
      * The current flow for this test instance.
      */
    private String flow;
    
    private static boolean DEBUG = false;

    /**
      * The following Set contains flows that seem broken or suspicious. 
      * Having been identified and reported they are awaiting fixing and will be exempt from the test
      * These values are set in the pom.xml in test configuration.
      */
     static Set<String> ignoredFlows = null;


    /*
      * Obtain the system properties for the test
      * These may be set up in the pom.xml or passed in from the command line with -D options.
      */
    static {
        apiKey = System.getProperty(API_PROPERTY_KEY,"");
        String host = System.getProperty(HOST_PROPERTY_KEY ,"sandbox.farreach.es");
        String port = System.getProperty(PORT_PROPERTY_KEY,"8080");
        
        requestUriString = host + ":" + port + "/c/" + apiKey   + "/apiv1"; 
        
        String ignoredFlowsStr = System.getProperty("ignoreFlows","");
        String[] ignoredFlowsArr = ignoredFlowsStr.split(",");
        ignoredFlows = new HashSet<String>(Arrays.asList(ignoredFlowsArr));


    }

    /**
      * This factory will be called repeatedly to generate test instances for each of the flows supported 
      * by the server. 
      */
   @Factory(dataProvider = "flows-list")
    public TestFlowTypesSandBox(String flow) {
        this.flow = flow;
    }

    @Override
    public String toString() {
        return flow;
    }

    /**
     * Provides all flow names for testing
     * 
     * @return
     */
    @DataProvider(name = "flows-list")
    public Object[][] getListOfFlows() {
      
        //Get list of supported flow types from server.
       List<String> flowList = (new GeneralFlowRequest(URI.create(requestUriString), null, renderAsJson )).listFlows().asList();
        
        debug("List of flows " + flowList);        
        
        // Drop the list into the Object[][] format which is standard for testNG data providers
        // This provides an array of parameters for each test
        Object[][] listOfFlowTypes = new Object[flowList.size()][];
        int index = 0;
        for (String flow : flowList) {
            listOfFlowTypes[index] = new Object[] { flow };
            index++;
        }
        
      //     return new Object[][]{new Object[]{"GetWordpressPlugin"},new Object[]{"GetWordpressPluginInfo"}};
        return listOfFlowTypes;
    }

    @Test()
    /**
      * Through TestNG DataProvider magic, this test method will be called multiple times; once for each of the
      * flows supoprted by the sandbox server. Each time this is called the "flow" member variable will be initialized with
      * the current flow name.
      */
    public void testConductor() {
        debug("@@@ testConductor for flow " + flow);   
        assertNotNull("flow should not be null" , flow);

         // 
        // This method calls other methods directly parsing data as parameters instead of 
        // via member variables. 
        String flowDefinitionResult = testFlowDefinitionResultString();
        
        // This will validate each of the flow descriptions and then proceed to make calls to their starting 
        // activities.
        JSONObject flowDefinition  = testFlowDefinitionResultJson(flowDefinitionResult);
      
      


    }

    /**
     * Verify that request return non-null non-empty string.
     *  @return The string containing the flow definition JSON data for the current flow.
     */
    public String testFlowDefinitionResultString() {
		debug("Sending request to " + requestUriString);
        String messageStart = "Returned FlowDefinition for " + flow + " ";
        String flowDefinitionResult = null;
        try {
            flowDefinitionResult = new GeneralFlowRequest(URI.create(requestUriString), flow).describeFlowRaw();
            assertNotNull(flowDefinitionResult,CONFIG_ERROR_MSG );
            assertFalse(flowDefinitionResult.trim().equals(""), messageStart + "was an empty String");
        } catch (IllegalArgumentException iae) {
            fail( "This Error can be caused by the API key not being set in the pom",iae);
        }
        return flowDefinitionResult;
    }

    /**
      * Determine whether the returned data is valid JSON data and whether it contains a list of activities which contain parameters 
      * for the flow. 
      */
    private JSONObject testFlowDefinitionResultJson(String flowDefinitionResult) {
        debug("flowDefinitionResult = " + flowDefinitionResult);
        assertTrue(flowDefinitionResult.charAt(0) == '{', "Flow definition is not a JSONObject, definition result: " + flowDefinitionResult);
        JSONObject flowDefinition = null;
        try {
            // parse the flow definition into a JSON 
            flowDefinition = new JSONObject(flowDefinitionResult);
            
             assertTrue( flowDefinition.has("flowTitle"),"flowTitle not found in flow description " );            
            
            // Two flows do not have activities, these are GetWordpressPlugin and GetWordpressPluginInfo
            
            // Other flows seem to have bugs. Having identified those bugs we need to ingore them so that we can move on and 
            // find new bugs.
     
            if (!ignoredFlows.contains(flow)){    
                // Some flows have no activities and can be called 
                if ( flowDefinition.has(JSON_ACTIVITY_KEY) ){
                
                    // Obtain the Activity list from the JSON data,            
                    JSONArray<JSONObject> activities = flowDefinition.getJSONArray(JSON_ACTIVITY_KEY);
                    assertFalse(activities.isEmpty(), "\"Activities\" array was empty.");    

                    // Loop over the activities in the flow definition and determine that each has a parameters attribute.
                    for (JSONObject activity : activities){
                    
                            // certain activities in flows such as AuditManagement do not have parameters. 
                            JSONArray<JSONObject> activityParameters = activity.getJSONArray(JSON_PARAMETER_KEY);
                            if (!activityParameters.isEmpty()){
                                // if an activity does have parameters, then we should check that each parameter definition
                                //  has the correct attributes.
                                for (JSONObject param : activityParameters){
                                     assertTrue( param.has("name"),"name not found for parameter in activity " );
                                     assertTrue(param.has("type"), "type not found for parameter in activity " );
                                     assertTrue(param.has("req"), "req not found for parameter in activity " );
                                }
                                
                                // Check that each there are no repeatedly defined parameters for this activity.
                                testFlowDefinitionParameterRepeats(activityParameters);  
                                
                                // Now we call the flow current flow but with each of the parameters set to a string 
                               String resultJSON = testFlowParametersSetWithStringsResultString(activity);    
                                
                                // Next validate the returned JSON data. 
                               testFlowParametersSetWithStringsResultJson(resultJSON);
                                
                            }
                            

                    }   
                } else {
                     // flow has no activities or parameters so we can just call it directly
                    URI requestUri = URI.create(requestUriString);
                    GeneralFlowRequest request = new GeneralFlowRequest(requestUri, flow);
                    testFlowParametersSetWithStringsResultJson(request.get());
                }
            }
            
        } catch (JSONException jsonException) {
            fail("Flow definition not valid JSON, JSON Error: " + jsonException.getMessage());
        }

        return flowDefinition;

    }

    /**
     * This utility method tests for repeating parameters in the flow's activity definition. 
     * In other words it fails if a parameter is defined twice.
     */
    private void testFlowDefinitionParameterRepeats(JSONArray<JSONObject> jsonParameters) {

        Set<String> parameters = new HashSet<String>();
        List<String> repeats = new ArrayList<String>();
        for (JSONObject jsonObject : jsonParameters) {
            String parameterName = jsonObject.getString(JSON_PARAMETER_NAME_KEY);
            if (!parameters.add(parameterName)) {
                repeats.add(parameterName);
            }
        }
        assertTrue(repeats.isEmpty(), "These parameters where repeated for flowtype " + flow + " :" + repeats.toString());
    }

    /**
     * This test uses the definition to request a flow with all of the parameters set to strings.
     */
    public String testFlowParametersSetWithStringsResultString(JSONObject activityDefinition) {
        login(); // does nothing and shouldn't be here in any case. 
        
        // Following method call creates a list of parameters with a fixed dummy string value 
        // It makes the request to the server and returns the response.
        String stringResult = getParametersAllStringsResult(activityDefinition);
        

        String messageStart = "Returned response, when all parameters are set to strings, for " + flow + " ";

        assertNotNull(stringResult);
        assertFalse(stringResult.trim().equals(""), messageStart + "was empty, should have returned something.");

        return stringResult;
    }

    /**
     * TODO: Shouldn't the application have to login before requesting flows?<br>
     * While running the test server if I drop
     * "http://localhost:8080/flow/CreateAlert?messageBody=foo" into the browser then I am directed
     * to login. However if I drop
     * "http://localhost:8080/flow/CreateAlert?messageBody=foo&fsRenderResult=json" into the browser
     * it returns the following:<br>
     * <br>
     * {"flowState":{"fsComplete":false,"fsCurrentActivityByName":"content","fsLookupKey":
     * "CreateAlert_fjd2s3b6"
     * ,"fsParameters":{"fsApiCall":false,"fsAutoComplete":false,"messageCalendarable"
     * :false,"broadcastMessageType"
     * :"nrm","basedOnMessagesList":true,"callbackCodes":[],"selectedEnvelopes"
     * :[],"messageBody":"foo"}}}<br>
     * <br>
     * Why is this? Shouldn't I either get a redirect to log in or a json formatted error telling me
     * to login?
     */
    private void login() {
        //TODO: perform login
    }

    /**
     * This method gets caches the results when a flow is passed all strings.
     */
    private String getParametersAllStringsResult(JSONObject flowDefinition) {
        assertNotNull(flowDefinition,
            "flowDefinition was null, The test should depend on testJsonStringIsReturnedWhenRequestingTheFlowDefinition() does it?");
        Collection<String> parameterNames = getAllParameterNames(flowDefinition);

        Collection<NameValuePair> parametersPopulatedWithBogusData = getBogusStringParameters(parameterNames);
        //add the json response parameter
        parametersPopulatedWithBogusData.add(renderAsJson);

        URI requestUri = URI.create(requestUriString);
        GeneralFlowRequest request = new GeneralFlowRequest(requestUri, flow, parametersPopulatedWithBogusData);
        return request.get();
    }

    /**
     * Validates a generic flow response
     * @param jsonStr - the flow response raw data. 
     */
    public JSONObject testFlowParametersSetWithStringsResultJson(String jsonStr) {
        JSONObject jsonResult = null;
        
        // certain flows may download files, in this case the General flow request will return a specific code.
        if (jsonStr != GeneralFlowRequest.APPLICATION_ZIP){
            try {
            

                //TO_PAT: Is a JSONObject allways returned?
                jsonResult = new JSONObject(jsonStr);
            } catch (JSONException jsonException) {

                fail("Flow definition not valid JSON, JSON Error: " + jsonException.getMessage());
            }

            
            // We would expect most flows to fail when random parameters are sent but this is not necessarily true. 
            // So we determine is the flow has failed or not and then validate the response. 
            
             if ( jsonResult.has(JSON_GENERIC_ERROR_MESSAGE_KEY) ){
                     assertFalse(jsonResult.getString(JSON_GENERIC_ERROR_MESSAGE_KEY).trim()
                                            .equalsIgnoreCase(JSON_GENERIC_ERROR_MESSAGE),
                                    "Generic Message, change this message to state the problem more specifically: " + jsonResult.toString());
             
             
             }
        }
        return jsonResult;
    }

    /**
      * @Returns a collection of parameter names in this activity.
      */
    private Collection<String> getAllParameterNames(JSONObject activityDefinition) {
        assertTrue(activityDefinition.has(JSON_PARAMETER_KEY), JSON_PARAMETER_KEY + " not found in JSONObject: " + activityDefinition.toString());
        JSONArray<JSONObject> parameters = activityDefinition.getJSONArray(JSON_PARAMETER_KEY);
        List<String> parameterNames = new ArrayList<String>();
        for (JSONObject jsonObject : parameters) {
            assertTrue(jsonObject.has(JSON_PARAMETER_NAME_KEY), JSON_PARAMETER_NAME_KEY + " not found in JSONObject: " + jsonObject.toString());
            parameterNames.add(jsonObject.getString(JSON_PARAMETER_NAME_KEY));
        }
        return parameterNames;
    }

    private Collection<NameValuePair> getBogusStringParameters(Collection<String> parameterNames) {
        String bogusData = "bogusData";
        List<NameValuePair> bogusDataList = new ArrayList<NameValuePair>();
        for (String parameterName : parameterNames) {
            bogusDataList.add(new BasicNameValuePair(parameterName, bogusData));
        }
        return bogusDataList;
    }
    
    private static void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }

}
