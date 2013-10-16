package org.amplafi.flow.ui.command;

/**
 * Builder for the exit command
 */
public class ExitBuilder implements ShellCommandBuilder {

    @Override
    public String getCommandName() {
        return "exit";
    }

    @Override
    public ShellCommand buildCommand(String options) {
        return new ExitCommand(options);
    }

    @Override
    public ShellCommand buildHelp(String options) {
        return new DisplayCommand("Exits the shell");
    }

}
