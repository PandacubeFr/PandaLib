package fr.pandacube.lib.ws.payloads;

/**
 * Payload used by the client to log in to key-protected websocket endpoint.
 */
public class LoginPayload extends Payload {
    /**
     * The key to use for login.
     */
    public String key;

    /**
     * Create a new LoginPayload with the provided key.
     * @param key the key to use for login.
     */
    public LoginPayload(String key) {
        this.key = key;
    }

    @SuppressWarnings("unused")
    private LoginPayload() { } // for Json deserialization
}
