package org.amplafi.dsl;

import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.flow.utils.AdminTool;
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
import org.testng.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * TO_DAISY: what's the difference from DescribeScriptDSL? Javadoc says they do the same.
 * 
 * TO_DAISY: yet this DSL seem to expose too much to the scripts. I expected it to expose methods like:
 * request, obtain_key, etc; instead it exposes a whole bunch of stuff.. why?
 * 
 * TO_DAISY: can we implement this class in Java? Ideally, only test and tool scripts are to be written in groovy.
 * 
 * This class defines the methods that are callable within the flow test DSL
 */
public class FlowTestDSL extends Assert {

    private String apiVersion;
    private String key;
    private ScriptRunner runner;
    private boolean verbose;
    private Log log;
    private static final String THICK_DIVIDER =
    "*********************************************************************************";
    private static final String THIN_DIVIDER =
    "---------------------------------------------------------------------------------";

    /** This stores the base uri including the host,port,apikey */
    private FarReachesServiceInfo serviceInfo = null;
    
    /**
     * This is used for data sharing between load tests.
     */
    private static stash = [:];

    public FlowTestDSL(FarReachesServiceInfo serviceInfo, ScriptRunner runner, boolean verbose){
        this.serviceInfo =  serviceInfo;
        this.runner = runner;
        this.verbose = verbose;
    }

    public FlowTestDSL(FarReachesServiceInfo serviceInfo, String key, ScriptRunner runner, boolean verbose){
        this.serviceInfo = serviceInfo;
        this.key = key;
        this.runner = runner;
        this.verbose = verbose;
    }

	/**
	 * Set api version
	 * @param apiVersion
	 */
    void setApiVersion(String apiVersion){
        this.apiVersion = apiVersion;
    }

	/**
	 * Set api key 
	 * @param key
	 */
    void setKey(String key){
        this.key = key;
    }

	/**
	 * get current api version
	 * @return
	 */
    String getApiVersion(){
        return apiVersion;
    }

	/**
	 * get current api key
	 * @return
	 */
    String getKey(){
        return key;
    }

    /**
     * Sends a request to the named flow with the specified parameters.
     * @param flowName to call.
     * @param paramsMap key value map of parameters to send.
     * @return response string
     */
    FlowResponse request(String flowName, Map paramsMap){
        GeneralFlowRequest request = createGeneralFlowRequest(flowName, paramsMap);
        FlowResponse response = request.sendRequest();
        return response;
    }

    /**
     * This method will automatically add a callbackParam into params and send the request.
     * With a callback uri It will then use openPort to call the flow and return the response.
     * @param flowName to call.
     * @param [params] key value map of parameters to send.
     * @param dataReturnProperty the property we want to return.
     * @return response string.
     */
    FlowResponse asyncRequest(String flowName, Map paramsMap) {
        paramsMap["callbackUri"] = "http://example.com:1234";
        def ret = openPort(1234,5, flowName, paramsMap);
        return ret;
    }

    /**
     * Sends a request to the named flow with the specified parameters.
     * @param flowName to call.
     * @param paramsMap key value map of parameters to send.
     */
    private GeneralFlowRequest createGeneralFlowRequest(String flowName, Map paramsMap){
        Collection<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        paramsMap.each{ k,v ->
            requestParams.add(new BasicNameValuePair(k, v));
        }
        serviceInfo.setApiVersion(getApiVersion());
        GeneralFlowRequest request = new GeneralFlowRequest(serviceInfo, this.key, flowName, requestParams);
        return request;
    }

    /**
     * @param message.
     * Print a message.
     */
    private void log(msg){
        emitOutput(msg)
    }

