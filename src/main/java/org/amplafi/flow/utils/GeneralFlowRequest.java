package org.amplafi.flow.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class GeneralFlowRequest {
    private static final NameValuePair fsRenderResult = new BasicNameValuePair("fsRenderResult", "json");
    private static final NameValuePair describe = new BasicNameValuePair("describe",null);
    

    private URI requestUri;

    private String queryString;

    /**
     * @param requestUri is expected to have everything accept the queryString
     * @param params
     */
    public GeneralFlowRequest(URI requestUri, NameValuePair... params) {
        this(requestUri, Arrays.asList(params));
    }

    /**
     * @param requestUri is expected to have everything accept the queryString
     * @param parameters
     */
    public GeneralFlowRequest(URI requestUri, Collection<NameValuePair> parameters) {
        this.requestUri = requestUri;
        queryString = URLEncodedUtils.format(new ArrayList<NameValuePair>(parameters), "UTF-8");       
    }

    /**
     * @param requestUriString This parameter is just the scheme and authority (with optional port)
     *            and no path. For development this is usually http://localhost:8080
     * @return List strings representing flowtypes
     */
    public  List<String> getListOfFlowTypes() {
        GeneralFlowRequest generalFlowRequest = new GeneralFlowRequest(this.requestUri, fsRenderResult,describe);
        String responseString = generalFlowRequest.get();
        return new JSONArray<String>(responseString).asList();
    }

    /**
     * @param requestUriString This parameter is just the scheme and authority (with optional port)
     *            and no path. For development this is usually http://localhost:8080
     * @param flow the flow name
     * @return JSONObject representation of all of the parameters that this flow has.
     */
    public static JSONObject getFlowDefinition(String requestUriString, String flow) {
        String responseString = getFlowDefinitionString(requestUriString, flow);
        return new JSONObject(responseString);
    }
    public static String getFlowDefinitionString(String requestUriString, String flow){
        URI requestUri = URI.create(requestUriString + "/flow/" + flow);
        GeneralFlowRequest generalFlowRequest = new GeneralFlowRequest(requestUri, fsRenderResult);
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
            HttpGet request = new HttpGet(requestUri + "?" + queryString);
            HttpResponse response = client.execute(request);           
            output = convertInputStreamToString(response.getEntity().getContent());
        } catch (Exception e) {
            // Throw an exception here ?
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Convert a stream to a string. This could be done with org.apache.commons.io.IOUtils tostring
     * method.
     * 
     * @param inputStream
     * @return
     */
    private static String convertInputStreamToString(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        try {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception exception2) {
                exception2.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }
}
