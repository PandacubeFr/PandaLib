package fr.pandacube.lib.netapi.server;

import fr.pandacube.lib.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;

public class NetworkAPIListener implements Runnable {

	private final InetAddress host;
	private final int port;
	final String pass;
	private ServerSocket serverSocket;
	private final HashMap<String, AbstractRequestExecutor> requestExecutors = new HashMap<>();
	private final String name;

	/**
	 * Instencie le côté serveur du NetworkAPI.
	 *
	 * @param n nom du networkAPI (permet l'identification dans les logs)
	 * @param p le port d'écoute
	 * @param pa le mot de passe réseau
	 */
	public NetworkAPIListener(String n, int p, String pa) {
		this(n, null, p, pa);
	}

	/**
	 * Instencie le côté serveur du NetworkAPI.
	 *
	 * @param n nom du networkAPI (permet l'identification dans les logs)
	 * @param p le port d'écoute
	 * @param pa le mot de passe réseau
	 */
	public NetworkAPIListener(String n, InetAddress h, int p, String pa) {
		name = n;
		host = h;
		port = p;
		pass = pa;
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				serverSocket = host == null ? new ServerSocket(port) : new ServerSocket(port, 50, host);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				return;
			}
		}

		Log.info("NetworkAPI '" + name + "' à l'écoute sur le socket " + serverSocket.getLocalSocketAddress());

		try {
			// réception des connexion client
			while (!serverSocket.isClosed()) {
				Thread t = new Thread(new PacketExecutor(serverSocket.accept(), this));
				t.setDaemon(true);
				t.start();
			}
		} catch (IOException ignored) {}

		synchronized (this) {
			try {
				if (!serverSocket.isClosed())
					serverSocket.close();
			} catch (IOException ignored) {}
		}

		Log.info("NetworkAPI '" + name + "' ferme le port " + port);

	}

	/**
	 * Ferme le ServerSocket. Ceci provoque l'arrêt du thread associé à
	 * l'instance de la classe
	 */
	public synchronized void closeServerSocket() {
		if (serverSocket != null) try {
			serverSocket.close();
		} catch (IOException ignored) {}
	}

	public int getPort() {
		return port;
	}

	public void registerRequestExecutor(String command, AbstractRequestExecutor executor) {
		requestExecutors.put(command, executor);
	}

	public AbstractRequestExecutor getRequestExecutor(String command) {
		return requestExecutors.get(command);
	}

	public String getCommandList() {
		return Arrays.toString(requestExecutors.keySet().toArray());
	}

}
