package org.amplafi.flow.shell;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Tuan Nguyen
 *
 */
public class ShellCommandParser {
    private static String USAGE = 
            "This shell is used to help the administrator access and manage " +
            "the farreaches wireservice" ;
    
    public static final String API_KEY = "key";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String API_VERSION = "apiv";
    public static final String SCRIPT = "script";

    private Options options = new Options();
    protected CommandLine cmd;

    public ShellCommandParser(String[] args) throws ParseException {
        addOption(API_KEY, true, "The user api key. This parameter is mandatory");
        addOption(HOST, true, "The wireservice host, the default value is http://sandbox.farreach.es");
        addOption(PORT, true, "The wireservice port, the default value is 8080");
        addOption(API_VERSION, true, "The wireservice api version, the default value is apiv1");            
        addOption(SCRIPT, true, "The batch script file to run");            

        CommandLineParser parser = new GnuParser();
        cmd = parser.parse(options, args);
    }
    
    void addOption(String opt, boolean hasArg, String description) {
        options.addOption(opt, hasArg, description) ;
    }
    
    void addOption(String opt, String longOpt, boolean required, boolean hasArg, String description) {
        Option option = new Option(opt, longOpt, hasArg, description) ;
        option.setRequired(required) ;
        options.addOption(option) ;
    }
    
    public String getOptionValue(String optionName) {
        return cmd.getOptionValue(optionName);
    }

    public List<String> getRemainingOptions() {
        return cmd.getArgList();
    }

    public boolean hasOption(String optionName) {
        return cmd.hasOption(optionName);
    }
    
    public String getOption(String name, String defaultValue) {
        return cmd.getOptionValue(name, defaultValue) ;
    }
    
    public int getOptionAsInt(String name, int defaultValue) {
        String value = cmd.getOptionValue(name) ;
        if(value == null) return defaultValue ;
        return Integer.parseInt(value) ;
    }
    
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, options);
    }
}