package org.amplafi.flow.utils;
import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.amplafi.dsl.ScriptRunner;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.*;
import java.util.EnumSet;

/**
 * Tool for load testing the wire server.
 * @author paul
 */
public class LoadTool extends UtilParent{
    /**
     * Main method for proxy server.
     * See TestGenerationProxyCommandLineOptions for usage.
     */
    public static void main(String[] args) throws IOException {
        LoadTool proxy = new LoadTool();

        try {
            proxy.processCommandLine(args);

        } catch (Exception e) {
            proxy.getLog().error(e);
        }
    }

    private Log log;

    private static final String THICK_DIVIDER =
    "*********************************************************************************";

    /**
     * Constructor for LoadTool
     */
    public LoadTool(){
    }

    /** File to write the test report to. If null write to screen. */
    private String reportFile;

    private boolean running = true;
    private boolean reported = false;

    //This file will be created when test is running ,and if you delete it ,the test will be stop.
    //And the same time you will see the report of the load test.
    private String runningFile = "LOAD_TOOL_RUNNING";

    /**
     * Process command line and run the server.
     * @param args
     */
    public void processCommandLine(String[] args) {
        // Process command line options.
        LoadToolCommandLineOptions cmdOptions = null;
        try {
                cmdOptions = new LoadToolCommandLineOptions(args);
        } catch (ParseException e) {
                getLog().error("Could not parse passed arguments, message:", e);
                return;
        }
        // Print help if there has no args.
        if (args.length == 0) {
                cmdOptions.printHelp();
                return;
        }

        if (!cmdOptions.hasOption(HOST)){
            getLog().error("You must specify the host.");
        }

        if (!cmdOptions.hasOption(HOST_PORT) ){
            getLog().error("You must specify the host post.");
        }

        if (!cmdOptions.hasOption(SCRIPT)  ){
            getLog().error("You must specify the script to run.");
        }

        if (cmdOptions.hasOption(HOST) && cmdOptions.hasOption(HOST_PORT) && cmdOptions.hasOption(SCRIPT)  ) {
            int remotePort = -1;
            int numThreads = 1;
            int frequency = -1;

            try {
                remotePort = Integer.parseInt(cmdOptions.getOptionValue(HOST_PORT));
            } catch (NumberFormatException nfe) {
                getLog().error("Remote port should be in numeric form e.g. 80");
                return;
            }

            String host = cmdOptions.getOptionValue(HOST);
            String reportFile = cmdOptions.getOptionValue(REPORT);
            String scriptName = cmdOptions.getOptionValue(SCRIPT);

            try {
                if (cmdOptions.hasOption(NUM_THREADS)){
                    numThreads = Integer.parseInt(cmdOptions.getOptionValue(NUM_THREADS));
                }
            } catch (NumberFormatException nfe) {
                getLog().error("numThreads should be in numeric form e.g. 10 , defaulting to 1.");
            }
            try {
                if (cmdOptions.hasOption(FREQUENCY)){
                    frequency = Integer.parseInt(cmdOptions.getOptionValue(FREQUENCY));
                }
            } catch (NumberFormatException nfe) {
                getLog().error("frequency should be in numeric form e.g. 10 , defaulting to -1 = max possible.");
            }

            // Register shutdown handler.
            Runtime.getRuntime().addShutdownHook(new Thread(){
                public void run() {
                    shutDown();
                    // TODO output above to file or speadsheet
                    // TODO output time variant call data to speadsheet
                }
            });

            String key = cmdOptions.getOptionValue(KEY);;

            final FarReachesServiceInfo service = new FarReachesServiceInfo(host, ""+remotePort, "apiv1");

            if (key == null) {
                /* Get the api key automatically*/
                key = getPermApiKey(service,null, true);
            }

            try {
                runLoadTest(service, key, scriptName,  numThreads, frequency, cmdOptions.hasOption(VERBOSE) ); // never returns
            } catch (IOException ioe) {
                getLog().error("Error running proxy", ioe);
                return;
            }

            while(running){
                if(!isFileExists()){
                    shutDown();
                    running = false;
                }
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    //
                }
            }
        }
    }

    /**
     * The method is handle threads and put out report of each thread.
     */
     private synchronized void shutDown(){
        if (!reported){
            reported = true;
            getLog().info("Generating Report. Please Wait...");

            // signal threads to stop
            running = false;

            // Wait for all threads to stop
            for (Thread t : threads){
                try {
                    t.join();
                } catch (InterruptedException ie){
                    getLog().error("Error",ie);
                }
            }

            // Loop over the ThreadReports
            int threadNum = 1;
            double totalTime = 0;
            double totalCalls = 0;
            double totalErrors = 0;
            getLog().info(THICK_DIVIDER);
            for (Thread t : threads){
               ThreadReport rep = threadReports.get(t);
               totalTime = rep.endTime - rep.startTime;
               double callsPerSecond = 0;
                if (totalTime > 0){
                    callsPerSecond = (rep.callCount*1000/totalTime);
                }

               getLog().info("threadNum" + threadNum + ": calls " + rep.callCount + " in " + (totalTime/1000) + "s. average = " + (callsPerSecond) + " calls per second. Error count " +  rep.errorCount);
               totalCalls += rep.callCount;
               totalErrors += rep.errorCount;
               threadNum++;
            }
            getLog().info(THICK_DIVIDER);

            double totalCallsPerSecond = 0;
            if (totalTime > 0){
                totalCallsPerSecond = (totalCalls*1000/totalTime);
            }

            getLog().info("Total calls in all threads=" + totalCalls + "  " + (totalCallsPerSecond) +  " calls per second.  total errors " + totalErrors );
            getLog().info(THICK_DIVIDER);


            getLog().info("Error Report "  );
            getLog().info(THICK_DIVIDER);
            for (Thread t : threads){
               ThreadReport rep = threadReports.get(t);
                getLog().info(rep.errors);
            }
        }
     }


    /**
     * Create a file.
     */
    private void createFile(){
        try{

            File file = new File(runningFile);

            if(!file.exists()){
                file.createNewFile();
            }
        }catch(IOException e){
            //
        }
    }

    /**
     * Validate if the file used for decidding the thread will run or stop is exists
     */
    private boolean isFileExists(){
        File file = new File(runningFile);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }

    // Never accessed by multiple threads
    private List<Thread> threads = new ArrayList<Thread>();
    // Never accessed by multiple threads
    private Map<Thread,ThreadReport> threadReports = new LinkedHashMap();

    /**
     * runs a single-threaded proxy server on
     * the specified local port. It never returns.
     */
    public void runLoadTest(final FarReachesServiceInfo service,
                            final String key,
                            final String scriptName,
                            final int numThreads,
                            final int frequency,
                            final boolean verbose)
                            throws IOException {

        getLog().info("Running LoadTest with host="
                        + " host port=" +  " script=" + scriptName
                        + " numThreads=" + numThreads + " frequency=" + frequency);
        getLog().info("Press Ctrl+C to stop");

        createFile();
        final ScriptRunner scriptRunner = new ScriptRunner(service,key);
        scriptRunner.setVerbose(verbose);

        for (int i=0; i<numThreads ; i++ ){
            final ThreadReport report = new ThreadReport();
            Thread thread = new Thread(new Runnable(){
                public void run() {

                    try {
                        // don't include the first run because this includes
                        // constructing gropvy runtime.
                        scriptRunner.loadAndRunOneScript(scriptName);
                        report.startTime = System.currentTimeMillis();
                      while (running){
                            try {
                                report.callCount++;
                                long startTime = System.currentTimeMillis();
                                scriptRunner.reRunLastScript();
                                long endTime = System.currentTimeMillis();
                                long duration = (endTime - startTime);
                                if (frequency != -1 ){
                                    int requiredDurationMS = 1000/frequency;
                                    if (duration < requiredDurationMS){
                                        long pause = requiredDurationMS - duration;
                                        Thread.currentThread().sleep(pause);
                                    }
                                }

                                report.callTimes.add(duration);
                            } catch (Throwable t){
                                report.errors.add(t);
                                report.errorCount++;
                            }
                        }
                        report.endTime = System.currentTimeMillis();
                    } catch (Throwable t){
                        getLog().error("Error on first run",t);
                    }
                }// End run
            });
            threads.add(thread);
            threadReports.put(thread, report);
        }
        for (Thread t : threads){
            t.start();
        }
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

    /**
     *  This class holds the load test for a single thread.
     */
    private class ThreadReport {
        int callCount = 0;
        int errorCount = 0;
        List<Throwable> errors = new ArrayList<Throwable>();
        long startTime = 0;
        long endTime = 0;
        List<Long> callTimes = new ArrayList<Long>();
    }


}
