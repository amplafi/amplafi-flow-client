package org.amplafi.flow.shell;

import java.io.Console;

interface Action {
    public void exec(Console c, ShellContext context, String[] params) throws Exception;
}