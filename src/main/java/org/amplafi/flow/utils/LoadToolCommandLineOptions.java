package org.amplafi.flow.utils;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command line options parser for the Test Generation Proxy
 *
 */
public class LoadToolCommandLineOptions extends AbstractCommandLineClientOptions {

    // command line switch used to specify which API key we want to use
    public static final String HOST = "host";
    public static final String HOST_PORT = "hostPort";
    public static final String VERBOSE = "verbose";
    public static final String REPORT = "reportFile";
    public static final String SCRIPT = "script";
    public static final String NUM_THREADS = "numThreads";
    public static final String FREQUENCY = "frequency";
    public static final String HELP = "help";

    /**
     * Constructor see AdminTool.md document for parameter string formats.
     */
    public LoadToolCommandLineOptions(String[] args) throws ParseException {
        super(args);
    }

    /**
     * Initialise the options.
     */
    protected Options initOptions() {
        //Options options = super.initOptions();
        Options options = new Options();

        options.addOption(HOST, true, "Host address.");
        options.addOption(HOST_PORT, true, "Host Port.");
        options.addOption(REPORT, true, "File to write report to. Otherwise will write to screen");
        options.addOption(SCRIPT, true, "Test script to run.");
        options.addOption(NUM_THREADS, true, "Number of concurrent Threads.");
        options.addOption(FREQUENCY, true, "Max Frequency per thread (e.g. 100 per second) -1 is max possible.");
        options.addOption(VERBOSE, false, "More verbose output.");
        options.addOption(HELP, false, "print script usage.");

        return options;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getPrinter(formatter), options);
    }

    private String getPrinter(HelpFormatter formatter){

        String newline = formatter.getNewLine();
        return newline + "Runs the specified script in multiple threads at the specified frequency. Press Ctrl+C to end.";
    }

}

