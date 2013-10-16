package org.amplafi.flow.ui.command;

import java.io.IOException;

import org.amplafi.flow.utils.AdminTool;

/**
 * Command that interacts with the ScriptRunner and calls a particular script.
 */
public abstract class RunScriptCommand extends AbstractShellCommand {

    public RunScriptCommand(String setOptions) {
        super(setOptions);
    }

    @Override
    public void execute(AdminTool adminTool) {
        String script = getScriptName();
        try {
            if (!adminTool.runScript(script)) {
                System.out.println("Invalid script name. Make sure you typed it correctly.");
            }
        } catch (IOException exception) {
            System.out.println("Problem reading script file:" + exception.getMessage());
        }
    }

    abstract String getScriptName();

}
