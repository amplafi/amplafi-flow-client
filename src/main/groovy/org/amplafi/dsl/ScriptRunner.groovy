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
	Map<String,String> paramsmap = null;

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
    
     public ScriptRunner(String host, String port, String apiVersion, String key, Map<String,String> paramsmap){

        this.host = host
		this.port = port
		this.apiVersion = apiVersion
		this.key = key
		this.paramsmap = paramsmap
		
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

    /**
     * Runs the source code for a FlowTest DSL script. Look at the Javadoc for
     * the FlowTestBuilder to see a description of the script syntax that is accepted.
     * @param sourceCode - the script source code.
     */
    def runScriptSource(String sourceCode, String builderCmd) throws NoDescriptionException, EarlyExitException{
		
        // The script code must be pre-processed to add the contents of the file
        // into a call to FlowTestBuilder.build then the processed script is run
        // with the GroovyShell.
		def builderParams = "";

		if (requestUriString != null ){
			builderParams = "\"${requestUriString}\""
		} else {
			builderParams = """ "${host}", "${port}", "${apiVersion}", "${key}" """;
		}


        // Create a full script string with the file source in the middle.

//        def script = """
//			import org.amplafi.dsl.FlowTestBuilder;
//			import org.amplafi.json.*;
//
////			def builder = null;
//			
////			builder = new FlowTestBuilder(${builderParams});
//			
//			
//			def source = {
//                ${sourceCode}
//            };
////           def execScript = builder.${builderCmd}(source);
////           execScript();
//			return source
//            """;
//
//		
//		def bindingMap = ["params":paramsmap]
//		
//        Binding binding = new Binding(bindingMap);
//        binding.setVariable("requestUriString",requestUriString);
//        GroovyShell shell = new GroovyShell(this.class.classLoader,binding);
//
//		def lineNo = 1;
//        script.split("\n").each{ line -> 
//			debug("${lineNo}>${line}")
//			lineNo++;
//		}
//
//		println("host = "+host)
//		println("port = "+port)
//		println("apiVersion = "+apiVersion)
//		println("key = "+key)
//		Object closure = shell.evaluate(script);
		Object closure = getClosure(sourceCode)
		def builder = new FlowTestBuilder(host,port,apiVersion,key);
	    def execScript = builder.buildExe(closure);
		if(host&&port&&apiVersion&&key){
        execScript();
		}

        return closure;

    }
	
	def getClosure(String sourceCode){
		def script = """
			import org.amplafi.dsl.FlowTestBuilder;
			import org.amplafi.json.*;
			
			
			def source = {
                ${sourceCode}
            };

			return source
            """;

		
		def bindingMap = ["params":paramsmap]
		
        Binding binding = new Binding(bindingMap);
        binding.setVariable("requestUriString",requestUriString);
        GroovyShell shell = new GroovyShell(this.class.classLoader,binding);

		def lineNo = 1;
        script.split("\n").each{ line -> 
			debug("${lineNo}>${line}")
			lineNo++;
		}

		println("host = "+host)
		println("port = "+port)
		println("apiVersion = "+apiVersion)
		println("key = "+key)
		Object closure = shell.evaluate(script);
		
		return closure;
	}
	
	private static String createClosure(String filePath){
		def file = new File(filePath)
        def sourceCode = file.getText();
		getClosure(sourceCode);
	}
	
	

	public List<ScriptDescription> describeScriptsInFolder(String path){
		List<ScriptDescription> ret = [];


		List<String> scriptPaths = findAllScripts(path)
		
		scriptPaths.each{ spath ->

			def desc = describeOneScript(spath)
			if (desc != null){
				ret << desc;
			}
		}
		
		return ret;
	
	}


    /**
     * describes one script specified by the file parameter
     * @param filePath is the full path to the script.
     */
    def describeOneScript(String filePath){
		String relativePath = getRelativePath(filePath)
        def file = new File(filePath)
        def script = file.getText();
		def value =  null
		
		try {

			value = runScriptSource(script,"buildDesc")
		} catch (EarlyExitException eee){

			value = eee.desc
			value.path = filePath
		} catch (NoDescriptionException nde){
			value = new ScriptDescription(hasErrors:true, errorMesg:"No Description Defined", path:relativePath);
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


    private static void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }

}
