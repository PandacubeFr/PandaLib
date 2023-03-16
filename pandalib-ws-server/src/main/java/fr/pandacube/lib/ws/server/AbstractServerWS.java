package fr.pandacube.lib.ws.server;

import fr.pandacube.lib.ws.AbstractWS;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.time.Duration;

/**
 * Minimal implementation of a Websocket server endpoint using the Jetty Websocket API.
 */
public abstract class AbstractServerWS extends WebSocketAdapter implements AbstractWS {
	
	@Override
	public final void onWebSocketConnect(Session sess)
	{
		super.onWebSocketConnect(sess);
		sess.setIdleTimeout(Duration.ofDays(1000)); // practically infinite
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
		onError(cause);
	}







	public final void sendString(String message) throws IOException {
		getSession().getRemote().sendString(message);
	}

	@Override
	public final void sendClose(int code, String reason) throws IOException {
		getSession().close(code, reason);
	}

	@Override
	public String getRemoteIdentifier() {
		return getSession().getRemoteAddress().toString();
	}
}
