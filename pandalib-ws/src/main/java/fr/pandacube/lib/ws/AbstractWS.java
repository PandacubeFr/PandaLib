package fr.pandacube.lib.ws;

import fr.pandacube.lib.util.Log;
import fr.pandacube.lib.util.ThrowableUtil.RunnableException;
import fr.pandacube.lib.ws.payloads.ErrorPayload;
import fr.pandacube.lib.ws.payloads.Payload;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Super interface for all websocket endpoint, either client or server, that provides minimal implementation of our
 * custom sub-protocol.
 */
public interface AbstractWS {

    /*
     * Receiving
     */

    /**
     * Handles the reception of a message, by deserializing the payloads and handling eventual deserialization errors.
     * @param message the raw message received.
     */
    default void handleReceivedMessage(String message) {
        Payload payload;
        try {
            payload = PayloadRegistry.fromString(message);
        } catch (IllegalArgumentException e) {
            logAndTrySendError(new ErrorPayload(e.getMessage())); // no need to log or send full exception stack trace
            return;
        }

        try {
            onReceivePayload(payload);
        } catch (Throwable t) {
            trySendAsJson(new ErrorPayload("Error handling payload: " + t));
            if (t instanceof Exception)
                logError("Error handling payload", t);
            else
                throw t;
        }
    }

    /**
     * Handles the reception of binary data. The default implementation reject any binary data by sending an
     * {@link ErrorPayload} to the remote endpoint.
     */
    default void handleReceivedBinary() {
        trySendAsJson(new ErrorPayload("Cannot accept binary payload."));
    }

    /**
     * Called when the websocket connection is established.
     */
    void onConnect();

    /**
     * Called on reception of a valid {@link Payload}.
     * @param payload the received {@link Payload}.
     */
    void onReceivePayload(Payload payload);

    /**
     * Called on reception of a websocket Close packet.
     * The connection is closed after this method call.
     * @param code the close code. 1000 for a normal closure.
     * @param reason the close reason.
     */
    void onClose(int code, String reason);

    /**
     * Called when an error occurs with the websocket API.
     * The connection is already closed when this method is called.
     * @param cause the error cause.
     */
    void onError(Throwable cause);








    /*
     * Sending
     */

    /**
     * Send the provided raw string to the remote endpoint.
     * <p>
     * <b>It is not advised for subclasses to call directly this method.
     * Please use {@link #sendAsJson(Payload)} or {@link #sendAsJson(String, Object, boolean)} instead.</b>
     * @param message the raw message to send.
     * @throws IOException if an IO error occurs when sending the message.
     */
    void sendString(String message) throws IOException;

    /**
     * Send the provided type and object that will be serialized using
     * {@link PayloadRegistry#arbitraryToString(String, Object, boolean)}.
     * @param type the type.
     * @param obj the object to Jsonify.
     * @param serializeNulls if null propreties must be included in the json object.
     * @throws IOException if an IO error occurs when sending the data.
     * @see PayloadRegistry#arbitraryToString(String, Object, boolean)
     */
    default void sendAsJson(String type, Object obj, boolean serializeNulls) throws IOException {
        sendString(PayloadRegistry.arbitraryToString(type, obj, serializeNulls));
    }

    /**
     * Send the provided type and object that will be serialized using
     * {@link PayloadRegistry#arbitraryToString(String, Object, boolean)}.
     * @param type the type.
     * @param obj the object to Jsonify.
     * @param serializeNulls if null propreties must be included in the json object.
     * @return true if the data is sent successfully, false if an IO error occurs.
     * @see PayloadRegistry#arbitraryToString(String, Object, boolean)
     */
    default boolean trySendAsJson(String type, Object obj, boolean serializeNulls) {
        return trySend(() -> sendAsJson(type, obj, serializeNulls), "Error sending object as json");
    }

    /**
     * Send the provided {@link Payload} to the remote endpoint.
     * @param payload the {@link Payload} to send.
     * @throws IOException if an IO error occurs when sending the data.
     * @see PayloadRegistry#toString(Payload)
     */
    default void sendAsJson(Payload payload) throws IOException {
        sendString(PayloadRegistry.toString(payload));
    }

