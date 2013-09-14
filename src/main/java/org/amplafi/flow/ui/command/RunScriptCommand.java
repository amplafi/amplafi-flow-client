package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.flow.utils.AdminTool;

/**
 * @author bfv
 * Command that interacts with the ScriptRunner and calls a particular script.
 */
public class RunScriptCommand extends AShellCommand {

	static final Pattern NO_SPACE_WORD = Pattern.compile("^([^\\s]*)$");
	
	public RunScriptCommand(String setOptions) {
		super(setOptions);
		
	}
	
	@Override
	public int execute(AdminTool adminTool) {
		String st = this.getOptions();
		Matcher m = NO_SPACE_WORD.matcher(st);
		if(m.matches()){
			String script = m.group(1);
			if(!adminTool.runScript(script))
				System.out.println("Invalid script name. Make sure you typed it correctly.");
		}else{
			System.out.println("Invalid options. Specify the name of a script. To see script names "
					+ "use command \"lists\"");
		}
		return 0;
	}

}
