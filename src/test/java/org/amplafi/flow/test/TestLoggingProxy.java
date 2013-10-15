package org.amplafi.flow.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.amplafi.flow.utils.LoggingProxy;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * A class tests LoggingProxy.
 *
 */
public class TestLoggingProxy {
    private LoggingProxy loggingProxy;

    /**
     * testNG test sets up data.
     */
    @BeforeMethod
    public void setUp() throws Exception {
        loggingProxy = new LoggingProxyTest();
    }

    /**
     * testNG test tears down the data.
     */
    @AfterMethod
    public void tearDown() throws Exception {
        loggingProxy = null;
    }

    /**
     * This test tests method getRequestParamMap() receive a good request.
     */
    @Test
    public void testGetRequestParamsMap() {
        String request = "/c/ampcb_e2ea34097f9aba4f69ff7c095d227beabccef732971eb2539065997d297b12c9/apiv1/AvailableCategoriesList?fsRenderResult=json";
        Map<String, String> map = loggingProxy.getRequestParamsMap(request);
        assertEquals(map.size(), 2);
        assertTrue(map.containsKey("flowName"));
        assertTrue(map.containsKey("fsRenderResult"));
        assertEquals(map.get("flowName"), "AvailableCategoriesList");
        assertEquals(map.get("fsRenderResult"), "json");
    }

    /**
     * This test tests method getRequestParamMap() receive a bad request.
     */
    @Test
    public void testGetRequestParamsMapWithBadRequest() {
        String request = "/asdf/lli/234/asdf/ladew/";
        Map<String, String> map = loggingProxy.getRequestParamsMap(request);
        assertEquals(map.size(), 0);
        assertFalse(map.containsKey("flowName"));
        assertFalse(map.containsKey("fsRenderResult"));
        assertEquals(map.get("flowName"), null);
        assertEquals(map.get("fsRenderResult"), null);
        // what do we do in the test script if there is a bad request?
    }

    /**
     * This test tests method getRequestParamMap().
     */
    @Test
    public void testAddTestScriptRequest() throws Exception {
        String request = "/c/ampcb_e2ea34097f9aba4f69ff7c095d227beabccef732971eb2539065997d297b12c9/apiv1/AvailableCategoriesList?fsRenderResult=json";
        loggingProxy.addTestScriptRequest(request);
        String req = ((LoggingProxyTest) loggingProxy).getTestScriptRequest();
        assertTrue(req.contains("AvailableCategoriesList"));
        assertTrue(req.contains("fsRenderResult"));
        assertTrue(req.contains("json"));
        assertTrue(req.contains("checkReturnedValidJson()"));
        assertFalse(req.contains("AvailableCategoriesList1"));
        assertFalse(req.contains("fsRenderResult1asd"));
        assertFalse(req.contains("jsonwe"));
        assertFalse(req.contains("checkReturnedValidJson(sf)"));

    }

    public class LoggingProxyTest extends LoggingProxy {

        private String testScriptRequest = "";

        /**
         * @return the testScriptRequest
         */
        public String getTestScriptRequest() {

            return testScriptRequest;
        }

        @Override
        protected Writer getTestFileWriter() {
            return new StringWriter();
        }

        @Override
        public void addTestScriptRequest(String req) {
            Map<String, String> reqMap = getRequestParamsMap(req);
            String flowName = reqMap.get("flowName");
            Set<String> paramSet = reqMap.keySet();
            paramSet.remove("flowName");
            try (Writer out = getTestFileWriter()) {
                out.write("request(\"" + flowName + "\",[");
                int i = 0;
                for (Object element : paramSet) {
                    if (i != 0) {
                        out.write(",");
                    }
                    String paramName = element.toString();
                    out.write("\"" + paramName + "\":\"" + reqMap.get(paramName) + "\"");
                    i++;
                }
                String newLine = System.getProperty("line.separator");

                //request("AvailableCategoriesList",["java.util.HashMap$KeyIterator@75324a":"null])
                out.write("])" + newLine);
                out.write("checkReturnedValidJson()");
                //Close the output stream
                testScriptRequest = out.toString();
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
            //  request("EligibleExternalServiceInstancesFlow", ["eligibleExternalServiceInstanceMap":"bogusData","eligibleExternalServiceInstances":"bogusData","fsRenderResult":"json"])
            //  checkReturnedValidJson()

        }

    }

}