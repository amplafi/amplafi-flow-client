package org.amplafi.flow.utils;

import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplafi.json.JSONArray;
import org.amplafi.json.JSONException;
import org.amplafi.json.JSONObject;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.internal.runners.statements.Fail;

import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.API_KEY;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.API_VERSION;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.HOST;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.OUTPATH;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.PORT;
import static org.amplafi.flow.utils.DSLTestGeneratorCommandLineOptions.STRATEGY;
import static org.amplafi.flow.utils.LoggingProxyCommandLineOptions.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*import javax.servlet.DispatcherType;
 import org.eclipse.jetty.servlets.ProxyServlet.Transparent;
 import org.eclipse.jetty.server.Connector;
 import org.eclipse.jetty.server.Server;
 import org.eclipse.jetty.server.nio.SelectChannelConnector;
 import org.eclipse.jetty.servlet.ServletHandler;
 import org.eclipse.jetty.servlet.ServletHolder;*/

/**
 * Heavily based on this http://www.java2s.com/Code/Java/Network-Protocol/Asimpleproxyserver.htm Runs a proxy between
 * the plugin and the wire-server. Detects requests and resposes and out puts a single integration style test in the
 * test DSL format.
 * 
 * @author paul
 */
public class LoggingProxy {

    private StringBuffer testFileContents = null;
    private static final int STANDARD_INDENTATION = 4;
    String NL = System.getProperty("line.separator");
    protected LoggingProxyCommandLineOptions cmdOptions = null;
    private Log log;
    
    public LoggingProxy() {
    }

    /**
     * Main method for proxy server. See TestGenerationProxyCommandLineOptions for usage.
     */
    public static void main(String[] args)
        throws IOException {
        LoggingProxy proxy = new LoggingProxy();
        try {
            proxy.processCommandLine(args);
        } catch (Exception e) {
            proxy.getLog().error(e);
        }
    }

    /**
     * Process command line and run the server.
     * @param args
     */
    public void processCommandLine(String[] args) {
        // Process command line options.
        try {
            cmdOptions = new LoggingProxyCommandLineOptions(args);
        } catch (ParseException e) {
            getLog().error("Could not parse passed arguments, message:", e);
            return;
        }
        // Print help if there has no args.
        if (args.length == 0) {
            cmdOptions.printHelp();
            return;
        }
        if (cmdOptions.hasOption(HOST) && cmdOptions.hasOption(HOST_PORT)
                && cmdOptions.hasOption(CLIENT_PORT)) {
            int remotePort = -1;
            int localPort = -1;
            try {
                remotePort = Integer.parseInt(cmdOptions
                        .getOptionValue(HOST_PORT));
            } catch (NumberFormatException nfe) {
                getLog().error("Remote port should be in numeric form e.g. 80");
                return;
            }
            try {
                localPort = Integer.parseInt(cmdOptions
                        .getOptionValue(CLIENT_PORT));
            } catch (NumberFormatException nfe) {
                getLog().error("Local port should be in numeric form e.g. 8080");
                return;
            }
            String host = cmdOptions.getOptionValue(HOST);
            try {
                runServer(host, remotePort, localPort); // never returns
            } catch (IOException ioe) {
                getLog().error("Error running proxy", ioe);
                return;
            }
        }
        String apiKey = cmdOptions.getOptionValue(API_KEY);
        String host = cmdOptions.getOptionValue(HOST);
        String port = cmdOptions.getOptionValue(PORT);
        String apiversion = cmdOptions.getOptionValue(API_VERSION);
    }

    /**
     * Using a jetty server will probably have many advantages over the code below especially in terms of reducing
     * complexity to of code in threading and processing the http request.
     * @param host is host that run proxy
     * @param remotePort is remote port
     * @param localPort is local port
     */
    public void runJettyProxy(String host, int remotePort, int localPort) {
        /*
         * Server server = new Server(); Connector connector=new SelectChannelConnector(); connector.setPort(8888);
         * server.setConnectors(new Connector[]{connector}); ServletHandler handler=new ServletHandler();
         * server.setHandler(handler); //FilterHolder gzip =
         * handler.addFilterWithMapping("org.eclipse.jetty.servlet.GzipFilter"
         * ,"/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ASYNC)); //gzip.setAsyncSupported(true);
         * //gzip.setInitParameter("minGzipSize","256"); ServletHolder proxy =
         * handler.addServletWithMapping("org.eclipse.jetty.servlets.ProxyServlet","/"); proxy.setAsyncSupported(true);
         * server.start(); server.join();
         */
    }

