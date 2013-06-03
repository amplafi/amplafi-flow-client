package org.amplafi.dsl;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
import groovy.io.FileType;
import org.amplafi.flow.utils.GeneralFlowRequest;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.lang.*;
import org.codehaus.groovy.control.MultipleCompilationErrorsException
/**
 * This class contains various methods for loading and running FlowTestDSL scripts
 * @author Paul
 */
public class ScriptRunner {

    private FarReachesServiceInfo serviceInfo;

    private String key;

    /** Allows re-running of last script */
    private Closure lastScript;

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
    public Map<String,ScriptDescription> scriptLookup;
    /**
     * Map of param name to param value that will be passed to the scripts.
     */
    private Map<String,String> paramsmap;

    /**
     * Allow verbose output
     */
    def boolean verbose = false;

    private Log log = null;

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
     * @param serviceInfo
     */
//    public ScriptRunner(FarReachesServiceInfo serviceInfo){
//println "############  In scrip2 runner " + key
//        this.serviceInfo = serviceInfo;
//    }

    /**
     * Constructs a Script runner with individual parameters that can be overridden in scripts.
     * @param host - host address e.g. http://www.farreach.es
     * @param port - e.g. 80
     * @param apiVersion - e.g. apiv1
     * @param key - Api Key string
     */
    public ScriptRunner(FarReachesServiceInfo serviceInfo, String key){
        this.serviceInfo = serviceInfo;
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
    public ScriptRunner(FarReachesServiceInfo serviceInfo, String key, Map<String,String> paramsmap, boolean verbose){
        this.serviceInfo = serviceInfo;
        this.key = key;
        this.paramsmap = paramsmap;
        this.verbose = verbose;
    }

    /**
     * This method runs all of the scripts in the DEFAULT_SCRIPT_PATH.
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
     * This method runs all of the scripts in the DEFAULT_SCRIPT_PATH.
     * @return
     */
    public List<String> findAllTestScripts(){
        findAllScripts(DEFAULT_SCRIPT_PATH);
    }

    /**
     * This method finds all scripts below the specified file path.
     * @param search path
     * @return list of script paths
     */
    public List<String> findAllScripts(def path){
        def list = [];
		try {
	        def dir = new File(path);
	        dir.eachFileRecurse (FileType.FILES) { file ->
	            list << file.getCanonicalPath();
	        }
		} catch (FileNotFoundException fnfe){
			getLog().error("WARNING: Can't find scripts folder, you must checkout farreaches-customer-service-client into the parent directory.");
		}
        return list.sort();
    }


    /**
     * Loads and runs one script specified by the file parameter.
     * @param filePath is the full path to the script.
     */
    def loadAndRunOneScript(String filePath){
        getLog().debug("loadAndRunOneScript() start to run describeOneScript() method");
        def description = describeOneScript(filePath);
        getLog().debug("loadAndRunOneScript() finished describeOneScript() method " + description );
        def file = new File(filePath);
        def script = file.getText();
        getLog().debug("loadAndRunOneScript() start to run runScriptSource() method");
        def value = runScriptSource(script,true,description);
        getLog().debug("loadAndRunOneScript() start to run runScriptSource() method");
        return value;
    }

    def reRunLastScript(){
        if (lastScript){
            lastScript();
        } else {
            getLog().error("No script was previously run." );
        }
    }

    /**
     * Runs or describes a script from source code.
     * @param sourceCode
     * @param execOrDescibe - If true then execute the source code as a command script if false run
     *                         the code as a description DSL to obtain its description
     * @return The groovy closure (Why?)
     * @throws NoDescriptionException - Thrown if the description DSL does not find a description directive
     * @throws EarlyExitException - thrown to prevent the description dsl from running any commands.
     */
    def runScriptSource(String sourceCode, boolean execOrDescribe, ScriptDescription description) throws NoDescriptionException, EarlyExitException{
        // The script code must be pre-processed to add the contents of the file
        // into a call to FlowTestBuil der.build then the processed script is run
        // with the GroovyShell.
        getLog().debug("runScriptSource() start to get closure");
        Object closure = getClosure(sourceCode,paramsmap,description);
        getLog().debug("runScriptSource() finished to get closure");
        def builder = null;
        if(key && key!=""){
            builder = getFlowTestBuilder(serviceInfo,key,this,verbose);
        }else{
            builder = getFlowTestBuilder(serviceInfo,this,verbose);
        }
        getLog().debug("runScriptSource() start to get closure");

        if(execOrDescribe){
            lastScript = builder.buildExe(closure);
            return lastScript();
        }else{
            lastScript = builder.buildDesc(closure);
            return lastScript();
        }
    }

    /**
     * To be overriden in tests
     */
    def getFlowTestBuilder(serviceInfo,runner,verbose){
        return new FlowTestBuilder(serviceInfo,this,verbose);
    }

    /**
     * To be overriden in tests
     */
    def getFlowTestBuilder(serviceInfo,key,runner,verbose){
        return new FlowTestBuilder(serviceInfo,key,this,verbose);
    }

    /**
     * Takes the source code string and wraps in into a valid groovy script that when run will return a closure.
     * that can be either configured to describe itself or to run as a sequence of commands.
     * @param sourceCode
     * @param paramsmap
     * @return
     */
    def getClosure(String sourceCode, Map<String,String> paramsmap, ScriptDescription description){
        String generateParams = generateParams(description,paramsmap);
        StringBuffer scriptSb = new StringBuffer();
        // Extract the import statements from the input source code and re-add them
        // to the top of the groovy program.
        scriptSb.append(getImportLines(sourceCode));
        String valibleScript = getValidClosureCode(sourceCode);

        // All the imports are prepended to the first line of the user script so error messages
        // have the correct line number in them
        def scriptStr = """import org.amplafi.flow.utils.*;import org.amplafi.dsl.*;import org.amplafi.json.*; ${generateParams} def source = { ${valibleScript} }; return source """;
        scriptSb.append(scriptStr);
        def script = scriptSb.toString();
        def bindingMap = ["params":paramsmap];
        Binding binding = new Binding(bindingMap);
        binding.setVariable("serviceInfo",serviceInfo);
        GroovyShell shell = new GroovyShell(this.class.classLoader,binding);
        def lineNo = 1;
        script.split("\n").each{ line ->
            //debug("${lineNo}>${line}");
            lineNo++;
        }

        String scriptName = "script";
        if ( description?.name){
            scriptName = description.name;
        }
        debug("scriptName = ${scriptName}");
        Object closure = shell.evaluate(script,scriptName);
        return closure;
    }

    /**
     * Generate the block of code at the start of a script that defines/initializes the parameters
     * and report missing params.
     */
    def generateParams(ScriptDescription description, Map<String,String> paramsmap){
        StringBuffer paramsSb = new StringBuffer("");
        getLog().debug("generateParams() for: " + description + " " + paramsmap);
        if(description){
            def usageList = description.getUsageList();

            if(usageList){
                getLog().debug("generateParams() for usage list: " + usageList);
                for(ParameterUsge paramUsage : usageList){
                    getLog().debug("generateParams() for paramUsage: " + paramUsage);
                    def name;
                    def optional;
                    def defaultValue;
                    //Validata usages in script
                    if(paramUsage.getName() && paramUsage.getName() != ""){
                        name = paramUsage.getName();
                    }else{
                        throw new Exception("Parameter Name Not Set");
                    }
                    if(paramUsage.getOptional()){
                        optional = paramUsage.getOptional();
                    }else{
                        optional = false;
                    }
                    if(paramUsage.getDefaultValue()){
                        defaultValue = paramUsage.getDefaultValue();
                    }else{
                        defaultValue = null;
                    }
                    getLog().debug("generateParams() name=${name} optional=${optional} defaultValue=${defaultValue}");
                    //Validata paramsmap and generate params in script
                    //StringBuffer paramSb = new StringBuffer("");
                    def paramName;
                    def paramValue;
                    if(paramsmap != null && paramsmap.containsKey(name) && paramsmap.get(name)){
                        getLog().debug("generateParams() parameters contain ${name}");
                        paramName = name;
                        paramValue = paramsmap.get(name)
                    }else{
                        getLog().debug("generateParams() parameters don't contain ${name}");
                        if(optional == false){
                            //TODO tell user should input name=<value>
                            throw new ParameterValidationException("Parameter ${name} must be supplied. Format is: ${name}=<${paramUsage.description}>");
                        }else{
                            if(defaultValue){
                                paramName = name;
                                paramValue = defaultValue;
                            }
                        }
                        getLog().debug("generateParams() final values ${paramName}=${paramValue}");
                    }
                    if(paramName&&paramValue){
                        paramsSb.append("def " + paramName + " = \""+ paramValue + "\";");
                    } else {

                        if(name){
                            paramsSb.append("def " + name + " = null ;");
                        }
                    }
                }
            }
        }
        getLog().debug("generateParams() returns : " + paramsSb.toString());
        return paramsSb.toString();
    }

    /**
     * Creates an un-configured closure (no delegate set).
     * @param scriptName
     * @param callParamsMap - map of parameters name to value
     * @return
     */
    public def createClosure(String scriptName,Map<String,String> callParamsMap){
        def filePath = getScriptPath(scriptName);
		
		if(filePath == null){
			filePath = scriptName;
		}
	
        ScriptDescription sd = describeOneScript(filePath);
        if(filePath){
            def file = new File(filePath);
            def sourceCode = file.getText();
            def closure = getClosure(sourceCode,callParamsMap,sd);
            return closure;
        }else{
            getLog().error("Script ${scriptName} does not exist" );
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
     * Describes one script specified by the file parameter.
     * @param filePath is the full path to the script.
     */
    public ScriptDescription describeOneScript(String filePath){
        try {
            def file = new File(filePath);
            def script = file.getText();
            def value =  null;
            try {
                value = runScriptSource(script,false,null);
            } catch (EarlyExitException eee){
                value = eee.desc;
                value.path = filePath;
            } catch (NoDescriptionException nde){
                value = new ScriptDescription(hasErrors:true, errorMesg:"No Description Defined", path:filePath);
            } catch (MultipleCompilationErrorsException mceee){
                value = new ScriptDescription(hasErrors:true, errorMesg:"Compilation Errors + ${mceee.getMessage()}", path:filePath);
                mceee.printStackTrace();
            }
            return value;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

     /**
     * Get the relative path by the absolute path.
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
                ret << "import ${importClass};${NL}";
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
     * Returns list of scripts that had errors and error description.
     * @return - list of bad scripts.
     */
    public List<ScriptDescription> getScriptsWithErrors(){
        return haveErrors;
    }

    public void setScriptLookup(Map<String,ScriptDescription> scriptLookup){
        this.scriptLookup = scriptLookup;
    }

    /**
     * Utility method for debugging code.
     * @param msg - message to print
     */
    private final void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }

    /**
     * Get the logger for this class.
     */
    public synchronized Log getLog(){
        if ( this.log == null ) {
            this.log = LogFactory.getLog(ScriptRunner.class);
        }
        return this.log;
    }

}
