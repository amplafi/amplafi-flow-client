package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

public class EmptyCommand extends AShellCommand {

	protected EmptyCommand(boolean setHelp, String options) {
		super(setHelp, "", options);
	}

	@Override
	public String printHelp() {
		return "Invalid command. Use help <command> for specific help";
	}

	@Override
	protected int executeCommand(AdminTool adminTool) {
		System.out
				.println("Invalid command. Use \"help <command>\" for specific help or \"help\" for a list of commands available");
		return 0;
	}
}
