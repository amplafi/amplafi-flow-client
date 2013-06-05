package org.amplafi.shell;

import static org.amplafi.flow.utils.AdminToolCommandLineOptions.LIST;

import java.util.Map;

import groovy.transform.Canonical;
import org.amplafi.dsl.FlowTestDSL;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.dsl.ScriptDescription;
import org.amplafi.dsl.ParameterUsge;
import org.amplafi.flow.utils.UtilParent;
import org.apache.commons.logging.Log;
import groovy.transform.CompileStatic;

@Canonical 
/**
 * Creates a shell to enter commands
 * @author daisy
 */
public class UserShell{
	
	def PROMPT = ">"
	FlowTestDSL dsl; 
	Console console;
	char[] passwd;
	boolean running = true;
	private List<Cmd> commandList = [];
	
	/**
	 * Constructor 
	 * @param dsl
	 */
	def UserShell(FlowTestDSL dsl){
		this.dsl = dsl;
		
		commandList << new Cmd(){
			def name = ["listAdmin","la"];
			def run = { bits -> list()	};
			def desc = "Show all available AdminTool command scripts.";
		}
		
		commandList << new Cmd(){
			def name = ["call","c"];
			def run = { bits -> callScript(bits)};
			def desc = "Call AdminTool script."
			def usage = """For example:
call CreateSuApiKey userEmail=admin@amplafi.com publicUri=http://fortunatefamilies.com """
		}
		commandList << new Cmd(){
			def name = ["exit","quit","q"];
			def run = { bits -> exit();};
			def desc = "Exit."
		}
		
		commandList << new Cmd(){
			def name = ["help","?"];
			def run = { bits -> help(bits);};
			def desc = "Shows help for each command."
			def usage = """ ${name[0]} or ${name[0]} <command> """
		}
		
		commandList << new Cmd(){
			def name = ["rungroovy","g"];
			def run = { bits -> runGroovy(bits);};
			def desc = "Runs any groovy code in the current environment. including DSL commands"
			def usage ="""${name[0]} println 'hello'
or
${name[1]} g getKey()

Use listDsl to get a list of methods.
"""
		}
		
		commandList << new Cmd(){
			def name = ["listDsl","ld"];
			def run = { bits -> listDsl(bits);};
			def desc = "List all the methods in the dsl"
		}
		
	}
	
	/**
	 * Runs the command loop
	 */
	 public void runCommand(){
		 if ((console = System.console()) != null) {
			 while (running) {
				 String commandLine = console.readLine(PROMPT, new Date());
		 
				 commandLine = commandLine.trim();
				 String[]  bits = commandLine.split(" ");
	 
				 if (bits.size() > 0){
					 def command = bits[0];
					 command = command.toLowerCase();
					 
					 Cmd cmd = commandList.find {it.name.find{it2 -> it2.toLowerCase() == command } != null};
					 
					 if (cmd != null){
						 cmd.run(bits);
	
					 } else {
					 	log("Command not found. Try help");
					 }
				}
				
			 }
		 }
	 }
	 
	 /** Admin script cache */
	 private List<ScriptDescription> adminScripts = null;
	 /**
	  * Gets a cached list of admin scripts
	  * @return
	  */
	 private List<ScriptDescription> getAdminScripts(){
		 if (adminScripts == null){
			 ScriptRunner runner = dsl.runner;
			 runner.processScriptsInFolder(UtilParent.DEFAULT_COMMAND_SCRIPT_PATH);
			 adminScripts = runner.getGoodScripts();
		 }
		 return adminScripts;
	 }
	 
	 /**
	  * List all the admin tool scripts with their descriptions 	 
	  */
	 private void list(){
		 log("Scripts are all in ${UtilParent.DEFAULT_COMMAND_SCRIPT_PATH}");
		 for (ScriptDescription sd : getAdminScripts()) {
			 log(String.format('%1$-35s      -      %2$-100s', sd.getName(), sd.getDescription()));
		 }
		 
	 }
	 
