package org.amplafi.flow.ui.command;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.flow.utils.AdminTool;

public class HelpCommand extends AbstractShellCommand {

	private static final Pattern OPT_PATTERN = Pattern.compile("(^$)|(.+)");

	private ShellCommandManager shellCommandManager;
	protected HelpCommand(ShellCommandManager scb, String options) {
		super(options);
		this.shellCommandManager = scb;
	}

	@Override
	public void execute(AdminTool adminTool) {
		String rawOpts = getOptions();
		Matcher m = OPT_PATTERN.matcher(rawOpts);
		m.matches();
		List<ShellCommandBuilder> commandBuilders = shellCommandManager.getCommandBuilders();
        if (m.group(2) != null) {
            ShellCommandBuilder shellCommandBuilder = shellCommandManager.getShellCommandBuilder(m.group(2));
            if ( shellCommandBuilder != null) {
				shellCommandBuilder.buildHelp("").execute(adminTool);
			    return;
			}
			System.out.println("Unrecognized command \"" + m.group(2)
					+ "\". To see a list of commands type \"help\"");
		} else {
			System.out.println("Commands available. To run them, type the name or their number:");
			int i = 0;
			for(ShellCommandBuilder scb : commandBuilders){
				new DisplayCommand(i++ + " - " + scb.getCommandName()).execute(adminTool);
			}
		}
	}
}
