package org.amplafi.flow.shell;

import java.io.Console;
import java.net.URI;

import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.json.JSONObject;

/**
 * @author Tuan Nguyen
 */
public class ListFlow extends Action {

    public void exec(Console c, ShellContext context, String args) throws Exception {
        String fullUri = buildBaseUriString(context) ;
        GeneralFlowRequest request = new GeneralFlowRequest(null, null,null,  fsRenderResult, describe);
        String result = request.get();
        JSONObject jsonObject = new JSONObject("{ result: " + result + "}") ;
        c.printf("%1s%n", jsonObject.toString(2));
    }

    public String getHelpInstruction() {
        return "List the available flow for the selected api version" ;
    }
}
