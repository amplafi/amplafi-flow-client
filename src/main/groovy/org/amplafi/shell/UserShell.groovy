package org.amplafi.shell;

import groovy.transform.Canonical;
import org.amplafi.dsl.FlowTestDSL;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.utils.UtilParent;
import org.apache.commons.logging.Log;

@Canonical // creates a shell to change current user.
public class UserShell{
	
	def PROMPT = ">"
	FlowTestDSL dsl; 
	Console console;
	char[] passwd;
	boolean running = true;
	 
	def UserShell(FlowTestDSL dsl){
		this.dsl = dsl;
	 }

	 public void runCommand(){
		 if ((console = System.console()) != null) {
				 while (running) {
					 String commandLine = console.readLine(PROMPT, new Date());
					 println "You said " + commandLine;
			 
					 //commandLine = commandLine.toLowerCase();
					 commandLine = commandLine.trim();
					 String[]  bits = commandLine.split(" ");
			 
					 if (bits.size() > 0){
						 def command = bits[0];
						 command = command.toLowerCase();
						 if (command == "list"){
							 list();
						 }
						if (command == "call"){
							call(bits);
						}
						if (command == "run"){
							run(bits);
						}
						if (command == "exit"){
							exit();
						}
						
						if (command == "help"){
							help();
						}
					}
				 }
		 }
	 }
	 
	 private void list(){
		 ScriptRunner runner = new ScriptRunner(null,null);
		 String[] scripts = runner.findAllScripts(UtilParent.DEFAULT_COMMAND_SCRIPT_PATH);
		 println scripts;
	 }
	 
	 private void call(def bits){
		 def response = "";
		 
		 def params = [:];
		 if(bits.size() == 1){
			getLog().info("please specify the script you run");
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
					getLog().error("you must give the parameter like:paramName=paramValue");
					return;
				}
				def paramName = paramSplit[0];
				def paramValue = paramSplit[1];
				params[paramName] = paramValue;
			}
			try{
				response = dsl.callScript(bits[1],params);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	 }
	 
	 private void run(def bits){
		 def sb = new StringBuffer();
		 for(int i = 1;i<bits.size();i++){
			 sb << bits[i] + " ";
		 }
		 String str = sb.toString();
		 dsl.runSnippet(str);
	 }
	
	 private void exit(){
		 running = false;
	 }
	
	 private void help(){
		 println "--------------------------------------------------";
		 println("you can enter the commands below:\n"); 
		 println("list:show all of the command scripts and their path");
		 println("call:run a script you specified,for example:");
		 println("     call CreateSuApiKey userEmail=admin@amplafi.com publicUri=http://fortunatefamilies.com");
		 println("run: ");
		 println("help:show the help document");
		 println("exit:exit from this shell");
		 println "--------------------------------------------------";
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