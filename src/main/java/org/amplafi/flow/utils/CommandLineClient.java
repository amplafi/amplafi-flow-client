package org.amplafi.flow.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * In lieu of an andriod client.
 * 
 * @author Tyrinslys Valinlore
 */
public class CommandLineClient {
    private static final String listFlowTypesUrl = "http://localhost:8080/flow?fsRenderResult=json/describe";

    public static void main(String[] args) {
        printListOfFlowTypes();
    }

    /**
     * Construct a request to "http://localhost:8080/flow?fsRenderResult=json/describe" and print
     * the json response.
     */
    private static void printListOfFlowTypes() {
        String jsonResponse = getUrl(listFlowTypesUrl);
        if (jsonResponse != null) {
            System.out.println(jsonResponse);
        } else {
            System.out.println("ERROR: no response or response empty.");
        }

    }

    private static String getUrl(String urlString) {
        URLConnection conn;
        try {
            URL url = new URL(urlString);
            conn = url.openConnection();
            return convertInputStreamToString(conn.getInputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Convert a stream to a string.
     * @param inputStream
     * @return
     */
    public static String convertInputStreamToString(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        try {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception exception2) {
                exception2.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }
}
