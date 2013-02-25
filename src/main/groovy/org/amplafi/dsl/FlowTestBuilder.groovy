package org.amplafi.dsl;
import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.flow.utils.FlowResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import static org.testng.Assert.*;
import java.util.HashMap;
import java.util.Map;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class defines a simple DSL for sending reqests to the amplafi wire server
 * and expecting results.
 *
 * The format of the test language will be
 *
 * request('HelloFlow',['param1':'dog','param2':'pig']);
 *
 * expect("""
 * {    "validationErrors":{
 *      "flow-result":{
 *        "flowValidationTracking":[
 *           {
 *              "key":"MissingRequiredTracking",
 *              "parameters":[
 *                 "HelloFlow"
 *              ]
 *           },
 *           {
 *              "key":"flow.definition-not-found",
 *              "parameters":[
 *
 *              ]
 *           }
 *        ]
 *     }
 *  }
 *}
 *""");
 *
 */
public class FlowTestBuilder {

    private String requestUriString;
    private String host;
    private String port;
    private String apiVersion;
    private String key;
    private ScriptRunner runner;
    private boolean verbose = false;

    /**
     * Constructor for tests
     */
    public FlowTestBuilder(String requestUriString, ScriptRunner runner, boolean verbose){
        this.requestUriString = requestUriString;
        this.runner = runner;
        this.verbose = verbose;
    }

    /**
     * Constructor for tools
     */
    public FlowTestBuilder(String host, String port, String apiVersion, String key, ScriptRunner runner, boolean verbose){
        this.requestUriString = requestUriString;
        this.host = host;
        this.port = port;
        this.apiVersion = apiVersion;
        this.key = key;
        this.runner = runner;
        this.verbose = verbose;
    }

     public FlowTestBuilder(String host, String port, String apiVersion, String key, List<String> paramArray){
        this.requestUriString = requestUriString;
        this.host = host;
        this.port = port;
        this.apiVersion = apiVersion;
        this.key = key;
    }

    /**
     * Configure the closure to be runnaable
     */
    public buildExe(Closure c){
        if (requestUriString != null){
            c.delegate = new FlowTestDSL(requestUriString, runner, verbose);
        } else {
            c.delegate = new FlowTestDSL(host, port, apiVersion, key, runner, verbose);
        }
        c.setResolveStrategy(Closure.DELEGATE_FIRST)
        return c;
    }

    /**
     * Configure the colsure to describe itself.
     */
    public buildDesc(Closure c){
        c.delegate = new DescribeScriptDSL();
        return c;
    }

}

/**
 * This class defines the methods that are callable within the flow test DSL
 */
public class FlowTestDSL extends DescribeScriptDSL {

    def host;
    def port;
    def apiVersion;
    def key;
    def ScriptRunner runner;
    def boolean verbose;
    private Log log;
    private static boolean DEBUG;
    private static final String THICK_DIVIDER =
    "*********************************************************************************";
    private static final String THIN_DIVIDER =
    "---------------------------------------------------------------------------------";
    private String tempApiKey;
    private boolean received = false;
    //private List<String> ignoreList = new ArrayList<String>();

    /** This stores the base uri including the host,port,apikey */
    private String requestUriString = null;

    /** This stores the last request to the server */
    private String lastRequestString = null;

    /**
     * Contains the last response from the server.
     */
    public String lastRequestResponse = null;

    public FlowTestDSL(String requestString, ScriptRunner runner, boolean verbose){
        this.requestUriString = requestString;
        this.runner = runner;
        this.verbose = verbose;
    }

    public FlowTestDSL(String host, String port, String apiVersion, String key, ScriptRunner runner, boolean verbose){
        this.host = host;
        this.port = port;
        this.apiVersion = apiVersion;
        this.key = key;
        this.runner = runner;
        this.verbose = verbose;
    }

    public void description (String name, String description){
        this.name = name;
        this.description = description;        
    }

    public void description (String name, String description, String usage){
        this.usage = usage;
        this.name = name;
        this.description = description;
    }
    
    public void description (String name, String description, List<ParameterUsge> usages){
        this.name = name;
        this.description = description;
        this.usages = usages;
    }
     
    public ParameterUsge paramDef(String name,String description,boolean optional,Object defaultValue){
    
        return new ParameterUsge(name,description,optional,defaultValue);
    }

    void setHost(String host){
        this.host = host;
    }

