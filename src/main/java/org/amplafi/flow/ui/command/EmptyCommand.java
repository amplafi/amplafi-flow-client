package org.amplafi.flow.ui.command;

import org.amplafi.flow.ui.CustomerServiceShell;

public class EmptyCommand extends AShellCommand {

	protected EmptyCommand(boolean setHelp, String commandParameters) {
		super(setHelp,"");
	}

	@Override
	public String printHelp() {
		return "Invalid command. Use help <command> for specific help";
	}

	@Override
	protected int executeCommand(CustomerServiceShell cSShell) {
		System.out.println("Invalid command. Use help <command> for specific help");
		return 0;
	}

}
