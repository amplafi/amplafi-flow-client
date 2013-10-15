package org.amplafi.flow.ui;

import org.amplafi.flow.ui.command.DisplayCommand;
import org.amplafi.flow.ui.command.SetParameterCommand;
import org.amplafi.flow.ui.command.ShellCommand;
import org.amplafi.flow.ui.command.ShellCommandBuilder;

public class SetParameterCommandBuilder implements ShellCommandBuilder {

    public SetParameterCommandBuilder() {

    }

    @Override
    public String getCommandName() {
        return "set";
    }

    @Override
    public ShellCommand buildCommand(String options) {
        return new SetParameterCommand(options);
    }

    @Override
    public ShellCommand buildHelp(String options) {
        return new DisplayCommand("set <parameter> <values>");
    }

}
