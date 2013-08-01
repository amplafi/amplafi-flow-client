package org.amplafi.flow.ui.command;

import org.amplafi.flow.ui.InteractiveShell;

public class HelpBuilder implements AShellCommandBuilder {
	
	private ShellCommandBuilder shellCommandBuilder;
	
	public HelpBuilder(InteractiveShell is){
		this.setShellCommandBuilder(is.getShellCommandBuilder());
	}

	@Override
	public String getCommandName() {
		return "help";
	}

	@Override
	public AShellCommand buildCommand(String options) {
		return new HelpCommand(getShellCommandBuilder(),options);
	}

	@Override
	public AShellCommand buildHelp(String options) {
		return new DisplayCommand("To see the current available commands write"
				+ " \"help\", or specify a command through \"help <command>\"");
	}

	public ShellCommandBuilder getShellCommandBuilder() {
		return shellCommandBuilder;
	}

	public void setShellCommandBuilder(ShellCommandBuilder shellCommandBuilder) {
		this.shellCommandBuilder = shellCommandBuilder;
	}

}
