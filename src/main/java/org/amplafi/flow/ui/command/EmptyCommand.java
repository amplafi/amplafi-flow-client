package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * A command that does nothing. It could be generated if no other command can be generated, and will
 * report back to the user that nothing happened.
 */
public class EmptyCommand extends AbstractShellCommand {

    protected EmptyCommand(String options) {
        super(options);
    }

    @Override
    public void execute(AdminTool adminTool) {
        System.out.println("Invalid command. Use \"help <command>\" for specific help or \"help\" for a list of commands available");
    }
}
