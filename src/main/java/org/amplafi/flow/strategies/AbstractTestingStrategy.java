/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.amplafi.flow.strategies;

import groovy.lang.GroovyShell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amplafi.flow.TestGenerationProperties;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.flow.utils.GenerationException;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Abstract Super class for all testing strategies. This class defines methods
 * that are useful to most strategies.
 *
 * @author paul
 */
public abstract class AbstractTestingStrategy {

	private StringBuffer testFileContents = null;
	private int sequence = 0;
	private String currentFlow = null;
	private String currentActivity = null;
	private Log log;
	private String actualName;
	private static final int STANDARD_INDENTATION = 4;
	private static final String JSON_PARAMETER_KEY = "parameters";
	private static final String JSON_PARAMETER_NAME_KEY = "name";
	private static final String JSON_PARAMETER_TYPE_KEY = "type";
	private static final String JSON_PARAMETER_REQ_KEY = "req";
	protected static final NameValuePair RENDER_AS_JSON = new BasicNameValuePair(
			"fsRenderResult", "json");

	/**
	 * @return the name of this strategy
	 */
	public abstract String getName();

	/**
	 * @return the name of this strategy
	 */
	public String getFileName() {
		return String.format("%04d", sequence) + "" + getName() + "_"
				+ currentFlow + ".groovy";
	}

	/**
	 * Generates parameters for the request.
	 *
	 * @param flow
	 *            - flow name.
	 * @param parameterNames
	 *            - list of paramenter names.
	 * @return Collection of parameter name and value.
	 */
	public abstract Collection<NameValuePair> generateParameters(String flow,
			Collection<String> parameterNames);

	/**
	 * Initializes a new test script.
	 *
	 * @param flow
	 *            -the current flow name
	 * @param activity
	 *            - the current activity - unused
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
	 * Called to determine if a test should be generated for this flow. A
	 * default implementation will return false if the flow is listed in the
	 * ignoreFlows system property. Different strategies could decide to
	 * generate test based on other criteria.
	 *
	 * @param flowName
	 *            - the name of the flow being processed
	 * @param flowDefinitionJson
	 *            - the flow definition if available - otherwise null
	 * @return whether it should generate test or not.
	 */
	public boolean shouldGenerateTest(String flowName, String flowDefinitionJson) {
		return !TestGenerationProperties.getInstance().getIgnoredFlows()
				.contains(flowName);
	}

	/**
	 * add a "request" directive to the test script.
	 *
	 * @param flow
	 *            - the name of the flow being processed
	 * @param params
	 *            - the params of request
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
			writeToFileBuffer("request(\"" + flow + "\", ["
					+ parameters.toString() + "])\n");
		} else {
			writeToFileBuffer("request(\"" + flow + "\", [:])\n");
		}
	}

	/**
	 * This method can be ovveridden in sub-strategies to add specific types of
	 * validation to different tests.
	 *
	 * @param typicalResponse
	 *            - the response when this flow was called during test creation.
	 */
	public void addVerification(String typicalResponse) {
		// default behaviour is to expect the typical response.
		addExpect(typicalResponse);
	}

	/**
	 * Adds an expected return to the test.
	 *
	 * @param json
	 *            - the expected json data
	 */
	public final void addExpect(String json) {
		String strValue = "";
		try {
			// Try to format a JSON string if possible.
			JSONObject jsonObj = new JSONObject(json);
			strValue = jsonObj.toString(STANDARD_INDENTATION);
		} catch (JSONException e) {
			// otherwise just use the raw string
			// strValue = json;
			try {
				JSONArray strValueArray = new JSONArray(json);
				strValue = strValueArray.toString();
			} catch (Exception e2) {
				return;
			}
		}
		writeToFileBuffer("expect(\"\"\"" + strValue + "\"\"\")\n");
	}

