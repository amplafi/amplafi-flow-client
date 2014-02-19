package org.amplafi.flow.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

/**
 * A class providing common methods for querying FarReaches service using HTTP.
 *
 */
public class GeneralFlowRequest {
	private static final NameValuePair fsRenderResult = new BasicNameValuePair(
			"fsRenderResult", "json");
	private static final NameValuePair describe = new BasicNameValuePair(
			"describe", null);
	public static final String APPLICATION_ZIP = "application/zip";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	private FarReachesServiceInfo serviceInfo;
	private String flowName;
	private String apiKey;
	private String queryString;
	private HttpClient httpClient;
	private Collection<NameValuePair> parameters;

	public GeneralFlowRequest(FarReachesServiceInfo serviceInfo, String apiKey, String flowName) {
	    this.serviceInfo = serviceInfo;
	    this.apiKey = apiKey;
	    this.flowName = flowName;
	}
	/**
	 * @param serviceInfo
	 *            is expected to have everything accept the queryString
	 * @param params
	 *            is the parameters of the request
	 * @param flowName
	 *            is name of the flow
	 */
	public GeneralFlowRequest(FarReachesServiceInfo serviceInfo, String apiKey, String flowName, NameValuePair... params) {
		this(serviceInfo, apiKey, flowName, Arrays.asList(params));
	}

   public GeneralFlowRequest(FarReachesServiceInfo serviceInfo, String apiKey, String flowName, String... params) {
       this.apiKey = apiKey;
       this.serviceInfo = serviceInfo;
       this.flowName = flowName;
       this.parameters = new ArrayList<>();

       for(int i=0; i < params.length;i+=2) {
           String name = params[i];
           String value = params[i+1];
           this.parameters.add(new BasicNameValuePair(name, value));
       }
       queryString = URLEncodedUtils.format(new ArrayList<NameValuePair>(parameters), "UTF-8");
   }
	/**
	 * @param serviceInfo
	 *            is expected to have everything accept the queryString
	 * @param parameters
	 *            is the parameters of the request
	 * @param flowName
	 *            is name of the flow
	 */
	public GeneralFlowRequest(FarReachesServiceInfo serviceInfo, String apiKey, String flowName, Collection<NameValuePair> parameters) {
		this.apiKey = apiKey;
		this.serviceInfo = serviceInfo;
		this.flowName = flowName;
		this.parameters = parameters;
		queryString = URLEncodedUtils.format(new ArrayList<NameValuePair>(parameters), "UTF-8");
	}

	/**
	 * @return List strings representing flowtypes
	 */
	public JSONArray<String> listFlows() {
		GeneralFlowRequest generalFlowRequest = new GeneralFlowRequest(
				this.serviceInfo, apiKey, null, fsRenderResult, describe);
		String responseString = generalFlowRequest.get();
		return new JSONArray<String>(responseString);
	}

	/**
	 * @return JSONObject representation of all of the parameters that this flow
	 *         has
	 */
	public JSONObject describeFlow() {
		String responseString = describeFlowRaw();
		return new JSONObject(responseString);
	}

	/**
	 * @return request response string
	 */
	public String describeFlowRaw() {
		GeneralFlowRequest generalFlowRequest = new GeneralFlowRequest(
				serviceInfo, apiKey, flowName, fsRenderResult, describe);
		return generalFlowRequest.get();
	}

	public FlowResponse describeFlowWithResponse(){
		GeneralFlowRequest generalFlowRequest = new GeneralFlowRequest(
				serviceInfo, apiKey, flowName, fsRenderResult, describe);
		return generalFlowRequest.sendRequest();
	}
	/**
	 * This method actually send the http request represented by this object.
	 *
	 * @return request response string.
	 */
	public String get() {
		String output = null;
		try {
			FlowResponse response = sendRequest();
			output = response.toString();
		} catch (Exception e) {
			// Throw an exception here ?
			// e.printStackTrace();
		}
		return output;
	}

	public FlowResponse sendRequest() {
		FlowResponse response;
		try {
			HttpClient client = getHttpClient();
			String fullUri = getFullUri();
			System.out.println("Sending request to: " + fullUri);
            HttpPost request = new HttpPost(fullUri);
			if (apiKey != null) {
				request.setHeader(AUTHORIZATION_HEADER, apiKey);
			}
			request.setEntity(new UrlEncodedFormEntity(parameters, Charset.forName("UTF-8")));
			response = new FlowResponse(client.execute(request));
		} catch (IOException e) {
			response = new FlowResponse();
		}
		return response;
	}

	/**
	 * @return the full url
	 */
	private String getFullUri() {
		return flowName != null ? (serviceInfo.getRequestString() + "/" + flowName)
				: serviceInfo.getRequestString();
	}

	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @return the request string
	 */
	public String getRequestString() {
		return getFullUri() + "?" + queryString;
	}

	private HttpClient getHttpClient() {
		if (httpClient == null) {
			HttpClientBuilder create = HttpClientBuilder.create();
			ArrayList<Header> arrayList = new ArrayList<>();
			arrayList.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
			create.setDefaultHeaders(arrayList);
			httpClient = create.build();
		}
		return httpClient;
	}
}
