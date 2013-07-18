package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//class that knows all the shell commands
public class ShellCommandBuilder {

	private static final Pattern basicCommand = Pattern
			.compile("^(([^\\s]*)\\s+(.*)|(.*))$");
	private static ShellCommandBuilder instance = null;

	protected ShellCommandBuilder() {

	}

	public static ShellCommandBuilder getBuilder() {
		if (instance == null)
			instance = new ShellCommandBuilder();
		return instance;
	}

	public AShellCommand build(String commandLine) {
		Matcher m = basicCommand.matcher(commandLine);
		m.matches();
		String commandName, commandParameters;
		boolean help = false;
		if (m.group(2) == null) {
			commandName = m.group(1);
			commandParameters = "";
		} else {
			commandName = m.group(2);
			commandParameters = m.group(3);
			if ("help".equals(commandName)) {
				commandName = commandParameters;
				help = true;
			}
		}
		switch (commandName.toLowerCase()) {
		case "exit":
			return new ExitCommand(help, commandParameters);
		case "tokenize":
			return new TokenizeCommand(help, commandParameters);
		case "run":
			return new RunScriptCommand(help, commandParameters);
		case "describe":
			return new DescribeFlowCommand(help, commandParameters);
		case "flows":
			return new ListFlowsCommand(help, commandParameters);
		case "scripts":
			return new ListScriptsCommand(help, commandParameters);
		case "help":
			return new HelpCommand(help, commandParameters);
		default:
			return new EmptyCommand(false, commandParameters);
		}
	}

}