    /**
     * Send the provided {@link Payload} to the remote endpoint.
     * @param payload the {@link Payload} to send.
     * @return true if the data is sent successfully, false if an IO error occurs.
     * @see PayloadRegistry#toString(Payload)
     */
    default boolean trySendAsJson(Payload payload) {
        return trySend(() -> sendAsJson(payload), "Error sending payload as json");
    }

    /**
     * Gracefully closes the connection by sending the close packet with the provided data.
     * @param code the status code.
     * @param reason the reason.
     * @throws IOException if an IO error occurs when sending the close packet.
     */
    void sendClose(int code, String reason) throws IOException;

    /**
     * Gracefully closes the connection by sending the close packet with the provided data.
     * @param code the status code.
     * @param reason the reason.
     * @return true if the data is sent successfully, false if an IO error occurs.
     */
    default boolean trySendClose(int code, String reason) {
        return trySend(() -> sendClose(code, reason), "Error sending close");
    }

    /**
     * Gracefully closes the connection by sending the close packet with the default status code (1000) and an empty
     * reason.
     * @throws IOException if an IO error occurs when sending the close packet.
     */
    default void sendClose() throws IOException {
        sendClose(1000, "");
    }

    /**
     * Gracefully closes the connection by sending the close packet with the default status code (1000) and an empty
     * reason.
     * @return true if the data is sent successfully, false if an IO error occurs.
     */
    default boolean trySendClose() {
        return trySend(this::sendClose, "Error sending close");
    }

    /**
     * Logs the error from the provided {@link ErrorPayload} and sends it to the remote endpoint.
     * @param p the {@link ErrorPayload}.
     * @throws IOException if an IO error occurs when sending the data.
     */
    default void logAndSendError(ErrorPayload p) throws IOException {
        logError(p.message, p.throwable);
        sendAsJson(p);
    }

    /**
     * Logs the error from the provided {@link ErrorPayload} and sends it to the remote endpoint.
     * @param p the {@link ErrorPayload}.
     * @return true if the data is sent successfully, false if an IO error occurs.
     */
    default boolean logAndTrySendError(ErrorPayload p) {
        return trySend(() -> logAndSendError(p), "Error sending error payload as json");
    }

    /**
     * Utility method to wrap sending operation into a try-catch.
     * @param run the sending operation that may throw an {@link IOException}.
     * @param errorMessage the error message to log if the runnable throws an {@link IOException}.
     * @return true if the data if the runnable is executed successfully, false if an IO error occurs.
     */
    default boolean trySend(RunnableException<IOException> run, String errorMessage) {
        try {
            run.run();
            return true;
        } catch (IOException e) {
            logError(errorMessage, e);
            return false;
        }
    }



    /*
     * Log
     */

    /**
     * Logs the provided message with logger level {@link Level#INFO}, prefixed with infos avout this web-socket.
     * @param message the message to log.
     */
    default void log(String message) {
        Log.info(formatLogMessage(message));
    }

    /**
     * Logs the provided message with logger level {@link Level#SEVERE}, prefixed with infos avout this web-socket.
     * @param message the message to log.
     */
    default void logError(String message) {
        logError(message, null);
    }

    /**
     * Logs the provided message and {@link Throwable} with logger level {@link Level#SEVERE}, prefixed with infos avout this web-socket.
     * @param message the message to log.
     * @param t the throwable to log.
     */
    default void logError(String message, Throwable t) {
        Log.severe(formatLogMessage(message), t);
    }

    /**
     * Gets an identifier for this web-socket, used for logging. May be the remote IP:port or URI.
     * @return an identifier for this web-socket.
     */
    String getRemoteIdentifier();

    private String formatLogMessage(String message) {
        return "[WS/" + getClass().getSimpleName() + "] [" + getRemoteIdentifier() + "] " + message;
    }
}
