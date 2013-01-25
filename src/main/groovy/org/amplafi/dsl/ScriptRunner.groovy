package org.amplafi.dsl;
import groovy.io.FileType;
import org.amplafi.flow.utils.GeneralFlowRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.lang.*;

/**
 * This class contains various methods for loading and running FlowTestDSL scripts
 * @author Paul
 */
public class ScriptRunner {
    
    private String requestUriString;
    private String host;
    private String port;
    private String apiVersion;
    private String key;
	
	/**
	 * List of scripts that had errors. 
	 */
    private List<ScriptDescription> haveErrors = new ArrayList<ScriptDescription>();
	/**
	 * List of the scripts that passed initial processing
	 */
    private List<ScriptDescription> goodScripts = new ArrayList<ScriptDescription>();
	/**
	 * Description of all scripts known to this runner. 
	 */
    private Map<String,ScriptDescription> scriptLookup;
	/**
	 * Map of param name to param value that will be passed to the scripts.   
	 */
    private Map<String,String> paramsmap;
	
	/**
	 * Allow verbose output
	 */
    private boolean verbose = false;
	
    private static final boolean DEBUG = false;
	private static final IMPORT_REGEX = /^\s*?import (.*)$/;
	private static final NL = System.getProperty("line.separator");

    /**
     *  Default path for test scripts
     */
    public static final String DEFAULT_SCRIPT_PATH = "src/test/resources/testscripts";

    /**
     * Constructs a script runner with a standard request string
     * Mostly used in tests
     * @param requestUriString
     */
    public ScriptRunner(String requestUriString){
        this.requestUriString = requestUriString;
    }

    /**
     * Constructs a Script runner with individual parameters that can be overridden in scripts.
     * @param host - host address e.g. http://www.farreach.es
     * @param port - e.g. 80
     * @param apiVersion - e.g. apiv1
     * @param key - Api Key string
     */
    public ScriptRunner(String host, String port, String apiVersion, String key){
        this.host = host;
        this.port = port;
        this.apiVersion = apiVersion;
        this.key = key;
    }
    /**
     * Constructs a Script runner with individual parameters that can be overridden in scripts.
     * passes a map of parameters to the script 
     * @param host - host address e.g. http://www.farreach.es
     * @param port - e.g. 80
     * @param apiVersion - e.g. apiv1
     * @param key - Api Key string
     * @param paramsmap - map of paramname to param value 
     * @param verbose - print verbose output.
     */
    public ScriptRunner(String host, String port, String apiVersion, String key, Map<String,String> paramsmap, boolean verbose){
        this.host = host;
        this.port = port;
        this.apiVersion = apiVersion;
        this.key = key;
        this.paramsmap = paramsmap;
        this.verbose = verbose;
    }

    /**
     * This method runs all of the scripts in the DEFAULT_SCRIPT_PATH
     * @return List<String> of script paths.
     */
    public List<String> loadAndRunAllSrcipts(){
        def list = [];
        def dir = new File(DEFAULT_SCRIPT_PATH);
        dir.eachFileRecurse (FileType.FILES) { file ->
            list << file.getCanonicalPath() ;
        }
        return list.sort();
    }

    /**
     * This method runs all of the scripts in the DEFAULT_SCRIPT_PATH
     * @return 
     */
    public List<String> findAllTestScripts(){
        findAllScripts(DEFAULT_SCRIPT_PATH);
    }

    /**
     * This method finds all scripts below the specified file path
     * @param search path
     * @return list of script paths
     */
    public List<String> findAllScripts(def path){
        def list = [];

        def dir = new File(path);
        dir.eachFileRecurse (FileType.FILES) { file ->
            list << file.getCanonicalPath();
        }
        return list.sort();
    }


    /**
     * Loads and runs one script specified by the file parameter
     * @param filePath is the full path to the script.
     */
    def loadAndRunOneScript(String filePath){
        
        def file = new File(filePath);
        def script = file.getText();
        def value = runScriptSource(script,true);
        return value;
    }

	/**
	 * 
	 * @param sourceCode
	 * @param execOrDescibe - If true then execute the source code as a command script if false run 
	 *                         the code as a description DSL to obtain its description  
	 * @return The groovy closure (Why?) 
	 * @throws NoDescriptionException - Thrown if the description DSL does not find a description directive
	 * @throws EarlyExitException - thrown to prevent the description dsl from running any commands. 
	 */
    def runScriptSource(String sourceCode, boolean execOrDescribe) throws NoDescriptionException, EarlyExitException{        
        // The script code must be pre-processed to add the contents of the file
        // into a call to FlowTestBuil der.build then the processed script is run
        // with the GroovyShell.
        Object closure = getClosure(sourceCode,paramsmap);
        def builder = null;
        if(requestUriString && requestUriString!=""){
            builder = new FlowTestBuilder(requestUriString,this,verbose);
        }else{
            builder = new FlowTestBuilder(host,port,apiVersion,key,this,verbose);
        }
        
        if(execOrDescribe){            
            def execScript = builder.buildExe(closure);
            execScript();
        }else{
            def execScript = builder.buildDesc(closure);
            execScript();
        }
        return closure;
    }
	
