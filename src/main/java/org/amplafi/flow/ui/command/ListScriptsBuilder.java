package org.amplafi.flow.ui.command;

public class ListScriptsBuilder implements AShellCommandBuilder {

	@Override
	public String getCommandName() {
		return "list";
	}

	@Override
	public AShellCommand buildCommand(String options) {
		return new ListScriptsCommand(options);
	}

	@Override
	public AShellCommand buildHelp(String options) {
		// TODO Auto-generated method stub
		return new DisplayCommand("Lists the currently available command scripts. To execute a script use the \"exec\" command ");
	}

}
