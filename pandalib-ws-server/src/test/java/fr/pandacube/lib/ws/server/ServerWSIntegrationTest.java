package fr.pandacube.lib.ws.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.CloseReason;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.ee11.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.pandacube.lib.ws.PayloadRegistry;
import fr.pandacube.lib.ws.payloads.ErrorPayload;
import fr.pandacube.lib.ws.payloads.MessagePayload;
import fr.pandacube.lib.ws.payloads.Payload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ServerWSIntegrationTest {

    private static final Map<String, Class<? extends AbstractServerWS>> ROUTES = Map.of(
            "/ping", PlainEndpoint.class,
            "/mc/backend", ProtectedEndpoint.class,
            "/mc/proxy", ProtectedEndpoint.class,
            "/mc/launcher", ProtectedEndpoint.class);

    private final Map<String, AbstractServerWS> endpoints = new ConcurrentHashMap<>();
    private final List<WebSocket> clients = new CopyOnWriteArrayList<>();
    private HttpClient httpClient;
    private Server server;
    private ServerConnector connector;
    private ServerContainer container;

    @Before
    public void start() throws Exception {
        httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        startContainer();
    }

    @After
    public void stop() throws Exception {
        for (WebSocket client : clients)
            client.abort();
        if (server != null)
            server.stop();
        if (httpClient != null)
            httpClient.close();
    }

    private void startContainer() throws Exception {
        startContainer(0);
    }

    private void startContainer(int port) throws Exception {
        server = new Server();
        connector = new ServerConnector(server);
        connector.setHost("127.0.0.1");
        connector.setPort(port);
        server.addConnector(connector);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
        JakartaWebSocketServletContainerInitializer.configure(context, (servletContext, serverContainer) -> container = serverContainer);
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                Class<? extends AbstractServerWS> endpointType = ROUTES.get(request.getPathInfo());
                if (endpointType == null) {
                    response.sendError(404);
                    return;
                }
                String identifier = new InetSocketAddress(InetAddress.getByName(request.getRemoteAddr()), request.getRemotePort()).toString();
                ServerEndpointConfig configuration = ServerEndpointConfig.Builder.create(endpointType, request.getPathInfo())
                        .configurator(new ServerEndpointConfig.Configurator() {
                            @Override
                            public <EndpointType> EndpointType getEndpointInstance(Class<EndpointType> requestedType) throws InstantiationException {
                                EndpointType instance = super.getEndpointInstance(requestedType);
                                AbstractServerWS endpoint = (AbstractServerWS) instance;
                                endpoint.initializeRemoteIdentifier(identifier);
                                endpoints.put(request.getParameter("connection"), endpoint);
                                return instance;
                            }
                        }).build();
                try {
                    container.upgradeHttpToWebSocket(request, response, configuration, Map.of());
                } catch (DeploymentException exception) {
                    throw new ServletException(exception);
                }
            }
        }), "/*");
        server.start();
    }

    @Test
    public void plainEndpointSendsFromConnectAndEchoesSharedGsonPayloads() throws Exception {
        Connection connection = connect("/ping");
        PlainEndpoint endpoint = (PlainEndpoint) connection.endpoint;
        assertEquals("message:{\"message\":\"connected\"}", connection.messages.next());
        assertEquals("connect:" + endpoint.getRemoteIdentifier(), endpoint.probe.events.getFirst());
        assertTrue(endpoint.getRemoteIdentifier().matches("/127\\.0\\.0\\.1:\\d+"));
        connection.send("message:{\"message\":\"héllo <world> 🙂\"}");
        assertEquals("message:{\"message\":\"héllo <world> 🙂\"}", connection.messages.next());
        assertTrue(endpoint.isConnected());
        assertEquals(Duration.ofDays(1000).toMillis(), endpoint.getSession().getMaxIdleTimeout());
    }

    @Test
    public void successfulLoginPrecedesCallbackMessagesAndAuthenticatedPayloads() throws Exception {
        Connection connection = connect("/mc/backend");
        ProtectedEndpoint endpoint = (ProtectedEndpoint) connection.endpoint;
        assertEquals(10000, endpoint.getSession().getMaxIdleTimeout());
        login(connection);
        assertEquals(List.of("login"), endpoint.probe.events);
        assertEquals(Duration.ofDays(1000).toMillis(), endpoint.getSession().getMaxIdleTimeout());
        connection.send("message:{\"message\":\"round trip\"}");
        assertEquals("message:{\"message\":\"round trip\"}", connection.messages.next());
        assertEquals(List.of("login", "payload"), endpoint.probe.events);
    }

    @Test
    public void badKeySendsExistingErrorThenNormalCloseWithoutAuthenticatedCallbacks() throws Exception {
        Connection connection = connect("/mc/backend");
        ProtectedEndpoint endpoint = (ProtectedEndpoint) connection.endpoint;
        connection.send("login:{\"key\":\"bad-synthetic-key\"}");
        assertEquals("Bad key", ((ErrorPayload) PayloadRegistry.fromString(connection.messages.next())).message);
        CloseReason close = connection.messages.closed.get(5, TimeUnit.SECONDS);
        assertEquals(1000, close.getCloseCode().getCode());
        assertEquals("", close.getReasonPhrase());
        awaitDisconnected(endpoint);
        assertTrue(endpoint.probe.events.isEmpty());
        assertTrue(endpoint.probe.logs.stream().noneMatch(message -> message.contains("bad-synthetic-key")));
    }

    @Test
    public void unexpectedFirstPayloadSendsExistingErrorThenCloses() throws Exception {
        Connection connection = connect("/mc/proxy");
        connection.send("message:{\"message\":\"too early\"}");
        ErrorPayload error = (ErrorPayload) PayloadRegistry.fromString(connection.messages.next());
        assertEquals("Please use the login packet first. Received MessagePayload instead.", error.message);
        assertEquals(1000, connection.messages.closed.get(5, TimeUnit.SECONDS).getCloseCode().getCode());
        assertTrue(((ProtectedEndpoint) connection.endpoint).probe.events.isEmpty());
    }

    @Test
    public void malformedUnknownAndBinaryMessagesRemainRecoverableBeforeLogin() throws Exception {
        Connection connection = connect("/mc/launcher");
        for (String malformed : List.of("malformed", "unknown:{}", "message:{\"message\":")) {
            connection.send(malformed);
            assertTrue(PayloadRegistry.fromString(connection.messages.next()) instanceof ErrorPayload);
        }
        connection.socket.sendBinary(ByteBuffer.wrap(new byte[]{1, 2, 3}), true).get(5, TimeUnit.SECONDS);
        assertEquals("Cannot accept binary payload.", ((ErrorPayload) PayloadRegistry.fromString(connection.messages.next())).message);
        assertTrue(connection.endpoint.isConnected());
        assertTrue(((ProtectedEndpoint) connection.endpoint).probe.events.isEmpty());
        login(connection);
    }

    @Test
    public void receivedErrorPayloadKeepsItsSeparatePreLoginHandling() throws Exception {
        Connection connection = connect("/mc/backend");
        connection.send("error:{\"message\":\"synthetic peer error\"}");
        login(connection);
        assertTrue(((ProtectedEndpoint) connection.endpoint).probe.logs.stream().anyMatch(message -> message.contains("synthetic peer error")));
    }

    @Test
    public void fragmentedLoginAndTextAreDispatchedOnlyWhenComplete() throws Exception {
        Connection connection = connect("/mc/backend");
        ProtectedEndpoint endpoint = (ProtectedEndpoint) connection.endpoint;
        connection.socket.sendText("login:{\"key\":\"synthetic-", false).get(5, TimeUnit.SECONDS);
        assertTrue(endpoint.probe.events.isEmpty());
        assertNull(connection.messages.received.poll(150, TimeUnit.MILLISECONDS));
        connection.socket.sendText("test-key\"}", true).get(5, TimeUnit.SECONDS);
        assertEquals("login-succeed:{}", connection.messages.next());
        assertEquals("message:{\"message\":\"authenticated\"}", connection.messages.next());
        connection.socket.sendText("message:{\"message\":\"fragmented ", false).get(5, TimeUnit.SECONDS);
        assertNull(connection.messages.received.poll(150, TimeUnit.MILLISECONDS));
        assertEquals(List.of("login"), endpoint.probe.events);
        connection.socket.sendText("héllo 🙂\"}", true).get(5, TimeUnit.SECONDS);
        assertEquals("message:{\"message\":\"fragmented héllo 🙂\"}", connection.messages.next());
        assertEquals(List.of("login", "payload"), endpoint.probe.events);
    }

    @Test
    public void fragmentedBinaryProducesOneErrorForTheWholeMessage() throws Exception {
        Connection connection = connect("/mc/backend");
        connection.socket.sendBinary(ByteBuffer.wrap(new byte[]{1}), false).get(5, TimeUnit.SECONDS);
        assertNull(connection.messages.received.poll(150, TimeUnit.MILLISECONDS));
        connection.socket.sendBinary(ByteBuffer.wrap(new byte[]{2}), true).get(5, TimeUnit.SECONDS);
        assertEquals("Cannot accept binary payload.", ((ErrorPayload) PayloadRegistry.fromString(connection.messages.next())).message);
        assertNull(connection.messages.received.poll(150, TimeUnit.MILLISECONDS));
        login(connection);
    }

    @Test
    public void payloadCallbackFailureSendsErrorWithoutDisconnecting() throws Exception {
        Connection connection = connect("/mc/backend");
        login(connection);
        connection.send("message:{\"message\":\"throw\"}");
        ErrorPayload error = (ErrorPayload) PayloadRegistry.fromString(connection.messages.next());
        assertTrue(error.message.contains("synthetic payload failure"));
        assertTrue(connection.endpoint.isConnected());
        assertFalse(((ProtectedEndpoint) connection.endpoint).probe.error.isDone());
        connection.send("message:{\"message\":\"still usable\"}");
        assertEquals("message:{\"message\":\"still usable\"}", connection.messages.next());
    }

    @Test(timeout = 25000)
    public void unauthenticatedIdleTimeoutClosesWhileAuthenticatedConnectionSurvives() throws Exception {
        Connection unauthenticated = connect("/mc/backend");
        Connection authenticated = connect("/mc/proxy");
        login(authenticated);
        CompletableFuture<Void> elapsed = new CompletableFuture<>();
        CompletableFuture.delayedExecutor(11, TimeUnit.SECONDS).execute(() -> elapsed.complete(null));
        unauthenticated.messages.closed.get(15, TimeUnit.SECONDS);
        elapsed.get(4, TimeUnit.SECONDS);
        awaitDisconnected(unauthenticated.endpoint);
        assertTrue(((ProtectedEndpoint) unauthenticated.endpoint).probe.events.isEmpty());
        assertTrue(authenticated.endpoint.isConnected());
        assertFalse(authenticated.messages.closed.isDone());
        authenticated.send("message:{\"message\":\"after idle\"}");
        assertEquals("message:{\"message\":\"after idle\"}", authenticated.messages.next());
    }

    @Test
    public void concurrentUpgradesUseFreshEndpointsAndDistinctDirectSocketIdentifiers() throws Exception {
        List<Connection> connections = new ArrayList<>();
        try (var executor = Executors.newFixedThreadPool(8)) {
            List<java.util.concurrent.Future<Connection>> upgrades = new ArrayList<>();
            for (String path : ROUTES.keySet()) {
                upgrades.add(executor.submit(() -> connect(path)));
                upgrades.add(executor.submit(() -> connect(path)));
            }
            for (var upgrade : upgrades)
                connections.add(upgrade.get(5, TimeUnit.SECONDS));
        }
        assertEquals(8, new HashSet<>(connections.stream().map(connection -> connection.endpoint).toList()).size());
        assertEquals(8, new HashSet<>(connections.stream().map(connection -> connection.endpoint.getRemoteIdentifier()).toList()).size());
        for (Connection connection : connections) {
            String identifier = connection.endpoint.getRemoteIdentifier();
            assertTrue(identifier.matches("/127\\.0\\.0\\.1:\\d+"));
            assertThrows(IllegalStateException.class, () -> connection.endpoint.initializeRemoteIdentifier("replacement"));
            assertEquals(identifier, connection.endpoint.getRemoteIdentifier());
        }
    }

    @Test
    public void forwardedHeadersDoNotReplaceTheDirectSocketAddress() throws Exception {
        Connection connection = connect("/ping", true);
        connection.messages.next();
        assertTrue(connection.endpoint.getRemoteIdentifier().startsWith("/127.0.0.1:"));
        assertFalse(connection.endpoint.getRemoteIdentifier().contains("203.0.113.9"));
    }

    @Test(timeout = 15000)
    public void concurrentCallbackAndLockHoldingSendsAreSerializedAndOrdered() throws Exception {
        Connection connection = connect("/ping");
        connection.messages.next();
        PlainEndpoint endpoint = (PlainEndpoint) connection.endpoint;
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(4)) {
            List<java.util.concurrent.Future<?>> sends = new ArrayList<>();
            for (int worker = 0; worker < 4; worker++) {
                String prefix = worker + ":";
                boolean holdApplicationLock = worker % 2 == 0;
                sends.add(executor.submit(() -> {
                    start.await();
                    for (int message = 0; message < 25; message++) {
                        MessagePayload payload = new MessagePayload(prefix + message);
                        if (holdApplicationLock) {
                            synchronized (endpoint.applicationLock) {
                                endpoint.sendAsJson(payload);
                            }
                        } else {
                            endpoint.sendAsJson(payload);
                        }
                    }
                    return null;
                }));
            }
            connection.send("message:{\"message\":\"callback\"}");
            start.countDown();
            for (var send : sends)
                send.get(5, TimeUnit.SECONDS);
        }
        List<String> received = new ArrayList<>();
        for (int message = 0; message < 101; message++)
            received.add(((MessagePayload) PayloadRegistry.fromString(connection.messages.next())).message);
        assertTrue(received.contains("callback"));
        assertEquals(101, new HashSet<>(received).size());
        for (int worker = 0; worker < 4; worker++) {
            String prefix = worker + ":";
            List<String> expected = new ArrayList<>();
            for (int message = 0; message < 25; message++)
                expected.add(prefix + message);
            assertEquals(expected, received.stream().filter(message -> message.startsWith(prefix)).toList());
        }
    }

    @Test
    public void remoteClosePreservesCallbackIdentityAddressCodeAndReason() throws Exception {
        Connection connection = connect("/mc/backend");
        login(connection);
        ProtectedEndpoint endpoint = (ProtectedEndpoint) connection.endpoint;
        String identifier = endpoint.getRemoteIdentifier();
        connection.socket.sendClose(3001, "finished").get(5, TimeUnit.SECONDS);
        CloseReason close = endpoint.probe.closed.get(5, TimeUnit.SECONDS);
        assertEquals(3001, close.getCloseCode().getCode());
        assertEquals("finished", close.getReasonPhrase());
        assertEquals(List.of("login", "close:" + identifier + ":false"), endpoint.probe.events);
        assertEquals(identifier, endpoint.getRemoteIdentifier());
        assertFalse(endpoint.isConnected());
        assertThrows(IOException.class, () -> endpoint.sendString("message:{}"));
    }

    @Test
    public void transportErrorDisconnectsBeforeAuthenticatedErrorCleanup() throws Exception {
        Connection connection = connect("/mc/backend");
        login(connection);
        ProtectedEndpoint endpoint = (ProtectedEndpoint) connection.endpoint;
        String identifier = endpoint.getRemoteIdentifier();
        IOException failure = new IOException("synthetic transport failure");
        endpoint.onError(endpoint.getSession(), failure);
        assertEquals(failure, endpoint.probe.error.get(5, TimeUnit.SECONDS));
        assertFalse(endpoint.isConnected());
        assertTrue(endpoint.probe.errorSawClosedSession);
        assertEquals(1011, connection.messages.closed.get(5, TimeUnit.SECONDS).getCloseCode().getCode());
        assertEquals(identifier, endpoint.getRemoteIdentifier());
        assertThrows(IOException.class, () -> endpoint.sendString("message:{}"));
    }

    @Test
    public void abortedClientProducesDisconnectedAuthenticatedCleanup() throws Exception {
        Connection connection = connect("/mc/backend");
        login(connection);
        ProtectedEndpoint endpoint = (ProtectedEndpoint) connection.endpoint;
        String identifier = endpoint.getRemoteIdentifier();
        connection.socket.abort();
        CompletableFuture.anyOf(endpoint.probe.closed, endpoint.probe.error).get(5, TimeUnit.SECONDS);
        awaitDisconnected(endpoint);
        assertEquals(identifier, endpoint.getRemoteIdentifier());
    }

    @Test
    public void stoppingServerClosesOldSessionAndNewServerAcceptsFreshConnection() throws Exception {
        Connection previous = connect("/mc/backend");
        login(previous);
        ProtectedEndpoint previousEndpoint = (ProtectedEndpoint) previous.endpoint;
        int port = connector.getLocalPort();
        server.stop();
        previousEndpoint.probe.closed.get(5, TimeUnit.SECONDS);
        assertFalse(previousEndpoint.isConnected());
        startContainer(port);
        Connection next = connect("/mc/backend");
        login(next);
        assertNotSame(previous.endpoint, next.endpoint);
        assertTrue(next.endpoint.isConnected());
    }

    private Connection connect(String path) throws Exception {
        return connect(path, false);
    }

    private Connection connect(String path, boolean forwardedHeader) throws Exception {
        String connectionId = UUID.randomUUID().toString();
        ClientMessages messages = new ClientMessages();
        WebSocket.Builder builder = httpClient.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(5));
        if (forwardedHeader) {
            builder.header("X-Forwarded-For", "203.0.113.9");
            builder.header("Forwarded", "for=203.0.113.9");
        }
        WebSocket socket = builder.buildAsync(URI.create("ws://127.0.0.1:" + connector.getLocalPort() + path + "?connection=" + connectionId), messages)
                .get(5, TimeUnit.SECONDS);
        clients.add(socket);
        AbstractServerWS endpoint = endpoints.remove(connectionId);
        assertNotNull(endpoint);
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (!endpoint.isConnected() && System.nanoTime() < deadline)
            Thread.sleep(10);
        assertTrue(endpoint.isConnected());
        if (endpoint instanceof ProtectedEndpoint) {
            while (endpoint.getSession().getMaxIdleTimeout() != 10000 && System.nanoTime() < deadline)
                Thread.sleep(10);
            assertEquals(10000, endpoint.getSession().getMaxIdleTimeout());
        }
        return new Connection(socket, messages, endpoint);
    }

    private static void login(Connection connection) throws Exception {
        connection.send("login:{\"key\":\"synthetic-test-key\"}");
        assertEquals("login-succeed:{}", connection.messages.next());
        assertEquals("message:{\"message\":\"authenticated\"}", connection.messages.next());
    }

    private static void awaitDisconnected(AbstractServerWS endpoint) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (endpoint.isConnected() && System.nanoTime() < deadline)
            Thread.sleep(10);
        assertFalse(endpoint.isConnected());
    }

    private record Connection(WebSocket socket, ClientMessages messages, AbstractServerWS endpoint) {
        void send(String message) throws Exception {
            socket.sendText(message, true).get(5, TimeUnit.SECONDS);
        }
    }

    private static class ClientMessages implements WebSocket.Listener {
        final BlockingQueue<String> received = new LinkedBlockingQueue<>();
        final CompletableFuture<CloseReason> closed = new CompletableFuture<>();
        private final StringBuilder partial = new StringBuilder();

        @Override
        public CompletionStage<?> onText(WebSocket socket, CharSequence text, boolean last) {
            partial.append(text);
            if (last) {
                received.add(partial.toString());
                partial.setLength(0);
            }
            socket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket socket, int code, String reason) {
            closed.complete(new CloseReason(CloseReason.CloseCodes.getCloseCode(code), reason));
            return null;
        }

        @Override
        public void onError(WebSocket socket, Throwable cause) {
            closed.completeExceptionally(cause);
        }

        String next() throws InterruptedException {
            String message = received.poll(5, TimeUnit.SECONDS);
            assertNotNull("Expected a complete server message", message);
            return message;
        }
    }

    private static class Probe {
        final List<String> events = new CopyOnWriteArrayList<>();
        final List<String> logs = new CopyOnWriteArrayList<>();
        final CompletableFuture<CloseReason> closed = new CompletableFuture<>();
        final CompletableFuture<Throwable> error = new CompletableFuture<>();
        volatile boolean errorSawClosedSession;
    }

    public static class PlainEndpoint extends AbstractServerWS {
        final Probe probe = new Probe();
        final Object applicationLock = new Object();

        @Override
        public void onConnect() {
            probe.events.add("connect:" + getRemoteIdentifier());
            trySendAsJson(new MessagePayload("connected"));
        }

        @Override
        public void onReceivePayload(Payload payload) {
            synchronized (applicationLock) {
                trySendAsJson(payload);
            }
        }

        @Override
        public void onClose(int code, String reason) {
            probe.closed.complete(new CloseReason(CloseReason.CloseCodes.getCloseCode(code), reason));
        }

        @Override
        public void onError(Throwable cause) {
            probe.error.complete(cause);
        }

        @Override
        public void logError(String message, Throwable cause) {
            probe.logs.add(message);
        }
    }

    public static class ProtectedEndpoint extends KeyProtectedServerWS {
        final Probe probe = new Probe();

        public ProtectedEndpoint() {
            super(() -> "synthetic-test-key");
        }

        @Override
        public void onLoginSucceed() {
            probe.events.add("login");
            trySendAsJson(new MessagePayload("authenticated"));
        }

        @Override
        public void onReceivePayloadLoggedIn(Payload payload) {
            probe.events.add("payload");
            if (payload instanceof MessagePayload message && message.message.equals("throw"))
                throw new IllegalArgumentException("synthetic payload failure");
            trySendAsJson(payload);
        }

        @Override
        public void onCloseLoggedIn(int code, String reason) {
            probe.events.add("close:" + getRemoteIdentifier() + ":" + isConnected());
            probe.closed.complete(new CloseReason(CloseReason.CloseCodes.getCloseCode(code), reason));
        }

        @Override
        public void onErrorLoggedIn(Throwable cause) {
            probe.events.add("error:" + getRemoteIdentifier() + ":" + isConnected());
            probe.errorSawClosedSession = !getSession().isOpen();
            probe.error.complete(cause);
        }

        @Override
        public void logError(String message, Throwable cause) {
            probe.logs.add(message);
        }
    }
}
