package org.amplafi.flow.dsl;

import static org.amplafi.dsl.ScriptRunner.DEFAULT_SCRIPT_PATH;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.TestGenerationProperties;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * This is a test for the TestScriptRunner itself, not the wire server
 */
public class TestScriptRunner {

    private ScriptRunner instance = null;

    @BeforeTest
    public void setup(){
        instance = new ScriptRunner(TestGenerationProperties.getInstance().getRequestUriString(),null);
    }

    @AfterTest
    public void tearDown(){
        instance = null;
    }

    @Test
    public void touchTest() throws Exception{

        String script = "println('FFFFFFFFFFFFFFFFFFFFFFFFFFFFFf'); \n " +
                    " request('HelloFlow',['cat':'dog','hippo':'pig']);" ;
        instance.runScriptSource(script,null);
    }

    @Test
    public void testLoadAndRunOneScript(){
        instance.loadAndRunOneScript(DEFAULT_SCRIPT_PATH + "/1example.groovy");
    }

    @Test
    public void testLoadAndRunAllSrcipts(){
        instance.loadAndRunAllSrcipts();
    }

}
