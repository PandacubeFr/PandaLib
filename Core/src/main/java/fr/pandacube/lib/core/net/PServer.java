package fr.pandacube.lib.core.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.MoreObjects;

import fr.pandacube.lib.core.util.Log;

public class PServer extends Thread implements Closeable {
	private static final AtomicInteger connectionCounterId = new AtomicInteger(0);

	private final int port;
	private ServerSocket socket;
	private final String socketName;

	private final List<TCPServerClientConnection> clients = Collections.synchronizedList(new ArrayList<>());

	private final AtomicBoolean isClosed = new AtomicBoolean(false);
	

	private final List<PPacketListener<PPacket>> globalPacketListeners = Collections.synchronizedList(new ArrayList<>());
	private final List<PSocketConnectionListener> clientConnectionListeners = Collections.synchronizedList(new ArrayList<>());
	
	
	
	private final String password;

	public PServer(int port, String sckName, String password) {
		super("PServer " + sckName);
		setDaemon(true);
		if (port <= 0 || port > 65535) throw new IllegalArgumentException("le numéro de port est invalide");
		socketName = sckName;
		this.port = port;
		this.password = password;
	}

	@Override
	public void run() {

		try {
			
			socket = new ServerSocket();
			socket.setReceiveBufferSize(PSocket.NETWORK_TCP_BUFFER_SIZE);
			socket.setPerformancePreferences(0, 1, 0);
			socket.bind(new InetSocketAddress(port));
			
			while (true) {
				Socket socketClient = socket.accept();
				socketClient.setSendBufferSize(PSocket.NETWORK_TCP_BUFFER_SIZE);
				socketClient.setSoTimeout(PSocket.NETWORK_TIMEOUT);

				TCPServerClientConnection co = new TCPServerClientConnection(socketClient,
						connectionCounterId.getAndIncrement());
				co.start();
			}
		} catch (SocketException ignored) {
		} catch (Exception e) {
			Log.warning("Plus aucune connexion ne peux être acceptée", e);
		}
	}
	

	
	public void addPacketListener(PPacketListener<PPacket> l) {
		globalPacketListeners.add(l);
	}
	
	public boolean removePacketListener(PPacketListener<PPacket> l) {
		return globalPacketListeners.remove(l);
	}

	public void addConnectionListener(PSocketConnectionListener l) {
		clientConnectionListeners.add(l);
	}
	
	public void removeConnectionListener(PSocketConnectionListener l) {
		clientConnectionListeners.remove(l);
	}

	protected class TCPServerClientConnection extends PSocket {
		
		boolean loggedIn;

		private TCPServerClientConnection(Socket s, int coId) {
			super(s, "Conn#" + coId + " via TCPSv " + socketName, password);
			addConnectionListener(new PSocketConnectionListener() {
				@Override
				public void onDisconnect(PSocket connection) {
					try {
						clientConnectionListeners.forEach(l -> l.onDisconnect(connection));
					} finally {
						clients.remove((TCPServerClientConnection)connection);
					}
				}
				@Override
				public void onConnect(PSocket connection) {
					clients.add((TCPServerClientConnection)connection);
					clientConnectionListeners.forEach(l -> l.onConnect(connection));
				}
			});
			addPacketListener((conn, packet) ->
					globalPacketListeners.forEach(l -> {
						try {
							l.onPacketReceive(conn, packet);
						} catch (Exception e) {
							Log.severe("Exception while calling PPacketListener.onPacketReceive().", e);
							sendSilently(PPacketAnswer.buildExceptionPacket(packet, e.toString()));
						}
					})
			);
		}

	}

	@Override
	public void close() {
		try {
			if (isClosed.get()) return;
			isClosed.set(true);

			clients.forEach(PSocket::close);

			socket.close();
		} catch (IOException ignored) {}
	}

	public boolean isClosed() {
		return isClosed.get() || socket.isClosed();
	}
	
	
	
	public List<PSocket> getClients() {
		synchronized (clients) {
			return new ArrayList<>(clients);
		}
	}
	

	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("thread", getName())
				.add("socket", socket)
				.toString();
	}

}
