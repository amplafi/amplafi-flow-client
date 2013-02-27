package org.amplafi.flow.test;

import static org.amplafi.flow.utils.LoggingProxyCommandLineOptions.OUT_FILE;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.amplafi.flow.utils.LoggingProxy;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sun.reflect.*;

import java.io.*;
import java.lang.reflect.*;
import org.apache.log4j.AppenderSkeleton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

/**
 * A class tests LoggingProxy.
 * @author YYB
 *
 */
public class TestLoggingProxy {
    private LoggingProxy loggingProxy = null;
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
        LoggingProxy loggingProxy = null;
    }

    /**
     * This test tests method getRequestParamMap() receive a good request.
     */
    @Test
    public void testGetRequestParamsMap(){
        String request = "/c/ampcb_e2ea34097f9aba4f69ff7c095d227beabccef732971eb2539065997d297b12c9/apiv1/AvailableCategoriesList?fsRenderResult=json";
        Map<String,String> map = loggingProxy.getRequestParamsMap(request);
        assertEquals(map.size(),2);
        assertTrue(map.containsKey("flowName"));
        assertTrue(map.containsKey("fsRenderResult"));
        assertEquals(map.get("flowName"),"AvailableCategoriesList");
        assertEquals(map.get("fsRenderResult"),"json");
    }
    
    /**
     * This test tests method getRequestParamMap() receive a bad request.
     */
    @Test
    public void testGetRequestParamsMapWithBadRequest(){
        String request = "/asdf/lli/234/asdf/ladew/";
        Map<String,String> map = loggingProxy.getRequestParamsMap(request);
        assertEquals(map.size(),0);
        assertFalse(map.containsKey("flowName"));
        assertFalse(map.containsKey("fsRenderResult"));
        assertEquals(map.get("flowName"),null);
        assertEquals(map.get("fsRenderResult"),null);
        // what do we do in the test script if there is a bad request?
    }
    /**
     * This test tests method getRequestParamMap().
     */
    @Test
    public void testAddTestScriptRequest() throws Exception{
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
    
    public class LoggingProxyTest extends LoggingProxy{

        private String testScriptRequest = "";
        
        /**
         * @return the testScriptRequest
         */
        public String getTestScriptRequest() {
            
            return testScriptRequest;
        }

        protected Writer getTestFileWriter(){
            return new StringWriter();
        }

        public void addTestScriptRequest(String req) {
            Map<String,String> reqMap = getRequestParamsMap(req);
            String flowName = reqMap.get("flowName");
            Set<String> paramSet = reqMap.keySet();
            paramSet.remove("flowName");
            try{
                // Create file 
                Writer out = getTestFileWriter();
                out.write("request(\"" + flowName + "\",[");
                Iterator it = paramSet.iterator();
                int i = 0;
                while(it.hasNext()){
                    if(i != 0){
                        out.write(",");
                    }
                    String paramName = it.next().toString(); 
                    out.write("\"" + paramName + "\":\"" + 
                            reqMap.get(paramName) + "\"" 
                            );
                    i++;
                }
                String newLine = System.getProperty("line.separator");
                
                //request("AvailableCategoriesList",["java.util.HashMap$KeyIterator@75324a":"null])
                out.write("])" + newLine);
                out.write("checkReturnedValidJson()");
                //Close the output stream
                testScriptRequest = out.toString();
                out.close();
            }catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
          //  request("EligibleExternalServiceInstancesFlow", ["eligibleExternalServiceInstanceMap":"bogusData","eligibleExternalServiceInstances":"bogusData","fsRenderResult":"json"])
          //  checkReturnedValidJson()
            
        }
    }

}