    void setPort(String port){
        this.port = port;
    }

    void setApiVersion(String apiVersion){
        this.apiVersion = apiVersion;
    }

    void setKey(String key){
        this.key = key;
    }

    String getHost(){
        return host;
    }

    String getPort(){
        return port;
    }

    String getApiVersion(){
        return apiVersion;
    }

    String getKey(){
        return key;
    }

    /**
     * Sends a request to the named flow with the specified parameters
     * @param flowName to call
     * @param paramsMap key value map of parameters to send.
     * @return response string
     */
    String request(String flowName, Map paramsMap){
        GeneralFlowRequest request = createGeneralFlowRequest(flowName, paramsMap);
        lastRequestResponse = request.get();
        debug(lastRequestResponse);
        return lastRequestResponse;
    }

    /**
     * Sends a request to the named flow with the specified parameters
     * @param flowName to call
     * @param paramsMap key value map of parameters to send.
     * @return response object
     */
    FlowResponse requestResponse(String flowName, Map paramsMap){
        GeneralFlowRequest request = createGeneralFlowRequest(flowName, paramsMap);
        FlowResponse response = request.sendRequest();
        lastRequestResponse = response.getResponseAsString() ;
        return response ;
    }

    /**
     * Sends a request to the named flow with the specified parameters
     * @param flowName to call
     * @param paramsMap key value map of parameters to send.
     */
    GeneralFlowRequest createGeneralFlowRequest(String flowName, Map paramsMap){
        debug("flowName ${flowName}");
        Collection<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        paramsMap.each{ k,v ->
            requestParams.add(new BasicNameValuePair(k, v));
        }
        URI requestUri = URI.create(getRequestString());
        GeneralFlowRequest request = new GeneralFlowRequest(requestUri, flowName, requestParams);
        debug(requestParams.toString());
        lastRequestString = request.getRequestString();
        if(verbose){
            emitOutput("");
            emitOutput(" Sent Request: " + request.getRequestString() );
            emitOutput("");
        }
        return request;
    }

    /**
     * @param message
     * Print a message
     */
    def log(msg){
        emitOutput(msg)
    }

     /**
     * Throws a test error if the actual data returned from the server is not the same as
     * the expected JSON
     * @param expectedJSONData
     */
    def expect(String expectedJSONData){
        try{
            JSONObject expected = new JSONObject(expectedJSONData);
            JSONObject actual = new JSONObject(lastRequestResponse);
            assertTrue(compare(expected,actual,null));
        }catch(JSONException ex){
            def expected = new JSONArray(expectedJSONData);
            def actual = new JSONArray(lastRequestResponse);
            assertEquals(expected, actual);
        }
    }

    /**
     * Throws a test error if the actual data returned from the server is not the same as
     * the expected JSON
     * @param expectedJSONData, ignorePathList
     */
    def expect(String expectedJSONData,List<String> ignorePathList){
        try{
            JSONObject expected = new JSONObject(expectedJSONData);
            JSONObject actual = new JSONObject(lastRequestResponse);
            assertTrue(compare(expected,actual,ignorePathList));
        }catch(JSONException ex){
            // then see if it is an array
            def expected = new JSONArray(expectedJSONData);
            def actual = new JSONArray(lastRequestResponse);
            assertEquals(expected, actual);
        }
    }

    /**
     * Pretty Prints Last Response.
     */
    def checkError(FlowResponse response){
        if(response.hasError()) {
          String mesg = "Error in the script " + this.name + ". The response status is " + response.getHttpStatusCode() + "\n" ;
                         response.getErrorMessage() ;
          throw new RuntimeException(mesg) ;
        }
    }
      

    /**
     * Pretty Prints Last Response.
     */
    def prettyPrintResponse(){
        emitOutput(getResponseData().toString(4));
    }

    def printTaskInfo(info){
        emitOutput "\n"
        emitOutput THICK_DIVIDER;
        emitOutput info ;
        emitOutput THICK_DIVIDER;
    }



    def printTabular(entries, tabularTmpl, headers, keyPaths){
        emitOutput sprintf(tabularTmpl, headers) ;
        emitOutput THIN_DIVIDER;
        for(int i = 0; i < entries.length(); i++) {
            def entry = entries.get(i) ;
            def value = new String[keyPaths.size() + 1] ;
            value[0] = Integer.toString(i + 1) ;
            for(int j = 0; j < value.length - 1; j++) {
                value[j + 1] = entry.optStringByPath(keyPaths[j]) ;
            }
            println sprintf(tabularTmpl, value) ;
        }
    }

