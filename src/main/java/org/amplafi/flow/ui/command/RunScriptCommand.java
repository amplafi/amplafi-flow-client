package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * @author bfv
 * Command that interacts with the ScriptRunner and calls a particular script.
 */
public abstract class RunScriptCommand extends AShellCommand {

	public RunScriptCommand(String setOptions) {
		super(setOptions);
	}
	
	@Override
	public int execute(AdminTool adminTool) {
	    String script = getScriptName();
        if(!adminTool.runScript(script))
		    System.out.println("Invalid script name. Make sure you typed it correctly.");
		return 0;
	}

    abstract String getScriptName();

}
