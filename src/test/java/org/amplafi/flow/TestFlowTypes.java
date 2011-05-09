package org.amplafi.flow;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * This test set is to call all of the different types of flows, pass in bogus data, and see if they
 * break the server, and if they cause error messages that define the problem well. TODO: make a
 * constructor with the factory annotation to create a test for each type of flow.
 * 
 * @author Tyrinslys Valinlore
 */
public class TestFlowTypes {
    private static final String requestUriString = "http://localhost:8080";

    private static final String jsonParamaterKey = "parameters";

    private static final String jsonParameterNameKey = "name";

    private static final NameValuePair renderAsJson = new BasicNameValuePair("fsRenderResult", "json");

    /**
     * The testing server needs to be running
     * 
     * @return
     */
    @DataProvider(name = "flows-list")
    public Object[][] getListOfFlows() {
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

    @DataProvider(name = "flow-all-strings-result")
    public Object[][] getAllStringsResult() {
        List<String> flowList = GeneralFlowRequest.getListOfFlowTypes(requestUriString);
        final Object[][] result = new Object[flowList.size()][];
        int index = 0;
        for (String flow : flowList) {
            Collection<NameValuePair> parametersPopulatedWithBogusData = null;
            try {
                JSONObject flowDefinition = GeneralFlowRequest.getFlowDefinition(requestUriString, flow);
                Collection<String> parameterNames = getAllParameterNames(flowDefinition);

                parametersPopulatedWithBogusData = getBogusStringParameters(parameterNames);
                //add the json response parameter
            } catch (JSONException jsonException) {
                /*
                 * HACK: The flowDefinition was not valid json. I would rather skip the test for
                 * this result, but do not know how. Instead set the parameters to an empty list;
                 */
                parametersPopulatedWithBogusData = Collections.emptyList();
            }
            parametersPopulatedWithBogusData.add(renderAsJson);

            URI requestUri = URI.create(requestUriString + "/flow/" + flow);
            GeneralFlowRequest request = new GeneralFlowRequest(requestUri, parametersPopulatedWithBogusData);
            result[index] = new Object[] { request.get() };
            index++;
        }
        return result;
    }

    /**
     * This test checks to see if a jsonString is returned when requested as that format.
     * 
     * @param flow
     */
    @Test(dataProvider = "flows-list")
    public void testJsonStringIsReturned(String flow) {
        URI requestUri = URI.create(requestUriString + "/flow/" + flow);
        GeneralFlowRequest request = new GeneralFlowRequest(requestUri, renderAsJson);
        String jsonResponse = request.get();
        if (jsonResponse == null) {
            fail("Null was returned: no response?");
        }
        failIfJsonIsNotWellFormed(jsonResponse);

    }

    @Test(dataProvider = "flows-list")
    public void testJsonStringIsReturnedWhenRequestingTheFlowDefinition(String flow) {
        try {
            GeneralFlowRequest.getFlowDefinition(requestUriString, flow);
        } catch (JSONException jsonException) {
            fail("JSON not returned, JSON Error: " + jsonException.getMessage());
        }
    }

    private void failIfJsonIsNotWellFormed(String jsonString) {
        if (jsonString == null) {
            fail("null was given: A null string cannot be a valid json string.");
        } else if (jsonString.charAt(0) == '{') {
            // Throws exception if json is not well formed.
            new JSONObject(jsonString);
        } else if (jsonString.charAt(0) == '[') {
            // Throws exception if json is not well formed.
            new JSONArray(jsonString);
        } else {
            fail("The following is not formatted in json: " + jsonString);
        }
    }

    @Test(dataProvider = "flow-all-strings-result")
    public void testAllFlows_ParametersSetWithStrings_testNotEmpty(String result) {
        assertFalse(result.trim().equals(""), "Response was empty, should have returned something.");
    }

    @Test(dataProvider = "flow-all-strings-result")
    public void testAllFlows_ParametersSetWithStrings_testIsJson(String result) {
        assertNotNull(result);
        failIfJsonIsNotWellFormed(result);
    }

    @Test(dataProvider = "flow-all-strings-result", dependsOnMethods = { "testAllFlows_ParametersSetWithStrings_testIsJson" })
    public void testAllFlows_ParametersSetWithStrings_testUsingGenericMessage(String result) {
        //        assertNotNull(result);
        //        failIfJsonIsNotWellFormed(result);
        if (result.trim().equalsIgnoreCase("{\"errorMessage\":\"Exception while running flowState\"}")) {
            fail("Generic Message, change this message to state the problem more specifically: " + result);
        }
    }
    // TODO: Check for parameter repeats
    // TODO: Check for empty parameters

    /**
     * This test requires the testing server to be running. <br>
     * This test feeds bogus data to the server (as Strings).<br>
     * This tests each of the flows by getting the definition of the flow and then constructing a
     * request based on the definition and sending the request. <br>
     * TODO: This test has too many responsibilities, break it out into a few tests.
     * 
     * @param flow the string for a flow
     */
    /*
     * @Test(dataProvider = "flows-list") public void testFlows(String flow) { JSONObject
     * flowDefinition = GeneralFlowRequest.getFlowDefinition(requestUriString, flow);
     * Collection<String> parameterNames = getAllParameterNames(flowDefinition);
     * Collection<NameValuePair> parametersPopulatedWithBogusData =
     * getBogusStringParameters(parameterNames); //add the json response parameter
     * parametersPopulatedWithBogusData.add(renderAsJson); URI requestUri =
     * URI.create(requestUriString + "/flow/" + flow); GeneralFlowRequest request = new
     * GeneralFlowRequest(requestUri, parametersPopulatedWithBogusData); String jsonResponse =
     * request.get(); if
     * (jsonResponse.trim().equalsIgnoreCase("{\"errorMessage\":\"Exception while running flowState\"}"
     * )) { fail("Generic Message, change this message to state the problem more specifically: " +
     * jsonResponse); }
     * fail("Right now I cause all of the methods to fail to see what was returned. RETURNED: " +
     * jsonResponse); //TODO: test the response. }
     */

    private Collection<String> getAllParameterNames(JSONObject flowDefinition) {
        assertTrue(flowDefinition.has(jsonParamaterKey), jsonParamaterKey + " not found in JSONObject: " + flowDefinition.toString());
        JSONArray<JSONObject> parameters = (JSONArray<JSONObject>) flowDefinition.get(jsonParamaterKey);
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
