package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * TO_BRUNO: add javadoc.
 *
 */
public class EmptyCommand extends AShellCommand {

	protected EmptyCommand(String options) {
		super(options);
	}

	@Override
	public int execute(AdminTool adminTool) {
		System.out
				.println("Invalid command. Use \"help <command>\" for specific help or \"help\" for a list of commands available");
		return 0;
	}
}
