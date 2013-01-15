package org.amplafi.flow.shell;

import java.io.Console;

public enum Command {
    EXIT(new Action() {
        public void exec(Console c, ShellContext context, String[] params) {
            c.printf("Bye%n");
            System.exit(0);
        }
    }),
    
    HELP(new Action() {
        public void exec(Console c, ShellContext context, String[] params) throws Exception {
            c.printf("Help instructions%n");
        }
    }),
    
    ENV(new Action() {
        public void exec(Console c, ShellContext context, String[] params) throws Exception {
            c.printf("HOST:  %1s%n", context.getHost());
            c.printf("PORT:  %1s%n", context.getPort());
            c.printf("API VERSION:  %1s%n", context.getApiVersion());
            c.printf("API Key:  %1s%n", context.getApiKey());
        }
    }),
    
    LIST(new ListFlow());

    private Action action;

    private Command(Action a) {
        this.action = a;
    }

    public void exec(final Console c, ShellContext context, final String[] params) throws Exception {
        action.exec(c, context, params);
    }
}