package org.amplafi.flow.shell;

import java.io.Console;
import java.net.URI;

import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.json.JSONObject;

/**
 * @author Tuan Nguyen
 */
public class ExecFlow extends Action {
    public void exec(Console c, ShellContext context, String[] params) throws Exception {
        if(params.length == 1) {
            String fullUri = buildBaseUriString(context) ;
            String flowName = params[0] ;
            GeneralFlowRequest request = new GeneralFlowRequest(URI.create(fullUri), flowName, fsRenderResult);
            String result = request.get();
            if(!result.startsWith("{")) {
                result = "{\"result\": " + result + "}" ;
            }
            JSONObject jsonObject = new JSONObject(result ) ;
            c.printf("%1s%n", jsonObject.toString(2));
        } else {
            c.printf("Wrong command format. Expect: ") ;
            c.printf("  desc FlowName") ;
        }
    }
}