     /**
     * Throws a test error if the actual data returned from the server is not the same as.
     * the expected JSON.
     * @param expectedJSONData.
     */
    void expect(String expectedJSONData){
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
     * Throws a test error if the actual data returned from the server is not the same as.
     * the expected JSON.
     * @param expectedJSONData, ignorePathList.
     */
    void expect(String expectedJSONData,List<String> ignorePathList){
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
     * Throws exception if response has an error.
     * @param response  
     */
    def checkError(FlowResponse response){
        if(response.hasError()) {
            String mesg = "Error in the script " + this.name + ". The response status is " + response.getHttpStatusCode() + "\n";
            response.getErrorMessage();
            throw new RuntimeException(mesg);
        }
    }

    /**
     * Pretty Prints Last Response.
     */
    private def prettyPrintResponse(){
        emitOutput(getResponseData().toString(4));
    }

	
    private def printTaskInfo(info){
        emitOutput "\n";
        emitOutput THICK_DIVIDER;
        emitOutput info;
        emitOutput THICK_DIVIDER;
    }

	/**
	 * Prints data in table  format. 
	 * @param entries
	 * @param tabularTmpl
	 * @param headers
	 * @param keyPaths
	 * @return
	 */
    private  def printTabular(entries, tabularTmpl, headers, keyPaths){
        emitOutput sprintf(tabularTmpl, headers);
        emitOutput THIN_DIVIDER;
        for(int i = 0; i < entries.length(); i++) {
            def entry = entries.get(i);
            def value = new String[keyPaths.size() + 1];
            value[0] = Integer.toString(i + 1);
            for(int j = 0; j < value.length - 1; j++){
                value[j + 1] = entry.optStringByPath(keyPaths[j]);
            }
            println sprintf(tabularTmpl,value);
        }
    }

	/**
	 * Prints a map in table form. 
	 * @param map
	 * @param tabularTmpl
	 * @param headers
	 * @param keys
	 * @return
	 */
    private def printTabularMap(map, tabularTmpl, headers, keys){
        emitOutput sprintf(tabularTmpl, headers);
        emitOutput THIN_DIVIDER;
        for(entry in map.values()){
            def value = new String[keys.size()];
            for(int j = 0; j < value.length; j++){
                value[j] = entry.get(keys[j]);
            }
            println sprintf(tabularTmpl, value);
        }
    }

    /**
     * Call a script with params.
     * @param scriptName script name.
     * @param callParamsMap script parameters.
     */
    Object callScript(String scriptName, Map callParamsMap){
        getLog().debug("In callScript() scriptName = " + scriptName);
        def exe = runner.createClosure(scriptName, callParamsMap);
        getLog().debug("callScript created closure  for scriptName = " + scriptName);
        def ret = null;
        if(exe){
            getLog().debug("callScript() closure not null ");
            exe.delegate = this;
            getLog().debug("callScript() about to run ${scriptName}");
            ret = exe();
            getLog().debug("callScript() finished running ${scriptName}");
        }
        return ret;
    }

    /**
     * Call a script with no params.
     * @param scriptName script name.
     */
    Object callScript(String scriptName){
        callScript(scriptName,[:]);
    }
	
    /**
     * method to compare the actual jsonObject return to us with our expected, and can ignore some compared things,return true when they are the same.
     * @param expected is expected JSONObject
     * @param actual is actual JSONObject
     * @param excludePaths is ignore list
     * @return true if the expected object is same with the actual object
     */
    private boolean compare(JSONObject expected, JSONObject actual, List<String> excludePaths){
        def isEqual = compare(expected,actual,excludePaths,"/");
        return isEqual;
    }

    /**
     * method to compare the actual jsonObject return to us with our expected, and can ignore some compared things,return true when they are the same.
     * @param expected is expected JSONObject.
     * @param actual is actual JSONObject.
     * @param excludePaths is ignore list.
     * @param currentPath is path of the property.
     * @return true if the expected object is same with the actual object.
     */
    private boolean compare(JSONObject expected, JSONObject actual, List<String> excludePaths, String currentPath){
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

    private def server = null;
   
    private def currentPort = 0;
	/**
	 * Gets a jetty server instance for the port. 
	 * @param portNo
	 * @return
	 */
    private Server getServer(int portNo){
        if(server == null || currentPort != portNo){
            server = new Server(portNo);
        }
        return server;
    }

     /**
     * The method is to open a port and listens request.
     * @param portNo is port number.
     * @param timeOutSeconds is time out seconds.
     * @param doNow is the request in script.
     * @param handleRequest is the handle method when recieved a request.
     */
    private FlowResponse openPort(int portNo, int timeOutSeconds, String flowName, Map parametersMap){
        def monitor = new Object();
        server = getServer(portNo);
        server.setGracefulShutdown(1000);
        MyHandler myHandler = new MyHandler(monitor);
        server.setHandler(myHandler);
        getLog().debug("openPort() start server on port ${portNo} ");
        server.start();
        //Wait for 10 seconds
        try{
            getLog().debug("openPort() about to run doNow() closure for port ${portNo}");
            request(flowName, parametersMap);
            getLog().debug("openPort() finsihed running doNow() closure for port ${portNo}");
            synchronized (monitor) {
                monitor.wait(timeOutSeconds * 1000);
            }
            if(myHandler.getReceived() == false){
                //TO_DAISY: doStop is a protected method and is not intended to be called directly.
                //Why're you not using stop()?
                server.doStop();
                fail("Server did not send any request");
            }
            if(myHandler.getHandlingError() != null){
                fail("Error Handling Request.", myHandler.getHandlingError());
            }
            server.doStop();
        } catch(InterruptedException ie) {
            server.doStop();
            ie.printStackTrace();
        } finally {
            server.doStop();
        }
        return myHandler.handlerReturn;
    }

    /**
     * This class defines the handler of the client jetty server.
     */
    public class MyHandler extends AbstractHandler {
        def monitor;
        def received = false;
        def handlerError;
        FlowResponse handlerReturn;

        /**
         * The method is constructor of the class.
         * @param handleRequest is handleRequest closure.
         * @param monitor is a synchronized lock.
         */
        MyHandler(def monitor){
            this.monitor = monitor;
        }

        /**
         * The method is handle of the client jetty server.
         * @param target is the target of the request - either a URI or a name.
         * @param request is the request either as the Request object or a wrapper
         * of that request.
         * @param response is the response as the Response object or a wrapper of that
         * request.
         * @param dispatch is the dispatch mode: REQUEST, FORWARD, INCLUDE, ERROR.
         */
        public void handle(String target, HttpServletRequest request,
                HttpServletResponse response, int dispatch)
            throws IOException,
                ServletException {
            getLog().debug("About to handle request");
            received = true;
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            ((Request) request).setHandled(true);
            try{
                getLog().debug("Calling script request handler");
                handlerReturn = new FlowResponse(request);
                getLog().debug("Returned from script request handler");
            }catch(Exception e){
                getLog().debug("In FlowTestBuilder, MyHandler. Request handler in DSL script returned error. " + e);
                handlerError = e;
            } finally {
                synchronized (monitor) {
                    monitor.notifyAll();
                }
            }
        }

        /**
         * Get method :get received value.
         */
        def getReceived(){
            return received;
        }

        /**
         * Get method :get handlerError;
         */
        def getHandlingError(){
            return handlerError;
        }
    }

    /**
     * For sharing data between load tests
     */
    private def getStash(){
        stash;
    }

    /**
     * @param msg is message to debug log.
     */
    private void debug(String msg){
        getLog().debug(msg);
    }

    /**
     * @param msg - message to emit.
     */
    private void emitOutput(String msg){
        getLog().info(msg);
    }

    /**
     * Get the logger for this class.
     */
    public Log getLog(){
        if ( this.log == null ) {
            this.log = LogFactory.getLog(AdminTool.class);
        }
        return this.log;
    }
}