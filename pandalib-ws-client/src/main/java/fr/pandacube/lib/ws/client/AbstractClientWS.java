package fr.pandacube.lib.ws.client;

import fr.pandacube.lib.util.Log;
import fr.pandacube.lib.util.ThrowableUtil;
import fr.pandacube.lib.ws.AbstractWS;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

// TODO implement auto-reconnect
/**
 * Minimal implementation of a Websocket client endpoint using the java.net.http Websocket API.
 */
public abstract class AbstractClientWS implements AbstractWS {

    private final URI uri;
    private boolean autoReconnect;
    private final AtomicReference<WebSocket> socket = new AtomicReference<>();


    private final Listener receiveListener = new Listener() {
        @Override
        public void onOpen(WebSocket webSocket) {
            AbstractClientWS.this.onConnect();
            Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            AbstractClientWS.this.handleReceivedMessage(data.toString());
            return Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            AbstractClientWS.this.handleReceivedBinary();
            return Listener.super.onBinary(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            AbstractClientWS.this.onClose(statusCode, reason);
            return Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            AbstractClientWS.this.onError(error);
            Listener.super.onError(webSocket, error);
        }
    };


    /**
     * Creates a new Websocket client that connect to the provided url.
     * @param uri the destination endpoint.
     * @param autoReconnect if this websocket should automatically try to reconnect when disconnected.
     * @throws URISyntaxException if the provided URI is invalid.
     */
    public AbstractClientWS(String uri, boolean autoReconnect) throws URISyntaxException {
        this.uri = new URI(uri);
        this.autoReconnect = autoReconnect;
        if (autoReconnect) {
            Log.warning("Websocket client auto-reconnect is not yet implemented.");
        }
        connect();
    }


    private void connect() {
        synchronized (socket) {
            socket.set(HttpClient.newHttpClient()
                    .newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .buildAsync(uri, receiveListener)
                    .join()
            );
        }
    }


    @Override
    public final void sendString(String message) throws IOException {
        try {
            synchronized (socket) {
                socket.get().sendText(message, true).join();
            }
        } catch (CompletionException ce) {
            if (ce.getCause() instanceof IOException ioe)
                throw ioe;
            throw ThrowableUtil.uncheck(ce.getCause(), false);
        }
    }

    @Override
    public String getRemoteIdentifier() {
        return uri.toString();
    }

    @Override
    public final void sendClose(int code, String reason) throws IOException {
        synchronized (socket) {
            autoReconnect = false; // if we ask for closing connection, dont reconnect automatically
            socket.get().sendClose(code, reason);
        }
    }

}
