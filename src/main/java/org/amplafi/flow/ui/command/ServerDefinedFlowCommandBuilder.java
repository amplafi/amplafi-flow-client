package org.amplafi.flow.ui.command;

/**
 * Make manual calls to server
 * @author patmoore
 *
 */
public class ServerDefinedFlowCommandBuilder implements ShellCommandBuilder {

    public ServerDefinedFlowCommandBuilder() {
    }


    @Override
    public ShellCommand buildCommand(String options) {
        return new ServerDefinedFlowCommand(options);
    }

    @Override
    public ShellCommand buildHelp(String options) {
        return null;
    }


    @Override
    public String getCommandName() {
        return "manual";
    }
}
