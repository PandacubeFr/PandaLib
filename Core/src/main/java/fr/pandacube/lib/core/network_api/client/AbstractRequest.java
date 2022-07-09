package fr.pandacube.lib.core.network_api.client;

import java.io.PrintStream;

@Deprecated
public abstract class AbstractRequest {

	private final String pass;
	private final String command;
	private String data;

	protected AbstractRequest(String cmd, String p) {
		if (cmd == null || cmd.isEmpty()) throw new IllegalArgumentException("Un message doit-être défini");
		command = cmd;
		pass = p;
	}

	protected void setData(String d) {
		if (d == null) d = "";
		data = d;
	}

	public void sendPacket(PrintStream out) {
		out.print(pass + "\n");
		out.print(command + "\n");
		out.print(data.getBytes().length + "\n");
		out.print(data);
		out.flush();
	}
}
