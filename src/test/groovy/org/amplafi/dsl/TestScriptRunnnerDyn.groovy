package org.amplafi.flow.dsl;

import static org.testng.Assert.*;

import java.util.List;

import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.TestGenerationProperties;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.amplafi.dsl.ScriptDescription;
import org.amplafi.dsl.EarlyExitException;
import org.amplafi.dsl.ParameterUsge;

/**
 * This is a test for the TestScriptRunner itself, not the wire server
 */
public class TestScriptRunnerDyn {
    
    private def instance = null;
    private String host;
    private String port;
    private String apiVersion;
    private String key;
    private Map<String,String> paramsmap;
    private boolean verbose = true;
    private static final NL = System.getProperty("line.separator");
    
    @BeforeMethod
    public void setUp(){

        host = "testhost";
        port = "testport";
        apiVersion = "testapiversion";
        key = "testKey";

        paramsmap = [:];

        verbose = true;

    }

    @AfterMethod
    public void tearDown(){
        instance = null;
    }

    //This test tests script description method.
    @Test 
    public void testDescribeScript(){
        def testScript1 = """
        description "Test1", "Description1", [paramDef("param1","test param1",true,"100"),
                                    paramDef("param2","test param2",true,"100"),
                                    paramDef("param3","test param3",true,"100")]
                                    
        return "hello from test1";
        """
        ScriptDescription desc = null;
        try {

            instance = new ScriptRunner(host, port, apiVersion,key, paramsmap,verbose)
            def returnValue = instance.runScriptSource(testScript1, false, null);
            
            fail("Expected an Early Exit Exception which didn't happen");
        } catch (EarlyExitException eee){
            desc = eee.desc;
        }
        
        assertFalse(desc.hasErrors);
        assertNull(desc.errorMesg);
        assertNull(desc.errorMesg);
        assertEquals(desc.name,"Test1");
        assertEquals(desc.description,"Description1");
        assertNull(desc.path);
        assertEquals(desc.usage,NL + "param1 = <test param1>" + NL + "param2 = <test param2>"+ NL +"param3 = <test param3>" +NL);
        ParameterUsge parameterUsge1 = new ParameterUsge("param1","test param1",true,"100");
        ParameterUsge parameterUsge2 = new ParameterUsge("param2","test param2",true,"100");
        ParameterUsge parameterUsge3 = new ParameterUsge("param3","test param3",true,"100");
    }
    
    //This test tests script runner with no param input.
    @Test 
    public void testRunScriptWithNoParamInput(){
        def testScript2 = """
        description "Test2", "Description2", [paramDef("param1","test param1",true,100),
                                    paramDef("param2","test param2",true,500),
                                    paramDef("param3","test param3",true,300)]
                                    
        return "---param1 : \${param1} ---param2 : \${param2} ---param3 : \${param3}";
        """
        ScriptDescription desc = new ScriptDescription();
        desc.name = "Test2";
        desc.description = "Description2";
        desc.usage = "param1 = <test param1>" + NL + "param2 = <test param2>"+ NL +"param3 = <test param3>" +NL;
        ParameterUsge parameterUsge1 = new ParameterUsge("param1","test param1",true,100);
        ParameterUsge parameterUsge2 = new ParameterUsge("param2","test param2",true,500);
        ParameterUsge parameterUsge3 = new ParameterUsge("param3","test param3",true,300);
        List<ParameterUsge> usageList = new ArrayList<ParameterUsge>();
        usageList.add(parameterUsge1);
        usageList.add(parameterUsge2);
        usageList.add(parameterUsge3);
        desc.usageList = usageList;
        try {

            instance = new ScriptRunner(host, port, apiVersion,key, paramsmap,verbose)	
            def returnValue = instance.runScriptSource(testScript2, true, desc);
            assertEquals(returnValue,"---param1 : 100 ---param2 : 500 ---param3 : 300");

        } catch (EarlyExitException eee){
            desc = eee.desc;
        }
        String generateParams = "def param1 = 100;def param2 = 500;def param3 = 300;"
        def scriptStr = """import org.amplafi.dsl.FlowTestBuilder;import org.amplafi.json.*; ${generateParams} def source = { ${testScript2} }; return source """;
    }
    
