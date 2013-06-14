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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.flow.utils.GenerationException;
import org.amplafi.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * This strategy produces tests that simply send in bogus String data.
 * @author paul
 */
public class CombinationsStrategy extends AbstractTestingStrategy {

    private static final String NAME = "ParamCombinations";
    private static final int MAX_GENERATED_COMBINATIONS = 10;
    /**
     * @return the name of this strategy
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Generates a test for an activity.
     * @param flow - flow name
     * @param activityDefinition - JSON object
     * @param requestUriString - base request url
     * @throws GenerationException if problem occurs
     */
    @Override
    public void generateTestForActivity(String flow,
                                         String key,
                                        JSONObject activityDefinition,
                                        FarReachesServiceInfo requestUriString) throws GenerationException {

        assertNotNull(activityDefinition,
            "flowDefinition was null, The test should depend on"
                + " testJsonStringIsReturnedWhenRequestingTheFlowDefinition() does it?");
        Collection<String> parameterNames = getAllParameterNames(activityDefinition);

        // How many combinations of being present or not are there?
        // should be 2^parameterNames.size()

        int totalCombinations = (int) Math.pow(2, parameterNames.size());
        totalCombinations = (totalCombinations > MAX_GENERATED_COMBINATIONS
                                                    ? MAX_GENERATED_COMBINATIONS : totalCombinations);

        // Enumerate those combinations.
        for (int combination = 0; combination < totalCombinations; combination++) {

            Collection<String> thisTestParams = new ArrayList<String>();
            // loop over the available param names and decide whether to include them.
            // for example combination 5 is 101 in binary so include the 1st and the 3rd parameter
            // but not the second.

            int order = 1;
            for (String pname : parameterNames) {
                if ((combination & order) == order) {
                    thisTestParams.add(pname);
                }
                order *= 2;
            }

            // Generate request for this combination.
            Collection<NameValuePair> parametersPopulatedWithBogusData = generateParameters(flow, thisTestParams);
            //add the json response parameter
            parametersPopulatedWithBogusData.add(RENDER_AS_JSON);

            addRequest(flow, parametersPopulatedWithBogusData);

            //callFlowForTypicalData(requestUriString, flow, parametersPopulatedWithBogusData )
            addVerification("");

        }

    }

    /**
     * Generates test parameters.
     * @param flow - flow name
     * @param parameterNames - parameter name for request
     * @return parameters for request
     */
    @Override
    public Collection<NameValuePair> generateParameters(String flow, Collection<String> parameterNames) {
        String bogusData = "bogusData";
        List<NameValuePair> bogusDataList = new ArrayList<NameValuePair>();
        for (String parameterName : parameterNames) {
            bogusDataList.add(new BasicNameValuePair(parameterName, bogusData));
        }
        return bogusDataList;
    }

    @Override
    public void addVerification(String typicalResponse) {
        writeToFileBuffer("checkReturnedValidJson()");
    }

    @Override
    public boolean shouldGenerateTest(String flowName, String flowDefinitionJson) {
        return !flowName.contains("Wordpress");
    }


}
