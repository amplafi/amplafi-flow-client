package org.amplafi.flow.ui.command;

import org.amplafi.flow.utils.AdminTool;

/**
 * TO_BRUNO: add javadoc.
 *
 */
public class TokenizeCommand extends AShellCommand {

	protected TokenizeCommand(boolean setHelp, String setOptions) {
		super(setOptions);
		//super(setHelp, "tokenize", setOptions);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int execute(AdminTool adminTool) {
		String chain = getOptions();
		/*List<String> stList = ParameterTokenizer.tokenize(chain);
		int l = stList.size();
		System.out.println(l + " tokens detected");
		Iterator<String> it = stList.iterator();
		for(int i =0; i< l; i++){
			System.out.println("Token " + i + ": " + it.next());
		}*/
		return 0;
	}

}
