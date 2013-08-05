package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * @author bfv
 * A command to print something to console output.
 */
public class DisplayCommand extends AShellCommand {

	public DisplayCommand(String setOptions) {
		super(setOptions);
	}

	@Override
	public int execute(AdminTool adminTool) {
		//TODO (Bruno) refactor this and other direct calls to sysste.out to a
		//			   stream belonging to AdminTool.
		System.out.println(getOptions());
		return 0;
	}

}
