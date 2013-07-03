package org.amplafi.flow.ui.command;

import org.amplafi.flow.ui.CustomerServiceShell;

public class ExitCommand extends AShellCommand{
	
	protected ExitCommand(boolean setHelp, String options) {
		super(setHelp,"Exit");
	}

	@Override
	protected String printHelp() {
		return "Exits the shell";
	}

	@Override
	protected int executeCommand(CustomerServiceShell cSShell) {
		System.exit(0);
		return 0;
	}
}
