package org.amplafi.flow.ui.command;

/**
 * Builder for api and flow description commands
 */
public class ServerChangeCommandBuilder implements ShellCommandBuilder {

    @Override
    public String getCommandName() {
        return "server";
    }

    @Override
    public ShellCommand buildCommand(String options) {
        return new ServerChangeFlowCommand(options);
    }

    @Override
    public ShellCommand buildHelp(String options) {
        return new DisplayCommand("Changes which server to use: [p]roduction, [l]ocal, [t]est");
    }

}