	/**
	 * Adds an expected return to the test.
	 *
	 * @param json
	 *            - the expected json data
	 * @param flowName
	 *            - the name of flow.
	 */
	public final void addExpectWithIgnoredPaths(String json, String flowName,
			Set<String> ignores) {
		String strValue = "";
		try {
			// Try to format a JSON string if possible.
			JSONObject jsonObj = new JSONObject(json);
			strValue = jsonObj.toString(STANDARD_INDENTATION);
		} catch (Exception e) {
			// otherwise just use the raw string
			strValue = json;
		}
		if (ignores.isEmpty()) {
			writeToFileBuffer("expect(\"\"\"" + strValue + "\"\"\")\n");
		} else {
			Set<String> paths = ignores;
			StringBuffer pathList = new StringBuffer("[");
			String sep = "";
			for (String path : paths) {
				pathList.append(sep);
				pathList.append("\"");
				pathList.append(path);
				pathList.append("\"");
				sep = ",";
			}
			pathList.append("]");
			if (testFileContents.toString().contains("def ignorePathList")) {
				writeToFileBuffer("ignorePathList = " + pathList + ";\n");
			} else {
				writeToFileBuffer("def ignorePathList = " + pathList + ";\n");
			}
			writeToFileBuffer("expect(\"\"\"" + strValue
					+ "\"\"\",ignorePathList)\n");
		}
	}

	/**
	 * Generate standard ignore list in file.
	 *
	 * @param json1
	 *            is the json data in first request
	 * @param json2
	 *            is the json data in second request
	 */
	public Set<String> generateStandarIgnoreList(String json1, String json2,
			String flow) {
		Set<String> ignoreList = new HashSet<String>();
		try {
			if (json1 != null && json2 != null) {
				JSONObject jsonObject1 = new JSONObject(json1);
				JSONObject jsonObject2 = new JSONObject(json2);
				System.out.println("#####jsonObject1 len = "
						+ jsonObject1.length());
				System.out.println("#####jsonObject2 len = "
						+ jsonObject2.length());
				if (jsonObject1.length() == jsonObject2.length()) {
					ignoreList = compare(jsonObject1, jsonObject2, "/",
							ignoreList);
				}
			}
		} catch (JSONException ge) {
			ge.printStackTrace();
		}
		Map<String, List<String>> allIgnores = getStandardIgnores();
		if (allIgnores != null) {
			List<String> thisFlowIgnores = allIgnores.get(flow);
			if (thisFlowIgnores != null) {
				ignoreList.addAll(thisFlowIgnores);
			}
		}
		return ignoreList;
	}

