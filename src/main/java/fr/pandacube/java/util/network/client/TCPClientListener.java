package fr.pandacube.java.util.network.client;

import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.network.packet.PacketServer;

public interface TCPClientListener {

	/**
	 * Called when a connection is opened
	 * @param connection the connection which is opening
	 */
	public void onConnect(TCPClient connection);
	
	/**
	 * Called when the server send us a PacketServerException when an exception is thrown
	 * server side when handling our packets or eventually when the client is concerned
	 * with this error.<br/>
	 * The default implementation of this method just call the internal Logger of PandacubeUtil
	 * to print the stacktrace of the Exception.
	 * @param connection the connection which just received the error
	 * @param exceptionString a string representation of the exception. If the server
	 * use this Java library, it may be a full representation of
	 * {@link Throwable#printStackTrace()}.
	 */
	public default void onServerException(TCPClient connection, String exceptionString) {
		Log.severe("Exception thrown by server through " + connection + " : \n"+exceptionString);
	}

	/**
	 * Called when the server send us a packet
	 * @param connection the connection where the packet come from
	 * @param packet the packet
	 */
	public void onPacketReceive(TCPClient connection, PacketServer packet);

	/**
	 * Called before the connection closed
	 * @param connection the connection which is closing
	 */
	public void onDisconnect(TCPClient connection);

}
