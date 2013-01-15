package org.amplafi.flow.utils;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A class used to 
 * a) define options for {@link CommandLineClient} and 
 * b) parse command line options from a given array of String objects.
 * 
 *  API_KEY, FLOW, HOST, PORT, API_VERSION command line arguments are used to set samenamed options through command line, and get them through this class' API.
 *  E.g. if the value of API_KEY is "key" then you can set the API key option by following this example: -key ampcb_0cf02bdcd1218c9f5556b632640749a53946923cb26d0e90d2b9cf2300280497
 *  Then you can getOptionValue(API_KEY) to access this API key's value.
 *  
 *  Another example of using arguments with {@link CommandLineClient}:
 *  -key ampcb_0cf02bdcd1218c9f5556b632640749a53946923cb26d0e90d2b9cf2300280497 -flow EnvelopeStatusesFlow -host http://sandbox.farreach.es -port 8080 -apiv apiv1 -Dpostid=123
 * @author haris
 *
 */
public class CommandLineClientOptions extends AbstractCommandLineClientOptions {

	// command line switch used to specify which API key we want to use
	public static final String API_KEY = "key";
	public static final String FLOW = "flow";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String API_VERSION = "apiv";
	public static final String DESCRIBE = "desc";
	public static final String FORMAT = "format";
	public static final String HELP = "help";
	public static final String TUTORIAL = "tutorial";

	// The options associated with this option are Java property style like options.
	// e.g. passing this argument: -DpostId=123 to the CommandLineClient tool, will give you access to a property called postId, and its value (123). 
	public static final String PARAMS = "D";

	public CommandLineClientOptions(String[] args) throws ParseException {
		super(args);
	}

	protected Options initOptions() {
		Options options = new Options();

		options.addOption(API_KEY, true, "API key");
		options.addOption(FLOW, true, "Flow name");
		options.addOption(HOST, true, "Host address");
		options.addOption(API_VERSION, true, "API version");			
		options.addOption(PORT, true, "Service port");

		options.addOption(DESCRIBE, false, "If used with no flow specified, lists all flows. If used with a flow specified, returns a list of flow properties.");
		options.addOption(FORMAT, false, "Switches JSON formatting on and off.");
		options.addOption(HELP, false, "Prints this message.");
		options.addOption(TUTORIAL, false, "Use this to run against tutorial server without a key.");
		
		OptionBuilder.withArgName("property=value");		
		OptionBuilder.hasArgs(2);
		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription("Specify query parameter name and value.");
		Option parameter = OptionBuilder.create(PARAMS);
		
		options.addOption(parameter);
		
		return options;
	}
}
