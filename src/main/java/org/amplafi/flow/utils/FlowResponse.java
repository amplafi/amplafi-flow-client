package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.GeneralFlowRequest.APPLICATION_ZIP;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONObject;
import org.amplafi.json.JsonConstruct;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class FlowResponse {

	private static Map<Integer, String> responseExplanations = new HashMap<Integer, String>();
	private static final String EXPLANATION_200 = "A call finished successfully. A call result (an object, an array, or a string might be returned as response body";
	private static final String EXPLANATION_400 = "An error (usually user related) happened on server. Usually means bad request parameters.";
	private static final String EXPLANATION_401 = "Authorization problem. Usually means you're using invalid API key.";
	private static final String EXPLANATION_404 = "Flow not found. Means that the request tried to access a non existent API entry point.";
	private static final String EXPLANATION_500 = "Server has problems. Contact server developers.";
	private static final String EXPLANATION_302 = "Redirect. Usually not handled by client, as redirects happen automatically.";

	static {
		responseExplanations.put(200, EXPLANATION_200);
		responseExplanations.put(400, EXPLANATION_400);
		responseExplanations.put(404, EXPLANATION_404);
		responseExplanations.put(401, EXPLANATION_401);
		responseExplanations.put(500, EXPLANATION_500);
		responseExplanations.put(302, EXPLANATION_302);
	}

	private final String responseText;
	private final int httpStatusCode;
	private final JsonConstruct parsedResponse;

	public FlowResponse() {
		responseText = null;
		httpStatusCode = 0;
		this.parsedResponse = null;
	}

	public FlowResponse(HttpResponse response) {
		httpStatusCode = response.getStatusLine().getStatusCode();
		Header contentTypeHeader = response.getFirstHeader("Content-Type");
		if (contentTypeHeader != null
		        && contentTypeHeader.getValue() != null
		        && contentTypeHeader.getValue().equals(APPLICATION_ZIP)) {
		    // calling classes should check for this.
		    responseText = APPLICATION_ZIP;
		    this.parsedResponse = null;
		} else {
		    try {
				responseText = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
				this.parsedResponse = JsonConstruct.Parser.toJsonConstruct(responseText);
		    } catch (IOException e) {
		        throw new IllegalStateException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
    public FlowResponse(HttpServletRequest serverCallbackRequest) {
		httpStatusCode = 200;
		Enumeration<String> parameterNames = serverCallbackRequest.getParameterNames();
		JSONObject parsedResponse = new JSONObject();
		while (parameterNames.hasMoreElements()) {
			String next = parameterNames.nextElement();
			String parameter = serverCallbackRequest.getParameter(next);
			if (parameter.startsWith("{")) {
				parsedResponse.put(next, JSONObject.toJsonObject(parameter));
			} else if (parameter.startsWith("[")) {
				parsedResponse.put(next, JSONArray.toJsonArray(parameter));
			} else {
				parsedResponse.put(next, parameter);
			}
		}
		this.parsedResponse = parsedResponse;
		responseText = parsedResponse.toString();
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public boolean hasError() {
		return httpStatusCode != 200;
	}

	@Override
    public String toString() {
        return hasError() ? buildErrorMessage() : this.parsedResponse != null ? this.parsedResponse.toString(2) : responseText;
	}

    private String buildErrorMessage() {
        StringBuilder error = new StringBuilder();
        if (getHttpStatusCode() == 401) {
            error.append("Your current key is invalid. This will happen if the farreach.es server restarts. Ask Pat for a new key");
        } else {
            error.append("response string:");
            error.append(responseText + "\n");
            error.append("response error:");
            error.append(getErrorMessage() + "\n");
        }
        return error.toString();
    }

	public JSONObject toJSONObject() {
		if (hasError()) {
			return null;
		}
		return new JSONObject(responseText);
	}

	public String getErrorMessage() {
        StringBuilder errorMessage = new StringBuilder("Http Status Code:")
            .append(this.getHttpStatusCode()).append(";")
            .append(responseExplanations.get(this.getHttpStatusCode()) ).append(";(");
        if ( this.parsedResponse !=null && this.parsedResponse instanceof JSONObject) {
            errorMessage.append(((JSONObject)this.parsedResponse).get("error"));
        } else {
            errorMessage.append(this.responseText);
        }
        errorMessage.append(")");
        return errorMessage.toString();
	}

	public String get(String key) {
		JSONObject jsonObject = toJSONObject();
		jsonObject = jsonObject.flatten();
		return jsonObject!=null?jsonObject.optString(key):null;
	}

	public String toTableString() {
	    return toTableString(false);
	}

	public String toFlattenedTableString() {
	    return toTableString(true);
	}

    private String toTableString(boolean flatten) {
        if (hasError()) {
            return buildErrorMessage();
        } else {
            JsonConstruct jsonConstruct = JsonConstruct.Parser.toJsonConstruct(responseText);
            return getSectionString(flatten, jsonConstruct);
        }
    }

    protected String getSectionString(boolean flatten, JsonConstruct jsonConstruct) {
        List<JSONObject> rows = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        if (jsonConstruct instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) jsonConstruct;
            Set<String> keys = jsonObject.keys();
            Set<String> toRemove = new HashSet<>();
            for (String key : keys) {
                JSONArray<Object> optJSONArray = jsonObject.optJSONArray(key);
                if (optJSONArray != null) {
                    result.append(key).append("\n");
                    result.append(getSectionString(flatten, optJSONArray));
                    toRemove.add(key);
                }
            }
            for (String key : toRemove) {
                jsonObject.remove(key);
            }
            rows.add(flatten ? jsonObject.flatten() : jsonObject);
        } else {
            JSONArray<JSONObject> jsonArray = (JSONArray<JSONObject>) jsonConstruct;
            for (JSONObject jsonObject : jsonArray.asList()) {
                rows.add(flatten ? jsonObject.flatten() : jsonObject);
            }
        }
        SortedMap<String, Integer> columns = getColumnsData(rows);
        for (Entry<String, Integer> entry : columns.entrySet()) {
            result.append(String.format("%" + entry.getValue() + "s | ", entry.getKey()));
        }
        result.append("\n");
        for (JSONObject row : rows) {
            for (Entry<String, Integer> entry : columns.entrySet()) {
                result.append(String.format("%" + entry.getValue() + "s | ", row.optString(entry.getKey())));
            }
            result.append("\n");
        }
        return result.toString();
    }

    private SortedMap<String, Integer> getColumnsData(List<JSONObject> rows) {
        SortedMap<String, Integer> columns = new TreeMap<>();
        for (JSONObject jsonObject : rows) {
            Set<String> keys = jsonObject.keys();
            for (String key : keys) {
                int maxLength;
                if (columns.containsKey(key)) {
                    maxLength = columns.get(key);
                } else {
                    maxLength = key.length();
                    columns.put(key, maxLength);
                }
                int currentLength = jsonObject.getString(key).length();
                if (currentLength > maxLength) {
                    columns.put(key, currentLength);
                }
            }
        }
        return columns;
    }
}
