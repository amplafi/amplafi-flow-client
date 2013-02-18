package org.amplafi.flow.shell;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public enum Command {
    EXIT(new Action() {
        public void exec(Console c, ShellContext context, String args) {
            c.printf("Bye%n");
            System.exit(0);
        }
        
        public String getHelpInstruction() {
            return "Exit this shell." ;
        }
    }),
    
    HELP(new Action() {
        public void exec(Console c, ShellContext context, String args) throws Exception {
            c.printf("\n\n");
            c.printf("Available commands: \n\n");
            String template = "%-10s%-70s%n" ;
            for(Command selCommand : Command.values()) {
                c.printf(template, selCommand.toString(), selCommand.action.getHelpInstruction());
            }
            c.printf("\n\n");
        }
        
        public String getHelpInstruction() {
            return "Print this help instructions." ;
        }
    }),
    
    ENV(new Action() {
        public void exec(Console c, ShellContext context, String args) throws Exception {
            c.printf("HOST:  %1s%n", context.getHost());
            c.printf("PORT:  %1s%n", context.getPort());
            c.printf("API VERSION:  %1s%n", context.getApiVersion());
            c.printf("API Key:  %1s%n", context.getApiKey());
        }
        
        public String getHelpInstruction() {
            return "Print the shell environment such: host, port, api version, api key..." ;
        }
    }),
    
    SET(new Action() {
        public void exec(Console c, ShellContext context, String args) throws Exception {
            Map<String, String> options = Command.parseOptions(args);
            c.printf(
              "\n" +
              "Usage: \n" +
              "  set --api.key=<key> --host=<host> --port=<port> --apiv=<apiv1, suv1>" +
              "\n"
            ) ;
            String apiKey = options.get("api.key") ;
            if(apiKey != null) {
                context.setApiKey(apiKey); 
            }
            String host = options.get("host") ;
            if(host != null) {
                context.setHost(host); 
            }
            String port = options.get("port") ;
            if(port != null) {
                context.setPort(port); 
            }
            String apiv = options.get("apiv") ;
            if(apiv != null) {
                context.setApiVersion(apiv); 
            }
        }
        
        public String getHelpInstruction() {
            return "Set the shell environment such: host, port, api version, api key..." ;
        }
    }),
    
    PRINTLN(new Action() {
        public void exec(Console c, ShellContext context, String args) throws Exception {
            c.printf(args.trim()) ;
            c.printf("\n") ;
        }
        
        public String getHelpInstruction() {
            return "Print a message" ;
        }
    }),
    
    LIST(new ListFlow()),
    DESC(new DescFlow()),
    FLOW(new ExecFlow()),
    GSCRIPT(new GScript()),
    TUTORIAL(new TutorialFlow());

    private Action action;

    private Command(Action a) {
        this.action = a;
    }

    public void exec(final Console c, ShellContext context, String args) throws Exception {
        action.exec(c, context, args);
    }
    
    /**
     * This method parse the string with option format: --param1=value1 --params2=value2 --param3
     * @param options
     * @return
     */
    static public Map<String, String> parseOptions(String options) {
        Map<String, String> optionMap = new HashMap<String, String>() ;
        String[] option = options.split("--") ;
        for(String selOption : option) {
            selOption = selOption.trim() ;
            if(selOption.indexOf('=') > 0) {
                String[] nameValuePair = selOption.split("=", 2) ;
                optionMap.put(nameValuePair[0], nameValuePair[1]) ;
            } else {
                optionMap.put(selOption, selOption) ;
            }
        }
        return optionMap ;
    }
}