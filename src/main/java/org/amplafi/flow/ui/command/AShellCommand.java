package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;

import org.amplafi.flow.ui.CustomerServiceShell;

//abstract shell command.
public abstract class AShellCommand {
	public boolean help;
	public String commandName;
	
	//to print help for your command
	protected abstract String printHelp();
	protected AShellCommand(boolean setHelp,String setCommandName){
		help = setHelp;
		commandName = setCommandName;
	}
	protected abstract int executeCommand(CustomerServiceShell cSShell);
	public int execute(CustomerServiceShell cSShell){
		if(help){
			System.out.println(commandName + ": " + printHelp());
			return 0;
		}else{
			return executeCommand(cSShell);
		}
	};
}
