package org.amplafi.dsl;

import groovy.lang.Closure;

import java.util.List;

import org.amplafi.flow.definitions.FarReachesServiceInfo;

/**
 * This class defines a simple DSL for sending reqests to the amplafi wire server
 * and expecting results.
 *
 * The format of the test language will be
 *
 * request('HelloFlow',['param1':'dog','param2':'pig']);
 *
 * expect("""
 * {    "validationErrors":{
 *      "flow-result":{
 *        "flowValidationTracking":[
 *           {
 *              "key":"MissingRequiredTracking",
 *              "parameters":[
 *                 "HelloFlow"
 *              ]
 *           },
 *           {
 *              "key":"flow.definition-not-found",
 *              "parameters":[
 *
 *              ]
 *           }
 *        ]
 *     }
 *  }
 *}
 *""");
 *
 */
public class FlowTestBuilder {

    private FarReachesServiceInfo serviceInfo;
    private String key;
    private ScriptRunner runner;
    private boolean verbose = false;

    /**
     * Constructor for tests
     */
    public FlowTestBuilder(FarReachesServiceInfo serviceInfo, ScriptRunner runner, boolean verbose){
        this.serviceInfo = serviceInfo;
        this.runner = runner;
        this.verbose = verbose;
    }

    /**
     * Constructor for tools
     */
    public FlowTestBuilder(FarReachesServiceInfo serviceInfo, String key, ScriptRunner runner, boolean verbose){
        this.serviceInfo =serviceInfo;
        this.key = key;
        this.runner = runner;
        this.verbose = verbose;
    }

     public FlowTestBuilder(String host, String port, String apiVersion, String key, List<String> paramArray){
        this.serviceInfo = new FarReachesServiceInfo( host, port, apiVersion);
        this.key = key;
    }

    /**
     * Configure the closure to be runnaable
     */
    public Closure buildExe(Closure c){
        if (key == null){
            c.setDelegate(new FlowTestDSL(serviceInfo, runner, verbose));
        } else {
            c.setDelegate(new FlowTestDSL(serviceInfo, key, runner, verbose));
        }
        c.setResolveStrategy(Closure.DELEGATE_FIRST);
        return c;
    }

    /**
     * Configure the colsure to describe itself.
     */
    public Closure buildDesc(Closure c){
        c.setDelegate(new DescribeScriptDSL());
        return c;
    }

}