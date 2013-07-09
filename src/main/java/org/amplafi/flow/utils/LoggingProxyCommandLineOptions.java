package org.amplafi.flow.utils;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command line options parser for the Test Generation Proxy
 * 
 */
public class LoggingProxyCommandLineOptions extends
		AbstractCommandLineClientOptions {

	// command line switch used to specify which API key we want to use
	public static final String HOST = "host";
	public static final String HOST_PORT = "hostPort";
	public static final String CLIENT_PORT = "clientPort";
	public static final String VERBOSE = "verbose";
	public static final String OUT_FILE = "out";
	public static final String HELP = "help";

	/**
	 * Constructor see AdminTool.md document for parameter string formats.
	 */
	public LoggingProxyCommandLineOptions(String[] args) throws ParseException {
		super(args);
	}

	/**
	 * Initialise the options.
	 */
	protected Options initOptions() {
		// Options options = super.initOptions();
		Options options = new Options();

		options.addOption(HOST, true, "Host address.");
		options.addOption(HOST_PORT, true, "Host Port.");
		options.addOption(CLIENT_PORT, true, "Port Client connects to.");
		options.addOption(OUT_FILE, true, "Ad-hoc script File Path.");
		options.addOption(VERBOSE, false, "More verbose output.");
		options.addOption(HELP, false, "print script usage.");

		return options;
	}

	public void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getPrinter(formatter), options);
	}

	private String getPrinter(HelpFormatter formatter) {

		String newline = formatter.getNewLine();
		return newline
				+ "Runs a logging proxy between the plugin and the wireserver.";
	}

}
