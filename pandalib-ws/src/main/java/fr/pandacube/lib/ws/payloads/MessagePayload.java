package fr.pandacube.lib.ws.payloads;

/**
 * A general use message Payload.
 */
public class MessagePayload extends Payload {
    /**
     * The message.
     */
    public String message;

    /**
     * Initialite a new MessagePayload with the provided message.
     * @param message the message.
     */
    public MessagePayload(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    private MessagePayload() { } // for Json deserialization
}
