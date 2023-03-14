package fr.pandacube.lib.ws.client;

import fr.pandacube.lib.ws.payloads.ErrorPayload;
import fr.pandacube.lib.ws.payloads.LoginPayload;
import fr.pandacube.lib.ws.payloads.LoginSucceedPayload;
import fr.pandacube.lib.ws.payloads.Payload;

import java.net.URISyntaxException;

/**
 * Websocket client that implements the login logic with a key protected server endpoint.
 */
public abstract class KeyProtectedClientWS extends AbstractClientWS {

    private final String key;

    private boolean loginSucceed = false;

    /**
     * Creates a new Websocket client that connect to the provided url and uses the provided key.
     * @param uri the destination endpoint.
     * @param autoReconnect if this websocket should automatically try to reconnect when disconnected.
     * @param key the login key.
     * @throws URISyntaxException if the provided URI is invalid.
     */
    public KeyProtectedClientWS(String uri, boolean autoReconnect, String key) throws URISyntaxException {
        super(uri, autoReconnect);
        this.key = key;
    }


    @Override
    public final void onConnect() {
        trySendAsJson(new LoginPayload(key));
    }

    @Override
    public final void onReceivePayload(Payload payload) {
        if (loginSucceed) {
            onReceivePayloadLoggedIn(payload);
        }
        else if (payload instanceof LoginSucceedPayload) {
            loginSucceed = true;
            onLoginSucceed();
        }
        else if (payload instanceof ErrorPayload err){
            logError("Received ErrorPayload instead of LoginSuccessPayload: " + err.message, err.throwable);
            trySendClose();
        }
        else {
            logError("Received unexpected Payload instead of LoginSuccessPayload: " + payload.getClass().getSimpleName());
            trySendClose();
        }
    }

    @Override
    public final void onClose(int code, String reason) {
        if (loginSucceed) {
            loginSucceed = false;
            onCloseLoggedIn(code, reason);
        }
    }

    @Override
    public final void onError(Throwable cause) {
        if (loginSucceed) {
            loginSucceed = false;
            onErrorLoggedIn(cause);
        }
    }

    /**
     * Called when this Websocket is succesfully logged in to the server.
     */
    public abstract void onLoginSucceed();

    /**
     * Called on reception of a valid payload when already logged in.
     * @param payload the received payload.
     */
    public abstract void onReceivePayloadLoggedIn(Payload payload);

    /**
     * Called on reception of a websocket Close packet, only if this client is already logged in.
     * The connection is closed after this method call.
     * @param code the close code. 1000 for a normal closure.
     * @param reason the close reason.
     */
    public abstract void onCloseLoggedIn(int code, String reason);

    /**
     * Called when an error occurs with the websocket API, only if this client is already logged in.
     * The connection is already closed when this method is called.
     * @param cause the error cause.
     */
    public abstract void onErrorLoggedIn(Throwable cause);
}
