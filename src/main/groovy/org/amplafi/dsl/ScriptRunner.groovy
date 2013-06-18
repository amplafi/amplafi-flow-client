package org.amplafi.dsl
import org.amplafi.flow.definitions.FarReachesServiceInfo
import groovy.io.FileType
import groovy.lang.Closure

import org.amplafi.flow.utils.GeneralFlowRequest
import java.util.HashMap

import org.apache.commons.codec.language.bm.Languages.SomeLanguages
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import java.util.Map
import java.util.List
import java.util.ArrayList

import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.control.MultipleCompilationErrorsException
/**
 * TO_DAISY: it doesn't look like this class can't be implemented in java. Why groovy?
 * 
 * This class contains various methods for loading and running FlowTestDSL scripts
 * @author Paul
 */
public class ScriptRunner {

    private FarReachesServiceInfo serviceInfo

    private String key

    /** Allows re-running of last script */
    private Closure lastScript

    /**
     * Description of all scripts known to this runner.
     */
    public Map<String, File> scriptLookup = new TreeMap<String, File>()
    /**
     * Map of param name to param value that will be passed to the scripts.
     */
    private Map<String,String> paramsmap

    /**
     * Allow verbose output
     */
    def boolean verbose = false

    private Log log = null

    private static final IMPORT_REGEX = /^\s*?import (.*)$/
    private static final NL = System.getProperty("line.separator")

    /**
     *  Default path for test scripts
     */
    public static final String DEFAULT_SCRIPT_PATH = "src/test/resources/testscripts"

    /**
     * Constructs a Script runner with individual parameters that can be overridden in scripts.
     * @param host - host address e.g. http://www.farreach.es
     * @param port - e.g. 80
     * @param apiVersion - e.g. apiv1
     * @param key - Api Key string
     */
    public ScriptRunner(FarReachesServiceInfo serviceInfo, String key){
        this.serviceInfo = serviceInfo
        this.key = key
        initDefaultScriptFolder()
    }

	private initDefaultScriptFolder() {
		def commandScriptsFolder = getClass().getResource("/commandScripts/")
        if (commandScriptsFolder != null) {
            processScriptsInFolder(commandScriptsFolder.getPath())
        }            
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
        this.serviceInfo = serviceInfo
        this.key = key
        this.paramsmap = paramsmap
        this.verbose = verbose
        initDefaultScriptFolder()
    }

    /**
     * This method runs all of the scripts in the DEFAULT_SCRIPT_PATH.
     * @return List<String> of script paths.
     */
    public List<String> loadAndRunAllSrcipts(){
        def list = []
        def dir = new File(DEFAULT_SCRIPT_PATH)
        dir.eachFileRecurse (FileType.FILES) { file ->
            list << file.getCanonicalPath() 
        }
        return list.sort()
    }

    /**
     * This method runs all of the scripts in the DEFAULT_SCRIPT_PATH.
     * @return
     */
    public List<String> findAllTestScripts(){
        findAllScripts(DEFAULT_SCRIPT_PATH)
    }

    /**
     * This method finds all scripts below the specified file path.
     * @param search path
     * @return list of script paths
     */
    public List<String> findAllScripts(def path){
        def list = []
		try {
	        def dir = new File(path)
	        dir.eachFileRecurse (FileType.FILES) { file ->
                
	            list << file.getCanonicalPath()
	        }
		} catch (FileNotFoundException fnfe){
			getLog().error("WARNING: Can't find scripts folder, you must checkout farreaches-customer-service-client into the parent directory.")
		}
        return list.sort()
    }


    /**
     * Loads and runs one script specified by the file parameter.
     * @param filePath is the full path to the script.
     */
    def loadAndRunOneScript(String filePathOrName){
        File file;
        if (scriptLookup.containsKey(filePathOrName)) {
            file = scriptLookup.get(filePathOrName);
        } else {
            file = new File(filePathOrName);
        }
        String script = file.getText()
        getLog().debug("loadAndRunOneScript() start to run runScriptSource() method")
        def value = runScriptSource(script, file.getName())
        getLog().debug("loadAndRunOneScript() start to run runScriptSource() method")
        return value
    }

