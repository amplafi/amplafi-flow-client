package org.amplafi.flow.ui.command;

/**
 * @author bfv
 * Interface for command builders. A command can build itself or a command to display
 * the help documentation for the command.
 */
public interface AbstractShellCommandBuilder {

	public String getCommandName();
	public AShellCommand buildCommand(String options);
	public AShellCommand buildHelp(String options);
}
