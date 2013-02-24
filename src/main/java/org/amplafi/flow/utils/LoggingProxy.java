package org.amplafi.flow.utils;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.amplafi.flow.utils.LoggingProxyCommandLineOptions.*;
import java.util.EnumSet;

/*import javax.servlet.DispatcherType;
import org.eclipse.jetty.servlets.ProxyServlet.Transparent;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;*/

/**
 * Heavily based on this http://www.java2s.com/Code/Java/Network-Protocol/Asimpleproxyserver.htm
 * Runs a proxy between the plugin and the wire-server. Detects requests and resposes and out puts a single
 * integration style test in the test DSL format.
 * @author paul
 */
public class LoggingProxy {
    /**
     * Main method for proxy server.
     * See TestGenerationProxyCommandLineOptions for usage.
     */
    public static void main(String[] args) throws IOException {
        LoggingProxy proxy = new LoggingProxy();

        try {
            proxy.processCommandLine(args);

        } catch (Exception e) {
            proxy.getLog().error(e);
        }
    }

    private Log log;

    public LoggingProxy(){
    }

    /**
     * Process command line and run the server.
     * @param args
     */
    public void processCommandLine(String[] args) {
        // Process command line options.
        LoggingProxyCommandLineOptions cmdOptions = null;
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

        if (cmdOptions.hasOption(HOST) && cmdOptions.hasOption(HOST_PORT) && cmdOptions.hasOption(CLIENT_PORT)) {

            int remotePort = -1;
            int localPort = -1;
            try {
                remotePort = Integer.parseInt(cmdOptions.getOptionValue(HOST_PORT));
            } catch (NumberFormatException nfe) {
                getLog().error("Remote port should be in numeric form e.g. 80");
                return;
            }

            try {
                localPort = Integer.parseInt(cmdOptions.getOptionValue(CLIENT_PORT));
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
    }


    /**
     * Using a jetty server will probably have many advantages over the code below
     * especially in terms of reducing complexity to of code in threading and processing
     * the http request.
     */
    public void runJettyProxy(String host, int remotePort, int localPort){
   /*     Server server = new Server();
        Connector connector=new SelectChannelConnector();
        connector.setPort(8888);
        server.setConnectors(new Connector[]{connector});
        
        ServletHandler handler=new ServletHandler();
        server.setHandler(handler);
        
        //FilterHolder gzip = handler.addFilterWithMapping("org.eclipse.jetty.servlet.GzipFilter","/*",EnumSet.of(DispatcherType.REQUEST,DispatcherType.ASYNC));
        //gzip.setAsyncSupported(true);
        //gzip.setInitParameter("minGzipSize","256");
        ServletHolder proxy = handler.addServletWithMapping("org.eclipse.jetty.servlets.ProxyServlet","/");
        proxy.setAsyncSupported(true);
        
        server.start();
        server.join();   */
    }

    /**
     * runs a single-threaded proxy server on
     * the specified local port. It never returns.
     */
    public void runServer(String host, int remoteport, int localport)
            throws IOException {

        // Print a start-up message
        getLog().info("Starting proxy for " + host + ":" + remoteport
                + " on port " + localport);
        // And start running the server

        // Create a ServerSocket to listen for connections with
        ServerSocket ss = new ServerSocket(localport);

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
                    server = new Socket(host, remoteport);
                } catch (IOException e) {
                    PrintWriter out = new PrintWriter(streamToClient);
                    getLog().info("Proxy server cannot connect to " + host + ":"
                            + remoteport + ":\n" + e + "\n");
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
                                String stringRead = new String(request,0,bytesRead);
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
                        String stringRead = new String(reply,0,bytesRead);
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

    private void handleRequest(String httpReq) {
        getLog().info("Handling Request <<" + httpReq + ">>");
        String request = getRequestURI(httpReq);
        getLog().info("Request URI <<" + request + ">>");
        addTestScriptRequest(request);


    }

    /**
     * request uri is of the form /c/ampcb_e1446aa0e3e46427b591fa044c5f51c57989e393b66269140af68709e1da228e/apiv1/CategoriesList?configuration#bogusData&deleteCategories#bogusData&originalDeleteCategories#bogusData&defaultCategory#bogusData&fsRenderResult#json
     */
    private void addTestScriptRequest(String req) {
        String flowName = null;
        String params = null;

        if (req != null){
            String[] tokens = req.split("/");

            if (tokens.length > 4){
                String apiKey = tokens[2];
                String apiVersion = tokens[3];
                String flowCall = tokens[4];

                String[] flowCallTokens = flowCall.split("\\?");

getLog().info(" flowCallTokens " + flowCall );
                if (flowCallTokens.length == 2){
getLog().info(" ---- " );
                    flowName = flowCallTokens[0];
                    params = flowCallTokens[1];
                    getLog().info("Flow Name " + flowName + " params " + params);
                }


            }
        }

    }


    private String getRequestURI(String req) {
        //getLog().info(req);
        String[] lines = req.split("\n");


        String request = null;

        for(String line : lines) {
            if (line.startsWith("GET")) {
                String[] tokens = line.split(" ");
                if (tokens.length == 3) {
                    request = tokens[1];

                }
            }
        }

        return request;

    }

    private void handleResponse(String resp) {
        getLog().info(resp);
    }

    /**
     * Get the logger for this class.
     */
    public Log getLog() {
        if ( this.log == null ) {
            this.log = LogFactory.getLog(this.getClass());
        }
        return this.log;
    }
}
