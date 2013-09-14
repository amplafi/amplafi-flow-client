package org.amplafi.flow.ui;

import org.amplafi.flow.ui.command.AShellCommand;
import org.amplafi.flow.ui.command.AbstractShellCommandBuilder;
import org.amplafi.flow.ui.command.DisplayCommand;
import org.amplafi.flow.ui.command.NamedScriptCommand;

public class NamedScriptCommandBuilder implements AbstractShellCommandBuilder {

    private String scriptName;

    public NamedScriptCommandBuilder(String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public String getCommandName() {
        return scriptName;
    }

    @Override
    public AShellCommand buildCommand(String options) {
        return new NamedScriptCommand(options, scriptName);
    }

    @Override
    public AShellCommand buildHelp(String options) {
        return new DisplayCommand("Runs " + scriptName);
    }

}
