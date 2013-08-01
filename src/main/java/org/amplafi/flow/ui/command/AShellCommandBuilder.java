package org.amplafi.flow.ui.command;

/**
 * 
 * TO_BRUNO: what does the A prefix in the interface name mean? Use a full word if needed.
 *
 */
public interface AShellCommandBuilder {

	public String getCommandName();
	public AShellCommand buildCommand(String options);
	public AShellCommand buildHelp(String options);
}
