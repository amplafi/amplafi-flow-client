package org.amplafi.flow.ui.command;

/**
 * Builder for api and flow description commands
 */
public class DescribeApiOrFlowBuilder implements ShellCommandBuilder {

    @Override
    public String getCommandName() {
        return "describe";
    }

    @Override
    public ShellCommand buildCommand(String options) {
        return new DescribeFlowCommand(options);
    }

    @Override
    public ShellCommand buildHelp(String options) {
        return new DisplayCommand("Describes an API. Can be used only if you're authorized for privileged. "
            + "Available APIs: 'public', 'api', 'su'");
    }

}
