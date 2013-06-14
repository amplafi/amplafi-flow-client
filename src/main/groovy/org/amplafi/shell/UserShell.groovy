package org.amplafi.shell;

import static org.amplafi.flow.utils.AdminToolCommandLineOptions.LIST;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import groovy.transform.Canonical;
import org.amplafi.dsl.FlowTestDSL;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.dsl.ScriptDescription;
import org.amplafi.dsl.ParameterUsage;
import org.amplafi.flow.utils.UtilParent;
import org.apache.commons.logging.Log;
import groovy.transform.CompileStatic;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.history.MemoryHistory;
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent;

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
	private Map<String,DSLHelp> dslDoc = null;
	private static final String LAST_RETURN = "LAST_RETURN"; 
	
	/**
	 * Constructor 
	 * @param dsl
	 */
	def UserShell(FlowTestDSL dsl){
		this.dsl = dsl;
		
		
		commandList << new Cmd(){
			def name = ["help","?"];
			def run = { bits -> help(bits);};
			def desc = "Shows help for each command, script and DSL method."
			def usage = """ ${name[0]} or ${name[0]} <command> """
		}
		
		commandList << new Cmd(){
			def name = ["listadmin","la"];
			def run = { bits -> list()	};
			def desc = "Show all available AdminTool command scripts.";
			def usage ="${name[0]}"
		}
		
		commandList << new Cmd(){
			def name = ["call","c"];
			def run = { bits -> callScript(bits)};
			def desc = "Call AdminTool script."
			def usage = """For example:
call CreateSuApiKey userEmail=admin@amplafi.com publicUri=http://fortunatefamilies.com """
		}


		
		commandList << new Cmd(){
			def name = ["rungroovy","g"];
			def run = { bits -> runGroovy(bits);};
			def desc = "Runs any groovy code in the current environment. including DSL methods."
			def usage ="""${name[0]} println 'hello'
or
${name[1]} getKey()

To store the response from a Groovy statement use the "stash" map e.g.

>${name[1]} stash["a"] = 7
returns: 7

>${name[1]} stash["a"] + 3
returns: 10

Or 
>${name[1]} stash["oldKey"] = getKey()

Use listDsl to get a list of DSL methods.
"""
		}
		
		commandList << new Cmd(){
			def name = ["listdsl","ld"];
			def run = { bits -> listDsl(bits);};
			def desc = "List all the methods in the DSL"
			def usage ="""${name[0]}"""
		}
		
		commandList << new Cmd(){
			def name = ["exit","quit","q"];
			def run = { bits -> exit();};
			def desc = "Exit."
			def usage ="""${name[0]}"""
		}
		
		
	}
	
	def divider = "--------------------------------";
	
	private PrintWriter out; 
	/**
	 * Runs the command loop
	 */
	 public void runCommand(){
		 try {
			 def reader = null;
			 if ("\\".equals(File.separator)) {
				 // Windows 
				 // has its own command history an jline doesn't work
				 console = System.console();
				 reader = console.reader;
				 out = new PrintWriter(console.writer(),true);
			 } else {
			 	 // Not Windows
			     // use jline
				 reader = new ConsoleReader();
				 reader.setHistoryEnabled(true);
				 reader.setHistory(new MemoryHistory());
				 
				 reader.addTriggeredAction((char)KeyEvent.VK_UP, new ActionListener (){
					 public void actionPerformed(ActionEvent e){
						reader.getHistory().previous();
					 }
				 });
		
			 	 out = new PrintWriter(reader.getOutput());
				 reader.setPrompt("> ");

			 }

			 while (running) {
				 String commandLine = reader.readLine();
				 commandLine = commandLine.trim();
				 String[]  bits = commandLine.split(" ");
	 
				 if (bits.size() > 0){
					 def command = bits[0];
					 command = command.toLowerCase();
					 
					 Cmd cmd = commandList.find {it.name.find{it2 -> it2.toLowerCase() == command } != null};
					 
					 if (cmd != null){
						 log divider;
						 cmd.run(bits);
	
					 } else {
					 	log("Command not found. Try help");
					 }
				}
				
			 }
							 
		} catch(Exception e){
			log e.message;
			e.printStackTrace();
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
		 log("Run a script with 'call <scriptname> [params]' or 'c <scriptname> [params]' ");
		 log("Get script usage with 'help <scriptname>'");

	 }
	 
	 /**
	  * call another admin tool script
	  * @param bits
	  */
	 private void callScript(def bits){
		 def response = "";
		 
		 def params = [:];
		 if(bits.size() == 1){
			log("Please specify the script to run,user command (listAdmin or la) to see the scripts list");
		 }else if(bits.size() == 2){
		 	checkForGroovy(bits);
			try{
				response = dsl.callScript(bits[1]);
			}catch(Exception ex){
				log ex.getMessage();
			}
		 }else{
		 	checkForGroovy(bits);
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
		dsl.stash[LAST_RETURN] = response;
	 }
	 
	 private void checkForGroovy(def bits){
		 def m = bits.find{it -> ((String)it).contains("(")}
		 if (m != null){
			 log("Looks like you are running groovy code. You should use 'g' to do that.");
			 log("Calling Admin scripts doesn't normally need brackets. Trying Anyway (Expect an error).");
			 log("");
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
		 buildDSLHelp();
		 
		 dslDoc.each{k,v ->
			 def summ = v.summary != null ? v.summary : "No Doc."
			 log(String.format('%1$-35s      -      %2$-100s', k + "(..)", summ));
		 }
		
		 log("Run this methods as groovy statements: g <statement>");
		 log("Help can give more info on 'g' or each of the methods.");
	 }
	 
	 private void buildDSLHelp(){
		 if (dslDoc == null){
			 log "Building DSL Help."
			 dslDoc = new HashMap<String,DSLHelp>();
			 
			 def methods = dsl.metaClass.methods;
			 
			  methods.each{
				  
				  if (it.getDeclaringClass().theClass == dsl.class){
					  DSLHelp dslHelp = new DSLHelp();
					  dslHelp.method = """${it.name}(${it.parameterTypes.name.join(', ')}) returns ${it.returnType.name} """;
					   
					  parseJavaDocForMethod(it,dslHelp);
					  dslDoc[it.name] = dslHelp;
				  }
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
			 Cmd c = commandList.find {it.name.find{it2 -> it2.toLowerCase() == command.toLowerCase()} != null};
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
					log "Not an admin script. Checking DSL functions"
					
					def methods = dsl.metaClass.methods;
					
					def meth = methods.find{it -> it.name.toLowerCase() == command.toLowerCase() }
					if (meth != null){
						printJavaDocForMethod(meth);
					} else {
						log "Not a DSL function. Giving up."
					}
					
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
				 for (ParameterUsage pu : sd.getUsageList()){
					 sb.append(" " + pu.getName() + "=<" + pu?.getDescription() + "> ");
				 }
				 log(sb.toString());
			 }
			 log(sd.getUsage());
		  } else {
			 log("Script does not have usage information");
		  }
		  log "See AdminTool.md for more details. (Maybe)";
	 }
	 
	 /**
	  * Logs to output
	  * @param msg
	  */
	 public void log(String msg){
		 if (out){
			 out.println msg;
		 } else {
		 	println msg;
		 }
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
	 
	 /**
	  * Prints the method javadoc for one method
	  * @param m
	  */
	 private void printJavaDocForMethod(MetaMethod m){
		 DSLHelp help = null;
		 if (dslDoc == null){
			 help = new DSLHelp();
			 parseJavaDocForMethod(m,help);
		 } else {
		 	help = dslDoc[m.name];
		 }
		 
		 if (help.full){
			 log help.full;
		 } else {
		 	if (help.summary){
				 log help.summary;
			 }
		 	log "No Doc."
			log help.method;
		 }
		 log "";
		 log "Call this method as groovy code with: g ${m.name}(...)"
		 log "See 'help g' for some handy tricks."
		 
	 }
	 
	 private void parseJavaDocForMethod(MetaMethod m,DSLHelp help){
		 String source = locateDSLSource();
		 if (source == null){
			 return;
		 }

		 
		 def returnType = ""; 
		 
		 if (m.returnType.name.contains(".")){
			 returnType = m.returnType.name.substring(m.returnType.name.lastIndexOf('.'));
		 } else {
		 	returnType = m.returnType.name
		 }
		 
		 def patterStr = null;
		 if ( m.returnType == Object ){
			 patterStr = "def ${m.name}\\(.*\\)";
		 } else {
		 	patterStr = "${returnType} ${m.name}\\(.*\\)";
		 }
		 
		 Pattern p = Pattern.compile(patterStr);
		 Matcher matcher = p.matcher(source);
		 
		 int lastStart = 0;
		 int lastEnd = 0;
		 while (matcher.find()){
			 int start = matcher.start();
			 int end = matcher.end();
			 
			 // Find bounds for java doc
			 String preceding = source.substring(lastEnd, end);
			 int precedingStart = preceding.lastIndexOf('}');
			 if (precedingStart == -1){
				 precedingStart = preceding.lastIndexOf('{');
			 }
			 
			 if (precedingStart == -1){
				 log "Unable to determine start of javadoc for method " + m.name;
			 }
			 
			 String javadocSection = preceding.substring(precedingStart);
			 help.full = javadocSection;
			 
			 def lines = javadocSection.readLines();
			 for (String line : lines){
				 if (line =~ /\\*.*[a-zA-Z]/){
					 if (line != null){
						 help.summary = "" + line.replaceAll("  " , "").replace('*', "").replace('\t', "") + "..." ;
					 }
					 break;	
				 }
			 }


			 
			 lastStart = start;
			 lastEnd = end;
		 }
		 
		 if (help.summary == null){
	 		 if (m.name.startsWith("get") || m.name.startsWith("is")){
				  help.summary = "  Property getter.";
			 }
			 if (m.name.startsWith("set")){
				  help.summary = "  Property setter.";
			 }
		  }
		  
		  if (help.full == null){
			 if (m.name.startsWith("get")){
			     help.full = "Property getter.";
			 }
			 
			 if (m.name.startsWith("set")){
			     help.full = "Property setter.";
			 }
		  }
	   
		  if (help.full == null || help.summary == null){
			 // println "############# " + patterStr;
		  }
	 }
	 
	 String dslSource = null; 
	 /**
	  * TODO change this after I sure this idea works.
	  * @return - the source code of FlowTestDSL
	  */
	 private String locateDSLSource(){

		 if (dslSource == null){
			 String basePath = "./src/main/groovy/";
			 String name = FlowTestDSL.class.name;
			 name = name.replace(".", "/");
			 String sourcePath = basePath + name + ".groovy";
			 
			 try {
				 def file = new File(sourcePath);
				 dslSource = file.getText();
			 } catch (Exception e){
			 	log "Can't find DSL source code in " + sourcePath;
			 }
			   
		 }
		return dslSource;
		 
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

/**
 * Stores dsl javadoc
 * @author daisy
 */
public class DSLHelp{
	def method;
	def summary;
	def full;
}