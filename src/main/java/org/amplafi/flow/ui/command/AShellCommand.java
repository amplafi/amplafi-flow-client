package org.amplafi.flow.ui.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import org.amplafi.flow.utils.AdminTool;

/*
 * Shell commands are represented by extending this abstract class.
 * Here only crucial, common information is held (command name, options, an abstract executor)
 */
public abstract class AShellCommand {
	// the help command works by executing building the command in help=true.
	private boolean help;
	private String commandName;
	// unparsed options that might be used however the command sees fit.
	private String options;
	private Collection<String> flags;
	private Map<String,String> optionsMap;
	protected abstract String helpString();

	protected AShellCommand(boolean setHelp, String setCommandName,
			String setOptions) {
		help = setHelp;
		setCommandName(setCommandName);
		setOptions(setOptions);
		flags = new HashSet<String>();
		
	}

	protected abstract int executeCommand(AdminTool adminTool);

	public int execute(AdminTool adminTool) {
		if (help) {
			System.out.println(getCommandName() + ": " + helpString());
			return 0;
		} else {
			return executeCommand(adminTool);
		}
	}

	private String getCommandName() {
		return commandName;
	}

	private void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	protected String getOptions() {
		return options;
	}

	protected void setOptions(String options) {
		this.options = options;
	};
}