    def printTabularMap(map, tabularTmpl, headers, keys){

        emitOutput sprintf(tabularTmpl, headers) ;
        emitOutput THIN_DIVIDER;
        for(entry in map.values()) {
            def value = new String[keys.size()] ;
            for(int j = 0; j < value.length; j++) {
                value[j] = entry.get(keys[j]) ;
            }
            println sprintf(tabularTmpl, value) ;
        }
    }


    /**
     * Call a script with params
     * @param scriptName script name
     * @param callParamsMap script parameters
     */
    def callScript(String scriptName, Map callParamsMap){
        def exe = runner.createClosure(scriptName,callParamsMap);
        if(exe){
            exe.delegate = this;
            exe();
        }
    }

    /**
     * Call a script with no params
     * @param scriptName script name
     */
    def callScript(String scriptName){
        callScript(scriptName,[:]);
    }

    /**
     * Get response data.
     * @return response data
     */
    def getResponseData(){
        def data = null;
        try {
            // first assume it is a normal object
            data = new JSONObject(lastRequestResponse);
        } catch (Exception e){
            try {
                // then see if it is an array
                data = new JSONArray(lastRequestResponse);
            } catch (Exception e2){
                fail("Invalid JSON. " + " request was: " + lastRequestString + " returned: " + lastRequestResponse );
            }
        }
        return data;
    }

    /**
     * @param expectedJSONData
     */
    def checkReturnedValidJson(){
        try {
            getResponseData();
        } catch (Exception e){
            getLog().error("Invalid JSON Returned: " + " request was: " + lastRequestString + " returned: " + lastRequestResponse );
        }
    }

    /**
     * @return pre-configured request string of constructs one if needed.
     */
    private String getRequestString(){
        if (requestUriString != null){
            return requestUriString;
        } else {
            return this.host + ":" + this.port + "/c/" + this.key   + "/" + this.apiVersion;
        }
    }

    /**
     * method to compare the actual jsonObject return to us with our expected, and can ignore some compared things,return true when they are the same.
     * @param expected is expected JSONObject
     * @param actual is actual JSONObject
     * @param excludePaths is ignore list
     * @return true if the expected object is same with the actual object
     */
    public boolean compare(JSONObject expected, JSONObject actual, List<String> excludePaths){
        def isEqual = compare(expected,actual,excludePaths,"/");
        return isEqual;
    }

    /**
     * method to compare the actual jsonObject return to us with our expected, and can ignore some compared things,return true when they are the same.
     * @param expected is expected JSONObject
     * @param actual is actual JSONObject
     * @param excludePaths is ignore list
     * @param currentPath is path of the property
     * @return true if the expected object is same with the actual object
     */
    public boolean compare(JSONObject expected, JSONObject actual, List<String> excludePaths, String currentPath){
        String newLine = System.getProperty("line.separator");
        def isEqual = false;
        // when the compared object is null,return true directly.
        if(expected == null && actual == null){
            return true;
        }
        if(expected == null || actual == null){
            fail("After Calling ${lastRequestString}.Response did not match expected:"+ newLine
                + "expected data was " + expected + newLine
                + "but the actual data was "+ actual);
            return false;
        }
        def expectedNames = expected.names();
        def actualNames = actual.names();
        if(expectedNames == null && actualNames == null){
            return true;
        }
        if(expectedNames == null || actualNames == null){
            fail("After Calling ${lastRequestString}.Response did not match expected names:" + newLine
                + "expected names was " + expectedNames + newLine 
                + "but the actual names was "+ actualNames + newLine
                + "expected data was " + expected + newLine
                + "but the actual data was "+ actual);
            return false;
        }
        int i = 0;
        //loops all of the property name in the object
        actualNames.each { actualName ->
            def expectedName = expectedNames.get(i);
            def actualValue = actual.get(actualName);
            def expectedValue = expected.get(expectedName);
            //if no ignore compared things or current compared thing is not in the ignore,then we go to compare.
            if(excludePaths == null){
                excludePaths = new ArrayList<String>();
                excludePaths.add("there is no ignore path");
            }
            if(!excludePaths.contains(currentPath)){
                if(actualName.equals(expectedName)){
                    if(expectedValue instanceof JSONObject && actualValue instanceof JSONObject ){
                        isEqual = compare(expectedValue,actualValue,excludePaths,currentPath  + actualName + "/");
                    }else{
                        if (!excludePaths.contains(currentPath  + actualName + "/")){
                            isEqual = actualValue.equals(expectedValue);
                            if(!isEqual){
                                fail("After Calling ${lastRequestString}.Response did not match expected in following path:"
                                + currentPath + newLine + actualName +":" +newLine
                                + "expected was " + expectedValue + newLine
                                + "but the actual was " + actualValue + newLine
                                + "expected data was " + expected + newLine
                                + "but the actual data was "+ actual);
                            }
                        }
                    }
                }else{
                    isEqual = false;
                    fail("After Calling ${lastRequestString}.Response did not match expected property name:"+ newLine
                    + "expected name was "+expectedName + newLine
                    + "but the actual name was " + actualName + newLine
                    + "expected data was " + expected + newLine
                    + "but the actual data was "+ actual);
                }
            }else{
                isEqual = true;
                return;
            }
            if(isEqual == false){
                return;
            }
            i++;
        }
        return isEqual;

    }
    
