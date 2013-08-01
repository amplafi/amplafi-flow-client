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
	// unparsed options that might be used however the command sees fit.
	private String options;
	private Collection<String> flags;
	private Map<String,String> optionsMap;

	protected AShellCommand(String setOptions) {
		setOptions(setOptions);
		flags = new HashSet<String>();
	}

	public abstract int execute(AdminTool adminTool);

	protected String getOptions() {
		return options;
	}

	protected void setOptions(String options) {
		this.options = options;
	};
}
