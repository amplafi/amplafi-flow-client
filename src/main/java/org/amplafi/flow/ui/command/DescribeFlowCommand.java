package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.flow.utils.AdminTool;

public class DescribeFlowCommand extends AShellCommand {

	static final Pattern p = Pattern.compile("^([^\\s]*)\\s*([^\\s]*)$");
	
	protected DescribeFlowCommand(String setOptions) {
		super(setOptions);
	}

	@Override
	public int execute(AdminTool adminTool) {
		String st = this.getOptions();
		Matcher m = p.matcher(st);
		if(m.matches()){
			String api = m.group(1);
			String flow = m.group(2);
			boolean success = adminTool.describeFlow(api,flow);
			if(!success){
				System.out.println("Invalid API. Make sure you typed it correctly, available APIs: 'public', 'api', 'su'");
			}

		}else{
			System.out.println("Invalid Options. Usage: describe <api>");
		}
		return 0;
	}

}
