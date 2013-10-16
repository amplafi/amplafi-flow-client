package org.amplafi.flow.ui.command;

public class NamedScriptCommand extends RunScriptCommand {

    private String scriptName;

    public NamedScriptCommand(String setOptions, String scriptName) {
        super(setOptions);
        this.scriptName = scriptName;
    }

    @Override
    protected String getScriptName() {
        return scriptName;
    }

}
