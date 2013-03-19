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
 * This is a test for TestCreateNewCategory.
 */
public class TestCreateNewCategory extends ScriptTester {

    @BeforeMethod
    public void setUp(){
        super.init();
    }

    @AfterMethod
    public void tearDown(){
        super.tearDown();
    }

    /**
     * run CreateNewCategory with good params and verify the result is correct.
     */
    @Test
    public void runSuccess(){
        def dslSpy = getExecDSLSpy();
        
        // Set up expected calls
        dslSpy.expect_requestPost("Category",["topicName":"hello",
                                            "topicTag":"hello world!",
                                            "topicAdditionalDescription":"my first java application",
                                            "externalContentId":"[22,'tag']"] ).andReturn = "hello";
        def ret = runScript("CreateNewCategory", ["topicName":"hello",
                                                "topicTag":"hello world!",
                                                "topicAdditionalDescription":"my first java application",
                                                "externalContentId":"[22,'tag']"] );
        assertEquals(ret,"hello");
    }
    
    /**
     * run CreateNewCategory with no params.
     */
    @Test
    public void runWithNoParams(){
        def dslSpy = getExecDSLSpy();
        
        // Set up expected calls
        dslSpy.expect_requestPost("Category",[:] ).andReturn = "";
    }
    
    /**
     * run CreateNewCategory with bad params.
     */
    @Test
    public void runWithBadParams(){
        def dslSpy = getExecDSLSpy();
        
        // Set up expected calls
        dslSpy.expect_requestPost("Category",["cats":"tomcat"] ).andReturn = "";
    }

}
