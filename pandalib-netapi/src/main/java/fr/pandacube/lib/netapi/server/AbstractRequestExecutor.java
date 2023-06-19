package fr.pandacube.lib.netapi.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import fr.pandacube.lib.util.Log;

public abstract class AbstractRequestExecutor {

	public final String command;

	public AbstractRequestExecutor(String cmd, NetworkAPIListener nAPIListener) {
		command = cmd.toLowerCase();
		nAPIListener.registerRequestExecutor(command, this);
	}

	public void execute(String data, Socket socket) throws IOException {
		if (socket == null || socket.isClosed() || socket.isOutputShutdown())
			throw new IllegalArgumentException("le socket doit être non null et doit être ouvert sur le flux d'entrée");

		try {

			Response rep = run(socket.getInetAddress(), data);
			rep.sendPacket(new PrintStream(socket.getOutputStream()));

		} catch (Exception e) {
			new Response(false, e.toString()).sendPacket(new PrintStream(socket.getOutputStream()));
			Log.severe(e);
		}

	}

	/**
	 *
	 * @param data The String representation of the request data.
	 * @return The response to send back to the client.
	 */
	protected abstract Response run(InetAddress source, String data);

}
