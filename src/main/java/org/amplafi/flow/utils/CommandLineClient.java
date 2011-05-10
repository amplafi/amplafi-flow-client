package org.amplafi.flow.utils;

/**
 * In lieu of an andriod client.
 * 
 * @author Tyrinslys Valinlore
 */
public class CommandLineClient {
    // full string to send a request to the server and receive a list of the different flow types.
    //private static final String listFlowTypesUrl = "http://localhost:8080/flow?fsRenderResult=json/describe";

    private static final String requestUriString = "http://localhost:8080";

    public static void main(String[] args) {
        //get list of flow types
        GeneralFlowRequest.getListOfFlowTypes(requestUriString);
    }
}