    /**
     * Takes the source code string and wraps in into a valid groovy script that when run will return a closure
     * that can be either configured to describe itself or to run as a sequence of commands.  
     * @param sourceCode
     * @param paramsmap
     * @return
     */
    def getClosure(String sourceCode, Map<String,String> paramsmap){       
        StringBuffer scriptSb = new StringBuffer();      
        
        // Extract the import statements from the input source code and re-add them 
		// to the top of the groovy program.
        scriptSb.append(getImportLines(sourceCode));
        String valibleScript = getValidClosureCode(sourceCode);        
        def scriptStr = """
            import org.amplafi.dsl.FlowTestBuilder;
            import org.amplafi.json.*;
            def source = {
                ${valibleScript}
            };
            return source
            """;
        
        scriptSb.append(scriptStr);
        def script = scriptSb.toString();
        def bindingMap = ["params":paramsmap];
        Binding binding = new Binding(bindingMap);
        binding.setVariable("requestUriString",requestUriString);
        GroovyShell shell = new GroovyShell(this.class.classLoader,binding);
        def lineNo = 1;
        script.split("\n").each{ line -> 
            debug("${lineNo}>${line}");
            lineNo++;
        }

        Object closure = shell.evaluate(script);
        return closure;
    }

    /**
     * Creates an un-configured closure (no delegate set) from the 
     * @param scriptName
     * @param callParamsMap - map of parameters name to value
     * @return
     */
    public def createClosure(String scriptName,Map<String,String> callParamsMap){
        def filePath = getScriptPath(scriptName);
        if(filePath){
            def file = new File(filePath);
            def sourceCode = file.getText();
            def closure = getClosure(sourceCode,callParamsMap);
            return closure;
        }else{
            println("Script "+ scriptName + " does not exsit" );
            return null;
        }

    }
    
	/**
	 * Obtain the script file path from the short name in the script description directive.
	 * This should be called after processScriptsInFolder(...) or it will return null. 
	 * @param scriptName
	 * @return file path string
	 */
    def getScriptPath(String scriptName){
        def filePath = null;
        if(scriptLookup){
            ScriptDescription sd = scriptLookup.get(scriptName);
            if(sd){
                filePath = sd.getPath();
            }
        }
        return filePath;
        
    }
    
    /**
     * Process all the scripts in the folder path and determine . 
     * @param path
     * @return map of all scripts and their descriptions
     */
    public Map<String,ScriptDescription> processScriptsInFolder(String path){
        List<ScriptDescription> ret = [];
        List<String> scriptPaths = findAllScripts(path);
        
        scriptPaths.each{ spath ->
            def desc = describeOneScript(spath);
            if (desc != null){
                ret << desc;
            }
        }
        scriptLookup = new HashMap<String,ScriptDescription>();
        
        // Determine which scripts are good to run and which have errors. 
        for (ScriptDescription sd : ret ){
            if (!sd.getHasErrors()){
                goodScripts.add(sd);
                scriptLookup.put(sd.getName(),sd);
            } else {
                haveErrors.add(sd);
            }
        }
        
        return scriptLookup;
    }

    /**
     * describes one script specified by the file parameter
     * @param filePath is the full path to the script.
     */
    public ScriptDescription describeOneScript(String filePath){
        
        def file = new File(filePath);
        def script = file.getText();
        def value =  null;
        
        try {
            value = runScriptSource(script,false);
        } catch (EarlyExitException eee){
            value = eee.desc;
            value.path = filePath;
        } catch (NoDescriptionException nde){
            value = new ScriptDescription(hasErrors:true, errorMesg:"No Description Defined", path:filePath);
        } catch (Exception e){
			value = new ScriptDescription(hasErrors:true, errorMesg:"Compilation Errors + ${e.getMessage()}", path:filePath);
        }

        return value;
    }
    
     /**
     * Get the relative path by the absolute path
     * @param filePath is the full path to the script.
     */
    def getRelativePath(String filePath){
        def relativePath = filePath;
        def currentPath = System.getProperty("user.dir");
        if(filePath.contains(currentPath)){
            relativePath = filePath.substring(currentPath.length()); 
        }
    
    }
    
    /**
     * Returns the script source with import lines removed.
     * @param source - original source code.
     * @return - modified code 
     */
    def getValidClosureCode(String source){
        StringBuffer sb = new StringBuffer();
        source.eachLine{ line ->
			// return ever line that isn't an import
            if (!(line =~ IMPORT_REGEX)){
                sb << "${line}${NL}"
            }
        } 
        return sb.toString();
    }
    
    /**
     * Returns the all of the import lines from the script so they can be put in the 
     * correct location in the wrapper script. 
     * @param source - source code
     * @return - string of import statements. 
     */
    private String getImportLines(String source){
        def ret = new StringBuffer();
        source.eachLine{ line ->
        
            if (line =~ IMPORT_REGEX ){
                def matcher = ( line =~ IMPORT_REGEX );
                def importClass = matcher[0][1];
                ret << "import ${importClass}${NL}";
            }
        
        }
        return ret.toString();
    }

    /**
     * Returns list of scripts that were parsed with no errors.
     * @return - list of scripts
     */
    public List<ScriptDescription> getGoodScripts(){
        return goodScripts;
    }

    /**
     * Returns list of scripts that had errors and error description
     * @return - list of bad scripts.
     */
    public List<ScriptDescription> getScriptsWithErrors(){
        return haveErrors;
    }

    /**
     * Utility method for debugging code. 
     * @param msg - message to print
     */
    private static final void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }

}
