package org.amplafi.flow.utils;

import static org.amplafi.flow.utils.CommandLineClientOptions.*;

import java.net.URI;

import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.amplafi.flow.definitions.FlowRequestDescription;
import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import org.amplafi.json.JsonConstruct;
import org.apache.commons.cli.ParseException;

/**
 * TODO: remove this class and its dependencies. Replace by Shell
 * A shell to manage the wireservice.
 *
 * @author Tuan
 * @author Haris Osmanagic
 */
public class CommandLineClient implements Runnable {
    private static final int INDENTATION_LEVEL = 5;

    private String apiKey;

    private FarReachesServiceInfo serviceInfo;
    private FlowRequestDescription flowRequestDescription;
    private boolean isTutorial;
    private boolean doFormatOutput;

    public static void main(String[] args) {
        CommandLineClientOptions cmdOptions = null;

        try {
            cmdOptions = new CommandLineClientOptions(args);
        } catch (ParseException e) {
            System.err.println("Could not parse passed arguments, message:");
            e.printStackTrace();
            System.exit(1);
        }

        if (args.length == 0 || cmdOptions.hasOption(HELP) ) {
            cmdOptions.printHelp();

            System.exit(0);
        }

        String apiKey = cmdOptions.getOptionValue(API_KEY);

        FarReachesServiceInfo serviceInfo = new FarReachesServiceInfo(
                cmdOptions.getOptionValue(HOST),
                cmdOptions.getOptionValue(PORT),
                cmdOptions.getOptionValue(API_VERSION));

        FlowRequestDescription flowRequestDescription = new FlowRequestDescription(cmdOptions.getOptionValue(FLOW),
                cmdOptions.hasOption(DESCRIBE), cmdOptions.getOptionProperties(PARAMS));

        CommandLineClient client = new CommandLineClient(apiKey, serviceInfo, flowRequestDescription, cmdOptions.hasOption(FORMAT), cmdOptions.hasOption(TUTORIAL));
        client.run();
    }

    public CommandLineClient(String apiKey, FarReachesServiceInfo serviceInfo,
            FlowRequestDescription flowRequest, boolean doFormatOutput, boolean isTutorial) {
        System.err.println("isTutorial " + isTutorial);
        this.apiKey = apiKey;
        this.serviceInfo = serviceInfo;
        this.flowRequestDescription = flowRequest;
        this.doFormatOutput = doFormatOutput;
        this.isTutorial = isTutorial;
    }

    private String buildBaseUriString() {
        String fullUri;
        if (!this.isTutorial){
            fullUri =  this.serviceInfo.getHost()
                    + ":" + this.serviceInfo.getPort() + "/c/"
                    + this.apiKey
                    + "/" + this.serviceInfo.getApiVersion();

        } else {
             fullUri =  this.serviceInfo.getHost()  +
             ":" + this.serviceInfo.getPort() +
             "/tutorial/flow";

        }
        return fullUri;
    }

    public void run() {
        GeneralFlowRequest flowRequest = new GeneralFlowRequest(serviceInfo, this.apiKey, this.flowRequestDescription.getFlowName(), this.flowRequestDescription.getParameters());
        JsonConstruct result = null;
        if (flowRequestDescription.isDescribe() && flowRequestDescription.getFlowName() == null) {
            result = flowRequest.listFlows();
        } else if (flowRequestDescription.isDescribe() && flowRequestDescription.getFlowName() != null) {
            result = flowRequest.describeFlow();
        } else {
            result = toJsonConstruct(flowRequest.get());
        }

        String output = formatIfNeeded(result);
        System.out.println(output);
    }

    private String formatIfNeeded(JsonConstruct result) {
        String output = null;

        if (doFormatOutput) {
            output = result.toString(INDENTATION_LEVEL);
        } else {
            output = result.toString();
        }

        return output;
    }

    private JsonConstruct toJsonConstruct(String string) {
        JsonConstruct json = null;

        try {
            json = new JSONObject(string);
        } catch (JSONException e) {
            json = new JSONArray<String>(string);
        }

        return json;
    }
}
