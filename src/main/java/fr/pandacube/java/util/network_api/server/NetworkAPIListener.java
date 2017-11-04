package fr.pandacube.java.util.network_api.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;

public class NetworkAPIListener implements Runnable {

	private int port = 0;
	String pass;
	private ServerSocket serverSocket;
	private HashMap<String, AbstractRequestExecutor> requestExecutors = new HashMap<>();
	private String name;
	private NAPIExecutionHandler nAPIExecutionHandler;

	/**
	 * Instencie le côté serveur du NetworkAPI
	 *
	 * @param n nom du networkAPI (permet l'identification dans les logs)
	 * @param p le port d'écoute
	 * @param pa le mot de passe réseau
	 * @param peh PacketExecutionHandler permettant de prendre en charge
	 *        l'exécution asynchrone d'une requête reçu pas un client
	 */
	public NetworkAPIListener(String n, int p, String pa, NAPIExecutionHandler peh) {
		port = p;
		pass = pa;
		name = n;
		nAPIExecutionHandler = peh;
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				return;
			}
		}

		System.out.println("NetworkAPI '" + name + "' à l'écoute sur le port " + port);

		try {
			// réception des connexion client
			while (!serverSocket.isClosed()) {
				nAPIExecutionHandler.handleRun(new PacketExecutor(serverSocket.accept(), this));
			}
		} catch (IOException e) {}

		synchronized (this) {
			try {
				if (!serverSocket.isClosed()) serverSocket.close();
			} catch (IOException e) {}
		}

		System.out.println("NetworkAPI '" + name + "' ferme le port " + port);

	}

	/**
	 * Ferme le ServerSocket. Ceci provoque l'arrêt du thread associé à
	 * l'instance de la classe
	 */
	public synchronized void closeServerSocket() {
		if (serverSocket != null) try {
			serverSocket.close();
		} catch (IOException e) {}
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
