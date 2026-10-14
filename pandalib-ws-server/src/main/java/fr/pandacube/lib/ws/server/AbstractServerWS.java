package fr.pandacube.lib.ws.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

import fr.pandacube.lib.ws.AbstractWS;

/**
 * Minimal implementation of a Websocket server endpoint using the Jakarta Websocket API.
 */
public abstract class AbstractServerWS extends Endpoint implements AbstractWS {

    private final Object sendLock = new Object();
    private final AtomicBoolean handlingError = new AtomicBoolean();
    private volatile Session session;
    private volatile String remoteIdentifier;
    private volatile boolean disconnected = true;
    private volatile boolean closeRequested;

    /**
     * Creates a Websocket server endpoint.
     */
    public AbstractServerWS() {
    }

    /**
     * Initializes the application-provided remote identifier before this endpoint is opened.
     *
     * @param identifier the remote identifier, usually the direct socket IP address and port.
     * @throws IllegalArgumentException if the identifier is null or blank.
     * @throws IllegalStateException    if already initialized or this endpoint has been opened.
     */
    public final synchronized void initializeRemoteIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank())
            throw new IllegalArgumentException("The remote identifier must not be blank");
        if (remoteIdentifier != null || session != null)
            throw new IllegalStateException("The remote identifier must be initialized exactly once before opening");
        remoteIdentifier = identifier;
    }

    @Override
    public final void onOpen(Session session, EndpointConfig config) {
        synchronized (this) {
            if (this.session != null)
                throw new IllegalStateException("Each connection requires a fresh endpoint instance");
            this.session = session;
            if (remoteIdentifier == null)
                throw new IllegalStateException("Initialize the remote identifier before opening the endpoint");
        }
        session.setMaxIdleTimeout(Duration.ofDays(1000).toMillis());
        session.addMessageHandler(String.class, message -> {
            if (!disconnected)
                handleReceivedMessage(message);
        });
        session.addMessageHandler(ByteBuffer.class, message -> {
            if (!disconnected)
                handleReceivedBinary();
        });
        disconnected = false;
        onConnect();
    }

    @Override
    public final void onClose(Session session, CloseReason reason) {
        disconnected = true;
        if (remoteIdentifier != null)
            onClose(reason.getCloseCode().getCode(), reason.getReasonPhrase());
    }

    @Override
    public final void onError(Session session, Throwable cause) {
        disconnected = true;
        if ((closeRequested && cause instanceof ClosedChannelException) || !handlingError.compareAndSet(false, true))
            return;
        try {
            try {
                if (session != null && session.isOpen())
                    session.close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, ""));
            } catch (IOException | IllegalStateException closeFailure) {
                if (closeFailure != cause)
                    cause.addSuppressed(closeFailure);
            }
            if (remoteIdentifier != null)
                onError(cause);
        } finally {
            handlingError.set(false);
        }
    }

    /**
     * Returns the Jakarta session, or null before opening. The session is retained after closing.
     *
     * @return the session.
     */
    public final Session getSession() {
        return session;
    }

    /**
     * Returns whether this endpoint is open and neither closing nor failed.
     *
     * @return true if connected.
     */
    public final boolean isConnected() {
        Session current = session;
        try {
            return !disconnected && current != null && current.isOpen();
        } catch (IllegalStateException exception) {
            return false;
        }
    }

    /**
     * Returns the inverse of {@link #isConnected()}.
     *
     * @return true if not connected.
     */
    public final boolean isNotConnected() {
        return !isConnected();
    }

    @Override
    public final void sendString(String message) throws IOException {
        synchronized (sendLock) {
            try {
                if (!isConnected())
                    throw new ClosedChannelException();
                session.getBasicRemote().sendText(message);
            } catch (IllegalStateException exception) {
                throw new IOException("The Websocket connection is closed or unable to send", exception);
            }
        }
    }

    @Override
    public final void sendClose(int code, String reason) throws IOException {
        Session current = session;
        if (current == null)
            throw new ClosedChannelException();
        CloseReason closeReason = new CloseReason(CloseCodes.getCloseCode(code), reason);
        closeRequested = true;
        disconnected = true;
        try {
            current.close(closeReason);
        } catch (IllegalStateException exception) {
            throw new IOException("The Websocket connection is unable to close", exception);
        }
    }

    @Override
    public String getRemoteIdentifier() {
        return remoteIdentifier;
    }
}
