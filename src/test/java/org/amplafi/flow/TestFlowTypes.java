package org.amplafi.flow;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 * The development server needs to be running for this test class to work.<br>
 * <br>
 * This test has been disabled by default, either run in eclipse or use the command <b>mvn
 * -Dtest=TestFlowTypes test</b> <br>
 * <br>
 * This test Class tests basic elements of interacting with the server using flows and JSON
 * responses. One of the major items covered is sending bogus data to the different flow types to
 * see if that breaks the server. What should happen when sending in bogus data is a JSON string
 * with an error message pointing out which of the parameters were incorrectly passed, should be
 * displayed. <br>
 * TODO: change the test so the dependent tests do not skip after a single instance fail.
 *
 * @author Tyrinslys Valinlore
 */
public class TestFlowTypes {

    private static final FarReachesServiceInfo serviceInfo = new FarReachesServiceInfo("localhost", "8080", "apiv1");

    private static final String jsonParamaterKey = "parameters";

    private static final String jsonParameterNameKey = "name";

    private static final String jsonGenericErrorMessageKey = "errorMessage";

    private static final String jsonGenericErrorMessage = "Exception while running flowState";

    private static final NameValuePair renderAsJson = new BasicNameValuePair("fsRenderResult", "json");

    private String flow;

    private String flowDefinitionResult;

    private JSONObject flowDefinition;

    private String jsonResultWhenAllParametersAreStringsResult;

    private JSONObject jsonResultWhenAllParametersAreStrings;

    private static boolean DEBUG = true;


   @Factory(dataProvider = "flows-list")
    public TestFlowTypes(String flow) {
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
        debug("@@@ List of flows " );
        //get list of flow types, currently this returns null because no api key is set.
        List<String> flowList = (new GeneralFlowRequest(serviceInfo,null, null)).listFlows().asList();

        debug("@@@ List of flows " + flowList);

        // Drop the list into the Object[][] format which is standard for testNG data providers
        // This provides an array of parameters for each test
        Object[][] listOfFlowTypes = new Object[flowList.size()][];
        int index = 0;
        for (String flow : flowList) {
            listOfFlowTypes[index] = new Object[] { flow };
            index++;
        }

        return listOfFlowTypes;
    }

    @Test()
    public void testConductor() {
        debug("@@@ testConductor for flow " + flow);
        assertNotNull("flow should not be null" , flow);

        testFlowDefinition_resultString();
        testFlowDefinition_resultJson();
        testFlowDefinition_parameterRepeats();
        testFlowParametersSetWithStrings_resultString();
        testFlowParametersSetWithStrings_resultJson();
    }

    /**
     * Verify that request return non-null non-empty string.
     */
    public void testFlowDefinition_resultString() {
        System.err.println("Sending request to " + serviceInfo);
        String messageStart = "Returned FlowDefinition for " + flow + " ";
        flowDefinitionResult = new GeneralFlowRequest(serviceInfo,"apikey", flow).describeFlowRaw();
        assertNotNull(flowDefinitionResult);
        assertFalse(flowDefinitionResult.trim().equals(""), messageStart + "was an empty String");
    }

    public void testFlowDefinition_resultJson() {
        assertTrue(flowDefinitionResult.charAt(0) == '{', "Flow definition is not a JSONObject, definition result: " + flowDefinitionResult);
        try {
            flowDefinition = new JSONObject(flowDefinitionResult);
        } catch (JSONException jsonException) {
            fail("Flow definition not valid JSON, JSON Error: " + jsonException.getMessage());
        }
        assertFalse(flowDefinition.getJSONArray(jsonParamaterKey).isEmpty(), "\"paramaters\" array was empty.");
    }

    /**
     * This test tests for repeating parameters in the flow definition.
     */
    public void testFlowDefinition_parameterRepeats() {
        JSONArray<JSONObject> jsonParameters = flowDefinition.getJSONArray(jsonParamaterKey);
        Set<String> parameters = new HashSet<String>();
        List<String> repeats = new ArrayList<String>();
        for (JSONObject jsonObject : jsonParameters) {
            String parameterName = jsonObject.getString(jsonParameterNameKey);
            if (!parameters.add(parameterName)) {
                repeats.add(parameterName);
            }
        }
        assertTrue(repeats.isEmpty(), "These parameters where repeated for flowtype " + flow + " :" + repeats.toString());
    }

    /**
     * This test uses the definition to request a flow with all of the parameters set to strings.
     */
    public void testFlowParametersSetWithStrings_resultString() {
        login();
        getParametersAllStringsResult();
        String messageStart = "Returned response, when all parameters are set to strings, for " + flow + " ";
        String stringResult = jsonResultWhenAllParametersAreStringsResult;
        assertNotNull(stringResult);
        assertFalse(stringResult.trim().equals(""), messageStart + "was empty, should have returned something.");
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
     * :"nrm","callbackCodes":[],"selectedEnvelopes"
     * :[],"messageBody":"foo"}}}<br>
     * <br>
     * Why is this? Shouldn't I either get a redirect to log in or a json formatted error telling me
     * to login?
     */
    private void login() {
        //TODO: perform login
    }

    /**
     * Tests that use this method need to depend on testFlowDefinition_resultJson() for this to
     * work. <br>
     * This method gets caches the results when a flow is passed all strings.
     */
    private void getParametersAllStringsResult() {
        assertNotNull(flowDefinition,
            "flowDefinition was null, The test should depend on testJsonStringIsReturnedWhenRequestingTheFlowDefinition() does it?");
        Collection<String> parameterNames = getAllParameterNames(flowDefinition);

        Collection<NameValuePair> parametersPopulatedWithBogusData = getBogusStringParameters(parameterNames);
        //add the json response parameter
        parametersPopulatedWithBogusData.add(renderAsJson);

        GeneralFlowRequest request = new GeneralFlowRequest(serviceInfo, "apikey",flow, parametersPopulatedWithBogusData);
        jsonResultWhenAllParametersAreStringsResult = request.get();
    }

    /**
     * Uses the result string, from flow parameters all set to strings, to build a json object and
     * test.
     */
    public void testFlowParametersSetWithStrings_resultJson() {
        try {
            //TO_PAT: Is a JSONObject allways returned?
            jsonResultWhenAllParametersAreStrings = new JSONObject(jsonResultWhenAllParametersAreStringsResult);
        } catch (JSONException jsonException) {
            fail("Flow definition not valid JSON, JSON Error: " + jsonException.getMessage());
        }
        JSONObject jsonResult = jsonResultWhenAllParametersAreStrings;
        assertFalse(jsonResult.getString(jsonGenericErrorMessageKey).trim().equalsIgnoreCase(jsonGenericErrorMessage),
            "Generic Message, change this message to state the problem more specifically: " + jsonResult.toString());
    }

    private Collection<String> getAllParameterNames(JSONObject flowDefinition) {
        assertTrue(flowDefinition.has(jsonParamaterKey), jsonParamaterKey + " not found in JSONObject: " + flowDefinition.toString());
        JSONArray<JSONObject> parameters = flowDefinition.getJSONArray(jsonParamaterKey);
        List<String> parameterNames = new ArrayList<String>();
        for (JSONObject jsonObject : parameters) {
            assertTrue(jsonObject.has(jsonParameterNameKey), jsonParameterNameKey + " not found in JSONObject: " + jsonObject.toString());
            parameterNames.add(jsonObject.getString(jsonParameterNameKey));
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