	 /**
	  * call another admin tool script
	  * @param bits
	  */
	 private void callScript(def bits){
		 def response = "";
		 
		 def params = [:];
		 if(bits.size() == 1){
			log("Please specify the script to run");
		 }else if(bits.size() == 2){
			try{
				response = dsl.callScript(bits[1]);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		 }else{
			for(int i = 2;i<bits.size();i++){
				def param = bits[i];
				def paramSplit = param.split("=");
				if(paramSplit.size()==1){
					log("You must give the parameters like:paramName=paramValue");
					return;
				}
				def paramName = paramSplit[0];
				def paramValue = paramSplit[1];
				params[paramName] = paramValue;
			}
			try{
				response = dsl.callScript(bits[1],params);
			}catch(Exception ex){
				log "Error, " + ex.getMessage();
			}
		}
	 }
	 
	 /**
	  * run a snippet of groovy in the current environment
	  * @param bits - command line as array of strings
	  */
	 private void runGroovy(def bits){
		 def sb = new StringBuffer();
		 for(int i = 1;i<bits.size();i++){
			 sb << bits[i] + " ";
		 }
		 String str = sb.toString();
		 
		 try{
			 log "returns: " + dsl.runSnippet(str);
		 }catch(Exception ex){
			 log "Error, " + ex.getMessage();
		 }
	 }
	
	 /**
	  * Exit the command loop
	  */
	 private void exit(){
		 running = false;
	 }
	
	 /**
	  * List all the methods in the dsl
	  */
	 private listDsl(def bits){
		 log "######################################################################";
		 log "Use rungroovy (g) to call these methods as groovy code:";
		 log "These methods are all located in ${dsl.class}";
		 log "See AdminTool.md for more details. ";
		 log "######################################################################";
		 def methods = dsl.metaClass.methods;
	
		 methods.each{
			 if (it.getDeclaringClass().theClass == dsl.class){
				 log """${it.name}(${it.parameterTypes.name.join(', ')}) returns ${it.returnType.name} """
			 }
		   }
			 	 
		
	 }
	 
	 /**
	  * Print help on the named shell command or admin script 
	  * @param bits - command line as array of strings
	  */
	 private void help(def bits){
	 	if (bits==null || bits.size() <= 1){
			 commandList.each{ c ->
				log(String.format('%1$-20s      -      %2$-20s', c.name, c.desc));
			 } 
		 }
		 
		 if ( bits?.size() > 1){
			 def command = bits[1];  
			 Cmd c = commandList.find {it.name.find{it2 -> it2.toLowerCase() == command} != null};;
			 if (c != null){
				 log("""${c.name}	- ${c?.desc}
USAGE:				 
${(c?.usage != null) ? c?.usage:"None"}
""");
			 } else {
			 	log "Not a shell command. Checking admin scripts."
				List<ScriptDescription> adminScr = getAdminScripts()
				ScriptDescription sd = adminScr.find{ it -> it.name.toLowerCase() == command.toLowerCase()}
				if (sd){
					printScriptUsage(sd);
				} else {
					log "Not an admin script."
				}
			 }
		 } 
		 
	 }
	
	 /**
	  * Formats the usage information for an admin tool script
	  * @param sd
	  */
	 private void printScriptUsage(ScriptDescription sd){
		 if (sd != null && sd.getUsage() != null && !sd.getUsage().equals("")) {
			 StringBuffer sb = new StringBuffer();
			 sb.append("Script Usage: call " + sd?.getName());
			 if(sd.getUsageList() != null){
				 for (ParameterUsge pu : sd.getUsageList()){
					 sb.append(" " + pu.getName() + "=<" + pu?.getDescription() + "> ");
				 }
				 log(sb.toString());
			 }
			 log(sd.getUsage());
		  } else {
			 log("Script does not have usage information");
		  }
	 }
	 
	 /**
	  * Logs to output
	  * @param msg
	  */
	 public void log(String msg){
		 println msg;
	 }
	 
	 /**
	  * Get the logger for this class.
	  */
	 public Log getLog(){
		 if ( this.log == null ) {
			 this.log = LogFactory.getLog(UserShell.class);
		 }
		 return this.log;
	 }
}

/**
 * Structure of a command object.
 * @author daisy
 */
public interface Cmd {
	def name; // array of aliases for this command 
	def run; // run closure
	def desc; // basic description 
	def usage; // usage examples
}