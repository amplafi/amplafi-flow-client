package org.amplafi.flow.ui.command;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.internal.Lists;

//class that knows all the shell commands
//and builds them when the time comes
//to add a command, add one keyword to the build method
public class ShellCommandBuilder {

	private static final Pattern basicCommand = Pattern
			.compile("^(([^\\s]*)\\s+(.*)|(.*))$");

	private List<AShellCommandBuilder> commandBuilders = Lists.newArrayList();
	public ShellCommandBuilder(){
	}
	public void addCommand(AShellCommandBuilder commandBuilder){
		getCommandBuilders().add(commandBuilder);
	}
	public AShellCommand build(String commandLine) {
		Matcher m = basicCommand.matcher(commandLine);
		m.matches();
		String commandName, commandParameters;
		boolean help = false;
		if (m.group(2) == null) {
			commandName = m.group(1);
			commandParameters = "";
		} else {
			commandName = m.group(2);
			commandParameters = m.group(3);
			if ("help".equals(commandName)) {
				commandName = commandParameters;
				help = true;
			}
		}
		String commandname = commandName.toLowerCase();
		//build commands by name
		for(AShellCommandBuilder builder: getCommandBuilders()){
			if(commandname.equals(builder.getCommandName()))
				if(!help)
					return builder.buildCommand(commandParameters);
				else
					return builder.buildHelp(commandParameters);
		}
		//in case no command is explicitly named, try building by number
		if(isInteger(commandName)){
			int commandNumber = Integer.parseInt(commandName);
			if(commandNumber < getCommandBuilders().size()){
				AShellCommandBuilder ascb = getCommandBuilders().get(commandNumber);
				if(!help)
					return ascb.buildCommand(commandParameters);
				else
					return ascb.buildHelp(commandParameters);
				}else{
					return new DisplayCommand("Index out of bounds, make sure you typed a number within the allowed range (0-" + getCommandBuilders().size() + ").");
				}
		}
		return new EmptyCommand(commandParameters);
		/*
		switch (commandName.toLowerCase()) {
		case "exit":
			return new ExitCommand(help, commandParameters);
		case "tokenize":
			return new TokenizeCommand(help, commandParameters);
		case "run":
			return new RunScriptCommand(help, commandParameters);
		case "describe":
			return new DescribeFlowCommand(help, commandParameters);
		case "flows":
			return new ListFlowsCommand(help, commandParameters);
		case "scripts":
			return new ListScriptsCommand(help, commandParameters);
		case "help":
			return new HelpCommand(help, commandParameters);
		default:
			return new EmptyCommand(false, commandParameters);
		}*/
	}
	public List<AShellCommandBuilder> getCommandBuilders() {
		return commandBuilders;
	}
	public void setCommandBuilders(List<AShellCommandBuilder> commandBuilders) {
		this.commandBuilders = commandBuilders;
	}
	
	//helper function for the bulid by number
	private boolean isInteger( String input ) {
	    try {
	        Integer.parseInt( input );
	        return true;
	    }
	    catch( Exception e ) {
	        return false;
	    }
	}
}
