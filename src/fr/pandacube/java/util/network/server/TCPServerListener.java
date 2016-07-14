package fr.pandacube.java.util.network.server;

import fr.pandacube.java.util.network.packet.PacketClient;
import fr.pandacube.java.util.network.server.TCPServer.TCPServerClientConnection;

public interface TCPServerListener {

	public void onSocketOpen(TCPServer svConnection);

	public void onClientConnect(TCPServer svConnection, TCPServerClientConnection clientConnection);

	public void onPacketReceive(TCPServer svConnection, TCPServerClientConnection clientConnection,
			PacketClient packet);

	public void onClientDisconnect(TCPServer svConnection, TCPServerClientConnection clientConnection);

	public void onSocketClose(TCPServer svConnection);

}
