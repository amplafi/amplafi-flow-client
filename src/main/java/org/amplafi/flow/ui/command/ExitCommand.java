package org.amplafi.flow.ui.command;

import org.amplafi.flow.ui.CustomerServiceShell;
import org.amplafi.flow.utils.AdminTool;

public class ExitCommand extends AShellCommand{
	
	protected ExitCommand(boolean setHelp, String options) {
		super(setHelp,"exit",options);
	}

	@Override
	protected String printHelp() {
		return "Exits the shell";
	}

	@Override
	protected int executeCommand(AdminTool adminTool) {
		System.exit(0);
		return 0;
	}
}