    /**
     * runs a single-threaded proxy server on the specified local port. It never returns.
     * @param host is host that run proxy
     * @param remotePort is remote port
     * @param localPort is local port
     */
    public void runServer(String host, int remotePort, int localPort)
        throws IOException {
        // Print a start-up message
        getLog().info(
                "Starting proxy for " + host + ":" + remotePort + " on port "
                        + localPort);
        // And start running the server
        // Create a ServerSocket to listen for connections with
        ServerSocket ss = new ServerSocket(localPort);
        final byte[] request = new byte[1024];
        byte[] reply = new byte[4096];
        while (true) {
            Socket client = null, server = null;
            try {
                // Wait for a connection on the local port
                client = ss.accept();
                final InputStream streamFromClient = client.getInputStream();
                final OutputStream streamToClient = client.getOutputStream();
                // Make a connection to the real server.
                // If we cannot connect to the server, send an error to the
                // client, disconnect, and continue waiting for connections.
                try {
                    server = new Socket(host, remotePort);
                } catch (IOException e) {
                    PrintWriter out = new PrintWriter(streamToClient);
                    getLog().info(
                            "Proxy server cannot connect to " + host + ":"
                                    + remotePort + ":\n" + e + "\n");
                    out.flush();
                    client.close();
                    continue;
                }
                // Get server streams.
                final InputStream streamFromServer = server.getInputStream();
                final OutputStream streamToServer = server.getOutputStream();
                // a thread to read the client's requests and pass them
                // to the server. A separate thread for asynchronous.
                Thread t = new Thread() {
                    public void run() {
                        int bytesRead;
                        try {
                            while ((bytesRead = streamFromClient.read(request)) != -1) {
                                String stringRead = new String(request, 0,
                                        bytesRead);
                                handleRequest(stringRead);
                                streamToServer.write(request, 0, bytesRead);
                                streamToServer.flush();
                            }
                        } catch (IOException e) {
                        }
                        // the client closed the connection to us, so close our
                        // connection to the server.
                        try {
                            streamToServer.close();
                        } catch (IOException e) {
                        }
                    }
                };
                // Start the client-to-server request thread running
                t.start();
                // Read the server's responses
                // and pass them back to the client.
                int bytesRead;
                try {
                    while ((bytesRead = streamFromServer.read(reply)) != -1) {
                        String stringRead = new String(reply, 0, bytesRead);
                        handleResponse(stringRead);
                        streamToClient.write(reply, 0, bytesRead);
                        streamToClient.flush();
                    }
                } catch (IOException e) {
                }
                // The server closed its connection to us, so we close our
                // connection to our client.
                streamToClient.close();
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * The method is handle the request.
     * @param httpReq is the request the proxy gets
     */
    private void handleRequest(String httpReq) {
        getLog().info("Handling Request <<" + httpReq + ">>");
        String request = getRequestURI(httpReq);
        getLog().info("Request URI <<" + request + ">>");
        addTestScriptRequest(request);
    }

    /**
     * The method generates request of the test script.
     * @param req is the the proxy gets
     */
    public void addTestScriptRequest(String req) {
        Map<String, String> reqMap = getRequestParamsMap(req);
        if (!reqMap.isEmpty()) {
            String flowName = reqMap.get("flowName");
            Set<String> paramSet = reqMap.keySet();
            paramSet.remove("flowName");
            try {
                // Create file
                Writer out = getTestFileWriter();
                out.write("request(\"" + flowName + "\",[");
                Iterator it = paramSet.iterator();
                int i = 0;
                while (it.hasNext()) {
                    if (i != 0) {
                        out.write(",");
                    }
                    String paramName = it.next().toString();
                    out.write("\"" + paramName + "\":\""
                            + reqMap.get(paramName) + "\"");
                    i++;
                }
                out.write("])");
                // Close the output stream
                out.close();
            } catch (Exception e) {// Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
        } else {
            getLog().error("Error: this is a bad request!");
        }
    }

    /**
     * The method is get a buffer writer of the script file.
     * @return a writer
     * @throws Exception IOException 
     */
    protected Writer getTestFileWriter()
        throws Exception {
        String fileName = cmdOptions.getOptionValue(OUT_FILE);
        String startString = "";
        // Create file
        File newScriptFile = new File(getFileName());
        if (!newScriptFile.exists()) {
            newScriptFile.createNewFile();
        } else {
            startString = System.getProperty("line.separator");
        }
        FileWriter fstream = new FileWriter(newScriptFile, true);
        fstream.write(startString);
        return new BufferedWriter(fstream);
    }

    /**
     * The method is get the test script file name.
     * @return test script file name.
     */
    public String getFileName() {
        return cmdOptions.getOptionValue(OUT_FILE);
    }

    /**
     * This method can get the flow name and params from the requestUrl and save them into a map.
     * @param req the request we get from the proxy server.
     * @return a map that consist of flow name and params.
     */
    public Map<String, String> getRequestParamsMap(String req) {
        Map<String, String> reqMap = new HashMap<String, String>();
        String flowName = null;
        String params = null;
        if (req != null) {
            String[] tokens = req.split("/");
            if (tokens.length > 4) {
                String apiKey = tokens[2];
                String apiVersion = tokens[3];
                String flowCall = tokens[4];
                String[] flowCallTokens = flowCall.split("\\?");
                getLog().info(" flowCallTokens " + flowCall);
                if (flowCallTokens.length == 2) {
                    getLog().info(" ---- ");
                    flowName = flowCallTokens[0];
                    reqMap.put("flowName", flowName);
                    params = flowCallTokens[1];
                    getLog().info("Flow Name " + flowName + " params " + params);
                    String[] paramsString = params.split("&");
                    for (int i = 0; i < paramsString.length; i++) {
                        String[] paramMap = paramsString[i].split("=");
                        if (paramMap.length == 1) {
                            reqMap.put(paramMap[0], "");
                        } else if (paramMap.length == 2) {
                            reqMap.put(paramMap[0], paramMap[1]);
                        }
                    }
                }
            }
        }
        return reqMap;
    }

    /**
     * The method is get the request url.
     * @param req is the request
     * @return request url String
     */
    private String getRequestURI(String req) {
        // getLog().info(req);
        String[] lines = req.split("\n");
        String request = null;
        for (String line : lines) {
            if (line.startsWith("GET")) {
                String[] tokens = line.split(" ");
                if (tokens.length == 3) {
                    request = tokens[1];
                }
            }
        }
        return request;
    }
    
    /**
     * Adds an expected return to the test.
     * 
     * @param json - the expected json data
     */
    public void addExpect(String json) {
        String strValue = "";
        try {
            // Try to format a JSON string if possible.
            JSONObject jsonObj = new JSONObject(json);
            strValue = jsonObj.toString(STANDARD_INDENTATION);
        } catch (JSONException e) {
            // otherwise just use the raw string
            // strValue = json;
            try {
                JSONArray strValueArray = new JSONArray(json);
                strValue = strValueArray.toString(STANDARD_INDENTATION);
            } catch (Exception e2) {
                return;
            }
        }

        writeToFileBuffer("expect(\"\"\"" + strValue + "\"\"\")\n");
    }

    /**
     * The method is write the String to a buffer writer. 
     * @param strValue is the String write to FileBuffer
     */
    protected void writeToFileBuffer(String strValue) {
        try{
            Writer writer = getTestFileWriter();
            writer.write(strValue);
            writer.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * The method handles response.
     * @param resp is the response.
     */
    private void handleResponse(String resp) {
        getLog().info(resp);
        String[] resps = resp.split(NL);
        if(resps != null && resps.length >0){
            String responseJson = resps[resps.length-1];
            addExpect(responseJson);
        }
    }

    /**
     * Get the logger for this class.
     */
    public Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(this.getClass());
        }
        return this.log;
    }
}
