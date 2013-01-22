/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package org.amplafi.flow.strategies;

import java.util.Collection;
import java.util.Formatter;

import org.amplafi.flow.TestProperties;
import org.amplafi.json.JSONObject;
import org.apache.http.NameValuePair;

/**
 * Abstract Super class for all testing strategies. 
 * This class defines methods that are useful to most strategies.
 * 
 * @author paul
 */
public abstract class AbstractTestingStrategy {
    private StringBuffer testFileContents = null;
    private int sequence = 0;
    private String currentFlow = null;
    private String currentActivity = null;
    
    
    /**
     * @return the name of this strategy 
     */
     public abstract String getName();

    /**
     * @return the name of this strategy 
     */
     public String getFileName(){
         Formatter formatter = new Formatter();
         return "" + formatter.format("%04d",sequence) + "" + getName() + "_" + currentFlow + ".groovy" ;
     }
     
     public abstract Collection<NameValuePair> generateParameters(String flow, Collection<String> parameterNames);
     
     /**
      * Initializes a new test script
      * @param flow -the current flow name
      * @param activity - the current activity - unused
      */
     public void newTest(String flow, String activity){
         testFileContents = new StringBuffer();
         currentFlow = flow;
         currentActivity = activity;
         sequence++;
     }
     
     /**
      * closes the test script file etc.
      */
     public void endTest(){
        
        testFileContents = null;
        currentFlow = null;
        currentActivity = null;		
        return;
     }

    /**
     * Called to determine if a test should be generated for this flow. A default implementation will 
     * return false if the flow is listed in the ignoreFlows system property. Different strategies could 
     * decide to generate test based on other criteria.
     * @param flowName - the name of the flow being processed
     * @param flowDefinitionJson - the flow definition if available - otherwise null
     */
     public boolean shouldGenerateTest(String flowName, String flowDefinitionJson ){
        return !TestProperties.ignoredFlows.contains(flowName);
     }


     /**
      * add a "request" directive to the test script.
      */
     public void addRequest(String flow, Collection<NameValuePair> params){
         if (params != null){
            StringBuffer parameters = new StringBuffer();
            String sep = "";
            for (NameValuePair nvp : params){
                
                parameters.append(sep);
                parameters.append("\"");
                parameters.append(nvp.getName());
                parameters.append("\"");
                parameters.append(":");
                parameters.append("\"");
                parameters.append(nvp.getValue());
                parameters.append("\"");
                sep = ",";
            }
            
            writeToFileBuffer("request(\"" + flow + "\", [" + parameters.toString() + "])\n");
         } else {
            writeToFileBuffer("request(\"" + flow + "\", [:])\n");
         }
         
     }

    
    /**
     * This method can be ovveridden in sub-strategies to add specific 
     * types of validation to different tests.
     * @param typicalResponse - the response when this flow was called during test creation.
     * 
     */
    public void addVerification(String typicalResponse){
        // default behaviour is to expect the typical response.
        addExpect(typicalResponse);
    }


     /**
      * Adds an expected return to the test	
      * @param json - the expected json data
      */
     public final void addExpect(String json){
         String strValue = "";
         try {
            // Try to format a JSON string if possible.
            JSONObject jsonObj = new JSONObject(json);
            strValue = jsonObj.toString(4);
         } catch (Exception e){
             // otherwise just use the raw string
            strValue = json; 
         }
         
         writeToFileBuffer("expect(\"\"\"" + strValue + "\"\"\")\n");
     }
     
     
    protected void writeToFileBuffer(String content){
        testFileContents.append(content);
        testFileContents.append("\n");
    }
    
     
     /**
      * @return the test script.
      */
     public String getTestFileContents(){
         return testFileContents.toString();
     }
     
}
