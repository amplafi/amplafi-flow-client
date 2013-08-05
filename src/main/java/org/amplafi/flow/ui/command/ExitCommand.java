package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * @author bfv
 * A command to exit the program without errors
 */
public class ExitCommand extends AShellCommand {

	public ExitCommand(String options) {
		super(options);
	}

	@Override
	public int execute(AdminTool adminTool) {
		System.exit(0);
		return 0;
	}
}
