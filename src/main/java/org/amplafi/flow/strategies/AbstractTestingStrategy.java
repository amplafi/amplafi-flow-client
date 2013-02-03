/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package org.amplafi.flow.strategies;

import java.net.URI;
import java.util.Collection;
import java.util.Formatter;
import java.util.ArrayList;
import java.util.List;
import org.amplafi.flow.TestGenerationProperties;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.amplafi.flow.utils.GenerationException;
import org.amplafi.flow.utils.GeneralFlowRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Abstract Super class for all testing strategies.
 * This class defines methods that are useful to most strategies.
 *
 * @author paul
 */
public abstract class AbstractTestingStrategy {
    private StringBuffer testFileContents = null;
    private int sequence = 0;
    private String currentFlow = null;
    private String currentActivity = null;

    private Log log;

    private static final int STANDARD_INDENTATION = 4;
    private static final String JSON_PARAMETER_KEY = "parameters";
    private static final String JSON_PARAMETER_NAME_KEY = "name";
    private static final String JSON_PARAMETER_TYPE_KEY = "type";
    private static final String JSON_PARAMETER_REQ_KEY = "req";
    protected static final NameValuePair RENDER_AS_JSON = new BasicNameValuePair("fsRenderResult", "json");
    /**
     * @return the name of this strategy
     */
    public abstract String getName();

    /**
     * @return the name of this strategy
     */
    public String getFileName() {
        Formatter formatter = new Formatter();
        return "" + formatter.format("%04d", sequence) + "" + getName() + "_" + currentFlow + ".groovy";
    }

    /**
     * Generates parameters for the request.
     * @param flow - flow name.
     * @param parameterNames - list of paramenter names.
     * @return Collection of parameter name and value.
     */
    public abstract Collection<NameValuePair> generateParameters(String flow, Collection<String> parameterNames);

    /**
     * Initializes a new test script.
     * @param flow -the current flow name
     * @param activity - the current activity - unused
     */
    public void newTest(String flow, String activity) {
        testFileContents = new StringBuffer();
        currentFlow = flow;
        currentActivity = activity;
        sequence++;
    }

    /**
     * Closes the test script file etc.
     */
    public void endTest() {

        testFileContents = null;
        currentFlow = null;
        currentActivity = null;
        return;
    }

    /**
     * Called to determine if a test should be generated for this flow. A default implementation will
     * return false if the flow is listed in the ignoreFlows system property. Different strategies could
     * decide to generate test based on other criteria.
     * @param flowName - the name of the flow being processed
     * @param flowDefinitionJson - the flow definition if available - otherwise null
     */
    public boolean shouldGenerateTest(String flowName, String flowDefinitionJson) {
        return !TestGenerationProperties.getInstance().getIgnoredFlows().contains(flowName);
    }


    /**
     * add a "request" directive to the test script.
     */
    public void addRequest(String flow, Collection<NameValuePair> params) {
        if (params != null) {
            StringBuffer parameters = new StringBuffer();
            String sep = "";
            for (NameValuePair nvp : params) {

                parameters.append(sep);
                parameters.append("\"");
                parameters.append(nvp.getName());
                parameters.append("\"");
                parameters.append(":");
                parameters.append("\"");
                parameters.append(nvp.getValue());
                parameters.append("\"");
                sep = ",";
            }
            writeToFileBuffer("request(\"" + flow + "\", [" + parameters.toString() + "])\n");
        } else {
            writeToFileBuffer("request(\"" + flow + "\", [:])\n");
        }

    }


    /**
     * This method can be ovveridden in sub-strategies to add specific
     * types of validation to different tests.
     * @param typicalResponse - the response when this flow was called during test creation.
     *
     */
    public void addVerification(String typicalResponse) {
        // default behaviour is to expect the typical response.
        addExpect(typicalResponse);
    }


    /**
     * Adds an expected return to the test.
     * @param json - the expected json data
     */
    public final void addExpect(String json) {

        String strValue = "";
        try {
            // Try to format a JSON string if possible.
            JSONObject jsonObj = new JSONObject(json);
            strValue = jsonObj.toString(STANDARD_INDENTATION);
        } catch (Exception e) {
            // otherwise just use the raw string
            strValue = json;
        }

        writeToFileBuffer("expect(\"\"\"" + strValue + "\"\"\")\n");
    }

    /**
     * Write expected value to test file buffer.
     * @param content - the expected value
     */
    protected void writeToFileBuffer(String content) {

        if (testFileContents == null) {
            testFileContents = new StringBuffer();
        }

        testFileContents.append(content);
        testFileContents.append("\n");
    }


    /**
     * @return the test script.
     */
    public String getTestFileContents() {
        return testFileContents.toString();
    }

