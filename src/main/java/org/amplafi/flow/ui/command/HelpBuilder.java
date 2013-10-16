package org.amplafi.flow.ui.command;

import org.amplafi.flow.ui.InteractiveShell;

/**
 * Builder for HelpCommand
 */
public class HelpBuilder implements ShellCommandBuilder {

    private ShellCommandManager shellCommandManager;

    public HelpBuilder(InteractiveShell is) {
        this.setShellCommandManager(is.getShellCommandManager());
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public ShellCommand buildCommand(String options) {
        return new HelpCommand(getShellCommandManager(), options);
    }

    @Override
    public ShellCommand buildHelp(String options) {
        return new DisplayCommand("To see the current available commands write \"help\", or specify a command through \"help <command>\"");
    }

    public ShellCommandManager getShellCommandManager() {
        return shellCommandManager;
    }

    public void setShellCommandManager(ShellCommandManager shellCommandManager) {
        this.shellCommandManager = shellCommandManager;
    }

}
