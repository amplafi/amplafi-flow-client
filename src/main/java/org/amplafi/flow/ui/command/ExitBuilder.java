package org.amplafi.flow.ui.command;

/**
 * @author bfv
 * Builder for the exit command
 */
public class ExitBuilder implements AbstractShellCommandBuilder {

	@Override
	public String getCommandName() {
		return "exit";
	}

	@Override
	public AShellCommand buildCommand(String options) {
		return new ExitCommand(options);
	}

	@Override
	public AShellCommand buildHelp(String options) {
		return new DisplayCommand("Exits the shell");
	}

}
