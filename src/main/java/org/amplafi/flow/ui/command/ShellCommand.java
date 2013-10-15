package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

public interface ShellCommand {
    int execute(AdminTool adminTool);
}
