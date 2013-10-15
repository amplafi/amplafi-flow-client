package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * @author bfv
 * command for testing purposes of the tokenizer class
 */
public class TokenizeCommand extends AbstractShellCommand {

	protected TokenizeCommand(boolean setHelp, String setOptions) {
		super(setOptions);
		//super(setHelp, "tokenize", setOptions);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(AdminTool adminTool) {
		String chain = getOptions();
		/*List<String> stList = ParameterTokenizer.tokenize(chain);
		int l = stList.size();
		System.out.println(l + " tokens detected");
		Iterator<String> it = stList.iterator();
		for(int i =0; i< l; i++){
			System.out.println("Token " + i + ": " + it.next());
		}*/
	}

}
