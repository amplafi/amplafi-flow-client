package org.amplafi.flow.shell;

import static org.amplafi.flow.utils.CommandLineClientOptions.API_KEY;
import static org.amplafi.flow.utils.CommandLineClientOptions.API_VERSION;
import static org.amplafi.flow.utils.CommandLineClientOptions.HELP;
import static org.amplafi.flow.utils.CommandLineClientOptions.HOST;
import static org.amplafi.flow.utils.CommandLineClientOptions.PORT;

import java.io.Console;
import java.util.Date;
import java.util.Scanner;

import org.amplafi.flow.utils.CommandLineClientOptions;
import org.apache.commons.cli.ParseException;

public class Shell {
    private static final String NO_CONSOLE = "Error: Console unavailable";
    private static final String GREETINGS = "Welcome to the System. Please login.%n";
    
    private static final String UNKNOWN_COMMAND = "Unknown command [%1$s]%n";

    private static final String TIME_FORMAT = "%1$tH:%1$tM:%1$tS";
    private static final String PROMPT = TIME_FORMAT + " $ ";

    public static void main(String[] args) {
        Console console = System.console();
        if (console != null) {
            if(args == null || args.length < 4) {
                args = new String[] {
                    "-key", "ampcb_0db23c81875e3245a77cd617767a476f01d8214de9c6e00923d5357ea02dfae7",
                    "-host", "http://sandbox.farreach.es",
                    "-port", "8080",
                    "-apiv", "apiv1",
                };
            }
            CommandLineClientOptions cmdOptions = null;

            try {
                cmdOptions = new CommandLineClientOptions(args);
            } catch (ParseException e) {
                System.err.println("Could not parse passed arguments, message:");
                e.printStackTrace();
                System.exit(1);
            }

            if (cmdOptions.hasOption(HELP)) {
                cmdOptions.printHelp();

                System.exit(0);
            }

            String apiKey = cmdOptions.getOptionValue(API_KEY);
            String host = cmdOptions.getOptionValue(HOST) ;
            String port = cmdOptions.getOptionValue(PORT) ;
            String apiVersion = cmdOptions.getOptionValue(API_VERSION) ;

            ShellContext context = new ShellContext() ;
            context.setApiKey(apiKey) ;
            context.setHost(host) ;
            context.setPort(port) ;
            context.setApiVersion(apiVersion) ;
            console.printf(GREETINGS);
            execCommandLoop(console, context);
        } else {
            throw new RuntimeException(NO_CONSOLE);
        }
    }

    private static void execCommandLoop(final Console console, ShellContext context) {
        while (true) {
            String commandLine = console.readLine(PROMPT, new Date());
            Scanner scanner = new Scanner(commandLine);

            if (scanner.hasNext()) {
                final String commandName = scanner.next().toUpperCase();

                try {
                    final Command cmd = Enum.valueOf(Command.class, commandName);
                    String param = scanner.hasNext() ? scanner.next() : null;
                    cmd.exec(console, context, new String[]{ param }) ;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace() ;
                    console.printf(UNKNOWN_COMMAND, commandName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            scanner.close();
        }
    }
}