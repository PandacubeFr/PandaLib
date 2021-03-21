package fr.pandacube.lib.core.network_api.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Deprecated
public class RequestAnalyser {

	private NetworkAPIListener networkAPIListener;
	public final String command;
	public final String data;

	public RequestAnalyser(Socket socket, NetworkAPIListener napiListener) throws IOException, BadRequestException {
		if (socket == null || socket.isClosed() || socket.isInputShutdown() || napiListener == null)
			throw new IllegalArgumentException(
					"le socket doit être non null et doit être ouvert sur le flux d'entrée et napiListener ne doit pas être null");

		networkAPIListener = napiListener;

		// on lis la réponse
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;

		// lecture de la première ligne
		line = in.readLine();
		if (line == null || !line.equals(networkAPIListener.pass)) throw new BadRequestException("wrong_password");

		// lecture de la deuxième ligne
		line = in.readLine();
		if (line == null || networkAPIListener.getRequestExecutor(line) == null)
			throw new BadRequestException("command_not_exists");
		command = line;

		// lecture de la troisième ligne
		line = in.readLine();

		int data_size = 0;
		try {
			data_size = Integer.parseInt(line);
		} catch (NumberFormatException e) {
			throw new BadRequestException("wrong_data_size_format");
		}

		// lecture du reste
		StringBuilder sB_data = new StringBuilder();
		char[] c = new char[100];
		int nbC = 0;
		while ((nbC = in.read(c)) != -1)
			sB_data.append(c, 0, nbC);

		data = sB_data.toString();

		if (data.getBytes().length != data_size) throw new BadRequestException("wrong_data_size");

		socket.shutdownInput();
	}

	public class BadRequestException extends Exception {

		private static final long serialVersionUID = 1L;

		public BadRequestException(String message) {
			super(message);
		}

	}

}