package org.amplafi.flow.utils;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;
import java.io.*;
import org.apache.commons.cli.ParseException;
import static org.amplafi.flow.utils.AdminToolCommandLineOptions.*;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.dsl.ScriptDescription;
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
    public static Properties configProperties;

    /**
     * Main entry point for tool
     */
    public static void main(String[] args){

		for (String arg : args){
			System.err.println("arg: " + arg );
		}

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
        Map<String,ScriptDescription>  scriptLookup = runner.processScriptsInFolder(COMMAND_SCRIPT_PATH); 
    
        if (cmdOptions.hasOption(LIST) || cmdOptions.hasOption(LISTDETAILED)){
            // If user has asked for a list of commands then list the good scripts with their 
            // descriptions.             
            for (ScriptDescription sd : runner.getGoodScripts() ){ 
                if(cmdOptions.hasOption(LIST)){
                    System.out.println("     " + sd.getName() + "       - " + sd.getDescription());
                }else{
                    System.out.println("     " + sd.getName() + "       - " + sd.getDescription() + "       - " + getRelativePath(sd.getPath()));
                }
            
            }
            // List scripts that have errors if there are any
            if (runner.getScriptsWithErrors().size() > 0){  
                System.out.println("The following scripts have errors: ");			
            }
            
            for (ScriptDescription sd : runner.getScriptsWithErrors() ){  
                    System.out.println("  " + getRelativePath(sd.getPath()) + "       - " + sd.getErrorMesg());
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
     * get the relative path by the absolute path
     * @param filePath is the full path to the script.
     */
    private static String getRelativePath(String filePath){
        String relativePath = filePath;
        String currentPath = System.getProperty("user.dir");
        if(filePath.contains(currentPath)){
            relativePath = filePath.substring(currentPath.length());
        }
        
        return relativePath;
    }
    
    /**
     *  Runs the named script.
     */
    private static void runScript(String filePath,Map<String,ScriptDescription> scriptLookup,AdminToolCommandLineOptions cmdOptions){
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
                        remainder.remove(0);						
                    }

                }
                Map<String,String> parammap = getParamMap(remainder);
				boolean verbose = false;
				if(cmdOptions.hasOption(VERBOSE)){
					verbose = true;
				}
				
                ScriptRunner runner2  = new ScriptRunner(host, port, apiVersion, key, parammap, verbose);
                runner2.processScriptsInFolder(COMMAND_SCRIPT_PATH);

                if (filePath != null){
                    
                    runner2.loadAndRunOneScript(filePath);
                } else {
                    System.err.println("No script to run or not found.");
                } 
            
            } catch (IOException ioe){
                    System.err.println("Error : " + ioe);  
            }

    }
    
    private static Map<String,String> getParamMap(List<String> remainderList){
        Map<String,String> map =new HashMap<String, String>();
        // On linux options like param1=cat comes through as a single param
        // On windows they come through as 2 params.         
        // To match options like param1=cat
        String patternStr = "(\\w+)=(\\S+)";
        Pattern p = Pattern.compile(patternStr);
        for(int i=0;i<remainderList.size();i++){
            Matcher matcher = p.matcher(remainderList.get(i));
            if (matcher.matches()){
                // 	then we are looking at param1=cat as a single param
                map.put(matcher.group(1),matcher.group(2));
                
            } else {
                if(remainderList.size()>i+1){		
                    map.put(remainderList.get(i),remainderList.get(i+1));
                }
                i++;
            }
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

    public void runScript(String filePath){
    	runner  = new ScriptRunner("");
        runner.loadAndRunOneScript(filePath);
    }	

}
