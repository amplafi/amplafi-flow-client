package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.GeneralFlowRequest.APPLICATION_ZIP;

import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class FlowResponse {
    private HttpResponse response ;
    private String responseText ;
    
    public FlowResponse(HttpResponse response) {
        this.response = response ;
    }
    
    public HttpResponse getHttpResponse() {
        return this.response ;
    }
    
    public int getHttpStatusCode() { 
        return this.response.getStatusLine().getStatusCode() ;
    }
    
    public boolean hasError() {
        return getHttpStatusCode() != 200 ;
    }
    
    public String getResponseAsString() {
        if(responseText == null) {
            try {
                Header contentTypeHeader = response.getFirstHeader("Content-Type");
                if (contentTypeHeader != null &&
                        contentTypeHeader.getValue() != null  && contentTypeHeader.getValue().equals(APPLICATION_ZIP)){
                    // calling classes should check for this.
                    responseText = APPLICATION_ZIP;
                } else {
                    responseText = EntityUtils.toString(response.getEntity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText ;
    }
    
    public JSONObject getResponseAsJSONObject() {
        if(hasError()) {
            return null ;
        }
        return new JSONObject(getResponseAsString()) ;
    }
    
    public JSONArray getResponseAsJSONArray() {
        if(hasError()) {
            return null ;
        }
        return new JSONArray(getResponseAsString()) ;
    }
    
    public String getErrorMessage() {
        return "Http Status Code " + this.getHttpStatusCode() + "\n" + this.getResponseAsString() ;
    }
}
