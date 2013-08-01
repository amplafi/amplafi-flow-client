package org.amplafi.flow.ui.command;

public class RunScriptBuilder implements AShellCommandBuilder {

	@Override
	public String getCommandName() {
		return "run";
	}

	@Override
	public AShellCommand buildCommand(String options) {
		return new RunScriptCommand(options);
	}

	@Override
	public AShellCommand buildHelp(String options) {
		return new DisplayCommand( "Runs a script. Usage: \"run <scriptname>\". To find which scripts are available "
				+ "use the \"scripts\" command.");
	}

}