    def reRunLastScript(){
        if (lastScript){
            lastScript()
        } else {
            getLog().error("No script was previously run." )
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
    def runScriptSource(String sourceCode, String scriptName) {
        // The script code must be pre-processed to add the contents of the file
        // into a call to FlowTestBuil der.build then the processed script is run
        // with the GroovyShell.
        getLog().debug("runScriptSource() start to get closure")
        Closure closure = getClosure(sourceCode, paramsmap, scriptName)
        getLog().debug("runScriptSource() finished to get closure")
        if (key == null || key.equals("")){
            closure.setDelegate(new FlowTestDSL(serviceInfo, this))
        } else {
            closure.setDelegate(new FlowTestDSL(serviceInfo, key, this))
        }
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        lastScript = closure
        return lastScript()
    }

    /**
     * Takes the source code string and wraps in into a valid groovy script that when run will return a closure.
     * that can be either configured to describe itself or to run as a sequence of commands.
     * @param sourceCode
     * @param paramsmap
     * @return
     */
    def getClosure(String sourceCode, Map<String,String> paramsmap, String scriptName){
        StringBuffer scriptSb = new StringBuffer()
        // Extract the import statements from the input source code and re-add them
        // to the top of the groovy program.
        scriptSb.append(getImportLines(sourceCode))
        String valibleScript = getValidClosureCode(sourceCode)

        // All the imports are prepended to the first line of the user script so error messages
        // have the correct line number in them
        def scriptStr = """import org.amplafi.flow.utils.*;import org.amplafi.dsl.*;import org.amplafi.json.*; def source = { ${valibleScript} }; return source """
        scriptSb.append(scriptStr)
        def script = scriptSb.toString()
        def bindingMap = ["params": paramsmap]
        Binding binding = new Binding(paramsmap)
        binding.setVariable("serviceInfo", serviceInfo)
        GroovyShell shell = new GroovyShell(this.class.classLoader, binding)
        Object closure = shell.evaluate(script, scriptName)
        return closure
    }

    /**
     * Creates an un-configured closure (no delegate set).
     * @param scriptName
     * @param callParamsMap - map of parameters name to value
     * @return
     */
    public def createClosure(String scriptName, Map<String,String> callParamsMap) {
        def filePath = getScriptPath(scriptName)
		
		if(filePath == null){
			def scriptResource = getClass().getResource("/commandScripts/" + scriptName + ".groovy")
            if (scriptResource) {
                filePath = scriptResource.getPath()
            }
		}
	
        if(filePath){
            def file = new File(filePath)
            def sourceCode = file.getText()
            def closure = getClosure(sourceCode, callParamsMap, file.getName())
            return closure
        } else {
            getLog().error("Script ${scriptName} does not exist" )
            return null
        }
    }

    /**
     * Obtain the script file path from the short name in the script description directive.
     * This should be called after processScriptsInFolder(...) or it will return null.
     * @param scriptName
     * @return file path string
     */
    def getScriptPath(String scriptName){
        def filePath = null
        File sd = scriptLookup.get(scriptName)
        if(sd){
            filePath = sd.getPath()
        }
        return filePath
    }

    /**
     * Process all the scripts in the folder path and determine .
     * @param path
     * @return map of all scripts and their descriptions
     */
    public Map<String, File> processScriptsInFolder(String path){
        List<File> ret = []
        List<String> scriptPaths = findAllScripts(path)
        scriptPaths.each{ spath ->
                ret << new File(spath)
        }
        for (File file : ret ){
            String fileName = file.getName();
            int postfixPosition = fileName.indexOf(".groovy");
            if (postfixPosition > 0) {
                scriptLookup.put(fileName.substring(0, postfixPosition), file)
            }
        }
        return scriptLookup
    }

     /**
     * Get the relative path by the absolute path.
     * @param filePath is the full path to the script.
     */
    def getRelativePath(String filePath){
        def relativePath = filePath
        def currentPath = System.getProperty("user.dir")
        if(filePath.contains(currentPath)){
            relativePath = filePath.substring(currentPath.length())
        }
    }

    /**
     * Returns the script source with import lines removed.
     * @param source - original source code.
     * @return - modified code
     */
    def getValidClosureCode(String source){
        StringBuffer sb = new StringBuffer()
        source.eachLine{ line ->
            // return ever line that isn't an import
            if (!(line =~ IMPORT_REGEX)){
                sb << "${line}${NL}"
            }
        }
        return sb.toString()
    }

    /**
     * Returns the all of the import lines from the script so they can be put in the
     * correct location in the wrapper script.
     * @param source - source code
     * @return - string of import statements.
     */
    private String getImportLines(String source){
        def ret = new StringBuffer()
        source.eachLine{ line ->
            if (line =~ IMPORT_REGEX ){
                def matcher = ( line =~ IMPORT_REGEX )
                def importClass = matcher[0][1]
                ret << "import ${importClass};${NL}"
            }
        }
        return ret.toString()
    }

    public void setScriptLookup(Map<String, File> scriptLookup){
        this.scriptLookup = scriptLookup
    }

    /**
     * Get the logger for this class.
     */
    public synchronized Log getLog(){
        if ( this.log == null ) {
            this.log = LogFactory.getLog(ScriptRunner.class)
        }
        return this.log
    }

}
