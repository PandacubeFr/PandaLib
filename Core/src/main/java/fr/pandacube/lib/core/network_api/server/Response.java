package fr.pandacube.lib.core.network_api.server;

import java.io.PrintStream;

@Deprecated
public class Response {
	public boolean good = true;
	public String data = "";

	public Response(boolean good, String data) {
		this.good = good;
		this.data = data;
	}

	/**
	 * Construit une réponse positive avec aucune donnée. Équivaut à
	 * <code>new Response(true, "")</code>
	 */
	public Response() {}

	public void sendPacket(PrintStream out) {

		if (data == null) data = "";

		out.print((good ? "OK" : "ERROR") + "\n");
		out.print(data.getBytes().length + "\n");
		out.print(data);
		out.flush();
	}
}
