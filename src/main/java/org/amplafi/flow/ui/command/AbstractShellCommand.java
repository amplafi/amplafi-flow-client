package org.amplafi.flow.ui.command;

import java.util.regex.Pattern;

/**
 * Shell commands are represented by extending this abstract class. Here only crucial, common
 * information is held (command name, options, an abstract executor)
 */
public abstract class AbstractShellCommand implements ShellCommand {
    protected static final Pattern ONE_AND_MAYBE_TWO_WORDS = Pattern.compile("^([^\\s]+)\\s*([^\\s]*)$");

    protected static final Pattern NOTHING = Pattern.compile("^\\s*$");

    // the help command works by executing building the command in help=true.
    // unparsed options that might be used however the command sees fit.
    private String options;

    protected AbstractShellCommand(String options) {
        this.options = options;
    }

    protected String getOptions() {
        return options;
    }
}
