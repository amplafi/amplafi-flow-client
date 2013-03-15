package org.amplafi.flow.commandScripts;

import static org.testng.Assert.*;

import java.util.List;

import org.amplafi.dsl.ScriptRunner;
import org.amplafi.dsl.ParameterValidationException;
import org.amplafi.flow.TestGenerationProperties;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.amplafi.dsl.ScriptDescription;
import org.amplafi.dsl.EarlyExitException;
import org.amplafi.dsl.ParameterUsge;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import org.amplafi.testutils.ScriptTester;
import groovy.mock.interceptor.*
/**
 * This is a test for TestCustomerProblemReport.
 */
public class TestCustomerProblemReport extends ScriptTester {

    @BeforeMethod
    public void setUp(){
        super.init();
    }

    @AfterMethod
    public void tearDown(){
        super.tearDown();
    }

    /**
     * run CustomerProblemReport with no params and verify that a usage error is raised.
     */
    @Test
    public void runWithNoParams(){
        try {
            runScript("CustomerProblemReport", [:])
            fail("Exception should have been thrown");
        } catch (ParameterValidationException pve){
            assertEquals(pve.getMessage(),"Parameter publicUri must be supplied. Format is: publicUri=<The public uri>");
        }

    }

    /**
     * run CustomerProblemReport a show a call is made to another script to get su api key which returns null.
     */
    @Test
    public void runWithPublicUri(){

        // Get A DSL spy object
        def dslSpy = getExecDSLSpy();

        // When callScript method is called with param "CreateSuApiKey"  we return null;
        dslSpy.expect_callScript("CreateSuApiKey",["publicUri":"www.acme.com","userEmail":"admin@amplafi.com"]).andReturn = null;

        runScript("CustomerProblemReport", ["publicUri":"www.acme.com"])

        dslSpy.verify();
    }

    /**
     * run CustomerProblemReport a show a call is made to another script to get su api key.
     */
    @Test
    public void runSuccess(){

        // Get A DSL spy object
        def dslSpy = getExecDSLSpy();

        //
        // Set up expected calls
        //

        // When callScript method is called with param "CreateSuApiKey"  we return a dummy key;
        dslSpy.expect_callScript("CreateSuApiKey",["publicUri":"www.acme.com","userEmail":"admin@amplafi.com"]).andReturn = "DUMMY_SU_API_KEY";
        dslSpy.expect_setKey("DUMMY_SU_API_KEY");
        dslSpy.expect_callScript("AvailableCategories");
        dslSpy.expect_callScript("AvailableExternalServices");//flow does not exists.

        dslSpy.expect_setKey("DUMMY_SU_API_KEY");
        dslSpy.expect_request("MessageEndPointList", ["fsRenderResult":"json", "messageEndPointCompleteList": "true"]);

        // Apparently the tool is still expecting a JSONArray to be returned
        // TODO. Look into this.
        dslSpy.expect_getResponseData().andReturn = new JSONArray("""[
        {
   "lookupKey":"luKey",
   "publicUri":"ww.aaa.bbb",
   "externalServiceDefinition":"no idea",
   "extServiceUsername":"hoola hoops",
   "extServiceUserFullName":"big hoola hoops"
        },
        {
   "lookupKey":"luKey2",
   "publicUri":"ww.ccc.ddd",
   "externalServiceDefinition":"no idea2",
   "extServiceUsername":"hoola hoops2",
   "extServiceUserFullName":"big hoola hoops2"
        }
       ] """);

        dslSpy.expect_callScript("ApiRequestAuditEntryLog",["apiKey": "DUMMY_SU_API_KEY", "fromDate": null, "toDate": null, "maxReturn": "1000"]);
        dslSpy.expect_callScript("ExternalApiMethodCallLog",["apiKey": "DUMMY_SU_API_KEY", "fromDate": null, "toDate": null, "maxReturn": "1000"]);
        dslSpy.expect_callScript("UserPost",["apiKey": "DUMMY_SU_API_KEY", "fromDate": null, "toDate": null]);


        runScript("CustomerProblemReport", ["publicUri":"www.acme.com"])


        // Verify calls took place
        dslSpy.verify();
    }

    /**
     * run CustomerProblemReport and have one of the internal calls throw an error
     */
    @Test
    public void runWithError(){

        // Get A DSL spy object
        def dslSpy = getExecDSLSpy();

        //
        // Set up expected calls
        //

        // When callScript method is called with param "CreateSuApiKey"  we return dumy key;
        dslSpy.expect_callScript("CreateSuApiKey",["publicUri":"www.acme.com","userEmail":"admin@amplafi.com"]).andReturn = "DUMMY_SU_API_KEY";
        dslSpy.expect_setKey("DUMMY_SU_API_KEY");
        dslSpy.expect_callScript("AvailableCategories");
        // when this is called throw an exception.
        dslSpy.expect_callScript("AvailableExternalServices").andThrow = new Exception("Deliberate exception in test");

        runScript("CustomerProblemReport", ["publicUri":"www.acme.com"])


        // Verify calls took place
        dslSpy.verify();
    }


}
