package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.AdminToolCommandLineOptions.API_KEY;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.API_VERSION;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.DESCRIBE;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.FILE_PATH;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.FLOWS;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.HOST;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.PORT;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.VERBOSE;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONObject;
import org.apache.commons.cli.ParseException;

/**
 * Command line interface for running scripts to communicate with the
 * Farreach.es wire server. Please read AdminTool.md
 * for more details
 */
public class AdminTool extends UtilParent {
    private static final String AUTO_OBTAIN_KEY = "Auto obtain key";
    private static final String PUBLIC_API = "public";
    private boolean verbose = false;

    /**
     * Main entry point for tool.
     * @param args
     */
    public static void main(String[] args) {
        AdminTool adminTool = new AdminTool();
        for (String arg : args) {
            adminTool.getLog().debug("arg: " + arg);
        }
        adminTool.processCommandLine(args);
    }

    /**
     * Process command line.
     * @param args
     */
    public void processCommandLine(String[] args) {
        // Process command line options.
        AdminToolCommandLineOptions cmdOptions = null;
        try {
            cmdOptions = new AdminToolCommandLineOptions(args);
        } catch (ParseException e) {
            getLog().error("Could not parse passed arguments, message:", e);
            return;
        }
        // Print help if there has no args.
        if (args.length == 0) {
            cmdOptions.printHelp();
            return;
        }

        if (cmdOptions.hasOption(FILE_PATH)) {
            // run an ad-hoc script from a file
            String filePath = cmdOptions.getOptionValue(FILE_PATH);
            runScript(filePath, cmdOptions);
        } else {
            runScript(null, cmdOptions);
        }
        // If the config properties were loaded, save them here.
        saveProperties();
    }

    /**
     * Runs the named script.
     * @param filePath is the full path to the script
     * @param cmdOptions is instance of AdminToolCommandLineOptions
     */
    private void runScript(String filePath,
                           AdminToolCommandLineOptions cmdOptions) {
        verbose = cmdOptions.hasOption(VERBOSE);
        List<String> remainder = cmdOptions.getRemainingOptions();
        try {
            // Get script options if needed
            String host = getOption(cmdOptions, HOST, DEFAULT_HOST);
            String port = getOption(cmdOptions, PORT, DEFAULT_PORT);
            String apiVersion = getOption(cmdOptions, API_VERSION, DEFAULT_API_VERSION);
            FarReachesServiceInfo serviceInfo = new FarReachesServiceInfo(host, port, apiVersion);

            String apiKey = getOption(cmdOptions, API_KEY, AUTO_OBTAIN_KEY);

            if (!PUBLIC_API.equals(apiVersion)){

                if ( AUTO_OBTAIN_KEY.equals(apiKey)){
                    apiKey = getPermApiKey(serviceInfo,null, verbose);
                }
            } else {
                apiKey = null;
            }

            if (cmdOptions.hasOption(FLOWS)){
                listFlows(cmdOptions, apiKey, serviceInfo);
                return;
            }

            if (cmdOptions.hasOption(DESCRIBE)){
                String flow = cmdOptions.getOptionValue(DESCRIBE);
                descFlow(cmdOptions, apiKey, flow, serviceInfo);
                return;
            }

            String scriptName = filePath;
            // Check if we are running and ad-hoc script
            if (filePath == null) {
                if (!remainder.isEmpty()) {
                    scriptName = remainder.get(0);
                    filePath = scriptName;
                    remainder.remove(0);
                }
            }
            // Get the parameter for the script itself.
            Map<String, String> commandLineParameters = getCommandLineParameters(remainder);
            
            ScriptRunner runner = new ScriptRunner(serviceInfo, apiKey, commandLineParameters, verbose);
            runner.processScriptsInFolder(getComandScriptPath());
            // Is verbose switched on?
            // run the script
            if (filePath != null) {
                runner.loadAndRunOneScript(filePath);
            } else {
                getLog().error("No script to run or not found.");
            }
        } catch (IOException ioe) {
            if(verbose) {
                getLog().error("Error: " + ioe);
            } else {
                getLog().error("Error: " + ioe.getMessage());
            }
        }
    }

    public void listFlows( AdminToolCommandLineOptions cmdOptions,String key, FarReachesServiceInfo service ){
        emitOutput("Using Key  >" + key);
        GeneralFlowRequest request = new GeneralFlowRequest(service, key, null);
        JSONArray<String> flows = request.listFlows();
        if(verbose){
            emitOutput("");
            emitOutput(" Sent Request: " + request.getRequestString() );
            emitOutput(" With key: " + request.getApiKey() );
            emitOutput("");
        }
        emitOutput(flows.toString());

    }

    public void descFlow( AdminToolCommandLineOptions cmdOptions,String key, String flowName, FarReachesServiceInfo service ){
        GeneralFlowRequest request = new GeneralFlowRequest(service, key, flowName);
        JSONObject flows = request.describeFlow();
        if(verbose){
            emitOutput("");
            emitOutput(" Sent Request: " + request.getRequestString() );
            emitOutput(" With key: " + request.getApiKey() );
            emitOutput("");
        }
        emitOutput(flows.toString(4));
    }

	public Object getCustomerSupportScriptsAvailable() {
		return null;
	}

}
