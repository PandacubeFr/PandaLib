package fr.pandacube.lib.ws.server;

import fr.pandacube.lib.ws.AbstractWS;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;

/**
 * Minimal implementation of a Websocket server endpoint using the Jetty Websocket API.
 */
public abstract class AbstractServerWS extends WebSocketAdapter implements AbstractWS {

	private boolean isClosed = false;

	/**
	 * Creates a Websocket server endpoint.
	 */
	public AbstractServerWS() {}
	
	@Override
	public final void onWebSocketConnect(Session session)
	{
		super.onWebSocketConnect(session);
		session.setIdleTimeout(Duration.ofDays(1000)); // practically infinite
		onConnect();
	}

	@Override
	public final void onWebSocketBinary(byte[] payload, int offset, int len) {
		handleReceivedBinary();
	}

	@Override
	public final void onWebSocketText(String message) {
		handleReceivedMessage(message);
	}

	@Override
	public final void onWebSocketClose(int statusCode, String reason) {
		onClose(statusCode, reason);
	}

	@Override
	public final void onWebSocketError(Throwable cause) {
		if (isClosed && cause instanceof ClosedChannelException)
			return; // ignore because this exception is expected when we just sent a close packet.
		onError(cause);
	}







	public final void sendString(String message) throws IOException {
		getSession().getRemote().sendString(message);
	}

	@Override
	public final void sendClose(int code, String reason) {
		getSession().close(code, reason);
		isClosed = true;
	}

	@Override
	public String getRemoteIdentifier() {
		return getSession().getRemoteAddress().toString();
	}
}
