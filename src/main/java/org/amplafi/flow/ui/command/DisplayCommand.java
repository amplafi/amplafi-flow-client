package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * TO_BRUNO: add javadoc.
 *
 */
public class DisplayCommand extends AShellCommand {

	public DisplayCommand(String setOptions) {
		super(setOptions);
	}

	@Override
	public int execute(AdminTool adminTool) {
		System.out.println(getOptions());
		return 0;
	}

}
