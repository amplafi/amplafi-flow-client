package org.amplafi.testutils;
import static org.testng.Assert.*;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.dsl.FlowTestBuilder;
import org.amplafi.dsl.FlowTestDSL;
import org.amplafi.dsl.DescribeScriptDSL;
import org.amplafi.flow.utils.UtilParent;
import groovy.mock.interceptor.*
import org.amplafi.dsl.ScriptDescription;

/**
 * This class offer features for testing DSL scripts, such as:
 * mocking calls to the server and to other scripts.
 */
public class ScriptTester {

    def execDSLMock;
    def execDescribeMock;
    ScriptRunner runner;
    public void init(){

    }

    public void tearDown(){
        execDSLMock = null;
        execDescribeMock = null;
        runner = null;
    }


    def getExecDSLSpy(){
        if ( !execDSLMock){
            FlowTestDSL real = new FlowTestDSL("",runner, true);
            execDSLMock = new SpyFlowTestDSL();
            execDSLMock.real = real;
            // some methods should not be mocked
        }
        return execDSLMock;
    }

    private class SpyFlowTestDSL {
        def methods = [:]
        def real;

        def invokeMethod(String name, args) {
            if (methods[name]){
                methods[name](args);
            } else {
                def metaMethod = real.metaClass.getMetaMethod(name, args)
                metaMethod.invoke(real, args)
            }
        }
   }

    class MyInterceptor implements Interceptor{
        def methods = [:];

        def doInvoke = true;

        Object beforeInvoke(Object object, String methodName, Object[] arguments){
            if (methods[name]){
                doInvoke = true;
            } else {
                doInvoke = false;
            }

        }
        boolean doInvoke(){
            doInvoke;
        }


        Object afterInvoke(Object object, String methodName, Object[] arguments,
                         Object result){
            methods[methodName](arguments);
       }
    }




    public getDescribeDSLMock(){
        if ( !execDescribeMock){
            execDescribeMock = new DescribeScriptDSL();
        }
        return execDescribeMock;
    }


    /**
     * The method is create a map of scriptLookup.
     * @return scriptLookup.
     */
    protected Map<String, ScriptDescription> getScriptLookup(ScriptRunner runner){
        Map<String, ScriptDescription> scriptLookup = runner.processScriptsInFolder(UtilParent.DEFAULT_COMMAND_SCRIPT_PATH);

        return scriptLookup;
    }

    def runScript(String scriptName, Map params){
        runner = new TinkerableScriptRunner(params);

        Map<String, ScriptDescription> scriptLookup = getScriptLookup(runner);
        Object ret = null;
        if (scriptLookup[scriptName]){
            runner.setScriptLookup(scriptLookup);
            ret = runner.loadAndRunOneScript(scriptLookup[scriptName].path);
        } else {
            fail("Script ${scriptName} does not exist");
        }
        return  ret;
    }


    private class TinkerableScriptRunner extends ScriptRunner{
        TinkerableScriptRunner(params){
             super( "",  "", "", "",  params, true);
        }

        /**
         * Configure for describing script
         */
        @Override
        def getFlowTestBuilder(requestUriString,runner,verbose){
            return new TinkerableFlowTestBuilder();
        }

        /**
         * Configure for running script
         */
        @Override
        def getFlowTestBuilder(host,port,apiVersion,key,runner,verbose){
            return new TinkerableFlowTestBuilder();
        }

    }

    private class TinkerableFlowTestBuilder extends FlowTestBuilder {

        /**
         * Constructor for tests
         */
        public TinkerableFlowTestBuilder(){
            super(null, null, false);
        }


        /**
         * Configure the closure to be runnaable
         */
        @Override
        public buildExe(Closure c){
            c.delegate = getExecDSLSpy();
            c.setResolveStrategy(Closure.DELEGATE_FIRST)
            return c;
        }

        /**
         * Configure the colsure to describe itself.
         */
        @Override
        public buildDesc(Closure c){
            c.delegate = getDescribeDSLMock();

            return c;
        }

    }





}