	//
	// private void writeIngnoresToFile(List<String> ingnoreList, String flow) {
	// try {
	// File file = new File("StandarIgnoreList.groovy");
	// String script = getContents(file);
	// FileWriter fileWriter = new FileWriter("StandarIgnoreList.groovy",
	// true);
	// //
	// fileWriter.write("Map<String, List<String>> ret = new HashMap<String, List<String>>();");
	// StringBuffer sb = new StringBuffer();
	// String newLine = System.getProperty("line.separator");
	// if(!script.contains(flow)){
	// sb.append(newLine);
	// sb.append("ret.put(\"");
	// sb.append(flow);
	// sb.append("\",");
	// // fileWriter.write(ingnoreList.toString());
	// sb.append("[");
	// String sep = "";
	// for (String ingnorePath : ingnoreList) {
	// sb.append(sep);
	// sb.append("\"");
	// sb.append(ingnorePath);
	// sb.append("\"");
	// sep = ",";
	// }
	// sb.append("]");
	//
	// sb.append(");");
	// }
	// fileWriter.write(sb.toString());
	// //
	// fileWriter.write("Map<String, List<String>> ret = [\""+flow+"\":"+ingnoreList+"]");
	// // for(String ingnore : ingnoreList){
	// // fileWriter.write(ingnore+",");
	// // }
	// fileWriter.flush();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	//
	// public void writeReturnRetToIngnoresFile() {
	// System.out.println("###########wirte return");
	// try {
	// File file = new File("StandarIgnoreList.groovy");
	// String script = getContents(file);
	// if(!script.contains("return ret;")){
	// System.out.println("###########not contains retrun");
	// FileWriter fileWriter = new FileWriter("StandarIgnoreList.groovy",
	// true);
	// fileWriter.write(System.getProperty("line.separator"));
	// fileWriter.write("return ret;");
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Compare of two JSONObjects.
	 *
	 * @param json1
	 *            is the json data in first request
	 * @param json2
	 *            is the json data in second request
	 * @param currentPath
	 *            is the current property path
	 * @param ignoreList
	 *            is list of path which is ignore when compare
	 */
	private Set<String> compare(JSONObject json1, JSONObject json2,
			String currentPath, Set<String> ignoreList) {

		if (json1 != null && json2 != null) {
			JSONArray jsonNames1 = json1.names();
			JSONArray jsonNames2 = json2.names();
			if (jsonNames1 != null && jsonNames2 != null) {
				// loops all of the property name in the object
				for (int i = 0; i < jsonNames2.length(); i++) {
					String jsonName1 = (String) jsonNames1.get(i);
					String jsonName2 = (String) jsonNames2.get(i);
					Object jsonValue1 = json2.get(jsonName1);
					Object jsonValue2 = json1.get(jsonName2);

					if (jsonName1.equals(jsonName2)) {
						if (jsonValue2 instanceof JSONObject
								&& jsonValue1 instanceof JSONObject) {
							try {
								compare((JSONObject) jsonValue2,
										(JSONObject) jsonValue1, currentPath
												+ jsonName2 + "/", ignoreList);
							} catch (JSONException ex) {
								ex.printStackTrace();
							}
						} else {
							if (!jsonValue1.equals(jsonValue2)) {
								ignoreList.add(currentPath + jsonName2 + "/");
							}
						}
					}
				}
			}
		}
		return ignoreList;
	}

	/**
	 * Write expected value to test file buffer.
	 *
	 * @param content
	 *            - the expected value
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
	 *
	 * @param flow
	 *            - flow name
	 * @param activityDefinition
	 *            - JSON object
	 * @param requestUriString
	 *            - base request url
	 * @throws GenerationException
	 *             if a problem occurs with test generation.
	 */
	public void generateTestForActivity(String flow, String key,
			JSONObject activityDefinition, FarReachesServiceInfo service)
			throws GenerationException {
		assertNotNull(
				activityDefinition,
				"flowDefinition was null, The test should depend on "
						+ "testJsonStringIsReturnedWhenRequestingTheFlowDefinition()"
						+ " does it?");
		Collection<String> parameterNames = getAllParameterNames(activityDefinition);
		Collection<NameValuePair> parametersPopulatedWithBogusData = generateParameters(
				flow, parameterNames);
		// add the json response parameter
		parametersPopulatedWithBogusData.add(RENDER_AS_JSON);
		addRequest(flow, parametersPopulatedWithBogusData);
		addVerification(callFlowForTypicalData(service, key, flow,
				parametersPopulatedWithBogusData));
	}

	/**
	 * Calls the flow to obtain a typical response.
	 *
	 * @param requestUriString
	 *            - request uri
	 * @param flow
	 *            - flow name
	 * @param parametersPopulatedWithBogusData
	 *            - data to send. -
	 * @return the request string.
	 */
	public String callFlowForTypicalData(FarReachesServiceInfo service,
			String key, String flow,
			Collection<NameValuePair> parametersPopulatedWithBogusData) {

		GeneralFlowRequest request = new GeneralFlowRequest(service, key, flow,
				parametersPopulatedWithBogusData);
		return request.get();
	}

