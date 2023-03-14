package fr.pandacube.lib.ws.server;

import fr.pandacube.lib.ws.payloads.ErrorPayload;
import fr.pandacube.lib.ws.payloads.LoginPayload;
import fr.pandacube.lib.ws.payloads.LoginSucceedPayload;
import fr.pandacube.lib.ws.payloads.Payload;

import java.util.function.Supplier;

/**
 * Websocket server endpoint that is protected with a key.
 */
public abstract class KeyProtectedServerWS extends AbstractServerWS {

	private boolean loginSucceed = false;

	private final Supplier<String> keySupplier;

	/**
	 * Creates a websocket server endpoint protected by the key given by the provided {@link Supplier}.
	 * @param keySupplier a {@link Supplier} for the key.
	 */
	public KeyProtectedServerWS(Supplier<String> keySupplier) {
		this.keySupplier = keySupplier;
	}

	@Override
	public final void onConnect() {
		// nothing, just wait for the client to login
	}

	@Override
	public final void onReceivePayload(Payload payload) {
		if (loginSucceed) {
			onReceivePayloadLoggedIn(payload);
		}
		else if (payload instanceof LoginPayload login) {
			if (keySupplier.get().equals(login.key)) {
				loginSucceed = true;
				trySendAsJson(new LoginSucceedPayload());
				onLoginSucceed();
			}
			else {
				logAndTrySendError(new ErrorPayload("Bad key"));
				trySendClose();
			}
		}
		else {
			logAndTrySendError(new ErrorPayload("Please use the login packet first. Received " + payload.getClass().getSimpleName() + " instead."));
			trySendClose();
		}
	}

	@Override
	public final void onClose(int code, String reason) {
		if (loginSucceed) {
			onCloseLoggedIn(code, reason);
		}
	}

	@Override
	public final void onError(Throwable cause) {
		if (loginSucceed) {
			onErrorLoggedIn(cause);
		}
	}

	/**
	 * Called when the client endpoint is succesfully logged in.
	 */
	public abstract void onLoginSucceed();

	/**
	 * Called on reception of a valid payload from the already logged in client.
	 * @param payload the received payload.
	 */
	public abstract void onReceivePayloadLoggedIn(Payload payload);

	/**
	 * Called on reception of a websocket Close packet, only if the client endpoint is already logged in.
	 * The connection is closed after this method call.
	 * @param code the close code. 1000 for a normal closure.
	 * @param reason the close reason.
	 */
	public abstract void onCloseLoggedIn(int code, String reason);

	/**
	 * Called when an error occurs with the websocket API, only if the client endpoint is already logged in.
	 * The connection is already closed when this method is called.
	 * @param cause the error cause.
	 */
	public abstract void onErrorLoggedIn(Throwable cause);


}