    def server = null;
    def currentPort = 0;
    public Server getServer(int portNo){
        
        if(server == null || currentPort != portNo){
            server = new Server(portNo);
        }
        
        return server;
        
    }
    
    /**
     * The method is to open a port and listens request.
     * @param portNo is port number
     * @param timeOutSeconds is time out seconds
     * @param doNow is the request in script
     * @param handleRequest is the handle method when recieved a request
     */
    public void openPort(int portNo, int timeOutSeconds, Closure doNow, Closure handleRequest){
        def monitor = new Object();
        server = getServer(portNo);
        server.setGracefulShutdown(1000);
        MyHandler myHandler = new MyHandler(handleRequest,monitor);
        server.setHandler(myHandler);
        server.start();
        doNow.delegate = this;
        
        //Wait for 10 seconds
        try{
            doNow();
            synchronized (monitor) {
                monitor.wait(timeOutSeconds * 1000);
            }
            
            if(myHandler.getReceived() == false){
                server.doStop();
                fail("Server did not send any request");
            }

            if(myHandler.getHandlingError() != null){
                fail("Error Handling Request.", myHandler.getHandlingError());
            }
            
            server.doStop();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            server.doStop();
        }
    }

    /**
     * This class defines the handler of the client jetty server
     */
    public class MyHandler extends AbstractHandler {
        def Closure handleRequest;
        def monitor;
        def received = false;
        def handlerError;

        /**
         * The method is constructor of the class.
         * @param handleRequest is handleRequest closure
         * @param monitor is a synchronized lock
         */
        MyHandler(Closure handleRequest,def monitor){
            this.handleRequest = handleRequest;
            this.monitor = monitor;
        }
        
        /**
         * The method is handle of the client jetty server.
         * @param target is the target of the request - either a URI or a name.
         * @param request is the request either as the Request object or a wrapper
         * of that request.
         * @param response is the response as the Response object or a wrapper of that
         * request.
         * @param dispatch is the dispatch mode: REQUEST, FORWARD, INCLUDE, ERROR 
         */
        public void handle(String target, HttpServletRequest request,
                HttpServletResponse response, int dispatch)
            throws IOException,
                ServletException {
            received = true;
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            ((Request) request).setHandled(true);
            handleRequest.delegate = this;
            try{
                handleRequest(request,response);
            }catch(Exception e){
                handlerError = e;
            } finally {
                synchronized (monitor) {
                    monitor.notifyAll();
                }
            }
        }

        def getReceived(){
            return received;
        }

        def getHandlingError(){
            return handlerError;
        }
    }

    /**
     * The method is set api key.
     * @param tempApiKey 
     */
    def setApiKey(String tempApiKey){
        this.tempApiKey=tempApiKey;
    }

    
     /**
     * @param msg is message to debug log
     */
    private void debug(String msg){
        getLog().debug(msg);
    }

    /**
     * @param msg - message to emit
     */
    public void emitOutput(String msg){
        getLog().info(msg);
    }

    /**
     * Get the logger for this class.
     */
    public Log getLog(){
        if ( this.log == null ) {
            this.log = LogFactory.getLog(FlowTestBuilder.class);
        }
        return this.log;
    }

}

