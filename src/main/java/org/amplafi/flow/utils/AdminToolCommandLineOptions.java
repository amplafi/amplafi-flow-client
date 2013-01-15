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


public class AdminToolCommandLineOptions extends CommandLineClientOptions {

	// command line switch used to specify which API key we want to use
	public static final String LIST = "l";
	public static final String NOCACHE = "x";
	

	// The options associated with this option are Java property style like options.
	// e.g. passing this argument: -DpostId=123 to the CommandLineClient tool, will give you access to a property called postId, and its value (123). 
	public static final String PARAMS = "D";
	
	
	public AdminToolCommandLineOptions(String[] args) throws ParseException {
		super(args);
	}

	protected Options initOptions() {
		Options options = super.initOptions();


		options.addOption(LIST, false, "List Scripts");
		options.addOption(NOCACHE, false, "Don't use cached server credentials");
		
				
		OptionBuilder.withArgName("property=value");		
		OptionBuilder.hasArgs(2);
		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription("Specify query .");
		Option parameter = OptionBuilder.create(PARAMS);
		
		options.addOption(parameter);
		
		return options;
	}

}