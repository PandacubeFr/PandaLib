package fr.pandacube.lib.net;

import com.google.common.annotations.Beta;

@Beta
public interface PSocketConnectionListener {

	/**
	 * Called when a socket is connected
	 * @param connection the connection
	 */
	void onConnect(PSocket connection);
	
	/**
	 * Called just before a socket is disconnected
	 * @param connection the connection
	 */
	void onDisconnect(PSocket connection);

}
