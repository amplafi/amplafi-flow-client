package org.amplafi.flow.ui.command;

/**
 * TO_BRUNO: add javadoc.
 *
 */
public class ExitBuilder implements AShellCommandBuilder {

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
