package org.amplafi.flow.ui.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class that knows all the shell commands and builds them when the time comes to add a command, add
 * one keyword to the build method
 */
public class ShellCommandManager {

    private static final Pattern BASIC_COMMAND = Pattern.compile("^(([^\\s]*)\\s+(.*)|(.*))$");

    private Map<String, ShellCommandBuilder> shellCommandBuilderMap = new HashMap<String, ShellCommandBuilder>();
    private List<ShellCommandBuilder> commandBuilders = new ArrayList<>();

    public ShellCommandManager() {
    }

    public void addCommand(ShellCommandBuilder commandBuilder) {
        getCommandBuilders().add(commandBuilder);
        this.shellCommandBuilderMap.put(commandBuilder.getCommandName(), commandBuilder);
    }

    public ShellCommand build(String commandLine) {
        Matcher m = BASIC_COMMAND.matcher(commandLine);
        m.matches();
        String commandName, commandParameters;
        boolean help = false;
        if (m.group(2) == null) {
            commandName = m.group(1);
            commandParameters = "";
        } else {
            commandName = m.group(2);
            commandParameters = m.group(3);
            if ("help".equals(commandName)) {
                commandName = commandParameters;
                help = true;
            }
        }
        String commandname = commandName.toLowerCase();
        //build commands by name
        ShellCommandBuilder builder = getShellCommandBuilder(commandname);
        if ( builder != null) {
            if (!help) {
                return builder.buildCommand(commandParameters);
            } else {
                return builder.buildHelp(commandParameters);
            }
        }
        return new EmptyCommand(commandParameters);
        /*
         * switch (commandName.toLowerCase()) { case "exit": return new ExitCommand(help,
         * commandParameters); case "tokenize": return new TokenizeCommand(help, commandParameters);
         * case "run": return new RunScriptCommand(help, commandParameters); case "describe": return
         * new DescribeFlowCommand(help, commandParameters); case "flows": return new
         * ListFlowsCommand(help, commandParameters); case "scripts": return new
         * ListScriptsCommand(help, commandParameters); case "help": return new HelpCommand(help,
         * commandParameters); default: return new EmptyCommand(false, commandParameters); }
         */
    }

    public List<ShellCommandBuilder> getCommandBuilders() {
        return commandBuilders;
    }

    public void setCommandBuilders(List<ShellCommandBuilder> commandBuilders) {
        this.commandBuilders = commandBuilders;
    }

    public ShellCommandBuilder getShellCommandBuilder(String indexOrName) {
        try {
            int index = Integer.parseInt(indexOrName);
            if ( index >=0 && index < this.commandBuilders.size()) {
                return this.commandBuilders.get(index);
            }
        } catch(NumberFormatException e) {
            // ignore
        }
        return this.shellCommandBuilderMap.get(indexOrName);
    }
}
