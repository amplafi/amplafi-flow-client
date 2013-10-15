package org.amplafi.flow.dsl;

import org.amplafi.dsl.ScriptRunner;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * This is a test for the TestScriptRunner itself, not the wire server
 * TODO BRUNO FIX THIS THING
 */
public class TestDSLScriptsBatchRunner {

    private ScriptRunner runner;

    private String currentScriptName = "";

    /**
     * This factory will be called repeatedly to generate test instances for each of the flows
     * supported by the server.
     */
    public TestDSLScriptsBatchRunner() {
        //runner  = new ScriptRunner(TestGenerationProperties.getInstance().getRequestUriString(),null);
    }

    @Override
    public String toString() {
        return currentScriptName;
    }

    /**
     * Provides all script names for testing
     *
     * @return
     */
    @DataProvider(name = "scripts-list")
    public Object[][] getListOfTestScripts() {
        //ScriptRunner runner  = new ScriptRunner(TestGenerationProperties.getInstance().getRequestUriString(),null);

        //Get list of supported flow types from server.
        //List<String> scriptList = runner.findAllTestScripts();
        Object[][] arrayOfScripts = null;
        /*if (scriptList.size() > 0){
            // Drop the list into the Object[][] format which is standard for testNG data providers
            // This provides an array of parameters for each test
            arrayOfScripts = new Object[scriptList.size()][];
            int index = 0;
            for (String script : scriptList) {
                arrayOfScripts[index] = new Object[] { script };
                index++;
            }
        } else {
           fail("No Test scripts found");
        }*/
        return arrayOfScripts;
    }

    @BeforeTest
    public void setup(){

    }

    @AfterTest
    public void tearDown(){
    }

    /**
     * This test will be run once for each of the scripts in the src/test/resources/testscripts folder
     */
    @Test(dataProvider = "scripts-list")
    public void testLoadAndRunOneScript(String script) throws Exception{
        currentScriptName = script;
        runner.loadAndRunOneScript(script);
    }
}
