package org.amplafi.flow.shell;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.util.Date;

public class Shell {
    private static final String NO_CONSOLE = "Error: Console unavailable";
    private static final String UNKNOWN_COMMAND = "Unknown command [%1$s]%n";

    private static final String TIME_FORMAT = "%1$tH:%1$tM:%1$tS";
    private static final String PROMPT = TIME_FORMAT + " $ ";

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        
        if (console != null) {
            ShellCommandParser commandParser = new ShellCommandParser(args) ;
            String apiKey = commandParser.getOption(ShellCommandParser.API_KEY, null);
            String host = commandParser.getOption(ShellCommandParser.HOST, "http://sandbox.farreach.es"); 
            String port =  commandParser.getOption(ShellCommandParser.PORT, "8080");
            String apiVersion = commandParser.getOption(ShellCommandParser.API_VERSION, "apiv1"); ;
            String scriptFile = commandParser.getOption(ShellCommandParser.SCRIPT, null);
            commandParser.printHelp(); 
            if(apiKey == null) {
                console.printf("\n\nERROR: The api key parameter is mandatory\n\n") ;
                System.exit(0) ;
            }
            ShellContext context = new ShellContext() ;
            context.setApiKey(apiKey) ;
            context.setHost(host) ;
            context.setPort(port) ;
            context.setApiVersion(apiVersion) ;
            if(scriptFile != null) {
                execScript(console, context, scriptFile);
            } 
            execCommandLoop(console, context);
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
            if(!commandLine.startsWith("#")) {
                execCommandLine(console, context, commandLine) ;
            }
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