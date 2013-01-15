package org.amplafi.flow.shell;

import java.io.Console;
import java.net.URI;

import org.amplafi.flow.utils.GeneralFlowRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Tuan Nguyen
 */
public class ListFlow implements Action {
    private static final NameValuePair fsRenderResult = new BasicNameValuePair("fsRenderResult", "json");
    private static final NameValuePair describe = new BasicNameValuePair("describe",null);
    
    private String buildBaseUriString(ShellContext context) {
        String fullUri =  context.getHost()  
                    + ":" + context.getPort() + "/c/"
                    + context.getApiKey() 
                    + "/" + context.getApiVersion(); 
        return fullUri;
    }
    
    public void exec(Console c, ShellContext context, String[] params) throws Exception {
        String fullUri = buildBaseUriString(context) ;
		GeneralFlowRequest request = new GeneralFlowRequest(URI.create(fullUri), null, fsRenderResult, describe);
        
        String result = request.get();

        System.out.println(result);
    }
}