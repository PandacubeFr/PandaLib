package fr.pandacube.java.util.network.packet;

/**
 * On attend d'un instance de {@link PacketClient} qu'il soit envoyé depuis
 * une connexion Client vers une connexion Serveur (d'un point de vue TCP)
 */
public abstract class PacketClient extends Packet {
	
	public PacketClient(byte c) {
		super(c);
	}
	
	
}
