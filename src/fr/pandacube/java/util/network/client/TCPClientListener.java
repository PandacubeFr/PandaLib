package fr.pandacube.java.util.network.client;

import fr.pandacube.java.util.network.packet.PacketServer;

public interface TCPClientListener {
	
	public void onConnect(TCPClient connection);
	
	public void onPacketReceive(TCPClient connection, PacketServer packet);
	
	public void onDisconnect(TCPClient connection);
	
}
