package org.amplafi.flow.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * A class used to 
 * a) define options for {@link CommandLineClient} and 
 * b) parse command line options from a given array of String objects.
 * 
 *  API_KEY, FLOW, HOST, PORT, API_VERSION command line arguments are used to set samenamed options through command line, and get them through this class' API.
 *  
 *  E.g. if the value of API_KEY is "key" then you can set the API key option by following this example: -key ampcb_0cf02bdcd1218c9f5556b632640749a53946923cb26d0e90d2b9cf2300280497
 *  
 *  Then you can getOptionValue(API_KEY) to access this API key's value.
 * @author haris
 *
 */
public class CommandLineClientOptions {
	// holds a list of all options available to CommandLineClient
	private Options options = initOptions();
	private CommandLine cmd;

	// command line switch used to specify which API key we want to use
	public static final String API_KEY = "key";
	public static final String FLOW = "flow";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String API_VERSION = "apiv";
	
	// The options associated with this option are Java property style like options.
	// e.g. passing this argument: -DpostId=123 to the CommandLineClient tool, will give you access to a property called postId, and its value (123). 
	public static final String PARAMS = "D";

	public CommandLineClientOptions(String[] args) throws ParseException {
		CommandLineParser parser = new GnuParser();
		cmd = parser.parse(options, args);
	}

	private Options initOptions() {
		Options options = new Options();

		options.addOption(API_KEY, true, "API key");
		options.addOption(FLOW, true, "Flow name");
		options.addOption(HOST, true, "Host address");
		options.addOption(API_VERSION, true, "API version");
		options.addOption(PORT, true, "Service port");
		
		OptionBuilder.withArgName("property=value");		
		OptionBuilder.hasArgs(2);
		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription("use value for given property");
		Option parameter = OptionBuilder.create(PARAMS);
		
		options.addOption(parameter);
		
		return options;
	}

	public String getOptionValue(String optionName) {
		return cmd.getOptionValue(optionName);
	}

	public List<NameValuePair> getOptionProperties(String string) {
		return toNameValuePairList(cmd.getOptionProperties(string));
	}

	/**
	 * Converts a Properties instances to a {@link List} of {@link NameValuePair} instances, if keys and values in the Properties instance are objects. Otherwise, throws an {@link IllegalArgumentException}. 
	 * @param properties
	 * @return
	 */
	// TODO TO_HARIS find a better place for toNameValuePairList method (can be reused and is not strictly bound to this class)
	private List<NameValuePair> toNameValuePairList(Properties properties) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		if (properties != null) {
			for (Entry<Object, Object> entry: properties.entrySet()) {
				if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
					NameValuePair pair = new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString());
					pairs.add(pair);
				} else {
					throw new IllegalArgumentException("properties instance does not map Strings to Strings, got: " + entry.getKey()
							+ " => " + entry.getValue());
				}
			}
		}
		
		return pairs;
	}
}
