package org.amplafi.flow.shell;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.util.Date;

public class Shell {
    private static final String NO_CONSOLE = "Error: Console unavailable";
    private static final String HELP_INFO = 
        "\n" +
        "This shell is used to help the administrator access and manage\n" +
        "the farreaches wireservice\n" +
        "Params: \n" +
        "  -Dfarreaches.api.key=<key>      The user api key. This parameter is mandatory\n" +
        "  -Dfarreaches.host=<key>         The wireservice host, the default value is http://sandbox.farreach.es\n" +
        "  -Dfarreaches.port=<port>        The wireservice port, the default value is 8080\n" +
        "  -Dfarreaches.apiv=<apiv1, suv1> The wireservice api version, the default value is apiv1\n" +
        "  -Dfarreaches.script=<file>      The script to run\n" +
        "\n";
    
    private static final String UNKNOWN_COMMAND = "Unknown command [%1$s]%n";

    private static final String TIME_FORMAT = "%1$tH:%1$tM:%1$tS";
    private static final String PROMPT = TIME_FORMAT + " $ ";

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        if (console != null) {
            String apiKey = System.getProperty("farreaches.api.key");
            String host = System.getProperty("farreaches.host", "http://sandbox.farreach.es") ;
            String port =  System.getProperty("farreaches.port", "8080") ;
            String apiVersion = System.getProperty("farreaches.apiv", "apiv1") ;
            String scriptFile = System.getProperty("farreaches.script") ;
            console.printf(HELP_INFO);
            if(apiKey == null) {
                System.exit(0) ;
            }
            ShellContext context = new ShellContext() ;
            context.setApiKey(apiKey) ;
            context.setHost(host) ;
            context.setPort(port) ;
            context.setApiVersion(apiVersion) ;
            if(scriptFile != null) {
                execScript(console, context, scriptFile);
            } else {
                execCommandLoop(console, context);
            }
        } else {
            throw new RuntimeException(NO_CONSOLE);
        }
    }
    
    private static void execCommandLoop(final Console console, ShellContext context) {
        while (true) {
            String commandLine = console.readLine(PROMPT, new Date());
            execCommandLine(console, context, commandLine) ;
        }
    }

    private static void execScript(final Console console, ShellContext context, String scriptFile) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(scriptFile)) ;
        String commandLine = null ;
        while((commandLine = reader.readLine()) != null) {
            execCommandLine(console, context, commandLine) ;
        }
    }

    private static void execCommandLine(final Console console, ShellContext context, String commandLine) {
        commandLine = commandLine.trim() ;
        if(commandLine.length() == 0) {
            return ;
        }
        int firstSpaceIndex = commandLine.indexOf(' ') ;
        if(firstSpaceIndex < 0) {
            firstSpaceIndex = commandLine.length() ;
        }
        String commandName = commandLine.substring(0, firstSpaceIndex) ;
        commandName = commandName.toUpperCase() ;
        String args = commandLine.substring(firstSpaceIndex) ;
        try {
            final Command cmd = Enum.valueOf(Command.class, commandName);
            cmd.exec(console, context, args) ;
        } catch (IllegalArgumentException e) {
            e.printStackTrace() ;
            console.printf(UNKNOWN_COMMAND, commandName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}