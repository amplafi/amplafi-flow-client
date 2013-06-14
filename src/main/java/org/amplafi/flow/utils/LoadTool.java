package org.amplafi.flow.utils;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.DURATION;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.FREQUENCY;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.HOST;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.HOST_PORT;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.KEY;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.NUM_THREADS;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.REPORT;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.SCRIPT;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.TEST_PLAN;
import static org.amplafi.flow.utils.LoadToolCommandLineOptions.VERBOSE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.amplafi.dsl.ScriptRunner;
import org.amplafi.flow.definitions.FarReachesServiceInfo;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Tool for load testing the wire server.
 * @author paul
 */
public class LoadTool extends UtilParent{
    private static final int DEFAULT_TEST_DURATION_SECS = 45;
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
    private String reportFile = "";

    private boolean running = true;
    private boolean reported = false;

    //This file will be created when test is running ,and if you delete it ,the test will be stop.
    //And the same time you will see the report of the load test.
    public final String RUNNING_FILE = "LOAD_TOOL_RUNNING";

    private int frequency;
    private String[] commandLine;

    /**
     * Process command line and run the server.
     * @param args
     */
    public void processCommandLine(String[] args) {
        commandLine = args;
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
            int duration = DEFAULT_TEST_DURATION_SECS;
            frequency = -1;

            try {
                remotePort = Integer.parseInt(cmdOptions.getOptionValue(HOST_PORT));
            } catch (NumberFormatException nfe) {
                getLog().error("Remote port should be in numeric form e.g. 80");
                return;
            }

            String host = cmdOptions.getOptionValue(HOST);
            reportFile = cmdOptions.getOptionValue(REPORT);
            String scriptName = cmdOptions.getOptionValue(SCRIPT);

            List<Map<String,Integer>> listMap = new ArrayList<Map<String,Integer>>();

            hasPlanTest = cmdOptions.hasOption(TEST_PLAN);

            if(hasPlanTest){

                listMap = getParamsInTestPlan(cmdOptions.getOptionValue(TEST_PLAN));

                if (cmdOptions.hasOption(DURATION)){
                    duration = Integer.parseInt(cmdOptions.getOptionValue(DURATION));
                }

            }else{
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
            }

            // Register shutdown handler.
            Runtime.getRuntime().addShutdownHook(new Thread(){
                public void run() {
                    shutDown();
                    // TODO output above to file or speadsheet
                    // TODO output time variant call data to speadsheet
                }
            });

            String key = cmdOptions.getOptionValue(KEY);

            final FarReachesServiceInfo service = new FarReachesServiceInfo(host, ""+remotePort, "apiv1");

            if (key == null) {
                /* Get the api key automatically*/
                key = getPermApiKey(service,null, true);
            }

            try {

                if(hasPlanTest){
                    if ( listMap != null ){
                        int planNum = listMap.get(listMap.size()-1).get("planNum");

                        for(int i=0;i<listMap.size()-1;i++){
                            running = true;
                            reported = false;

                            numThreads = (Integer)listMap.get(i).get("numThreads");
                            frequency = (Integer)listMap.get(i).get("frequency");
                            runLoadTest(service, key, scriptName,  numThreads, frequency, cmdOptions.hasOption(VERBOSE) ); // never returns
                            //after a given time should stop the test.one way is delete the running file,another is control key press "ctrl+c"?

                            startTestEndTimer(duration);

                            while(running){

                                try{
                                    Thread.sleep(1000);
                                }catch(InterruptedException e){
                                    //
                                }
                            }

                            if (!running){
                                shutDown();
                            }


                            //automatically generate csv formatting testing report.there maybe has a report file,that once a test end will add a new report record.
                        }
                    }
                }else{
                    runLoadTest(service, key, scriptName,  numThreads, frequency, cmdOptions.hasOption(VERBOSE) ); // never returns

                     while(running){
                        if(!isFileExists(RUNNING_FILE)){
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

            } catch (IOException ioe) {
                ioe.printStackTrace();
                getLog().error("Error running proxy", ioe);

                return;
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
             Set errSet = new HashSet();
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

               errSet.addAll(rep.errors);
            }
            getLog().info(THICK_DIVIDER);

            double totalCallsPerSecond = 0;
            if (totalTime > 0){
                totalCallsPerSecond = (totalCalls*1000/totalTime);
            }

            getLog().info("Total calls in all threads=" + totalCalls + "  " + (totalCallsPerSecond) +  " calls per second.  total errors " + totalErrors );
            getLog().info(THICK_DIVIDER);


            getLog().info("Error Report ");
            getLog().info(THICK_DIVIDER);

            for (Thread t : threads){
               ThreadReport rep = threadReports.get(t);
                getLog().info(rep.errors);
            }

            if(reportFile != ""){
                try{
                    writeCsvReport(frequency,threadNum,totalTime/1000,totalCalls,totalCallsPerSecond,totalErrors,errSet,commandLine);
                }catch(Exception ie){
                    getLog().error("Error",ie);
                }
            }
            threads.clear();
            threadReports.clear();
        }
     }

    /**
     * This method is generate a csv report when running test automatically with test plan.
     */
     private void writeCsvReport(int frequency,
                                int threadNum,
                                double totalTime,
                                double totalCalls,
                                double totalCallsPerSecond,
                                double totalErrors,
                                Set errorSet,
                                String[] args) throws IOException{
        Object err = "";
        if(!errorSet.isEmpty()){
            Iterator it = errorSet.iterator();
            while(it.hasNext()){
                err = it.next();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append((frequency*(threadNum-1)) + ",");
        sb.append(frequency + ",");
        sb.append((threadNum-1) + ",");
        sb.append(totalCalls + ",");
        sb.append(totalCallsPerSecond + ",");
        sb.append(totalErrors + ",");
        if (totalCalls > 0){
            sb.append((double)((totalCalls-totalErrors)/totalCalls)*100.0000 + "%,");
        } else {
            sb.append("0%,");
        }
        sb.append(totalTime + ",");
        sb.append(err);

        if(!isFileExists(reportFile)){
            createFile(reportFile);

            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(reportFile), true));

            StringBuffer commandLine = new StringBuffer("'ant LoadTest -Dargs=\"");

            for (String arg : args){
                commandLine.append(arg);
                commandLine.append(" ");
            }
            commandLine.append("\"'");

            bw.write("'" + getSystemInfo() + "'");
            bw.newLine();
            bw.write(commandLine.toString());
            bw.newLine();
            bw.newLine();
            bw.write("total attempted calls per second,frequency,numThreads,Total calls in all threads,calls per second,total errors,% of Successful Calls,Duration of test,remarks");
            bw.newLine();
            bw.close();
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(reportFile), true));
            bw.write(sb.toString());
            bw.newLine();
            bw.close();
        } catch (Exception ie) {
            getLog().error("Error",ie);
        }

     }

