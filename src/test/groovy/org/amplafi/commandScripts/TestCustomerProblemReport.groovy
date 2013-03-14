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
     * run CustomerProblemReport a show a call is made to another script to get su api key.
     */
    @Test
    public void runWithPublicUri(){

        def dslSpy = getExecDSLSpy();
        def createSuApiKeyCalled = false;

        // When callScript method is called with param "CreateSuApiKey"  we return null;
        dslSpy.methods["callScript"] = { params ->
            if ( params[0] == "CreateSuApiKey" ) {
                assertEquals( params[1], ["publicUri":"www.acme.com","userEmail":"admin@amplafi.com"]);
                createSuApiKeyCalled = true;
                return null;
            }
        }

        runScript("CustomerProblemReport", ["publicUri":"www.acme.com"])
        assertTrue(createSuApiKeyCalled);
    }


}
