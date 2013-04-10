package org.amplafi.dsl;

import org.amplafi.flow.utils.GeneralFlowRequest;
import org.amplafi.flow.utils.FlowResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.testng.Assert.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the methods that are callable within the flow test DSL.
 */
public class DescribeScriptDSL {
    def String name;
    def String usage;
    def String description;
    def List<ParameterUsge> usages;
    def scriptDescription;
    private static final boolean DEBUG = false;

    /** This stores the base uri including the host,port,apikey */
    private String serviceInfo;

    /** This stores the last request to the server */
    private String lastRequestString;

    /**
     * Contains the last response from the server.
     */
    public String lastRequestResponse = null;

    public DescribeScriptDSL(){
    }

    public String getKey(){
    }

    String getApiVersion(){
    }

    void setApiVersion(String apiVersion){
    }


    /**
     * The description of the name.
     *@param name - the name to description.
     *@param description - the name 's description.
     *@throw EarlyExitException if it just need description.
     */
    public void description (String name, String description){
        this.name = name;
        this.description = description;
        this.scriptDescription = new ScriptDescription(name:name , description:description, usage:"" );
        // This pevents the other commands in the script fom being executed.
        throw new EarlyExitException(scriptDescription);
    }

    /**
     * The description of the name.
     *@param name - the name to description.
     *@param description - the name 's description.
     *@param usage is the usage of the script.
     *@throw EarlyExitException  if it just need description.
     */
    public void description (String name, String description, String usage){
        this.usage = usage;
        this.name = name;
        this.description = description;
        this.scriptDescription = new ScriptDescription(name:name , description:description, usage:usage );
        // This pevents the other commands in the script fom being executed.
        throw new EarlyExitException(scriptDescription);
    }

    /**
     * The description of the name.
     *@param name - the name to description.
     *@param description - the name 's description.
     *@param usage is the list of the cript usages.
     *@throw EarlyExitException  if it just need description.
     */
     public void description (String name, String description, List<ParameterUsge> usages){
        String newLine = System.getProperty("line.separator");
        StringBuffer usageSb = new StringBuffer(newLine);
        for(ParameterUsge paramUsage : usages){
            if(paramUsage.getName()&& paramUsage.getDescription()){
                usageSb.append(sprintf('%-15s = <%-15s>', paramUsage.getName(), paramUsage.getDescription()));
                // usageSb.append(paramUsage.getName() + " = " +"<" +paramUsage.getDescription() + ">");
                if(paramUsage.getOptional()){
                    usageSb.append(" , optional");
                }else{
                    usageSb.append(" , required");
                }
                if(paramUsage.getDefaultValue()){
                    usageSb.append(" , defaultValue = " + paramUsage.getDefaultValue());
                }
                usageSb.append(newLine);
            }
        }
        this.usage =  usageSb.toString();
        this.name = name;
        this.description = description;
        this.scriptDescription = new ScriptDescription(name:name , description:description, usage:usage, usageList:usages );
        // This pevents the other commands in the script fom being executed.
        throw new EarlyExitException(scriptDescription);
     }

    /**
     * send a post request.
     */
    String requestPost(String flowName, Map paramsMap){
        throw new NoDescriptionException();
    }

    /**
     * Sends a request to the named flow with the specified parameters.
     * @param flowName to call.
     * @param paramsMap key value map of parameters to send.
     */
    String request(String flowName, Map paramsMap){
        throw new NoDescriptionException();
    }

    /**
     * This method will automatically add a callbackParam into params and send the request.
     * With a callback uri It will then use openPort to call the flow and return the response.
     * @param flowName to call.
     * @param [params] key value map of parameters to send.
     * @param dataReturnProperty the property we want to return.
     * @return response string.
     */
    String asyncRequest(String flowName, Map paramsMap,dataReturnProperty){
        throw new NoDescriptionException();
    }

    /**
     * Sends a request to the named flow with the specified parameters.
     * @param flowName to call.
     * @param paramsMap key value map of parameters to send.
     * @return response object.
     */
    FlowResponse requestResponse(String flowName, Map paramsMap){
        throw new NoDescriptionException();
    }

    /**
     * Throws a test error if the actual data returned from the server is not the same as.
     * the expected JSON.
     * @param expectedJSONData.
     */
    def expect(String expectedJSONData){
        throw new NoDescriptionException();
    }

    /**
     * Throws a test error if the actual data returned from the server is not the same as.
     * the expected JSON, ignorePathList.
     * @param expectedJSONData.
     */
    def expect(String expectedJSONData,List<String> ignorePathList){
        throw new NoDescriptionException();
    }

    /**
     * @param expectedJSONData.
     */
    def checkReturnedValidJson(){
        throw new NoDescriptionException();
    }

    /**
     * @param expectedJSONData.
     */
    def prettyPrintResult(){
        throw new NoDescriptionException();
    }

    /**
     * Call a script with params.
     * @param scriptName script name.
     * @param callParamsMap script parameters.
     */
    def callScript(String scriptName, Map callParamsMap){
        throw new NoDescriptionException();
    }

    public def openPort(int portNo, int timeOutSeconds, Closure doNow, Closure handleRequest){
        throw new NoDescriptionException();
    }

    /**
     * Call a script with no params.
     * @param scriptName script name.
     */
    def callScript(String scriptName){
        throw new NoDescriptionException();
    }

    def getscriptDescription(){
        return scriptDescription;
    }

    /**
     * @param message.
     * Print a message.
     */
    def log(msg){
         getLog().info(msg);
    }

    /**
     * @param msg is message to debug log.
     */
    private void debug(String msg){
        getLog().debug(msg);
    }

    /**
     * Get the logger for this class.
     * @return log.
     */
    public Log getLog(){
        if ( this.log == null ) {
            this.log = LogFactory.getLog(FlowTestBuilder.class);
        }
        return this.log;
    }

    /**
     * This method returns a ParameterUsge.
     * @param name is name of the param.
     * @param description is description of the param.
     * @param optional is optional of the param.
     * @param defaultValue is default value of the param.
     * @return a ParameterUsge.
     */
    public ParameterUsge paramDef(String name,String description,boolean optional,Object defaultValue){
        return new ParameterUsge(name,description,optional,defaultValue);
    }
}