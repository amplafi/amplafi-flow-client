package org.amplafi.flow.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.amplafi.flow.ui.command.AShellCommand;
import org.amplafi.flow.ui.command.AShellCommandBuilder;
import org.amplafi.flow.ui.command.DescribeApiOrFlowBuilder;
import org.amplafi.flow.ui.command.ExitBuilder;
import org.amplafi.flow.ui.command.HelpBuilder;
import org.amplafi.flow.ui.command.ListScriptsBuilder;
import org.amplafi.flow.ui.command.RunScriptBuilder;
import org.amplafi.flow.ui.command.ShellCommandBuilder;
import org.amplafi.flow.utils.AdminTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// An entry point to load a shell and use the flow client
// scripts easily
public class InteractiveShell {
	private AdminTool adminTool;
	private BufferedReader reader;
	private Log log;
	private ShellCommandBuilder shellCommandBuilder;

	private static final String prompt = "cs>";

	public InteractiveShell() {
		setReader(new BufferedReader(new InputStreamReader(System.in)));
		setAdminTool(new AdminTool(new InteractiveBindingFactory(getReader())));
		setShellCommandBuilder(new ShellCommandBuilder());
		this.setLog(LogFactory.getLog(this.getClass()));
		try{
		}catch(Exception e){
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		InteractiveShell is = new InteractiveShell();
		is.addCommand(new HelpBuilder(is));
		is.addCommand(new RunScriptBuilder());
		is.addCommand(new ListScriptsBuilder());
		is.addCommand(new DescribeApiOrFlowBuilder());
		is.addCommand(new ExitBuilder());
		is.ioLoop();
	}

	public void ioLoop() {
		getShellCommandBuilder().build("help").execute(getAdminTool());
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
			return getShellCommandBuilder().build(commandLine);
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
	
	public void addCommand(AShellCommandBuilder commandBuilder){
		getShellCommandBuilder().addCommand(commandBuilder);
	}

	public ShellCommandBuilder getShellCommandBuilder() {
		return shellCommandBuilder;
	}

	public void setShellCommandBuilder(ShellCommandBuilder commandBuilder) {
		this.shellCommandBuilder = commandBuilder;
	}
}
