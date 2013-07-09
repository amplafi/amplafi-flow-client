package org.amplafi.flow.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.amplafi.flow.ui.command.AShellCommand;
import org.amplafi.flow.ui.command.ShellCommandBuilder;
import org.amplafi.flow.utils.AdminTool;

// An entry point to load a shell and use the flow client
// scripts easily
public class CustomerServiceShell {
	private AdminTool adminTool;
	private BufferedReader reader;
	private static final String prompt = "cs>";

	CustomerServiceShell() {
		setAdminTool(new AdminTool());
		setReader(new BufferedReader(new InputStreamReader(System.in)));
	}

	public static void main(String[] args) {
		CustomerServiceShell cSShell = new CustomerServiceShell();
		cSShell.ioLoop();
	}

	private void ioLoop() {
		while (true) {
			AShellCommand comm = parseCommand();
			comm.execute(getAdminTool());
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
}
