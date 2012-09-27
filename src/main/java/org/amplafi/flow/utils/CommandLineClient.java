package org.amplafi.flow.utils;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

/**
 * In lieu of an andriod client.
 * 
 * @author Tyrinslys Valinlore
 */

public class CommandLineClient implements Runnable {
	private final String requestUriString;

	private final String apiKey;
	
	private final String flow;
	
	// command line switch used to specify which API key we want to use
	private static final String API_KEY_CMD_SWITCH = "-key";
		
	// command line switch used to specify which flow we want to use
	private static final String FLOW_CMD_SWITCH = "-flow";
	
	
	public static void main(String[] args) {
//	    HACK TO_HARIS: doesn't looks like a convenient way to parse command line parameters can we use something like
//	          http://commons.apache.org/cli/ ??
//	     
		Map<String, String> params = extractParams(args);
		
		if (params == null) {
			System.err.println("Invalid input arguments, application will stop.");
			System.exit(0);
		}
		
		CommandLineClient client = new CommandLineClient(params);
		client.run();		
	}
	
	public CommandLineClient(Map<String, String> params) {
	    //HACK TO_HARIS: apiKey and flow got to be separate (mandatory) parameters to the constructor.
	    //We need a cleaner interface to later reuse this class in other tasks.
		this.apiKey = params.get(API_KEY_CMD_SWITCH);
		this.flow = params.get(FLOW_CMD_SWITCH);
		
		this.requestUriString = buildRequestUriString();
	}

	private String buildRequestUriString() {
	    //HACK TO_HARIS: Why is server address hardcoded? 
		return "http://sandbox.farreach.es:8080/c/"
				+ apiKey + "/apiv1/" 
				+ flow;
	}

	/**
	 * HACK TO_HARIS: doesn't looks like a convenient way to parse command line parameters can we use something like
	 *     http://commons.apache.org/cli/ ??
	 * 
	 * Extract parameters from user's input. Every switch (-key, -flow...) has to be followed by a value.
	 * @param args
	 * @return
	 */
	private static Map<String, String> extractParams(String[] args) {
		if (ArrayUtils.isEmpty(args)) {
			return null;
		}
		
		Map<String, String> params = new HashMap<String, String>();
		
		for (int i = 0; i < args.length; i++) {
		    //
			if (API_KEY_CMD_SWITCH.equals(args[i])) {
				if (i == args.length - 1) {
					System.err.println("No API key specified");
					params = null;
					break;
				}
				
				params.put(API_KEY_CMD_SWITCH, args[i+1]);
				//skip parameter value, continue to next parameter name
				i++;
			} else if (FLOW_CMD_SWITCH.equals(args[i])) {
				if (i == args.length - 1) {
					System.err.println("No flow specified");
					params = null;
					break;
				}
				
				params.put(FLOW_CMD_SWITCH, args[i+1]);
				//skip parameter value, continue to next parameter name
				i++;
			} else {
				System.err.println("Unknown switch");
				params = null;
				break;
			}
		}
		
		return params;
	}

	public void run() {
	    //HACK TO_HARIS: How to supply parameters to the flow call?
		GeneralFlowRequest flowRequest = new GeneralFlowRequest(URI.create(requestUriString), Collections.EMPTY_LIST);
		System.out.println(flowRequest.get());
	}

}
