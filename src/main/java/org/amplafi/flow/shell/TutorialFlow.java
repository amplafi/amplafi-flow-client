package org.amplafi.flow.shell;

import java.io.Console;
import java.net.URI;

import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.json.JSONObject;

/**
 * @author Tuan Nguyen
 */
public class TutorialFlow extends Action {
    public void exec(Console c, ShellContext context, String[] params) throws Exception {
        String fullUri = tutorialUriString(context) ;
        String flowName = params[0] ;
        GeneralFlowRequest request = new GeneralFlowRequest(URI.create(fullUri), flowName, fsRenderResult);
        String result = request.get();
        if(!result.startsWith("{")) {
            result = "{\"result\": " + result + "}" ;
        }
        JSONObject jsonObject = new JSONObject(result ) ;
        c.printf("%1s%n", jsonObject.toString(2));
    }
}