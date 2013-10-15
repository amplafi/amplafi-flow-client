package org.amplafi.flow.ui;

import org.amplafi.flow.ui.command.ShellCommand;
import org.amplafi.flow.ui.command.ShellCommandBuilder;
import org.amplafi.flow.ui.command.DisplayCommand;
import org.amplafi.flow.ui.command.NamedScriptCommand;

public class NamedScriptCommandBuilder implements ShellCommandBuilder {

    private String scriptName;

    public NamedScriptCommandBuilder(String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public String getCommandName() {
        return scriptName;
    }

    @Override
    public ShellCommand buildCommand(String options) {
        return new NamedScriptCommand(options, scriptName);
    }

    @Override
    public ShellCommand buildHelp(String options) {
        return new DisplayCommand("Runs " + scriptName);
    }

}
