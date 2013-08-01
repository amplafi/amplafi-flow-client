package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * TO_BRUNO: add javadoc.
 *
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
