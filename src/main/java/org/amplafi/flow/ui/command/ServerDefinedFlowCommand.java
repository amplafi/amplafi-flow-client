package org.amplafi.flow.ui.command;

import java.util.Map;
import java.util.regex.Matcher;

import org.amplafi.flow.utils.AdminTool;
import org.amplafi.flow.utils.FlowResponse;

public class ServerDefinedFlowCommand extends AbstractShellCommand {

    public ServerDefinedFlowCommand(String options) {
        super(options);
    }

    @Override
    public void execute(AdminTool adminTool) {
        String options = this.getOptions();
        Matcher m = ONE_AND_MAYBE_TWO_WORDS.matcher(options);
        if(m.matches()){
            String apiKey = m.group(1);
            String flowName = m.group(2);
            Map paramsMap = null;
            FlowResponse flowResponse = adminTool.request(apiKey, flowName, paramsMap);
            if ( flowResponse.hasError() ) {
                System.err.println("Error message: " + flowResponse.getErrorMessage());
            } else {
                System.out.println(flowResponse.toString());
            }
        }
    }

}
