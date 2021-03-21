package fr.pandacube.lib.core.net;

public interface PSocketConnectionListener {

	/**
	 * Called when a socket is connected
	 * @param connection the connection
	 */
	public void onConnect(PSocket connection);
	
	/**
	 * Called just before a socket is disconnected
	 * @param connection the connection
	 */
	public void onDisconnect(PSocket connection);

}