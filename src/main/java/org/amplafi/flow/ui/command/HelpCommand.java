package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.flow.utils.AdminTool;

public class HelpCommand extends AShellCommand {

	private static final Pattern optPattern = Pattern.compile("(^$)|(.+)");

	protected HelpCommand(boolean setHelp, String options) {
		super(setHelp, "help", options);
	}

	@Override
	protected String helpString() {
		return "To see the current available commands write"
				+ " \"help\", or specify a commandthrough \"help <command>\"";
	}

	@Override
	protected int executeCommand(AdminTool adminTool) {
		String rawOpts = getOptions();
		Matcher m = optPattern.matcher(rawOpts);
		m.matches();
		if (m.group(2) != null) {
			System.out.println("Unrecognized command \"" + m.group(2)
					+ "\". To see a list of commands type \"help\"");
		} else {
			// TODO find a way to display all currently installed commands
			System.out.println("FIXME");
		}
		return 0;
	}
}
