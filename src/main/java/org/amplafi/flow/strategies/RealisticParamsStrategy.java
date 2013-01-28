package org.amplafi.flow.strategies;

import java.util.ArrayList;
import java.util.List;
import org.amplafi.flow.TestProperties;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONObject;
import org.apache.http.NameValuePair;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.amplafi.flow.utils.GenerationException;

import org.amplafi.flow.utils.GenerationException;
import org.apache.http.NameValuePair;

public class RealisticParamsStrategy extends AbstractTestingStrategy {

    private static final String NAME = "RealisticParams";
    private static final String PARAM_TYPE_STRING = "String";
    private static final String PARAM_TYPE_INTEGER = "Integer";
    private static final String PARAM_TYPE_BOOLEAN = "Boolean";
    private static final String PARAM_TYPE_LIST = "List";
    /**
     * @return the name of this strategy 
     */
    @Override
    public String getName(){
        return NAME;
    }
    @Override
    public Collection<NameValuePair> generateParameters(String flow,
        Collection<String> parameterNames) {
        return null;
    }

    /**
     * Generates a test for an activity
     * @param flow - flow name
     * @param activityDefinition - JSON object
     * @param requestUriString - base request url
     */
    public void generateTestForActivity(String flow, JSONObject activityDefinition, String requestUriString)throws GenerationException {
        assertNotNull(activityDefinition,
            "flowDefinition was null, The test should depend on testJsonStringIsReturnedWhenRequestingTheFlowDefinition() does it?");
            
        Collection<RequestParameter> requestParameters = getRequestParameters(activityDefinition);
            
        Collection<NameValuePair> parametersPopulatedWithRealParamData = generateRealisticParameters(requestParameters);
        
        parametersPopulatedWithRealParamData.add(renderAsJson);

        addRequest(flow,parametersPopulatedWithRealParamData);
        
        addVerification(callFlowForTypicalData(requestUriString, flow, parametersPopulatedWithRealParamData ));
   
    }

    /**
     * @param requestParameters - Collection of RequestParameter
     * @return parameters with validate data for request
     */
    private Collection<NameValuePair> generateRealisticParameters(Collection<RequestParameter> requestParameters) {
        List<NameValuePair> realisticDataList = new ArrayList<NameValuePair>();
        for(RequestParameter requestParam : requestParameters){
            String requestParamName = requestParam.getName();
            String requestParamType = requestParam.getType();
            String requestParamValue = getRequestParamValue(requestParamType);
            realisticDataList.add(new BasicNameValuePair(requestParamName,requestParamValue));
        }
        return realisticDataList;
    }
    
    @Override
    public void addVerification(String typicalResponse){
        writeToFileBuffer("checkReturnedValidJson()");
    }

    
    private String getRequestParamValue(String requestParamType){
        if(requestParamType.equals(PARAM_TYPE_BOOLEAN)){
            return "true";
        }if(requestParamType.equals(PARAM_TYPE_INTEGER)){
            return "100";
        }if(requestParamType.equals(PARAM_TYPE_LIST)){
            return "['a','b','c']";
        }if(requestParamType.equals(PARAM_TYPE_STRING)){
            return "abc";
        }else{
            return "null";
        }
    }
    
}