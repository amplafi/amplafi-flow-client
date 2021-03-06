package org.amplafi.flow.ui;

import groovy.lang.Binding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amplafi.dsl.BindingFactory;
import org.amplafi.flow.ui.command.DescribeApiOrFlowBuilder;
import org.amplafi.flow.ui.command.ExitBuilder;
import org.amplafi.flow.ui.command.HelpBuilder;
import org.amplafi.flow.ui.command.ServerChangeCommandBuilder;
import org.amplafi.flow.ui.command.ServerDefinedFlowCommandBuilder;
import org.amplafi.flow.ui.command.ShellCommand;
import org.amplafi.flow.ui.command.ShellCommandBuilder;
import org.amplafi.flow.ui.command.ShellCommandManager;
import org.amplafi.flow.utils.AdminTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An entry point to load a shell and use the flow client scripts easily. The key interaction with
 * FlowTest and the groovy runtime is made through the {@link BindingFactory} interface which allows
 * you to resolve unresolved variable names in the scripts (this is how you input values through the
 * console for example, but could be any 'value provider' that knows how to properly override the
 * {@link Binding} class. See {@link InteractiveBinding} and {@link InteractiveBindingFactory}
 *
 */
public class InteractiveShell {
    private AdminTool adminTool;

    private BufferedReader reader;

    private Log log;

    private ShellCommandManager shellCommandManager;

    public InteractiveShell(BufferedReader reader) {
        this.reader = reader;
        setAdminTool(new AdminTool(new InteractiveBindingFactory(reader)));
        setShellCommandManager(new ShellCommandManager());
        this.setLog(LogFactory.getLog(this.getClass()));
    }

    public static void main(String[] args) {
        InteractiveShell is = new InteractiveShell(new BufferedReader(new InputStreamReader(System.in)));
        is.addCommand(new ExitBuilder());
        is.addCommand(new HelpBuilder(is));
        is.addCommand(new DescribeApiOrFlowBuilder());
        is.addCommand(new SetParameterCommandBuilder());
        is.addCommand(new ServerChangeCommandBuilder());
        is.addCommand(new ServerDefinedFlowCommandBuilder());
        is.addNamedCommands();
        is.ioLoop();
    }

    private void addNamedCommands() {
        List<String> scriptNames = new ArrayList<>(adminTool.getAvailableScripts().keySet());
        Collections.sort(scriptNames);
        for (String script : scriptNames) {
            addCommand(new NamedScriptCommandBuilder(script));
        }
    }

    public void ioLoop() {
        getShellCommandManager().build("help").execute(getAdminTool());
        while (true) {
            ShellCommand comm = parseCommand();
            try {
                comm.execute(getAdminTool());
            } catch (Exception e) {
                getLog().error("Command failed. Exception attached next:", e);
                System.out.println("Error in the command, look at the error logs for more info");
            }
        }
    }

    private ShellCommand parseCommand() {
        System.out.print(this.adminTool.getPrompt());
        String commandLine;
        try {
            commandLine = getReader().readLine().trim();
            return getShellCommandManager().build(commandLine);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    BufferedReader getReader() {
        return reader;
    }

    AdminTool getAdminTool() {
        return adminTool;
    }

    void setAdminTool(AdminTool adminTool) {
        this.adminTool = adminTool;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public void addCommand(ShellCommandBuilder commandBuilder) {
        getShellCommandManager().addCommand(commandBuilder);
    }

    public ShellCommandManager getShellCommandManager() {
        return shellCommandManager;
    }

    public void setShellCommandManager(ShellCommandManager commandManager) {
        this.shellCommandManager = commandManager;
    }
}
