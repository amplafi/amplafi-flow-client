package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.flow.utils.AdminTool;

public class RunScriptCommand extends AShellCommand {

	static final Pattern p = Pattern.compile("^([^\\s]*)$");
	
	public RunScriptCommand(boolean setHelp, String setOptions) {
		super(setHelp, "run", setOptions);
		
	}

	@Override
	protected String helpString() {
		return "Runs a script. Usage: \"run <scriptname>\". To find which scripts are available "
				+ "use the \"scripts\" command.";
	}

	@Override
	protected int executeCommand(AdminTool adminTool) {
		String st = this.getOptions();
		Matcher m = p.matcher(st);
		if(m.matches()){
			String script = m.group(1);
			if(!adminTool.runScript(script))
				System.out.println("Invalid script name. Make sure you typed it correctly");
		}else{
			System.out.println("Invalid options. Specify the name of a script. To see script names "
					+ "use command \"scripts\"");
		}
		return 0;
	}

}
