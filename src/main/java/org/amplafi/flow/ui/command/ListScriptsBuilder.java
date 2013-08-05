package org.amplafi.flow.ui.command;

/**
 * @author bfv
 * Builder for ListScriptsCommand
 */
public class ListScriptsBuilder implements AbstractShellCommandBuilder {

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
