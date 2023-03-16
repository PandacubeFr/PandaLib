package fr.pandacube.lib.ws.payloads;

/**
 * Error message payload.
 */
public class ErrorPayload extends Payload {
    /**
     * The error message.
     */
    public String message;
    /**
     * The error Throwable, may be null.
     */
    public Throwable throwable;

    /**
     * Initialize an error payload with a message but not throwable.
     * @param message the error message.
     */
    public ErrorPayload(String message) {
        this(message, null);
    }

    /**
     * Initialize an error payload with a message and a throwable.
     * @param message the error message.
     * @param throwable the error Throwable, may be null.
     */
    public ErrorPayload(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    @SuppressWarnings("unused")
    private ErrorPayload() { } // for Json deserialization
}
