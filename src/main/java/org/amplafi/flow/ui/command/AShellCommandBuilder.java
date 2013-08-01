package org.amplafi.flow.ui.command;

public interface AShellCommandBuilder {

	public String getCommandName();
	public AShellCommand buildCommand(String options);
	public AShellCommand buildHelp(String options);
}
