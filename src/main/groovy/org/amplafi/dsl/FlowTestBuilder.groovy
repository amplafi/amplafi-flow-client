package org.amplafi.dsl;
import org.amplafi.flow.utils.GeneralFlowRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import static org.testng.Assert.*;
import java.util.HashMap;
import java.util.Map;

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
 *
 *
 *
 *
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
    
    public FlowTestBuilder(String requestUriString, ScriptRunner runner, boolean verbose){
        this.requestUriString = requestUriString;
        this.runner = runner;
        this.verbose = verbose;
    }

    
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


    public buildExe(Closure c){
    
        if (requestUriString != null){
            c.delegate = new FlowTestDSL(requestUriString, runner, verbose);
        } else {
            c.delegate = new FlowTestDSL(host, port, apiVersion, key, runner, verbose);
        }
        return c;
    }

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
    private static boolean DEBUG;

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
     */
    String request(String flowName, Map paramsMap){

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
            println("");
            println(" Sent Request: " + lastRequestString );
            println("");
        }
        
        lastRequestResponse = request.get();
        debug(lastRequestResponse);
        return lastRequestResponse;
    }

    def printlnMsg(String msg){ 
        System.out.println(msg);
    }

    /**
     * Throws a test error if the actual data returned from the server is not the same as
     * the expected JSON
     * @param expectedJSONData
     */
    def expect(String expectedJSONData){
        JSONObject expected = new JSONObject(expectedJSONData);
        JSONObject actual = new JSONObject(lastRequestResponse);
        assertEquals(expected,actual );

    }

    /**
     * Pretty Prints Last Response
     */
    def prettyPrintResponse(){

        println(getResponseData().toString(4));
    
    }

    /**
     * Call a script
     */
    def callScript(String scriptName, Map callparamsmap){

         //def exe = ScriptRunner.createClosure(scriptPath);
         def exe = runner.createClosure(scriptName,callparamsmap);
         if(exe){
            exe.delegate = this;
            exe();
         }
  
    }

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
           println("Invalid JSON Returned: " + " request was: " + lastRequestString + " returned: " + lastRequestResponse );
        }

    }

    private String getRequestString(){
        if (requestUriString != null){
            return requestUriString;
        } else {
            return this.host + ":" + this.port + "/c/" + this.key   + "/" + this.apiVersion; 
        }
    }


    private static void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }
    
}

/**
 * This class defines the methods that are callable within the flow test DSL
 */
public class DescribeScriptDSL {

    def String name = null;
    def String description = null;
    private static final boolean DEBUG = false;
    /** This stores the base uri including the host,port,apikey */
    private String requestUriString = null;
    /** This stores the last request to the server */
    private String lastRequestString = null;

    /**
     * Contains the last response from the server.
     */
    public String lastRequestResponse = null;

    public DescribeScriptDSL(){


    }

    public void description (String name, String description){
        this.name = name;
        this.description = description;
        // This pevents the other commands in the script fom being executed.
        throw new EarlyExitException(new ScriptDescription(name:name , description:description ));

    }

    /**
     * Sends a request to the named flow with the specified parameters
     * @param flowName to call
     * @param paramsMap key value map of parameters to send.
     */
    String request(String flowName, Map paramsMap){
        throw new NoDescriptionException();
    }


    /**
     * Throws a test error if the actual data returned from the server is not the same as
     * the expected JSON
     * @param expectedJSONData
     */
    def expect(String expectedJSONData){
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
     * Call a script
     */
    def callScript(String scriptPath){
         throw new NoDescriptionException();
    }

    private static void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }
}
