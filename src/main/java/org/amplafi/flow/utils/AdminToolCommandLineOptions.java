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
import java.util.Properties;  


public class AdminToolCommandLineOptions extends AbstractCommandLineClientOptions {

	// command line switch used to specify which API key we want to use
	public static final String API_KEY = "key";
	public static final String FLOW = "flow";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String API_VERSION = "apiv";
	public static final String LIST = "l";
	public static final String NOCACHE = "x";
	public static final String FILE_PATH = "f";
	

	// The options associated with this option are Java property style like options.
	// e.g. passing this argument: -DpostId=123 to the CommandLineClient tool, will give you access to a property called postId, and its value (123). 
	//public static final String PARAMS = "D";
	
	
	public AdminToolCommandLineOptions(String[] args) throws ParseException {
		super(args);
	}

	protected Options initOptions() {
		//Options options = super.initOptions();
		Options options = new Options();
		options.addOption(LIST, false, "List Scripts");
		options.addOption(API_KEY, true, "API key");
		options.addOption(HOST, true, "Host address");
		options.addOption(API_VERSION, true, "API version");			
		options.addOption(PORT, true, "Service port");
		options.addOption(NOCACHE, false, "Don't use cached server credentials");
		options.addOption(LIST, false, "List In-built Scripts");
		options.addOption(FILE_PATH, true, "Ad-hoc script File Path");
		
				
		OptionBuilder.withArgName("property=value");		
		OptionBuilder.hasArgs(2);
		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription("Specify query .");
	//	Option parameter = OptionBuilder.create(PARAMS);
		
	//	options.addOption(parameter);
		
		return options;
	}
	
	public void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getPrinter(), options);
	}
	
	private String getPrinter(){
		Properties props=System.getProperties();
		String osName = props.getProperty("os.name");
		String newline = "\n";
		if(osName.contains("Windows")){
			newline = "\n\r";
			
		}
		return newline+"FAdmin -l : To list the currently available commands"+newline
				+"FAdmin -x : To reset host, port, api version and key"+newline
				+"            FAdmin example <param1Name>=<param1Value> <param2Name>=<param2Value> ... : To run one of the commands enter the command name";
	}

}
