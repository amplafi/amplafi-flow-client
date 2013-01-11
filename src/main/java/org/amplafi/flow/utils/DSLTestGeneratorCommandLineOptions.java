package org.amplafi.flow.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.amplafi.flow.strategies.TestingStrategiesEnum;
/**
 * This is a class used to parse options for the command line test generator.
 * 
 * @author Paul
 */
public class DSLTestGeneratorCommandLineOptions extends AbstractCommandLineClientOptions {

	// command line switch used to specify which API key we want to use
	public static final String API_KEY = "key";
	public static final String FLOW = "flow";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String API_VERSION = "apiv";
	public static final String DESCRIBE = "desc";
	public static final String HELP = "help";
	public static final String TUTORIAL = "tutorial";
	public static final String STRATEGY = "strategy";
	public static final String OUTPATH = "out";

	// The options associated with this option are Java property style like options.
	// e.g. passing this argument: -DpostId=123 to the CommandLineClient tool, will give you access to a property called postId, and its value (123). 
	public static final String PARAMS = "D";
	
	
	public DSLTestGeneratorCommandLineOptions(String[] args) throws ParseException {
		super(args);
	}

	protected Options initOptions() {
		Options options = new Options();

		options.addOption(API_KEY, true, "API key");
		options.addOption(FLOW, true, "Flow name - If no know is specified tests will be generated for all flows.");
		options.addOption(STRATEGY, true, "One of: " + TestingStrategiesEnum.listStrategyNames());
		options.addOption(HOST, true, "Host address");
		options.addOption(API_VERSION, true, "API version");			
		options.addOption(PORT, true, "Service port");
		options.addOption(OUTPATH, true, "The output path for test scripts");

		options.addOption(HELP, false, "Prints this message.");
				
		OptionBuilder.withArgName("property=value");		
		OptionBuilder.hasArgs(2);
		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription("Specify query parameter name and value.");
		Option parameter = OptionBuilder.create(PARAMS);
		
		options.addOption(parameter);
		
		return options;
	}

}
