package org.amplafi.flow.shell;

import java.io.Console;
import java.net.URI;
import java.util.Map;

import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.json.JSONObject;

/**
 * @author Tuan Nguyen
 */
public class DescFlow extends Action {
    static String USAGE = 
        "Usage: \n"+
        "  desc --name=FlowName --param1=<value> --param2=<value>\n" ;
    
    public void exec(Console c, ShellContext context, String args) throws Exception {
        Map<String, String> options = Command.parseOptions(args);
        String fullUri = buildBaseUriString(context) ;
        String flowName = options.get("name") ;
        if(flowName == null) {
            c.printf(USAGE) ;
        } else {
            GeneralFlowRequest request = new GeneralFlowRequest(URI.create(fullUri), flowName, fsRenderResult, describe);
            String result = request.get();
            JSONObject jsonObject = new JSONObject(result ) ;
            c.printf("%1s%n", jsonObject.toString(2));
        }
    }

    public String getHelpInstruction() {
        return "Show the description of a flow" ;
    }
}