package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.flow.utils.AdminTool;

public class HelpCommand extends AShellCommand {

	private static final Pattern OPT_PATTERN = Pattern.compile("(^$)|(.+)");

	private ShellCommandBuilder shellCommandBuilder;
	protected HelpCommand(ShellCommandBuilder scb, String options) {
		super(options);
		this.shellCommandBuilder = scb;
	}

	@Override
	public int execute(AdminTool adminTool) {
		String rawOpts = getOptions();
		Matcher m = OPT_PATTERN.matcher(rawOpts);
		m.matches();
		if (m.group(2) != null) {
			for(AbstractShellCommandBuilder scb : shellCommandBuilder.getCommandBuilders()){
				if(scb.getCommandName().equals(m.group(2))){
					return scb.buildHelp("").execute(adminTool);
				}
			}
			System.out.println("Unrecognized command \"" + m.group(2)
					+ "\". To see a list of commands type \"help\"");
		} else {
			System.out.println("Commands available. To run them, type the name or their number:");
			int i = 0;
			for(AbstractShellCommandBuilder scb : shellCommandBuilder.getCommandBuilders()){
				new DisplayCommand(i++ + " - " + scb.getCommandName()).execute(adminTool);
			}			
		}
		return 0;
	}
}
