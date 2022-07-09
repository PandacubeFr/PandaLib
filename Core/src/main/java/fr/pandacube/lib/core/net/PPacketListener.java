package fr.pandacube.lib.core.net;

@FunctionalInterface
public interface PPacketListener<P extends PPacket> {

	/**
	 * Called when we receive a packet (except responses)
	 * @param connection the connection from where the packet comes
	 * @param packet the received packet
	 */
	void onPacketReceive(PSocket connection, P packet);

}
