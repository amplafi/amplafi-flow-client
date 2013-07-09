package org.amplafi.flow.utils;

import static org.amplafi.dsl.ScriptRunner.DEFAULT_SCRIPT_PATH;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.API_KEY;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.API_VERSION;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.FLOW;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.HELP;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.HOST;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.OUTPATH;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.PORT;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.STRATEGY;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.flow.strategies.AbstractTestingStrategy;
import org.amplafi.flow.strategies.TestingStrategiesEnum;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * This class generates tests according to a generation strategy. It may be
 * called from the command line with a single parameter which is the strategy to
 * use test scripts are generated in the src/test/resources/testscripts folder
 * where they will be run as part of normal unit tests.
 */
public class DSLTestGenerator {

	/** The strategy in use */
	private AbstractTestingStrategy strategy;
	private static final String JSON_ACTIVITY_KEY = "activities";
	private static final String JSON_PARAMETER_KEY = "parameters";
	private static final String JSON_PARAMETER_NAME_KEY = "name";
	private static final String JSON_GENERIC_ERROR_MESSAGE_KEY = "errorMessage";
	private static final String JSON_GENERIC_ERROR_MESSAGE = "Exception while running flowState";
	private static final String CONFIG_ERROR_MSG = "\n***************************** \n"
			+ " This Error can be caused by the API key \n"
			+ " not being set in the pom.xml, or the\n"
			+ " sandbox host not running or configured. \n"
			+ " Please check these things first.\n"
			+ "***************************** \n";
	private static final int MIN_ACCEPTED_FLOWS = 2;

	private static final NameValuePair renderAsJson = new BasicNameValuePair(
			"fsRenderResult", "json");
	private static final NameValuePair keyParam = new BasicNameValuePair(
			"fsRenderResult", "json");

	private FarReachesServiceInfo serviceInfo;
	private String outPath;
	private String apiKey;
	private Log log;

	/**
	 * Entry point for test generator usage: ant -apiv <arg> API version -D
	 * <property=value> Specify query parameter name and value. -flow <arg> Flow
	 * name - If no know is specified tests will be generated for all flows.
	 * -help Prints this message. -host <arg> Host address -key <arg> API key
	 * -port <arg> Service port -strategy <arg> One of BogusString,
	 */
	public static void main(String[] args) throws GenerationException {
		DSLTestGeneratorCommandLineOptions cmdOptions = null;

		try {
			cmdOptions = new DSLTestGeneratorCommandLineOptions(args);
		} catch (ParseException e) {
			LogFactory.getLog(GenerationException.class).error(
					"Could not parse passed arguments, message:"
							+ e.getMessage(), e);
			System.exit(1);
		}

		if (cmdOptions.hasOption(HELP)) {
			cmdOptions.printHelp();

			System.exit(0);
		}

		if (!cmdOptions.hasOption(STRATEGY)) {
			cmdOptions.printHelp();

			System.exit(0);
		}

		String apiKey = cmdOptions.getOptionValue(API_KEY);
		String host = cmdOptions.getOptionValue(HOST);
		String port = cmdOptions.getOptionValue(PORT);
		String apiversion = cmdOptions.getOptionValue(API_VERSION);
		String strategyName = cmdOptions.getOptionValue(STRATEGY);
		String outPath = cmdOptions.getOptionValue(OUTPATH);

		TestingStrategiesEnum strategy = Enum.valueOf(
				TestingStrategiesEnum.class, strategyName);
		DSLTestGenerator gen = new DSLTestGenerator(strategy.getStrategy(),
				apiKey, host, port, apiversion, outPath);

		if (cmdOptions.hasOption(FLOW)) {
			String flowName = cmdOptions.getOptionValue(FLOW);
			gen.generateOne(flowName);
		} else {
			gen.generate();
		}

	}

	/**
	 * Constructor for the generator.
	 * 
	 * @param strategy
	 *            - the current strategy
	 * @param apiKey
	 *            - the api key
	 * @param host
	 *            - host address
	 * @param port
	 *            - port
	 * @param apiversion
	 *            - api version
	 * @param outPath
	 *            - path to generate the files.
	 */
	public DSLTestGenerator(AbstractTestingStrategy strategy, String apiKey,
			String host, String port, String apiversion, String outPath) {
		this.strategy = strategy;
		this.outPath = outPath;
		this.apiKey = apiKey;
		this.serviceInfo = new FarReachesServiceInfo(host, port, "apiv1");
	}

	/**
	 * Generates a bunch of test scripts for this strategy.
	 */
	public void generate() throws GenerationException {
		List<String> flows = getListOfFlows();
		for (String flow : flows) {
			String ignoredFlowsStr = System.getProperty("ignoreFlows", "");
			System.err.println("Will ignore flows" + ignoredFlowsStr);
			if (!ignoredFlowsStr.contains(flow)) {
				if (strategy.shouldGenerateTest(flow, null)) {
					strategy.newTest(flow, "");
					generateOne(flow);
					writeTestFile();
					strategy.endTest();
				}
			}

		}

	}

