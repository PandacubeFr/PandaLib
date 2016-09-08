package fr.pandacube.java.util.network.packet;

@FunctionalInterface
public interface ResponseCallback<T extends Packet> {
	
	public void call(T packet);

}
