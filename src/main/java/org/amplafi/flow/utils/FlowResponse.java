package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.GeneralFlowRequest.APPLICATION_ZIP;
import java.util.Map;
import java.util.HashMap;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class FlowResponse {

    private HttpResponse response ;
    private String responseText ;
    private Map<Integer,String> responseExplanations = new HashMap<Integer,String>();
    private static final String EXPLANATION_200 = "A call finished successfully. A call result (an object, an array, or a string might be returned as response body";
    private static final String EXPLANATION_400 = "An error (usually user related) happened on server. Usually means bad request parameters.";
    private static final String EXPLANATION_401 = "Authorization problem. Usually means you're using invalid API key.";
    private static final String EXPLANATION_404 = "Flow not found. Means that the request tried to access a non existent API entry point.";
    private static final String EXPLANATION_500 = "Server has problems. Contact server developers.";
    private static final String EXPLANATION_302 = "Redirect. Usually not handled by client, as redirects happen automatically.";

    public FlowResponse(HttpResponse response) {
        this.response = response ;

        responseExplanations.put(200, EXPLANATION_200);
        responseExplanations.put(400, EXPLANATION_400);
        responseExplanations.put(404, EXPLANATION_404);
        responseExplanations.put(401, EXPLANATION_401);
        responseExplanations.put(500, EXPLANATION_500);
        responseExplanations.put(302, EXPLANATION_302);

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
        // Construct error message, connsisting of explanation of code (for Pat at 3am) and
        // error message returned from server.
        return "Http Status Code " +
                this.getHttpStatusCode() + "\n" +
                    responseExplanations.get(this.getHttpStatusCode()) + "\n" +
                        this.getResponseAsString() ;
    }
}
