package org.amplafai.dsl;
import org.amplafi.flow.utils.GeneralFlowRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import static org.testng.Assert.*;

/**
 * This class defines a simple DSL for sending reqests to the amplafai wire server 
 * and expecting results.
 * 
 * The format of the test language will be
 * 
 * request('HelloFlow',['param1':'dog','param2':'pig']);
 * 
 * expect("""
 * {	"validationErrors":{
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
	
	private String requestUriString = null
	public FlowTestBuilder(String requestUriString){
		this.requestUriString = requestUriString;
	}
	
	
	public build(Closure c){
		c.delegate = new FlowTestDSL(requestUriString)
		c()
	}
	
	
	
}

/**
 * This class defines the methods that are callable within the flow test DSL
 */
public class FlowTestDSL{
	
	private static boolean DEBUG = false;
	
	/** This stores the base uri including the host,port,apikey */
	private String requestUriString = null
	
	/** This stores the last request to the server */
	private String lastRequestString = null;
	
	/**
	 * Contains the last response from the server.
	 */
	public String lastRequestResponse = null;
	
	
	public FlowTestDSL(String requestUriString){
		this.requestUriString = requestUriString;
	}
	
	

    /**
     * Sends a request to the named flow with the specified parameters
     * @param flowName to call
     * @param paramsMap key value map of parameters to send.
     */
	String request(String flowName, Map paramsMap ){
		debug("flowName ${flowName}");
		
		Collection<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		
		paramsMap.each{ k,v ->
			requestParams.add(new BasicNameValuePair(k, v));
			
		}
		
        URI requestUri = URI.create(requestUriString);
        GeneralFlowRequest request = new GeneralFlowRequest(requestUri, flowName, requestParams);
        
        debug(requestParams.toString());
        
        lastRequestString = request.getRequestString();
        lastRequestResponse = request.get();
        
        debug(lastRequestResponse)
        return lastRequestResponse;
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
	 * @param expectedJSONData
	 */
	def checkReturnedValidJson(){
		try {
			JSONObject actual = new JSONObject(lastRequestResponse);
		} catch (Exception e){
			fail("Invalid JSON. " + " request was: " + lastRequestString + " returned: " + lastRequestResponse );
		}
		
	}
	
	
	
	private static void debug(String msg){
        if (DEBUG){
            System.err.println(msg);
        }
    }
}
