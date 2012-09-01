package org.amplafi.flow.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;





import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * In lieu of an andriod client.
 * 
 * @author Tyrinslys Valinlore
 */
public class CommandLineClient implements Runnable {
    // full string to send a request to the server and receive a list of the different flow types.
    //private static final String listFlowTypesUrl = "http://localhost:8080/flow?fsRenderResult=json/describe";

    private  final String requestUriString = "http://localhost:8080";
     URI init = URI.create(requestUriString + "/tutorial"+"/flow/");
    private   NameValuePair flow;
    private   URI requestUri;
    
    public static void main(String[] args) {
    	CommandLineClient commandLineClient = new CommandLineClient();
    	commandLineClient.run();
    }
    public void run() {
    	FlowTestGenerator flowTestGenerator = new FlowTestGenerator(init);
    	for(GeneralFlowRequest generalFlowRequest: flowTestGenerator) {
    		if ( !test(generalFlowRequest) ) {
    			flowTestGenerator.addToFailedGeneralFLowRequests(generalFlowRequest);
    		}
    	}
    	//apache method.
    	System.out.println(StringUtils.join(flowTestGenerator.getFailedGeneralFlowRequests(),"/n"));
    }
	private boolean test(GeneralFlowRequest generalFlowRequest) {
		
		return false;
	}  }	/*	List<String> flowList = new ArrayList<String>();
    	List<String> flowResponseList = new ArrayList<String>();
    	NameValuePair begin = new BasicNameValuePair("flow",null);;
		GeneralFlowRequest generalFlowRequest = new GeneralFlowRequest(init, begin);
        //get list of flow types         	
    	flowList =  generalFlowRequest.getListOfFlowTypes(init);
       // Case 1: Get Responses with url containing only the flow type in the query part
       for(String s : flowList){
    	   flow = new BasicNameValuePair("flow",s);
    	   requestUri = URI.create(requestUriString + "/tutorial"+"/flow/");
    	   generalFlowRequest = new GeneralFlowRequest(requestUri, flow);    	   
    	   flowResponseList = generalFlowRequest.getFuzzInputResponse();
    	   for (String a : flowResponseList){
    		   System.out.println(a);
    	   }
       }
    }*/