	private void writeTestFile() throws GenerationException {
		try {

			String outFile = null;
			if (this.outPath == null) {
				outFile = "../" + DEFAULT_SCRIPT_PATH + "/"
						+ strategy.getFileName();
			} else {
				outFile = this.outPath + "/" + strategy.getFileName();
			}

			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			out.write(strategy.getTestFileContents());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new GenerationException("Error writing file", e);
		}

	}

	/**
	 * Provides all flow names for testing
	 * 
	 * @return the names of all the flows supported by this server api
	 */
	public List<String> getListOfFlows() throws GenerationException {

		// Get list of supported flow types from server.
		List<String> flowList = (new GeneralFlowRequest(serviceInfo, null,
				null, renderAsJson)).listFlows().asList();

		// If the size of the flowlist is less than 2 then it implies an error
		// has occured.
		if (flowList.size() >= MIN_ACCEPTED_FLOWS) {
			debug("List of flows " + flowList);

		} else {
			fail(CONFIG_ERROR_MSG, null);
		}

		return flowList;
	}

	/**
	 * Generates a test scripts for this strategy and this Flow.
	 * 
	 * @param flow
	 */
	public void generateOne(String flow) throws GenerationException {

		debug("@@@ testConductor for flow " + flow);
		assertNotNull("flow should not be null", flow);
		//
		// This method calls other methods directly parsing data as parameters
		// instead of
		// via member variables.
		String flowDefinitionResult = getFlowDefinitionResultString(flow);

		// This will validate each of the flow descriptions and then proceed to
		// make calls to their starting
		// activities.
		JSONObject flowDefinition = generateTestFromFlowDefinitionResultJson(
				flow, flowDefinitionResult);

	}

	/**
	 * Verify that request returns a non-null non-empty string.
	 * 
	 * @return The string containing the flow definition JSON data for the
	 *         current flow.
	 */
	public String getFlowDefinitionResultString(String flow)
			throws GenerationException {
		debug("Sending request to " + serviceInfo);
		String messageStart = "Returned FlowDefinition for " + flow + " ";
		String flowDefinitionResult = null;
		try {
			flowDefinitionResult = new GeneralFlowRequest(serviceInfo, apiKey,
					flow).describeFlowRaw();
			debug("@@@ got flowDefinitionResult ");
			assertNotNull(flowDefinitionResult, CONFIG_ERROR_MSG);
			assertFalse(flowDefinitionResult.trim().equals(""), messageStart
					+ "was an empty String");
		} catch (IllegalArgumentException iae) {
			fail("This Error can be caused by the API key not being set in the pom",
					iae);
		}
		return flowDefinitionResult;
	}

	/**
	 * Determine whether the returned data is valid JSON data and whether it
	 * contains a list of activities which contain parameters for the flow. Then
	 * generate script from that.
	 * 
	 * @param flow
	 *            - the flow name
	 * @param flowDefinitionResult
	 *            - the JSON definition of this flow.
	 */
	private JSONObject generateTestFromFlowDefinitionResultJson(String flow,
			String flowDefinitionResult) throws GenerationException {
		debug("flowDefinitionResult = " + flowDefinitionResult);
		assertTrue(flowDefinitionResult.charAt(0) == '{',
				"Flow definition is not a JSONObject, definition result: "
						+ flowDefinitionResult);
		JSONObject flowDefinition = null;
		try {
			// parse the flow definition into a JSON
			flowDefinition = new JSONObject(flowDefinitionResult);

			assertTrue(flowDefinition.has("flowTitle"),
					"flowTitle not found in flow description ");

			// Two flows do not have activities, these are GetWordpressPlugin
			// and GetWordpressPluginInfo

			// Other flows seem to have bugs. Having identified those bugs we
			// need to ingore them so that we can move on and
			// find new bugs.
			// Some flows have no activities and can be called
			if (flowDefinition.has(JSON_ACTIVITY_KEY)) {
				debug("@ has JSON_ACTIVITY_KEY ");
				// Obtain the Activity list from the JSON data,
				JSONArray<JSONObject> activities = flowDefinition
						.getJSONArray(JSON_ACTIVITY_KEY);
				assertFalse(activities.isEmpty(),
						"\"Activities\" array was empty.");

				// Loop over the activities in the flow definition and determine
				// that each has a parameters attribute.
				for (JSONObject activity : activities) {
					debug("@ Looping over activities ");
					// certain activities in flows such as AuditManagement do
					// not have parameters.
					JSONArray<JSONObject> activityParameters = activity
							.getJSONArray(JSON_PARAMETER_KEY);
					if (!activityParameters.isEmpty()) {
						debug("@ Activity has parameters ");
						// if an activity does have parameters, then we should
						// check that each parameter definition
						// has the correct attributes.
						for (JSONObject param : activityParameters) {
							debug("@ param start ");
							assertTrue(param.has("name"),
									"name not found for parameter in activity ");
							assertTrue(param.has("type"),
									"type not found for parameter in activity ");
							assertTrue(param.has("req"),
									"req not found for parameter in activity ");
						}

					}

					// Now we call the flow current flow but with each of the
					// parameters set to a string
					strategy.generateTestForActivity(flow, apiKey, activity,
							serviceInfo);

				}
			} else {
				debug("@ No JSON_ACTIVITY_KEY ");
				// flow has no activities or parameters so we can just call it
				// directly
				GeneralFlowRequest request = new GeneralFlowRequest(
						serviceInfo, apiKey, flow);
				strategy.addRequest(flow, null);

				strategy.addVerification(request.get());
				// testFlowParametersSetWithStringsResultJson(request.get());
			}

		} catch (JSONException jsonException) {
			fail("Flow definition not valid JSON, JSON Error: "
					+ jsonException.getMessage());
		}

		return flowDefinition;

	}

