package fr.pandacube.lib.netapi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ResponseAnalyser {
	/**
	 * Indique si la requête s'est bien exécutée (l'entête de la réponse est
	 * 'ok')
	 */
	public final boolean good;

	public final String data;

	public ResponseAnalyser(Socket socket) throws IOException {
		if (socket == null || socket.isClosed() || socket.isInputShutdown())
			throw new IllegalArgumentException("le socket doit être non null et doit être ouvert sur le flux d'entrée");

		// on lit la réponse
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;

		// lecture de la première ligne
		line = in.readLine();
		if (line == null)
			throw new IOException("Not enough data to read first line of response.");
		good = line.equalsIgnoreCase("OK");

		// lecture de la deuxième ligne
		line = in.readLine();
		if (line == null)
			throw new IOException("Not enough data to read second line of response.");

		int data_size;
		try {
			data_size = Integer.parseInt(line);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Réponse mal formée : la deuxième ligne doit-être un nombre entier");
		}

		// lecture du reste
		StringBuilder sB_data = new StringBuilder();
		char[] c = new char[100];
		int nbC;
		while ((nbC = in.read(c)) != -1)
			sB_data.append(c, 0, nbC);
		data = sB_data.toString();

		if (data.getBytes().length != data_size) throw new RuntimeException("Réponse mal formée : " + data_size
				+ " caractères annoncée dans la requête, mais " + data.getBytes().length + " s'y trouvent.");

	}

}
