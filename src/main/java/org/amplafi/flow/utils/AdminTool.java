package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.AdminToolCommandLineOptions.API_KEY;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.API_VERSION;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.HOST;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.PORT;
import groovy.lang.Script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.json.JSONArray;

import com.sworddance.util.NotNullIterator;

/**
 * Command line interface for running scripts to communicate with the
 * Farreach.es wire server. Please read AdminTool.md
 * for more details
 */
public class AdminTool extends UtilParent {
    private static final String PUBLIC_API = "public";
    private Map<String,String> scriptsAvailable = null;
    private ScriptRunner runner;
    private boolean verbose = false;
    private Properties props;
    private static final Pattern scriptPattern = Pattern.compile("^(.*)\\.groovy$");

    public AdminTool(){
    	this.props = new Properties();
    	try{
    	this.props.load(new FileInputStream("adminTool.default.properties"));
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	loadScriptsAvailable();
    	runner = ScriptRunner.getNewScriptRunner(props);
    }
    private void loadScriptsAvailable() {
    	scriptsAvailable = new HashMap<String, String>();
        File dir = new File(props.getProperty("scripts_folder"));
        File[] files = dir.listFiles();
        for (File file : NotNullIterator.<File> newNotNullIterator(files)) {
        	Matcher m = scriptPattern.matcher(file.getName());
        	if(m.matches()) try {
                scriptsAvailable.put(m.group(1), file.getCanonicalPath());
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
	}
    
    public void runScriptName(String name){
    	// TODO make this work
    	
    }
    /**
     * Runs the named script.
     * @param filePath is the full path to the script
     * @param cmdOptions is instance of AdminToolCommandLineOptions
     */
    private void runScript(String filePath) {
        // Get script options if needed
		String host = props.getProperty(HOST); //, DEFAULT_HOST);
		String port = props.getProperty(PORT); //, DEFAULT_PORT);
		String apiVersion = props.getProperty(API_VERSION); //, DEFAULT_API_VERSION);
		FarReachesServiceInfo serviceInfo = new FarReachesServiceInfo(host, port, apiVersion);

		String apiKey = props.getProperty(API_KEY);

		if (!PUBLIC_API.equals(apiVersion)){

		    if ( apiKey == null ){
		        apiKey = getPermApiKey(serviceInfo,null, verbose);
		    }
		} else {
		    apiKey = null;
		}
		
		String scriptName = filePath;
		// Check if we are running and ad-hoc script
		// Get the parameter for the script itself.
		Map<String, String> commandLineParameters = new HashMap<>();
		
		ScriptRunner runner = new ScriptRunner(serviceInfo); //, apiKey, commandLineParameters, verbose);
		// Is verbose switched on?
		// run the script
		if (filePath != null) {
		    runner.loadAndRunOneScript(filePath);
		} else {
		    getLog().error("No script to run or not found.");
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

	public Map<String, String> getAvailableScripts() {
		return scriptsAvailable;
	}

}
