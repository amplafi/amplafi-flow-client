package org.amplafi.flow.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.amplafi.dsl.FlowTestDSL;
import org.amplafi.flow.ui.command.AShellCommand;
import org.amplafi.flow.ui.command.ShellCommandBuilder;
import org.amplafi.flow.utils.AdminTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// An entry point to load a shell and use the flow client
// scripts easily
public class CustomerServiceShell {
	private AdminTool adminTool;
	private BufferedReader reader;
	private Log log;

	private static final String prompt = "cs>";

	CustomerServiceShell() {
		setReader(new BufferedReader(new InputStreamReader(System.in)));
		setAdminTool(new AdminTool(new InteractiveBindingFactory(getReader())));
		this.setLog(LogFactory.getLog(this.getClass()));
		try{
		}catch(Exception e){
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		CustomerServiceShell cSShell = new CustomerServiceShell();
		cSShell.ioLoop();
	}

	private void ioLoop() {
		while (true) {
			AShellCommand comm = parseCommand();
			try{
			comm.execute(getAdminTool());
			}catch(Exception e){
				getLog().error("There was a problem running the command. Look at the program error log", e);
				System.out.println("Error in the command, look at the error logs for more info");
			}
		}
	}

	private AShellCommand parseCommand() {
		System.out.print(prompt);
		String commandLine;
		try {
			commandLine = getReader().readLine();
			return ShellCommandBuilder.getBuilder().build(commandLine);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public String getUserInput() {
		try {
			String value = getReader().readLine();
			return value;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	BufferedReader getReader() {
		return reader;
	}

	void setReader(BufferedReader reader) {
		this.reader = reader;
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
}
