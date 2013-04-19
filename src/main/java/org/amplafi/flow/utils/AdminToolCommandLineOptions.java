package org.amplafi.flow.utils;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command line options parser for the Admin tool
 *
 */
public class AdminToolCommandLineOptions extends AbstractCommandLineClientOptions {

    // command line switch used to specify which API key we want to use
    public static final String API_KEY = "key";
    public static final String FLOWS = "flows";
    public static final String DESCRIBE = "describe";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String API_VERSION = "apiv";
    public static final String CALLBACK_HOST = "callBackHost";
    public static final String LIST = "l";
    public static final String LISTDETAILED = "L";
    public static final String NOCACHE = "x";
    public static final String FILE_PATH = "f";
    public static final String VERBOSE = "verbose";
    public static final String HELP = "help";

    /**
     * Constructor see AdminTool.md document for parameter string formats.
     */
    public AdminToolCommandLineOptions(String[] args) throws ParseException {
        super(args);
    }

    /**
     * Initialise the options.
     */
    protected Options initOptions() {
        //Options options = super.initOptions();
        Options options = new Options();
        options.addOption(LIST, false, "List In-built Scripts");
        options.addOption(FLOWS, false, "List all Flows");
        options.addOption(DESCRIBE, true, "Describe Flow");
        options.addOption(LISTDETAILED, false, "List In-built Scripts and show all scripts path");
        options.addOption(API_KEY, true, "API key");
        options.addOption(API_KEY, true, "API key");
        options.addOption(HOST, true, "Host address");
        options.addOption(CALLBACK_HOST, true, "API version");
        options.addOption(PORT, true, "Service port");
        options.addOption(NOCACHE, false, "Don't use cached server credentials");
        options.addOption(FILE_PATH, true, "Ad-hoc script File Path");
        options.addOption(VERBOSE, false, "print request url");
        options.addOption(HELP, false, "print script usage");


        /*options.addOption(OptionBuilder.withArgName("property=value").
                hasArgs(2).
                    withValueSeparator().
                        withDescription("Specify query .").create("D"));*/

        return options;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getPrinter(formatter), options);
    }

    private String getPrinter(HelpFormatter formatter){

        String newline = formatter.getNewLine();
        return newline+"FAdmin -l : To list In-built Scripts"+newline
                +"FAdmin -L : To list In-built Scripts and show all scripts path"+newline
                +"FAdmin -x : To reset host, port, api version and key"+newline
                +"            FAdmin example <param1Name>=<param1Value> <param2Name>=<param2Value> ... : To run one of the commands enter the command name";
    }

}