    //This test tests script runner with params input.
    @Test 
    public void testRunScriptWithParamInput(){
        paramsmap = ["param1":888,"param2":999];


        def testScript3 = """
        description "Test3", "Description3", [paramDef("param1","test param1",true,100),
                                    paramDef("param2","test param2",true,500),
                                    paramDef("param3","test param3",true,300)]
                                    
        return "---param1 : \${param1} ---param2 : \${param2} ---param3 : \${param3}";
        """
        ScriptDescription desc = new ScriptDescription();
        desc.name = "Test3";
        desc.description = "Description3";
        desc.usage = "param1 = <test param1>" + NL + "param2 = <test param2>"+ NL +"param3 = <test param3>" +NL;
        ParameterUsge parameterUsge1 = new ParameterUsge("param1","test param1",true,100);
        ParameterUsge parameterUsge2 = new ParameterUsge("param2","test param2",true,500);
        ParameterUsge parameterUsge3 = new ParameterUsge("param3","test param3",true,300);
        List<ParameterUsge> usageList = new ArrayList<ParameterUsge>();
        usageList.add(parameterUsge1);
        usageList.add(parameterUsge2);
        usageList.add(parameterUsge3);
        desc.usageList = usageList;
        try {
            instance = new ScriptRunner(host, port, apiVersion,key, paramsmap,verbose)
            def returnValue = instance.runScriptSource(testScript3, true, desc);
            assertEquals(returnValue,"---param1 : 888 ---param2 : 999 ---param3 : 300");

        } catch (EarlyExitException eee){
            desc = eee.desc;
        }
    }
    
    //This test tests script runner with params which is optional.
    @Test 
    public void testRunScriptWithParamRequired(){
        paramsmap = ["param1":666];
        def testScript4 = """
        description "Test4", "Description4", [paramDef("param1","test param1",true,100),
                                    paramDef("param2","test param2",true,500),
                                    paramDef("param3","test param3",true,300)]
                                    
        return "---param1 : \${param1} ---param2 : \${param2} ---param3 : \${param3}"; 
        """
        ScriptDescription desc = new ScriptDescription();
        desc.name = "Test4";
        desc.description = "Description4";
        desc.usage = "param1 = <test param1>" + NL + "param2 = <test param2>"+ NL +"param3 = <test param3>" +NL;
        ParameterUsge parameterUsge1 = new ParameterUsge("param1","test param1",false,100);
        ParameterUsge parameterUsge2 = new ParameterUsge("param2","test param2",true,500);
        ParameterUsge parameterUsge3 = new ParameterUsge("param3","test param3",true,300);
        List<ParameterUsge> usageList = new ArrayList<ParameterUsge>();
        usageList.add(parameterUsge1);
        usageList.add(parameterUsge2);
        usageList.add(parameterUsge3);
        desc.usageList = usageList;
        try {
            instance = new ScriptRunner(host, port, apiVersion,key, paramsmap,verbose)
            def returnValue = instance.runScriptSource(testScript4, true, desc);
            
            assertEquals(returnValue,"---param1 : 666 ---param2 : 500 ---param3 : 300");
            
        } catch (EarlyExitException eee){
            desc = eee.desc;
        }
    }
    
    //This test tests script runner with params which is required.
    @Test 
    public void testRunScriptWithNoParamRequired(){

        def testScript5 = """
        description "Test5", "Description5", [paramDef("param1","test param1",true,100),
                                    paramDef("param2","test param2",true,500),
                                    paramDef("param3","test param3",true,300)]
                                    
        return "---param1 : \${param1} ---param2 : \${param2} ---param3 : \${param3}"; 
        """
        ScriptDescription desc = new ScriptDescription();
        desc.name = "Test5";
        desc.description = "Description5";
        desc.usage = "param1 = <test param1>" + NL + "param2 = <test param2>"+ NL +"param3 = <test param3>" +NL;
        ParameterUsge parameterUsge1 = new ParameterUsge("param1","test param1",false,100);
        ParameterUsge parameterUsge2 = new ParameterUsge("param2","test param2",true,500);
        ParameterUsge parameterUsge3 = new ParameterUsge("param3","test param3",true,300);
        List<ParameterUsge> usageList = new ArrayList<ParameterUsge>();
        usageList.add(parameterUsge1);
        usageList.add(parameterUsge2);
        usageList.add(parameterUsge3);
        desc.usageList = usageList;
        try {

            instance = new ScriptRunner(host, port, apiVersion,key, paramsmap,verbose)
            try {
                def returnValue = instance.runScriptSource(testScript5, true, desc);
                fail("RuntimeException an Early Exit Exception which didn't happen");
            } catch (RuntimeException eee){
                
            }
            
        } catch (EarlyExitException eee){
            desc = eee.desc;
        }

    }
    
    
}