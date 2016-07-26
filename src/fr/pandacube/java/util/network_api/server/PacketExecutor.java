package fr.pandacube.java.util.network_api.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import fr.pandacube.java.util.Log;

/**
 * Prends en charge un socket client et le transmet au gestionnaire de paquet
 * correspondant.<br/>
 * La connexion est fermée après chaque requête du client (règle pouvant
 * évoluer)
 *
 * @author Marc Baloup
 *
 */
public class PacketExecutor implements Runnable {
	private Socket socket;
	private NetworkAPIListener networkAPIListener;

	public PacketExecutor(Socket s, NetworkAPIListener napiListener) {
		socket = s;
		networkAPIListener = napiListener;
	}

	@Override
	public void run() {
		try {

			// analyse de la requête
			RequestAnalyser analyse = new RequestAnalyser(socket, networkAPIListener);

			AbstractRequestExecutor executor = networkAPIListener.getRequestExecutor(analyse.command);

			executor.execute(analyse.data, socket);

		} catch (Throwable e) {
			Response rep = new Response();
			rep.good = false;
			rep.data = e.toString();
			try {
				rep.sendPacket(new PrintStream(socket.getOutputStream()));
			} catch (IOException e1) {}
			if (e instanceof IOException)
				Log.warning("Impossible de lire le packet reçu sur le socket " + socket + " : " + e.toString());
			else
				Log.severe(e);
		}

		try {
			socket.close();
		} catch (Exception e) {}
	}
}
