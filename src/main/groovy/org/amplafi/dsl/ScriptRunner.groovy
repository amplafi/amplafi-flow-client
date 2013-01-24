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
 */
public class ScriptRunner {
    
    private String requestUriString = null;
    String host = null;
    String port = null;
    String apiVersion = null;
    String key = null;
    List<ScriptDescription> haveErrors = new ArrayList<ScriptDescription>();
    List<ScriptDescription> goodScripts = new ArrayList<ScriptDescription>();
    HashMap<String,ScriptDescription> scriptLookup = null;
    Map<String,String> paramsmap = null;
    boolean verbose = false;

    private static boolean DEBUG = false;	
    

    // default path for test scripts
    public static final String DEFAULT_SCRIPT_PATH = "src/test/resources/testscripts";


    public ScriptRunner(String requestUriString){
        this.requestUriString = requestUriString;
    }

    public ScriptRunner(String host, String port, String apiVersion, String key){
        this.host = host
        this.port = port
        this.apiVersion = apiVersion
        this.key = key
        
    }
    
     public ScriptRunner(String host, String port, String apiVersion, String key, Map<String,String> paramsmap, boolean verbose){

        this.host = host
        this.port = port
        this.apiVersion = apiVersion
        this.key = key
        this.paramsmap = paramsmap
        this.verbose = verbose
    }




    /**
     * This method runs all of the scripts in the DEFAULT_SCRIPT_PATH
     */
    def loadAndRunAllSrcipts(){
        def list = []
        def dir = new File(DEFAULT_SCRIPT_PATH)
        dir.eachFileRecurse (FileType.FILES) { file ->
            list << file.getCanonicalPath() 
        }
        return list.sort();
    }
    

    /**
     * This method runs all of the script in the DEFAULT_SCRIPT_PATH
     */
    public List<String> findAllTestScripts(){
        findAllScripts(DEFAULT_SCRIPT_PATH)
    }

    /**
     * This method runs all of the script in the DEFAULT_SCRIPT_PATH
     */
    public List<String> findAllScripts(def path){
        def list = []

        def dir = new File(path)
        dir.eachFileRecurse (FileType.FILES) { file ->
            list << file.getCanonicalPath()
        }
        return list.sort();
    }


    /**
     * Loads and runs one script specified by the file parameter
     * @param filePath is the full path to the script.
     */
    def loadAndRunOneScript(String filePath){

        
        def file = new File(filePath)

        def script = file.getText();

        def value = runScriptSource(script,"buildExe")

        return value
    }


    def runScriptSource(String sourceCode, String builderCmd) throws NoDescriptionException, EarlyExitException{
        
        // The script code must be pre-processed to add the contents of the file
        // into a call to FlowTestBuil der.build then the processed script is run
        // with the GroovyShell.


        Object closure = getClosure(sourceCode,paramsmap)
        def builder = null;
        if(requestUriString && requestUriString!=""){
            builder = new FlowTestBuilder(requestUriString,this,verbose);
        }else{
            builder = new FlowTestBuilder(host,port,apiVersion,key,this,verbose);
        }
        
        if(builderCmd == "buildDesc"){
            
            def execScript = builder.buildDesc(closure);
            execScript();
        }else{
            
            def execScript = builder.buildExe(closure);
            execScript();
        }


        return closure;

    }


    
    def getClosure(String sourceCode, Map paramsmap){
        
        StringBuffer scriptSb = new StringBuffer()
        
        scriptSb.append(getImportLines(sourceCode))
        String valibleScript = getValibleScript(sourceCode)
        
        def scriptStr = """
            import org.amplafi.dsl.FlowTestBuilder;
            import org.amplafi.json.*;
            
            
            def source = {
                ${valibleScript}
            };

            return source
            """;
        
        scriptSb.append(scriptStr)
        
        def script = scriptSb.toString()

        
        def bindingMap = ["params":paramsmap];
        
        Binding binding = new Binding(bindingMap);
        binding.setVariable("requestUriString",requestUriString);
        GroovyShell shell = new GroovyShell(this.class.classLoader,binding);

        def lineNo = 1;
        script.split("\n").each{ line -> 
            debug("${lineNo}>${line}")
            lineNo++;
        }

        Object closure = shell.evaluate(script);
        
        
        return closure;
    }
    
    public def createClosure(String scriptName,Map callparamsmap){
        def filePath = getScriptPath(scriptName)
        if(filePath){
            def file = new File(filePath)
            def sourceCode = file.getText();
            def closure = getClosure(sourceCode,callparamsmap);
            return closure;
        }else{
            println("Script "+ scriptName + " does not exsit" );
            return null;
        }
        

    }
    
    def getScriptPath(String scriptName){
        def filePath = null;
        if(scriptLookup){
            ScriptDescription sd = scriptLookup.get(scriptName)
            if(sd){
                filePath = sd.getPath()
            }
        }
        return filePath;
        
    }
    

    public HashMap<String,ScriptDescription> processScriptsInFolder(String path){
        List<ScriptDescription> ret = [];


        List<String> scriptPaths = findAllScripts(path)
        
        scriptPaths.each{ spath ->

            def desc = describeOneScript(spath)
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
    def describeOneScript(String filePath){
        
        def file = new File(filePath)
        def script = file.getText();
        
        def value =  null
        
        try {
        
            value = runScriptSource(script,"buildDesc")
        } catch (EarlyExitException eee){

            value = eee.desc
            value.path = filePath
        } catch (NoDescriptionException nde){
            value = new ScriptDescription(hasErrors:true, errorMesg:"No Description Defined", path:filePath);
        }

        return value
    }
    
     /**
     * get the relative path by the absolute path
     * @param filePath is the full path to the script.
     */
    def getRelativePath(String filePath){
        def relativePath = filePath;
        def currentPath = System.getProperty("user.dir")
        if(filePath.contains(currentPath)){
            relativePath = filePath.substring(currentPath.length()) 
        }
        
        
    }
    

    def getValibleScript(String source){
        
        StringBuffer sb = new StringBuffer();
        
        source.eachLine{ line ->
        
            def regex = /^\s*?import (.*)$/
            if (!(line =~ regex)){
                sb << line + System.getProperty("line.separator")
            }
        } 

        return sb.toString()
    }
    
    def getImportLines(String source){
        def ret = new StringBuffer();
        source.eachLine{ line ->
        
            def regex = /^\s*?import (.*)$/
            if (line =~ regex ){

                def matcher = ( line =~ regex )
                def importClass = matcher[0][1];
                ret << "import ${importClass} \n"
            
            }
        
        }
    
        return ret.toString()
    }
    

    public List<ScriptDescription> getGoodScripts(){
        return goodScripts;
    }

    public List<ScriptDescription> getScriptsWithErrors(){
        return haveErrors;
    }
    

    private static void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }

}
