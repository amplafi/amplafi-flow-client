package org.amplafai.dsl;
import groovy.io.FileType;
import org.amplafi.flow.utils.GeneralFlowRequest;

/**
 * This class contains various methods for loading and running FlowTestDSL scripts
 */
public class ScriptRunner {
	// default path for test scripts
	public static final String DEFAULT_SCRIPT_PATH = "src/test/resources/testscripts";
	
	private String requestUriString = null
	public ScriptRunner(String requestUriString){
		this.requestUriString = requestUriString;
	}
	
	private static boolean DEBUG = false;	

	/**
	 * This method runs all of the scripts in the DEFAULT_SCRIPT_PATH
	 */
	def loadAndRunAllSrcipts(){
		
		List<String> list = findAllTestScripts();
		
		list.each { file ->

		    loadAndRunOneScript(file)

		}
		
		
	}
	
	/**
	 * This method runs all of the script in the DEFAULT_SCRIPT_PATH
	 */	
	public List<String> findAllTestScripts(){
		def list = []

		def dir = new File(DEFAULT_SCRIPT_PATH)
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
		

		
		def value = runScriptSource(script)
		

		return value
	}
	
	/**
	 * Runs the source code for a FlowTest DSL script. Look at the Javadoc for 
	 * the FlowTestBuilder to see a description of the script syntax that is accepted.
	 * @param sourceCode - the script source code.
	 */
	def runScriptSource(String sourceCode){
		// The script code must be pre-processed to add the contents of the file
		// into a call to FlowTestBuilder.build then the processed script is run 
		// with the GroovyShell.
	

		// Create a full script string with the file source in the middle.
		def script = """
			import org.amplafai.dsl.FlowTestBuilder;
			def builder = new FlowTestBuilder(requestUriString);
			def source = {
				def a=1;
			
				${sourceCode}
			};
			builder.build(source);
			""";

		
		
		
	    Binding binding = new Binding();
	    binding.setVariable("requestUriString",requestUriString);
		GroovyShell shell = new GroovyShell(this.class.classLoader,binding);
	
		debug("Running: ${script}");
	
		Object value = shell.evaluate(script);    
		
		return value;
		
	}
	
	
	private static void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }
	
}
