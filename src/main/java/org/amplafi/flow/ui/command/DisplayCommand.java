package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * A command to print something to console output.
 */
public class DisplayCommand extends AbstractShellCommand {

    public DisplayCommand(String setOptions) {
        super(setOptions);
    }

    @Override
    public void execute(AdminTool adminTool) {
        System.out.println(getOptions());
    }

}
