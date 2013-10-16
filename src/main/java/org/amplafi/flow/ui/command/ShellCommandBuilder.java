package org.amplafi.flow.ui.command;

/**
 * Interface for command builders. A command can build itself or a command to display the help
 * documentation for the command.
 */
public interface ShellCommandBuilder {

    public String getCommandName();

    public ShellCommand buildCommand(String options);

    public ShellCommand buildHelp(String options);
}
