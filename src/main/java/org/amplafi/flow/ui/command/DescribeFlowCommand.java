package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.flow.utils.AdminTool;

public class DescribeFlowCommand extends AShellCommand {

	static final Pattern p = Pattern.compile("^([^\\s]*)$");
	
	protected DescribeFlowCommand(boolean setHelp, String setOptions) {
		super(setHelp, "describeapi", setOptions);
	}

	@Override
	protected String helpString() {
		return "Describes an API. Can be used only if you're authorized for privileged. "
				+ "Available APIs: 'public', 'api', 'su'";
	}

	@Override
	protected int executeCommand(AdminTool adminTool) {
		String st = this.getOptions();
		Matcher m = p.matcher(st);
		if(m.matches()){
			String api = m.group(1);
			if(!adminTool.describeApi(api))
				System.out.println("Invalid API. Make sure you typed it correctly, available APIs: 'public', 'api', 'su'");
		}else{
			System.out.println("Invalid Options. Usage: describe <api>");
		}
		return 0;
	}

}
