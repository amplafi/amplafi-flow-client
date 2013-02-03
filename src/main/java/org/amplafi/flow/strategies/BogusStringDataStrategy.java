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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;

/**
 * This strategy produces tests that simply send in bogus String data.
 * @author paul
 */
public class BogusStringDataStrategy extends AbstractTestingStrategy {

    private static final String NAME = "BogusStringData";
    /**
     * @return the name of this strategy
     */
     @Override
     public String getName() {
         return NAME;
     }

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

}