    /**
     * Generates a test for an activity.
     * @param flow - flow name
     * @param activityDefinition - JSON object
     * @param requestUriString - base request url
     * @throws GenerationException if a problem occurs with test generation.
     */
    public void generateTestForActivity(String flow,
                                        JSONObject activityDefinition,
                                        String requestUriString)throws GenerationException {
        assertNotNull(activityDefinition, "flowDefinition was null, The test should depend on "
                                            + "testJsonStringIsReturnedWhenRequestingTheFlowDefinition() does it?");
        Collection<String> parameterNames = getAllParameterNames(activityDefinition);

        Collection<NameValuePair> parametersPopulatedWithBogusData = generateParameters(flow, parameterNames);
        //add the json response parameter
        parametersPopulatedWithBogusData.add(RENDER_AS_JSON);

        addRequest(flow, parametersPopulatedWithBogusData);

        addVerification(callFlowForTypicalData(requestUriString, flow, parametersPopulatedWithBogusData));

    }


    /**
     * Calls the flow to obtain a typical response.
     * @param requestUriString - request uri
     * @param flow - flow name
     * @param parametersPopulatedWithBogusData - data to send. -
     */
    public String callFlowForTypicalData(String requestUriString,
                                         String flow,
                                         Collection<NameValuePair> parametersPopulatedWithBogusData) {
        URI requestUri = URI.create(requestUriString);
        GeneralFlowRequest request = new GeneralFlowRequest(requestUri, flow, parametersPopulatedWithBogusData);

        return request.get();

    }

    /**
     * Process the activity definition to get its params.
     * @param activityDefinition - JSON object
     * @return a collection of RequestParameter in this activity.
     */
    protected Collection<RequestParameter> getRequestParameters(JSONObject activityDefinition)
                                                                                throws GenerationException {
        List<RequestParameter> requestParameters = new ArrayList<RequestParameter>();
        assertTrue(activityDefinition.has(JSON_PARAMETER_KEY), JSON_PARAMETER_KEY
                                                                    + " not found in JSONObject: "
                                                                        + activityDefinition.toString());
        JSONArray<JSONObject> parameters = activityDefinition.getJSONArray(JSON_PARAMETER_KEY);

        for (JSONObject jsonObject : parameters) {
            assertTrue(jsonObject.has(JSON_PARAMETER_NAME_KEY), JSON_PARAMETER_NAME_KEY
                                                                    + " not found in JSONObject: "
                                                                        + jsonObject.toString());
            String name = jsonObject.getString(JSON_PARAMETER_NAME_KEY);
            String type = jsonObject.getString(JSON_PARAMETER_TYPE_KEY);
            String req = jsonObject.getString(JSON_PARAMETER_REQ_KEY);
            RequestParameter requestParameter = new RequestParameter(name, type, req);
            requestParameters.add(requestParameter);
        }
        return requestParameters;
    }

    /**
     * @return a collection of parameter names in this activity.
     */
    protected Collection<String> getAllParameterNames(JSONObject activityDefinition)
                                                                                throws GenerationException {
        assertTrue(activityDefinition.has(JSON_PARAMETER_KEY), JSON_PARAMETER_KEY
                                                                    + " not found in JSONObject: "
                                                                        + activityDefinition.toString());
        JSONArray<JSONObject> parameters = activityDefinition.getJSONArray(JSON_PARAMETER_KEY);
        List<String> parameterNames = new ArrayList<String>();
        for (JSONObject jsonObject : parameters) {
            assertTrue(jsonObject.has(JSON_PARAMETER_NAME_KEY), JSON_PARAMETER_NAME_KEY
                                                                    + " not found in JSONObject: "
                                                                        + jsonObject.toString());
            parameterNames.add(jsonObject.getString(JSON_PARAMETER_NAME_KEY));
        }
        return parameterNames;
    }

    /**
     * Get the logger for this class.
     */
    public Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(this.getClass());
        }
        return this.log;
    }


    protected void debug(String msg) {
        if (getLog().isDebugEnabled()) {
            getLog().debug(msg);
        }
    }

    protected void assertNotNull(Object obj, String msg) throws GenerationException {
        if (obj == null) {
            throw new GenerationException(msg);
        }
    }

    protected void assertNotNull(Object obj) throws GenerationException {
        if (obj == null) {
            throw new GenerationException("");
        }
    }

    protected void assertFalse(boolean b, String msg) throws GenerationException {
        if (b) {
            throw new GenerationException(msg);
        }
    }

    protected void assertTrue(boolean b, String msg) throws GenerationException {
        if (!b) {
            throw new GenerationException(msg);
        }
    }

    protected void fail(String msg) throws GenerationException {
            throw new GenerationException(msg);
    }

    protected void fail(String msg, Throwable t) throws GenerationException {
        throw new GenerationException(msg, t);
    }

}
