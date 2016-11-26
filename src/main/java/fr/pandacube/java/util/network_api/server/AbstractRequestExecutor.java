package fr.pandacube.java.util.network_api.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import fr.pandacube.java.util.Log;

public abstract class AbstractRequestExecutor {

	public final String command;

	public AbstractRequestExecutor(String cmd, NetworkAPIListener napiListener) {
		command = cmd.toLowerCase();
		napiListener.registerRequestExecutor(command, this);
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
	 * @param data La représentation sous forme de String des données envoyés
	 *        dans la requête
	 * @return La réponse à retourner au client
	 */
	protected abstract Response run(InetAddress source, String data);

}
