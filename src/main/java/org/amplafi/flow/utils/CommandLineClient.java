package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.CommandLineClientOptions.API_KEY;
import static org.amplafi.flow.utils.CommandLineClientOptions.API_VERSION;
import static org.amplafi.flow.utils.CommandLineClientOptions.FLOW;
import static org.amplafi.flow.utils.CommandLineClientOptions.HOST;
import static org.amplafi.flow.utils.CommandLineClientOptions.PARAMS;
import static org.amplafi.flow.utils.CommandLineClientOptions.PORT;
import static org.amplafi.flow.utils.CommandLineClientOptions.DESCRIBE;

import java.net.URI;

import org.apache.commons.cli.ParseException;

/**
 * A Java CL client used to query FarReaches services. For usage options, please refer to {@link CommandLineClientOptions}.
 * 
 * @author Tyrinslys Valinlore
 * @author Haris Osmanagic
 */

public class CommandLineClient implements Runnable {
	private String apiKey;
	private FarReachesServiceInfo serviceInfo;
	private SimpleFlowRequest flowRequestDescription;
	
	public static void main(String[] args) {
		CommandLineClientOptions cmdOptions = null;
		
		try {
			cmdOptions = new CommandLineClientOptions(args);
		} catch (ParseException e) {
			System.err.println("Could not parse passed arguments, message:");
			e.printStackTrace();
			System.exit(1);
		}

		String apiKey = cmdOptions.getOptionValue(API_KEY);
		
		FarReachesServiceInfo serviceInfo = new FarReachesServiceInfo(
				cmdOptions.getOptionValue(HOST),
				cmdOptions.getOptionValue(PORT),
				cmdOptions.getOptionValue(API_VERSION));
		
		SimpleFlowRequest flowRequestDescription = new SimpleFlowRequest(cmdOptions.getOptionValue(FLOW), 
				cmdOptions.hasOption(DESCRIBE), cmdOptions.getOptionProperties(PARAMS));

		CommandLineClient client = new CommandLineClient(apiKey, serviceInfo, flowRequestDescription);
		client.run();
	}

	public CommandLineClient(String apiKey, FarReachesServiceInfo serviceInfo,
			SimpleFlowRequest flowRequest) {
		
		this.apiKey = apiKey;
		this.serviceInfo = serviceInfo;
		this.flowRequestDescription = flowRequest;
	}

	private String buildRequestUriString() {
		String fullUri =  serviceInfo.getHost()  
				+ ":" + serviceInfo.getPort() + "/c/"
				+ this.apiKey 
				+ "/" + this.serviceInfo.getApiVersion()  
				+ "/" + this.flowRequestDescription.getFlowName(); 
		
		return fullUri;
	}

	public void run() {
		GeneralFlowRequest flowRequest = new GeneralFlowRequest(URI.create(buildRequestUriString()), this.flowRequestDescription.getParameters());
		Object result = null;
		
		if (flowRequestDescription.isDescribe()) {
			result = flowRequest.getListOfFlowTypes();
		} else {
			result = flowRequest.get();
		}
		
		System.out.println(result);
	}

}