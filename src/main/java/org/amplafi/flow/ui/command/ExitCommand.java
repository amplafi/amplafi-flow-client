package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * A command to exit the program without errors
 */
public class ExitCommand extends AbstractShellCommand {

    public ExitCommand(String options) {
        super(options);
    }

    @Override
    public void execute(AdminTool adminTool) {
        System.exit(0);
    }
}
