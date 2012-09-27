package org.amplafi.flow.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A class used to define options for {@link CommandLineClient} and parse command line options from a given array of String objects. 
 * @author haris
 *
 */
public class CommandLineClientOptions {
	private Options options = initOptions();
	private CommandLine cmd;

	// command line switch used to specify which API key we want to use
	public static final String API_KEY = "key";
	public static final String FLOW = "flow";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String API_VERSION = "apiv";
	public static final String PARAMS = "params";

	public CommandLineClientOptions(String[] args) {
		CommandLineParser parser = new GnuParser();

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Options initOptions() {
		Options options = new Options();

		options.addOption(API_KEY, true, "API key");
		options.addOption(FLOW, true, "Flow name");
		options.addOption(HOST, true, "Host address");
		options.addOption(API_VERSION, true, "API version");
		options.addOption(PORT, true, "Service port");
		options.addOption(PARAMS, true, "HTTP Query parameters");
		
		return options;
	}

	public String getOptionValue(String optionName) {
		return cmd.getOptionValue(optionName);
	}
}