	/**
	 * This utility method tests for repeating parameters in the flow's activity
	 * definition. In other words it fails if a parameter is defined twice.
	 */
	private void testFlowDefinitionParameterRepeats(String flow,
			JSONArray<JSONObject> jsonParameters) throws GenerationException {

		Set<String> parameters = new HashSet<String>();
		List<String> repeats = new ArrayList<String>();
		for (JSONObject jsonObject : jsonParameters) {
			String parameterName = jsonObject
					.getString(JSON_PARAMETER_NAME_KEY);
			if (!parameters.add(parameterName)) {
				repeats.add(parameterName);
			}
		}
		assertTrue(repeats.isEmpty(),
				"These parameters where repeated for flowtype " + flow + " :"
						+ repeats.toString());
	}

	/**
	 * Validates a generic flow response
	 * 
	 * @param jsonStr
	 *            - the flow response raw data.
	 */
	public JSONObject testFlowParametersSetWithStringsResultJson(String jsonStr)
			throws GenerationException {
		JSONObject jsonResult = null;

		// certain flows may download files, in this case the General flow
		// request will return a specific code.
		if (jsonStr != GeneralFlowRequest.APPLICATION_ZIP) {
			try {

				// TO_PAT: Is a JSONObject allways returned?
				jsonResult = new JSONObject(jsonStr);
			} catch (JSONException jsonException) {

				fail("Flow definition not valid JSON, JSON Error: "
						+ jsonException.getMessage());
			}

			// We would expect most flows to fail when random parameters are
			// sent but this is not necessarily true.
			// So we determine is the flow has failed or not and then validate
			// the response.

			if (jsonResult.has(JSON_GENERIC_ERROR_MESSAGE_KEY)) {
				assertFalse(jsonResult
						.getString(JSON_GENERIC_ERROR_MESSAGE_KEY).trim()
						.equalsIgnoreCase(JSON_GENERIC_ERROR_MESSAGE),
						"Generic Message, change this message to state the problem more specifically: "
								+ jsonResult.toString());
			}
		}

		return jsonResult;
	}

	/**
	 * Do debug logging
	 * 
	 * @param msg
	 */
	private void debug(String msg) {
		if (getLog().isDebugEnabled()) {
			getLog().debug(msg);
		}
	}

	/**
	 * Complain if condition not met (Hangover from when this was a unit test)
	 * 
	 * @param obj
	 * @param msg
	 * @throws GenerationException
	 */
	private void assertNotNull(Object obj, String msg)
			throws GenerationException {
		if (obj == null) {
			throw new GenerationException(msg);
		}
	}

	/**
	 * Complain if condition not met (Hangover from when this was a unit test)
	 * 
	 * @param obj
	 * @throws GenerationException
	 */
	private void assertNotNull(Object obj) throws GenerationException {
		if (obj == null) {
			throw new GenerationException("");
		}
	}

	/**
	 * Complain if condition not met (Hangover from when this was a unit test)
	 * 
	 * @param b
	 * @param msg
	 * @throws GenerationException
	 */
	private void assertFalse(boolean b, String msg) throws GenerationException {
		if (b) {
			throw new GenerationException(msg);
		}
	}

	/**
	 * Complain if condition not met (Hangover from when this was a unit test)
	 * 
	 * @param b
	 * @param msg
	 * @throws GenerationException
	 */
	private void assertTrue(boolean b, String msg) throws GenerationException {
		if (!b) {
			throw new GenerationException(msg);
		}
	}

	/**
	 * Thros Exception
	 * 
	 * @param msg
	 * @throws GenerationException
	 */
	private void fail(String msg) throws GenerationException {
		throw new GenerationException(msg);
	}

	/**
	 * Throw exception
	 * 
	 * @param msg
	 * @param t
	 * @throws GenerationException
	 */
	private void fail(String msg, Throwable t) throws GenerationException {
		throw new GenerationException(msg, t);
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

}
