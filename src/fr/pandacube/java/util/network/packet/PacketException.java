package fr.pandacube.java.util.network.packet;

/**
 * 
 * Thrown when there is a problem when constructing, sending or handling a packet.
 * 
 * Only the server may send a string representation of an exception to the client, not the reverse
 * 
 */
public class PacketException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PacketException(String m) {
		super(m);
	}

	public PacketException(String m, Throwable t) {
		super(m, t);
	}

	public PacketException(Throwable t) {
		super(t);
	}
}
