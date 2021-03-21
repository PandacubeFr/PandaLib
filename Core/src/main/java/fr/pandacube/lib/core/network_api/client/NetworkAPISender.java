package fr.pandacube.lib.core.network_api.client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Deprecated
public class NetworkAPISender {

	public static ResponseAnalyser sendRequest(InetSocketAddress cible, AbstractRequest request) throws IOException {
		Socket s = new Socket(cible.getAddress(), cible.getPort());

		PrintStream out = new PrintStream(s.getOutputStream());

		request.sendPacket(out);
		s.shutdownOutput();

		ResponseAnalyser response = new ResponseAnalyser(s);

		s.close();

		return response;
	}

}