/**
 * This class defines the methods that are callable within the flow test DSL
 */
public class DescribeScriptDSL {
    def String name;
    def String usage;
    def String description;
    def List<ParameterUsge> usages;
    def scriptDescription;
    private static final boolean DEBUG = false;
    /** This stores the base uri including the host,port,apikey */
    private String requestUriString;
    /** This stores the last request to the server */
    private String lastRequestString;
    /**
     * Contains the last response from the server.
     */
    public String lastRequestResponse = null;
    public DescribeScriptDSL(){
    }

    /**
     * The description of the name
     *@param name - the name to description
     *@param description - the name 's description
     *@throw EarlyExitException if it just need description
     */
    public void description (String name, String description){
        this.name = name;
        this.description = description;
        this.scriptDescription = new ScriptDescription(name:name , description:description, usage:"" );
        // This pevents the other commands in the script fom being executed.
        throw new EarlyExitException(scriptDescription);
    }

    /**
     * The description of the name.
     *@param name - the name to description
     *@param description - the name 's description
     *@param usage is the usage of the script
     *@throw EarlyExitException  if it just need description
     */
    public void description (String name, String description, String usage){
        this.usage = usage;
        this.name = name;
        this.description = description;
        this.scriptDescription = new ScriptDescription(name:name , description:description, usage:usage );
        // This pevents the other commands in the script fom being executed.
        throw new EarlyExitException(scriptDescription);
    }
    
     public void description (String name, String description, List<ParameterUsge> usages){
        String newLine = System.getProperty("line.separator");
        StringBuffer usageSb = new StringBuffer(newLine);
        
        for(ParameterUsge paramUsage : usages){
            if(paramUsage.getName()&& paramUsage.getDescription()){
                usageSb.append(paramUsage.getName() + " = " +"<" +paramUsage.getDescription() + ">");
                usageSb.append(newLine);
            }
            
        }
        this.usage =  usageSb.toString();
        this.name = name;
        this.description = description;
        this.scriptDescription = new ScriptDescription(name:name , description:description, usage:usage, usageList:usages );
        // This pevents the other commands in the script fom being executed.
        throw new EarlyExitException(scriptDescription);
     }

    /**
     * Sends a request to the named flow with the specified parameters.
     * @param flowName to call
     * @param paramsMap key value map of parameters to send.
     */
    String request(String flowName, Map paramsMap){
        throw new NoDescriptionException();
    }

    FlowResponse requestResponse(String flowName, Map paramsMap){
        throw new NoDescriptionException();
    }

    /**
     * Throws a test error if the actual data returned from the server is not the same as.
     * the expected JSON
     * @param expectedJSONData
     */
    def expect(String expectedJSONData){
        throw new NoDescriptionException();
    }

    /**
     * Throws a test error if the actual data returned from the server is not the same as.
     * the expected JSON, ignorePathList
     * @param expectedJSONData
     */
    def expect(String expectedJSONData,List<String> ignorePathList){
        throw new NoDescriptionException();
    }

    /**
     * @param expectedJSONData
     */
    def checkReturnedValidJson(){
        throw new NoDescriptionException();
    }

    /**
     * @param expectedJSONData
     */
    def prettyPrintResult(){
        throw new NoDescriptionException();
    }

    /**
     * Call a script with params
     * @param scriptName script name
     * @param callParamsMap script parameters
     */
    def callScript(String scriptName, Map callParamsMap){
        throw new NoDescriptionException();
    }

    public void openPort(int portNo, int timeOutSeconds, Closure doNow, Closure handleRequest){
        
    }
    /**
     * Call a script with no params
     * @param scriptName script name
     */
    def callScript(String scriptName){
        throw new NoDescriptionException();
    }
    
    def getscriptDescription(){
    	return scriptDescription;
    }

    /**
     * @param message
     * Print a message
     */
    def log(msg){
         getLog().info(msg);
    }

    /**
     * @param msg is message to debug log
     */
    private void debug(String msg){
        getLog().debug(msg);
    }

    /**
     * Get the logger for this class.
     * @return log
     */
    public Log getLog(){
        if ( this.log == null ) {
            this.log = LogFactory.getLog(FlowTestBuilder.class);
        }
        return this.log;
    }
    
    public ParameterUsge paramDef(String name,String description,boolean optional,Object defaultValue){
    
        return new ParameterUsge(name,description,optional,defaultValue);
    }
}


