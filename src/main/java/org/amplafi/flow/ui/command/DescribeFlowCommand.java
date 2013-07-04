package org.amplafi.flow.ui.command;

import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.flow.ui.CustomerServiceShell;
import org.amplafi.flow.utils.AdminTool;
import org.amplafi.flow.utils.AdminToolCommandLineOptions;
import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.json.JSONObject;

public class DescribeFlowCommand extends AShellCommand {

	protected DescribeFlowCommand(boolean setHelp, String setCommandName,
			String setOptions) {
		super(setHelp, "describeflow", setOptions);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String printHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int executeCommand(AdminTool adminTool) {
		/*GeneralFlowRequest request = new GeneralFlowRequest(service, key, flowName);
		JSONObject flows = request.describeFlow();
	        if(verbose){
	            emitOutput("");
	            emitOutput(" Sent Request: " + request.getRequestString() );
	            emitOutput(" With key: " + request.getApiKey() );
	            emitOutput("");
	        }
	        emitOutput(flows.toString(4));
	    }*/
		System.out.println("Not implemented");
		return 0;
	}
    

}
