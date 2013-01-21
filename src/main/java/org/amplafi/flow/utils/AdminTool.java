package org.amplafi.flow.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.io.*;  
import org.apache.commons.cli.ParseException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import static org.amplafi.dsl.ScriptRunner.*;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.*;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.dsl.ScriptDescription;
import static org.amplafi.dsl.ScriptRunner.*;
import  java.util.prefs.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Command line interface for running scripts to communicate with the Farreach.es
 * wire server.
 * 
 * Please read AdminTool.md for more details
 */
public class AdminTool{

	/** Standard location for admin scritps */
	public static final String COMMAND_SCRIPT_PATH = "src/main/resources/commandScripts";

	public static final String CONFIG_FILE_NAME = "fareaches.fadmin.properties";


	public static final String DEFAULT_HOST = "http://apiv1.farreach.es";
	public static final String DEFAULT_PORT = "80";
	public static final String DEFAULT_API_VERSION = "apiv1";
	
	

	public static Properties configProperties = null;


	/**
	 * Main entry point for tool 
	 */
	public static void main(String[] args){

		// Process command line options.
		AdminToolCommandLineOptions cmdOptions = null;
		try {
			cmdOptions = new AdminToolCommandLineOptions(args);
		} catch (ParseException e) {
			System.err.println("Could not parse passed arguments, message:" + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		
		// Print help if -h option was specified. 
		if (cmdOptions.hasOption(HELP) || args.length == 0) {
			cmdOptions.printHelp();
			
			System.exit(0);
		}

	
		String list = cmdOptions.getOptionValue(LIST);
		
		// Obtain a list of script descriptions from the script runner
		// this will also check for basic script compilation errors or lack of description lines in script.
		ScriptRunner runner  = new ScriptRunner("");
		List<ScriptDescription>  descs = runner.describeScriptsInFolder(COMMAND_SCRIPT_PATH);
		
		
		List<ScriptDescription> haveErrors = new ArrayList<ScriptDescription>();
		List<ScriptDescription> goodScripts = new ArrayList<ScriptDescription>();
		HashMap<String,ScriptDescription> scriptLookup = new HashMap<String,ScriptDescription>();
		
		// Determine which scripts are good to run and which have errors. 
		for (ScriptDescription sd : descs ){
			if (!sd.getHasErrors()){
				goodScripts.add(sd);
				scriptLookup.put(sd.getName(),sd);
			} else {
				haveErrors.add(sd);
			}
		}
		

		
		if (cmdOptions.hasOption(LIST)){
			// If user has asked for a list of commands then list the good scripts with their 
			// descriptions. 
			
			for (ScriptDescription sd : goodScripts ){
				System.out.println("     " + sd.getName() + "       - " + sd.getDescription() + "       - ");
			}
			
			
			
			// List scripts that have errors if there are any
			if (haveErrors.size() > 0){
				System.out.println("The following scripts have errors: ");			
			}
			
			for (ScriptDescription sd : haveErrors ){
				
				System.out.println("  " + sd.getPath() + "       - " + sd.getErrorMesg());
				
			}		
	
			
		} else if(cmdOptions.hasOption(FILE_PATH)){
			// run an ad-hoc script from a file
			String filePath = cmdOptions.getOptionValue(FILE_PATH);
			
			runScript(filePath,scriptLookup,cmdOptions);
	
			
				
		} else{

				runScript(null,scriptLookup,cmdOptions);

		}
		
		// If the config properties were loaded, save them here. 
		saveProperties();
		
	}
	
	/**
	 * @param remainderList - remaininf parameters of command line. 
	 * @return remaining parameters to be passed to the script. 
	 */
	private static String[] getParamArray(List<String> remainderList){
		List<String> paramsList = remainderList;
		paramsList.remove(0);
		final int size =  paramsList.size();
		String[] arr = (String[])paramsList.toArray(new String[size]);
		
			
		return arr;
		
	}
	
	/**
	 *  Runs the named script.
	 */
	private static void runScript(String filePath,HashMap<String,ScriptDescription> scriptLookup,AdminToolCommandLineOptions cmdOptions){
		List<String> remainder =  cmdOptions.getRemainingOptions();
			
			try {
				String host = getOption(cmdOptions,HOST,DEFAULT_HOST);

				String port = getOption(cmdOptions,PORT,DEFAULT_PORT);

				String apiVersion = getOption(cmdOptions,API_VERSION,DEFAULT_API_VERSION);
				String key = getOption(cmdOptions,API_KEY,"");
				if(filePath == null){
					if (remainder.size() > 0){
						String scriptName = remainder.get(0);
						if (scriptLookup.containsKey(scriptName ) ){
							ScriptDescription sd = scriptLookup.get(scriptName);
							filePath = sd.getPath();
						}
					}
					remainder.remove(0);
				}
				Map<String,String> parammap = getParamMap(remainder);
				System.out.println("in AdminTool host = "+host);
				System.out.println("in AdminTool port = "+port);
				System.out.println("in AdminTool apiVersion = "+apiVersion);
				System.out.println("in AdminTool key = "+key);
				System.out.println("in AdminTool parammap = "+parammap);
				ScriptRunner runner2  = new ScriptRunner(host, port, apiVersion, key, parammap);		
				runner2.loadAndRunOneScript(filePath);
				

			
			
			} catch (IOException ioe){
					System.err.println("Error : " + ioe);  
			}

	}
	
	
	private static Map<String,String> getParamMap(List<String> remainderList){
		Map<String,String> map =new HashMap<String, String>();

		for(int i=0;i<remainderList.size();i++){
			
			if(remainderList.size()>i+1){		
				map.put(remainderList.get(i),remainderList.get(i+1));
			}
			
			i++;
		
		}

			
		return map;
		
	}
	

	private static String getOption(AdminToolCommandLineOptions cmdOptions, String key, String defaultVal) throws IOException{
		Properties props = getProperties();		
		
		String value = null;
		if (cmdOptions.hasOption(key)) {
			// if option passed in on commandline then use that
			value = cmdOptions.getOptionValue(key);
			

		} else {
			// if option is in properties then use that

			String prefValue = props.getProperty(key, "");
		
			if (cmdOptions.hasOption(NOCACHE) || prefValue.equals("")){
				// prompt the user for the option
	
				System.out.print("Please, Enter : " + key + " ( Enter defaults to: "+ defaultVal +") " );  
				System.out.println();  
				BufferedReader consoleIn =  new BufferedReader(new InputStreamReader(System.in));  
				value = consoleIn.readLine(); 	
				
				if ("".equals(value)){
					value = defaultVal;
				}
			
			
			} else {
				return prefValue;
			}
		}
	
		props.setProperty(key,value);
	
		return value;
	
	}

	
	/**
	 *  Gets the configuration properties, loading it if hasn't been loaded
	 *  @return configuaration properties. 
	 */
	private static Properties getProperties(){
		if (configProperties == null){
			
			configProperties = new Properties();

			try {
				//load a properties file
				configProperties.load(new FileInputStream(CONFIG_FILE_NAME));
 
			} catch (IOException ex) {
				System.err.println("Error loading file " + CONFIG_FILE_NAME );
			}
		}

		return configProperties;
		
	}


	/**
	 *  Saves the configuration properties, loading it if hasn't been loaded
	 */
	private static void saveProperties(){
		if (configProperties != null){
			
		
			
			try {
				//load a properties file
				configProperties.store(new FileOutputStream(CONFIG_FILE_NAME),"Farreach.es Admin tool properties");
 
			} catch (IOException ex) {
				System.err.println("Error saving file " + CONFIG_FILE_NAME );
			}
			
		}
		
	}
	

	private ScriptRunner runner = null;


	public AdminTool(){
		
	}


	public List<ScriptDescription> listScripts(){
	
		runner  = new ScriptRunner("");
		return runner.describeScriptsInFolder(COMMAND_SCRIPT_PATH);

	}


	public void runScript(String filePath){
	runner  = new ScriptRunner("");
		runner.loadAndRunOneScript(filePath);

	}	
	
	
	
	

}
