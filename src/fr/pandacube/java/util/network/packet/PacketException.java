package fr.pandacube.java.util.network.packet;

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
