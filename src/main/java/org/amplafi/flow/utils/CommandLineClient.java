package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.CommandLineClientOptions.API_KEY;
import static org.amplafi.flow.utils.CommandLineClientOptions.API_VERSION;
import static org.amplafi.flow.utils.CommandLineClientOptions.FLOW;
import static org.amplafi.flow.utils.CommandLineClientOptions.HOST;
import static org.amplafi.flow.utils.CommandLineClientOptions.PARAMS;
import static org.amplafi.flow.utils.CommandLineClientOptions.PORT;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * In lieu of an andriod client.
 * 
 * @author Tyrinslys Valinlore
 */

public class CommandLineClient implements Runnable {
	private FarReachesServiceInfo serviceInfo;
	private CommandLineClientOptions cmdOptions;
	private List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
	
	public static void main(String[] args) {
		CommandLineClientOptions cmdOptions = new CommandLineClientOptions(args);

		FarReachesServiceInfo serviceInfo = new FarReachesServiceInfo(
				cmdOptions.getOptionValue(HOST),
				cmdOptions.getOptionValue(PORT),
				cmdOptions.getOptionValue(API_VERSION));

		CommandLineClient client = new CommandLineClient(serviceInfo, cmdOptions);
		client.run();
	}

	// TO_HARIS Replace cmdOptions with a flow definition and params
	// TO_HARIS Extract query params to the flow call
	// For now, parameters are required to be a ready-made query (i.e. in the form of ?param1=value1&param2=value2).
	// Params should be a list of name-value pairs, 
	// so that the caller of the constructor doesn't have to build the query himself
	public CommandLineClient(FarReachesServiceInfo serviceInfo,
			CommandLineClientOptions cmdOptions) {
		
		this.serviceInfo = serviceInfo;
		this.cmdOptions = cmdOptions;
	}

	private String buildRequestUriString() {
		String fullUri =  serviceInfo.getHost()  + ":" + serviceInfo.getPort() + "/c/"
				+ cmdOptions.getOptionValue(API_KEY) 
				+ "/" + serviceInfo.getApiVersion()  
				+ "/" + cmdOptions.getOptionValue(FLOW); 
 
		if (cmdOptions.getOptionValue(PARAMS) != null) {
			fullUri += "/" + cmdOptions.getOptionValue(PARAMS);
		}
		
		return fullUri;
	}

	public void run() {
		GeneralFlowRequest flowRequest = new GeneralFlowRequest(
				URI.create(buildRequestUriString()), queryParams);
		System.out.println(flowRequest.get());
	}

}