    private String getSystemInfo(){
        String systemInfo = "Client details. applies to server if running against localhost. os architecture " + System.getProperty("os.arch") + ". os name: "
                                    + System.getProperty("os.name") + ". os version: "
                                        + System.getProperty("os.version") + ". num processors     "
                                             +  ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + ". System load average for last minute of test "
                                                +  ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() + ". Free Memory " + Runtime.getRuntime().freeMemory();;
        return systemInfo;
    }

    private void stopTest(){
        running = false;
    }

    /**
     * Create a file.
     */
    private void createFile(String fileName){
        try{

            File file = new File(fileName);

            if(!file.exists()){
                file.createNewFile();
            }
        }catch(IOException ie){
            getLog().error("Error",ie);
        }
    }

    /**
     * Validate if the file used for decidding the thread will run or stop is exists
     */
    private boolean isFileExists(String fileName){
        System.out.println("############# fileName =" + fileName);
        File file = new File(fileName);
        if(file.exists()){
            return true;
        }else{
            return false;
        }

    }

    /**
     * Delete the file after a given time.
     * @param fileName is the file to delete.
     * @param time :after time in seconds after the test will stop
     */
    public void startTestEndTimer(int time){
        //Long currentTime = System.currentTimeMillis();
        //Long deleteTime = currentTime + time*60*1000;
        try{
            long delay = time*1000;
            Timer timer = new Timer(true);

            timer.schedule(new TimerTask(){
                  public void run(){
                        stopTest();
                  }}, delay);

        }catch(Exception ie){
            ie.printStackTrace();
            getLog().error("Error",ie);
        }

    }

    // Never accessed by multiple threads
    private List<Thread> threads = new ArrayList<Thread>();
    // Never accessed by multiple threads
    private Map<Thread,ThreadReport> threadReports = new LinkedHashMap<Thread, LoadTool.ThreadReport>();

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
        getLog().info("Running LoadTest with host= " + service.getHost()
                        + " host port= " + service.getPort()  + " script= " + scriptName
                        + " numThreads=" + numThreads + " frequency=" + frequency);
        getLog().info("Press Ctrl+C to stop or if running through 'ant' delete file called " + RUNNING_FILE);

        createFile(RUNNING_FILE);

        for (int i=0; i<numThreads ; i++ ){
            final ThreadReport report = new ThreadReport();
            Thread thread = new Thread(new Runnable(){
                public void run() {
                    try {
                        // don't include the first run because this includes
                        // constructing gropvy runtime.
                        final ScriptRunner scriptRunner = new ScriptRunner(service,key);
                        scriptRunner.setVerbose(verbose);

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
                                        Thread.sleep(pause);
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
                        t.printStackTrace();
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

    private boolean hasPlanTest = true;

    /**
     * This method is read the params in the test plan file.Now is just handle frequency and numThreads.
     */
    public List<Map<String,Integer>> getParamsInTestPlan(String planFileName){
        List<Map<String,Integer>> listMap = new ArrayList<Map<String,Integer>>();

        if(!isFileExists(planFileName)){
            getLog().error("ERROR: Test Plan File" + planFileName + " does not exist.");
            return listMap;
        }


        String paramName1 = "";
        String paramName2 = "";
        Integer lineNum = 1;
        String readoneline = "";
        try{
           FileReader fr = new FileReader(planFileName);
           BufferedReader br = new BufferedReader(fr);
           while((readoneline = br.readLine()) != null){
                String[] param = readoneline.split(",");
                System.err.println("read on line " + readoneline );
                if(param != null && lineNum == 1){
                    paramName1 = param[0];
                    paramName2 = param[1];
                }
                if(lineNum >1){
                    Map<String,Integer> map = new HashMap<String,Integer>();
                    map.put(paramName1,Integer.parseInt(param[0]));
                    map.put(paramName2,Integer.parseInt(param[1]));
                    listMap.add(map);
                }
                lineNum++;
            }

           if(lineNum>1){
                Map<String,Integer> map = new HashMap<String,Integer>();
                map.put("planNum",lineNum-2);
                listMap.add(map);
            }

            br.close();
            fr.close();

        }catch(IOException ie){
            getLog().error("Error",ie);
        }

        return listMap;
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