	/**
	 * Process the activity definition to get its params.
	 *
	 * @param activityDefinition
	 *            - JSON object
	 * @return a collection of RequestParameter in this activity.
	 * @throws GenerationException
	 *             if a problem occurs with test generation.
	 */
	protected Collection<RequestParameter> getRequestParameters(
			JSONObject activityDefinition) throws GenerationException {
		List<RequestParameter> requestParameters = new ArrayList<RequestParameter>();
		assertTrue(activityDefinition.has(JSON_PARAMETER_KEY),
				JSON_PARAMETER_KEY + " not found in JSONObject: "
						+ activityDefinition.toString());
		JSONArray<JSONObject> parameters = activityDefinition
				.getJSONArray(JSON_PARAMETER_KEY);
		for (JSONObject jsonObject : parameters) {
			assertTrue(jsonObject.has(JSON_PARAMETER_NAME_KEY),
					JSON_PARAMETER_NAME_KEY + " not found in JSONObject: "
							+ jsonObject.toString());
			String name = jsonObject.getString(JSON_PARAMETER_NAME_KEY);
			String type = jsonObject.getString(JSON_PARAMETER_TYPE_KEY);
			String req = jsonObject.getString(JSON_PARAMETER_REQ_KEY);
			RequestParameter requestParameter = new RequestParameter(name,
					type, req);
			requestParameters.add(requestParameter);
		}
		return requestParameters;
	}

	/**
	 * @return a collection of parameter names in this activity.
	 * @param activityDefinition
	 *            - JSON object
	 * @throws GenerationException
	 *             if a problem occurs with test generation.
	 */
	protected Collection<String> getAllParameterNames(
			JSONObject activityDefinition) throws GenerationException {
		assertTrue(activityDefinition.has(JSON_PARAMETER_KEY),
				JSON_PARAMETER_KEY + " not found in JSONObject: "
						+ activityDefinition.toString());
		JSONArray<JSONObject> parameters = activityDefinition
				.getJSONArray(JSON_PARAMETER_KEY);
		List<String> parameterNames = new ArrayList<String>();
		for (JSONObject jsonObject : parameters) {
			assertTrue(jsonObject.has(JSON_PARAMETER_NAME_KEY),
					JSON_PARAMETER_NAME_KEY + " not found in JSONObject: "
							+ jsonObject.toString());
			parameterNames.add(jsonObject.getString(JSON_PARAMETER_NAME_KEY));
		}
		return parameterNames;
	}

	private Map<String, List<String>> standardIgnores;

	/**
	 * Get standard ignores.
	 *
	 * @return a map of ignore list.
	 */
	public Map<String, List<String>> getStandardIgnores() {
		if (standardIgnores == null) {
			// TODO move this into resources.
			try {
				GroovyShell shell = new GroovyShell(this.getClass()
						.getClassLoader());
				File file = new File("StandarIgnoreList.groovy");
				String script = getContents(file);
				standardIgnores = (Map<String, List<String>>) shell
						.evaluate(script);
			} catch (Exception e) {
				e.printStackTrace();
				standardIgnores = new HashMap<String, List<String>>();
			}
		}
		return standardIgnores;
	}

	public String getContents(File aFile) {
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();
		// use buffering, reading one line at a time
		// FileReader always assumes default encoding is OK!
		try(FileReader fileReader = new FileReader(aFile); BufferedReader input = new BufferedReader(fileReader)) {
			String line = null; // not declared within while loop
			/*
			 * readLine is a bit quirky : it returns the content of a line
			 * MINUS the newline. it returns null only for the END of the
			 * stream. it returns an empty String if two newlines appear in
			 * a row.
			 */
			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return contents.toString();
	}

	/**
	 * Get the logger for this class.
	 *
	 * @return a log
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

	protected void assertNotNull(Object obj, String msg)
			throws GenerationException {
		if (obj == null) {
			throw new GenerationException(msg);
		}
	}

	protected void assertNotNull(Object obj) throws GenerationException {
		if (obj == null) {
			throw new GenerationException("");
		}
	}

	protected void assertFalse(boolean b, String msg)
			throws GenerationException {
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
