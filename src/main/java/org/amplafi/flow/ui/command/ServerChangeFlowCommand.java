package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;

import org.amplafi.flow.utils.AdminTool;

/**
 * Command to describe flows or APIs. Constructor Options should be API name and optionally flow
 * name.
 */
public class ServerChangeFlowCommand extends AbstractShellCommand {

    protected ServerChangeFlowCommand(String setOptions) {
        super(setOptions);
    }

    @Override
    public void execute(AdminTool adminTool) {
        String options = this.getOptions();
        Matcher m = ONE_WORD.matcher(options);
        if (m.matches() ) {
            String mode = m.group(1);
            adminTool.setMode(mode);

        } else {
            System.out.println("Invalid Options. Usage: mode <server> (local|production|test)");
        }
    }

}
