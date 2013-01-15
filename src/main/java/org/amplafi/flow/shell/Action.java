package org.amplafi.flow.shell;

import java.io.Console;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public abstract class Action {
    public static final NameValuePair fsRenderResult = new BasicNameValuePair("fsRenderResult", "json");
    public static final NameValuePair describe = new BasicNameValuePair("describe",null);
    
    abstract public void exec(Console c, ShellContext context, String[] params) throws Exception;
    
    
    static protected String buildBaseUriString(ShellContext context) {
        String fullUri =  context.getHost()  
                    + ":" + context.getPort() + "/c/"
                    + context.getApiKey() 
                    + "/" + context.getApiVersion(); 
        return fullUri;
    }
    
    static protected String tutorialUriString(ShellContext context) {
        String fullUri =  context.getHost()  
                    + ":" + context.getPort()
                    + "/tutorial/flow";
        return fullUri;
    }
}