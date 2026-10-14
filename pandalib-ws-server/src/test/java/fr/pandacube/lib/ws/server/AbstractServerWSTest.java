package fr.pandacube.lib.ws.server;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.RemoteEndpoint.Basic;
import jakarta.websocket.Session;
import org.junit.Test;

import fr.pandacube.lib.ws.payloads.LoginPayload;
import fr.pandacube.lib.ws.payloads.Payload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class AbstractServerWSTest {

    @Test
    public void remoteIdentifierMustBeNonblankAndInitializedOnce() {
        TestEndpoint endpoint = new TestEndpoint();
        assertNull(endpoint.getRemoteIdentifier());
        assertThrows(IllegalArgumentException.class, () -> endpoint.initializeRemoteIdentifier(null));
        assertThrows(IllegalArgumentException.class, () -> endpoint.initializeRemoteIdentifier(" \t"));
        endpoint.initializeRemoteIdentifier("/127.0.0.1:12345");
        assertThrows(IllegalStateException.class, () -> endpoint.initializeRemoteIdentifier("/127.0.0.1:23456"));
        assertEquals("/127.0.0.1:12345", endpoint.getRemoteIdentifier());
    }

    @Test
    public void missingIdentifierRejectsOpeningWithoutApplicationCallbacks() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = new SessionStub();
        IllegalStateException failure = assertThrows(IllegalStateException.class, () -> endpoint.onOpen(stub.session, null));
        endpoint.onError(stub.session, failure);
        endpoint.onClose(stub.session, new CloseReason(CloseCodes.UNEXPECTED_CONDITION, ""));
        assertTrue(endpoint.events.isEmpty());
        assertFalse(stub.open);
        assertFalse(endpoint.isConnected());
        assertThrows(IllegalStateException.class, () -> endpoint.initializeRemoteIdentifier("late"));
    }

    @Test
    public void openingRetainsSessionAndExposesConnectedStateBeforeCallback() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        assertSame(stub.session, endpoint.getSession());
        assertEquals(List.of("connect:/127.0.0.1:12345:true"), endpoint.events);
        assertEquals(Duration.ofDays(1000).toMillis(), stub.idleTimeout);
        assertTrue(endpoint.isConnected());
        assertFalse(endpoint.isNotConnected());
        assertThrows(IllegalStateException.class, () -> endpoint.onOpen(new SessionStub().session, null));
    }

    @Test
    public void sendsBeforeOpeningReportIOException() {
        TestEndpoint endpoint = new TestEndpoint();
        assertFalse(endpoint.isConnected());
        assertTrue(endpoint.isNotConnected());
        assertThrows(IOException.class, () -> endpoint.sendString("message:{}"));
        assertThrows(IOException.class, () -> endpoint.sendClose(1000, ""));
    }

    @Test
    public void remoteCloseRetainsIdentifierAndMarksDisconnectedBeforeCallback() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        stub.open = false;
        endpoint.onClose(stub.session, new CloseReason(CloseCodes.NORMAL_CLOSURE, "done"));
        assertEquals("close:1000:done:/127.0.0.1:12345:false", endpoint.events.getLast());
        assertSame(stub.session, endpoint.getSession());
        assertEquals("/127.0.0.1:12345", endpoint.getRemoteIdentifier());
        assertThrows(IOException.class, () -> endpoint.sendString("message:{}"));
    }

    @Test
    public void localCloseTranslatesCodeAndReasonAndSuppressesExpectedClosedChannelError() throws IOException {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        endpoint.sendClose(3001, "finished");
        assertEquals(3001, stub.closeReason.getCloseCode().getCode());
        assertEquals("finished", stub.closeReason.getReasonPhrase());
        assertFalse(endpoint.isConnected());
        endpoint.onError(stub.session, new ClosedChannelException());
        assertNull(endpoint.error);
    }

    @Test
    public void otherErrorsAfterLocalCloseStillReachTheErrorHook() throws IOException {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        endpoint.sendClose(1000, "");
        IOException failure = new IOException("synthetic failure");
        endpoint.onError(stub.session, failure);
        assertSame(failure, endpoint.error);
        assertEquals("error:/127.0.0.1:12345:false", endpoint.events.getLast());
    }

    @Test
    public void transportErrorClosesBeforeErrorCallback() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        IOException failure = new IOException("synthetic failure");
        endpoint.onError(stub.session, failure);
        assertFalse(stub.open);
        assertFalse(endpoint.isConnected());
        assertEquals(1011, stub.closeReason.getCloseCode().getCode());
        assertSame(failure, endpoint.error);
    }

    @Test
    public void reentrantCloseErrorDoesNotReplaceTheOriginalErrorOrRepeatCleanup() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        IOException failure = new IOException("original synthetic failure");
        AtomicInteger closeCalls = new AtomicInteger();
        stub.closeAction = () -> {
            if (closeCalls.getAndIncrement() == 0)
                endpoint.onError(stub.session, new IOException("synthetic error raised by close"));
        };
        endpoint.onError(stub.session, failure);
        assertEquals(1, closeCalls.get());
        assertSame(failure, endpoint.error);
        assertEquals(List.of("connect:/127.0.0.1:12345:true", "error:/127.0.0.1:12345:false"), endpoint.events);
    }

    @Test
    public void errorWithoutSessionStillReportsTheOriginalFailureAfterInitialization() {
        TestEndpoint endpoint = new TestEndpoint();
        endpoint.initializeRemoteIdentifier("/127.0.0.1:12345");
        IOException failure = new IOException("synthetic opening failure");
        endpoint.onError(null, failure);
        assertSame(failure, endpoint.error);
        assertFalse(endpoint.isConnected());
    }

    @Test
    public void errorCallbackStillRunsWhenTransportCloseFails() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        IOException failure = new IOException("synthetic failure");
        stub.closeFailure = new IOException("synthetic close failure");
        endpoint.onError(stub.session, failure);
        assertSame(failure, endpoint.error);
        assertSame(stub.closeFailure, failure.getSuppressed()[0]);
        assertFalse(endpoint.isConnected());
    }

    @Test
    public void closedSessionStateRacesDoNotEscapeAsIllegalStateException() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        stub.stateFailure = new IllegalStateException("session already closed");
        assertFalse(endpoint.isConnected());
        assertThrows(IOException.class, () -> endpoint.sendString("message:{}"));
    }

    @Test
    public void sendStateRacesAreTranslatedToIOExceptionAndTryHelpersReportFailure() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        stub.sendFailure = new IllegalStateException("synthetic send race");
        IOException failure = assertThrows(IOException.class, () -> endpoint.sendString("message:{}"));
        assertSame(stub.sendFailure, failure.getCause());
        assertFalse(endpoint.trySendAsJson("message", new Object(), false));
    }

    @Test
    public void closeStateRacesAreTranslatedToIOExceptionAndTryHelpersReportFailure() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        stub.closeFailure = new IllegalStateException("synthetic close race");
        IOException failure = assertThrows(IOException.class, () -> endpoint.sendClose(1000, ""));
        assertSame(stub.closeFailure, failure.getCause());
        assertFalse(endpoint.trySendClose());
    }

    @Test
    public void sendIOExceptionIsPreserved() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        stub.sendFailure = new IOException("synthetic send failure");
        assertSame(stub.sendFailure, assertThrows(IOException.class, () -> endpoint.sendString("message:{}")));
    }

    @Test
    public void closeIOExceptionIsPreservedAndTryHelperReportsFailure() {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        stub.closeFailure = new IOException("synthetic close failure");
        assertSame(stub.closeFailure, assertThrows(IOException.class, () -> endpoint.sendClose(1000, "")));
        assertFalse(endpoint.trySendClose());
    }

    @Test
    public void preLoginTransportErrorsDoNotInvokeAuthenticatedCallbacks() {
        TestProtectedEndpoint endpoint = new TestProtectedEndpoint(() -> "synthetic-test-key");
        SessionStub stub = open(endpoint);
        endpoint.onError(stub.session, new IOException("synthetic unauthenticated failure"));
        assertTrue(endpoint.events.isEmpty());
        assertFalse(endpoint.isConnected());
        assertFalse(stub.open);
    }

    @Test
    public void concurrentSendsAreSerializedAndPreserveEachCallersOrder() throws Exception {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(4)) {
            List<java.util.concurrent.Future<?>> sends = new ArrayList<>();
            for (int worker = 0; worker < 4; worker++) {
                String prefix = worker + ":";
                sends.add(executor.submit(() -> {
                    start.await();
                    for (int message = 0; message < 50; message++)
                        endpoint.sendString(prefix + message);
                    return null;
                }));
            }
            start.countDown();
            for (var send : sends)
                send.get(5, TimeUnit.SECONDS);
        }
        assertEquals(1, stub.maxConcurrentSends.get());
        assertEquals(200, stub.sent.size());
        for (int worker = 0; worker < 4; worker++) {
            String prefix = worker + ":";
            List<String> expected = new ArrayList<>();
            for (int message = 0; message < 50; message++)
                expected.add(prefix + message);
            assertEquals(expected, stub.sent.stream().filter(message -> message.startsWith(prefix)).toList());
        }
    }

    @Test
    public void closeDoesNotHoldTheSendLockWhileRunningLifecycleCallbacks() throws IOException {
        TestEndpoint endpoint = new TestEndpoint();
        SessionStub stub = open(endpoint);
        stub.closeAction = () -> CompletableFuture.runAsync(() ->
                        assertThrows(IOException.class, () -> endpoint.sendString("message:{}")))
                .orTimeout(3, TimeUnit.SECONDS).join();
        endpoint.sendClose(1000, "");
        assertFalse(endpoint.isConnected());
    }

    @Test
    public void authenticationUsesSupplierAndAttemptsSuccessSendBeforeCallbackEvenOnSendFailure() {
        String[] currentKey = {"original-synthetic-key"};
        TestProtectedEndpoint endpoint = new TestProtectedEndpoint(() -> currentKey[0]);
        SessionStub stub = open(endpoint);
        assertEquals(10000, stub.idleTimeout);
        currentKey[0] = "replacement-synthetic-key";
        stub.sendFailure = new IOException("synthetic send failure");
        endpoint.onReceivePayload(new LoginPayload(currentKey[0]));
        assertEquals(List.of("login"), endpoint.events);
        assertEquals(List.of("login-succeed:{}"), stub.sent);
        assertEquals(Duration.ofDays(1000).toMillis(), stub.idleTimeout);
    }

    private static SessionStub open(AbstractServerWS endpoint) {
        SessionStub stub = new SessionStub();
        endpoint.initializeRemoteIdentifier("/127.0.0.1:12345");
        endpoint.onOpen(stub.session, null);
        return stub;
    }

    private static class TestEndpoint extends AbstractServerWS {
        final List<String> events = new CopyOnWriteArrayList<>();
        Throwable error;

        @Override
        public void onConnect() {
            events.add("connect:" + getRemoteIdentifier() + ":" + isConnected());
        }

        @Override
        public void onReceivePayload(Payload payload) {
            events.add("payload");
        }

        @Override
        public void onClose(int code, String reason) {
            events.add("close:" + code + ":" + reason + ":" + getRemoteIdentifier() + ":" + isConnected());
        }

        @Override
        public void onError(Throwable cause) {
            error = cause;
            events.add("error:" + getRemoteIdentifier() + ":" + isConnected());
        }

        @Override
        public void logError(String message, Throwable cause) {
        }
    }

    private static class TestProtectedEndpoint extends KeyProtectedServerWS {
        final List<String> events = new ArrayList<>();

        TestProtectedEndpoint(Supplier<String> supplier) {
            super(supplier);
        }

        @Override
        public void onLoginSucceed() {
            events.add("login");
        }

        @Override
        public void onReceivePayloadLoggedIn(Payload payload) {
            events.add("payload");
        }

        @Override
        public void onCloseLoggedIn(int code, String reason) {
            events.add("close");
        }

        @Override
        public void onErrorLoggedIn(Throwable cause) {
            events.add("error");
        }

        @Override
        public void logError(String message, Throwable cause) {
        }
    }

    private static class SessionStub {
        boolean open = true;
        long idleTimeout;
        CloseReason closeReason;
        Throwable sendFailure;
        Throwable closeFailure;
        RuntimeException stateFailure;
        Runnable closeAction = () -> {
        };
        final List<String> sent = new CopyOnWriteArrayList<>();
        final AtomicInteger activeSends = new AtomicInteger();
        final AtomicInteger maxConcurrentSends = new AtomicInteger();
        final Basic remote = (Basic) Proxy.newProxyInstance(Basic.class.getClassLoader(), new Class<?>[]{Basic.class}, (proxy, method, arguments) -> {
            if (!method.getName().equals("sendText"))
                throw new UnsupportedOperationException(method.getName());
            maxConcurrentSends.accumulateAndGet(activeSends.incrementAndGet(), Math::max);
            try {
                sent.add((String) arguments[0]);
                Thread.yield();
                if (sendFailure != null)
                    throw sendFailure;
                return null;
            } finally {
                activeSends.decrementAndGet();
            }
        });
        final Session session = (Session) Proxy.newProxyInstance(Session.class.getClassLoader(), new Class<?>[]{Session.class}, (proxy, method, arguments) -> {
            return switch (method.getName()) {
                case "isOpen" -> {
                    if (stateFailure != null)
                        throw stateFailure;
                    yield open;
                }
                case "setMaxIdleTimeout" -> {
                    idleTimeout = (Long) arguments[0];
                    yield null;
                }
                case "getBasicRemote" -> remote;
                case "addMessageHandler" -> null;
                case "close" -> {
                    if (closeFailure != null)
                        throw closeFailure;
                    closeReason = (CloseReason) arguments[0];
                    open = false;
                    closeAction.run();
                    yield null;
                }
                default -> throw new UnsupportedOperationException(method.getName());
            };
        });
    }
}
