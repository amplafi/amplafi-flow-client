package org.amplafi.flow.ui.command;

import java.util.regex.Matcher;

import org.amplafi.flow.utils.AdminTool;

/**
 * @author bfv
 * Command to describe flows or APIs. Constructor Options should be API name and optionally flow name.
 */
public class DescribeFlowCommand extends AbstractShellCommand {

	protected DescribeFlowCommand(String setOptions) {
		super(setOptions);
	}

	@Override
	public void execute(AdminTool adminTool) {
		String options = this.getOptions();
		Matcher m = ONE_AND_MAYBE_TWO_WORDS.matcher(options);
		Matcher m2 = NOTHING.matcher(options);
		if(m.matches() && !m2.matches()){
			String api = m.group(1);
			String flow = m.group(2);
			boolean success = adminTool.describeFlow(api,flow);
			if(!success){
				System.out.println("Invalid API. Make sure you typed it correctly, available APIs: 'public', 'api', 'su'");
			}

		}else{
			System.out.println("Invalid Options. Usage: describe <api> [<flow>]");
		}
	}

}
