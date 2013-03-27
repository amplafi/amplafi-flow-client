package org.amplafi.testutils;
import static org.testng.Assert.*;
import org.amplafi.dsl.ScriptRunner;
import org.amplafi.dsl.FlowTestBuilder;
import org.amplafi.dsl.FlowTestDSL;
import org.amplafi.dsl.DescribeScriptDSL;
import org.amplafi.flow.utils.UtilParent;
import groovy.mock.interceptor.*
import org.amplafi.dsl.ScriptDescription;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
/**
 * This class offer features for testing DSL scripts, such as:
 * mocking calls to the server and to other scripts.
 */
public class ScriptTester {

    def static log(msg){
        println msg;
    }

    def execDSLMock;
    def execDescribeMock;
    ScriptRunner runner;
    FarReachesServiceInfo serviceInfo;

    public void init(){
        serviceInfo = new FarReachesServiceInfo("_host_","_port_","_api_");
    }

    public void tearDown(){
        execDSLMock = null;
        execDescribeMock = null;
        runner = null;
    }


    def getExecDSLSpy(){
        if ( !execDSLMock){

            FlowTestDSL real = new FlowTestDSL(serviceInfo,runner, true);
            execDSLMock = new Spy();
            execDSLMock.real = real;
            // some methods should not be mocked

        }
        return execDSLMock;
    }


    // Groovy mocking is as ugly as easymock
    // and mockito has problems.
    // If you want to pass through calls to a real object set it to real
    // To set up an expectation on x.myMethod(123) simply call x.expect_myMethod(123).andDo{ param -> assert param == 123 }
    // or                          x.expect_myMethod(123).andThrow = new Exception("bad cats")
    // or                          x.expect_myMethod(123).andReturn = cats
    //
    private class Spy  implements GroovyInterceptable {
        def methods = [:];
        def callCount = 0;
        def expectedCalls = [];

        def real;
        public static final String EXPECT = "expect_";

        def invokeMethod(String name, args) {

            def realMethodName = name;

            if (name == "verify" ) {
                def metaMethod = metaClass.getMetaMethod(name, args)
                def result = metaMethod.invoke(this, args)
                return;
            }

            if (name.startsWith(EXPECT) ) {

                realMethodName = name.replaceFirst(EXPECT, "");
                def expectation = new ExpectedCall(methodName:realMethodName, params:args );

                expectedCalls << expectation;

                //
                methods[realMethodName] = expectation;
                return expectation;
            }
ScriptTester.log("SPY CAUGHT: ${name}(${args})")

            // Have we set up an expectation for this method?
            if (methods[name]){
                // if yes then get current call.

                if (callCount >= expectedCalls.size()){
                    fail("Received too many calls on spy. call number ${callCount+1} received call to ${name}(${args})")
                }

                def expected = expectedCalls[callCount]

                if (expected.methodName == name){
                    expected.called = true;
                    callCount++;
                    assertEquals(args,expected.params,"Parameters to method ${name} call ${callCount} differ from expected: ")
                    if (expected.andThrow) {
                        throw expected.andThrow;
                    } else if (expected.andReturn) {
                        return expected.andReturn;
                    } else if(expected.andDoC){
                        expected.andDoC(args);
                    } else if (expected.andPassThrough){
                        if (real){
                            def metaMethod = real.metaClass.getMetaMethod(name, args)
                            metaMethod.invoke(real, args)
                        } else {
                            fail("No real object set for call to  ${name} with params ${args}")
                        }
                    }

                } else {
                    fail("Unexpected call to ${name} with params ${args}")
                }

            } else {
                if (real){
                    def metaMethod = real.metaClass.getMetaMethod(name, args)
                    metaMethod.invoke(real, args)
                } else {
                    fail("No real object set for call to  ${name} with params ${args}")
                }
            }
        }

        def verify(){
            expectedCalls.each{
                if (!it.called){
                    fail("No call recived on ${it.methodName} with params ${it.params}")
                }
            }
        }

        public class ExpectedCall {
            def called = false;
            def methodName;
            def params;
            def andThrow;
            def andReturn;
            def andPassThrough;
            def andDoC; // closure
            def andDo(Closure c){
                andDoC = c;
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
        runner = new TinkerableScriptRunner(serviceInfo,params);

        Map<String, ScriptDescription> scriptLookup = getScriptLookup(runner);
println scriptLookup;
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
        TinkerableScriptRunner(serviceInfo,params){
             super(serviceInfo, "key",  params, true);
        }

        /**
         * Configure for describing script
         */
        @Override
        def getFlowTestBuilder(serviceInfo,runner,verbose){
            return new TinkerableFlowTestBuilder(serviceInfo);
        }

        /**
         * Configure for running script
         */
        @Override
        def getFlowTestBuilder(serviceInfo,key,runner,verbose){
            return new TinkerableFlowTestBuilder(serviceInfo);
        }

    }

    private class TinkerableFlowTestBuilder extends FlowTestBuilder {

        /**
         * Constructor for tests
         */
        public TinkerableFlowTestBuilder(serviceInfo){
            super(serviceInfo, null, false);
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



