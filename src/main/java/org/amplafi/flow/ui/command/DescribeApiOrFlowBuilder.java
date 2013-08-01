package org.amplafi.flow.ui.command;

public class DescribeApiOrFlowBuilder implements AShellCommandBuilder {

	@Override
	public String getCommandName() {
		return "describe";
	}

	@Override
	public AShellCommand buildCommand(String options) {
		return new DescribeFlowCommand(options);
	}

	@Override
	public AShellCommand buildHelp(String options) {
		return new DisplayCommand("Describes an API. Can be used only if you're authorized for privileged. "
				+ "Available APIs: 'public', 'api', 'su'");
	}

}
