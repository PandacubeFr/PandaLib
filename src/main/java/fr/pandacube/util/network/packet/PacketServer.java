package fr.pandacube.util.network.packet;

/**
 * On attend d'un instance de {@link PacketServer} qu'il soit envoyé depuis
 * une connexion Serveur vers une connexion Client (d'un point de vue TCP)
 */
public abstract class PacketServer extends Packet {

	public PacketServer(byte c) {
		super(c);
	}

}
