package org.amplafi.flow.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * TO_HARIS: javadocs?
 *
 */
public class GeneralFlowRequest {
    private static final NameValuePair fsRenderResult = new BasicNameValuePair("fsRenderResult", "json");
    private static final NameValuePair describe = new BasicNameValuePair("describe",null);

    private URI requestUri;
    private String flowName;    
    private String queryString;

    /**
     * @param requestUri is expected to have everything accept the queryString
     * @param params
     */
    public GeneralFlowRequest(URI requestUri, String flowName, NameValuePair... params) {
        this(requestUri, flowName, Arrays.asList(params));
    }

    /**
     * @param requestUri is expected to have everything accept the queryString
     * @param parameters
     */
    public GeneralFlowRequest(URI requestUri, String flowName, Collection<NameValuePair> parameters) {
        this.requestUri = requestUri;
        this.flowName = flowName;
        queryString = URLEncodedUtils.format(new ArrayList<NameValuePair>(parameters), "UTF-8");
    }

    /**
     * @return List strings representing flowtypes
     */
	public JSONArray<String> listFlows() {
		GeneralFlowRequest generalFlowRequest = new GeneralFlowRequest(this.requestUri, null, fsRenderResult, describe);
		String responseString = generalFlowRequest.get();
		return new JSONArray<String>(responseString);
	}

    /**
     * @param requestUriString This parameter is just the scheme and authority (with optional port)
     *            and no path. For development this is usually http://localhost:8080
     * @param flow the flow name
     * @return JSONObject representation of all of the parameters that this flow has.
     */
    public JSONObject describeFlow() {
        String responseString = describeFlowRaw();
        return new JSONObject(responseString);
    }
    
    public String describeFlowRaw(){
        GeneralFlowRequest generalFlowRequest = new GeneralFlowRequest(requestUri, flowName, fsRenderResult, describe);
        
        return generalFlowRequest.get();
    }

    public  List<String> getFuzzInputResponse(){
        String responseString = this.get();
        return new JSONArray<String>(responseString).asList();
    }

    public String get() {
        String output = null;
        try {
            HttpClient client = new DefaultHttpClient();
            
            HttpGet request = new HttpGet(getFullUri() + "?" + queryString);
            HttpResponse response = client.execute(request);
            output = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            // Throw an exception here ?
            e.printStackTrace();
        }
        return output;
    }

	private String getFullUri() {
		return flowName != null ? (requestUri + "/" + flowName) : requestUri.toString();
	}
}
