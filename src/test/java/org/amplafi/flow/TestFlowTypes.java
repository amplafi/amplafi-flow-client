package org.amplafi.flow;

import java.net.URI;
import java.util.ArrayList;
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
 * The development server needs to be running for this test class to work.<br>
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
    private static final String requestUriString = "http://localhost:8080";

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

    @Factory(dataProvider = "flows-list")
    public TestFlowTypes(String flow) {
        this.flow = flow;
    }

    @Override
    public String toString() {
        return flow;
    }

    /**
     * Provides a all flow names for testing
     * 
     * @return
     */
    @DataProvider(name = "flows-list")
    public static Object[][] getListOfFlows() {
        //get list of flow types
        List<String> flowList = GeneralFlowRequest.getListOfFlowTypes(requestUriString);
        //drop the list into the Object[][] format
        Object[][] listOfFlowTypes = new Object[flowList.size()][];
        int index = 0;
        for (String flow : flowList) {
            listOfFlowTypes[index] = new Object[] { flow };
            index++;
        }
        return listOfFlowTypes;
    }

    //got flow list each flow cached in flow
    //get flow definition string
    //test string
    @Test
    public void testFlowDefinition_resultString() {
        String messageStart = "Returned FlowDefinition for " + flow + " ";
        flowDefinitionResult = GeneralFlowRequest.getFlowDefinitionString(requestUriString, flow);
        assertNotNull(flowDefinitionResult);
        assertFalse(flowDefinitionResult.trim().equals(""), messageStart + "was an empty String");
    }

    //make json
    //test json
    @Test(dependsOnMethods = { "testFlowDefinition_resultString" })
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
    @Test(dependsOnMethods = { "testFlowDefinition_resultJson" })
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
    @Test(dependsOnMethods = { "testFlowDefinition_resultJson" })
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

        URI requestUri = URI.create(requestUriString + "/flow/" + flow);
        GeneralFlowRequest request = new GeneralFlowRequest(requestUri, parametersPopulatedWithBogusData);
        jsonResultWhenAllParametersAreStringsResult = request.get();
    }

    /**
     * Uses the result string, from flow parameters all set to strings, to build a json object and
     * test.
     */
    @Test(dependsOnMethods = { "testFlowParametersSetWithStrings_resultString" })
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

}
