package fr.pandacube.java.util.network.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import org.apache.commons.lang.builder.ToStringBuilder;

import fr.pandacube.java.Pandacube;
import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.network.packet.Packet;
import fr.pandacube.java.util.network.packet.PacketClient;
import fr.pandacube.java.util.network.packet.PacketException;
import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.ResponseCallback;
import fr.pandacube.java.util.network.packet.packets.global.PacketServerException;
import org.javatuples.Pair;

public class TCPClient extends Thread implements Closeable {

	private Socket socket;
	private SocketAddress addr;
	private TCPClientListener listener;
	private InputStream in;
	private OutputStream out;
	private Object outSynchronizer = new Object();
	private List<Pair<Predicate<PacketServer>, ResponseCallback<PacketServer>>> callbacks = Collections.synchronizedList(new ArrayList<>());
	private List<Pair<Predicate<PacketServer>, ResponseCallback<PacketServer>>> callbacksAvoidListener = Collections.synchronizedList(new ArrayList<>());

	private AtomicBoolean isClosed = new AtomicBoolean(false);

	public TCPClient(InetSocketAddress a, String connName, TCPClientListener l) throws IOException {
		super("TCPCl " + connName);
		setDaemon(true);
		if (a == null || l == null) throw new IllegalArgumentException("les arguments ne peuvent pas être null");
		socket = new Socket();
		socket.setReceiveBufferSize(Pandacube.NETWORK_TCP_BUFFER_SIZE);
		socket.setSendBufferSize(Pandacube.NETWORK_TCP_BUFFER_SIZE);
		socket.setSoTimeout(Pandacube.NETWORK_TIMEOUT);
		socket.connect(a);
		addr = a;
		listener = l;
		try {
			listener.onConnect(this);
		} catch (Exception e) {
			Log.severe("Exception while calling TCPClientListener.onConnect()", e);
		}
	}

	@Override
	public void run() {

		try {
			byte[] code = new byte[1];
			while (!socket.isClosed() && in.read(code) != -1) {
				byte[] sizeB = new byte[4];
				if (in.read(sizeB) != 4) throw new IOException("Socket " + addr + " fermé");

				int size = ByteBuffer.wrap(sizeB).getInt();

				byte[] content = new byte[size];

				forceReadBytes(content);

				byte[] packetData = ByteBuffer.allocate(1 + 4 + size).put(code).put(sizeB).put(content).array();

				try {
					
					Packet p = Packet.constructPacket(packetData);

					if (!(p instanceof PacketServer))
						throw new PacketException(p.getClass().getCanonicalName() + " is not a subclass of PacketServer");
					
					if (p instanceof PacketServerException) {

						try {
							listener.onServerException(this, ((PacketServerException)p).getExceptionString());
						} catch (Exception e) {
							Log.severe("Exception while calling TCPClientListener.onServerException()", e);
						}
					}
					
					PacketServer ps = (PacketServer) p;

					boolean callbackExecuted = executeCallbacks(ps, callbacksAvoidListener);
					
					try {
						if (!callbackExecuted)
							listener.onPacketReceive(this, ps);
					} catch (Exception e) {
						Log.severe("Exception while calling TCPClientListener.onPacketReceive()", e);
					}
					
					executeCallbacks(ps, callbacks);
					
				} catch (Exception e) {
					Log.severe("Exception while handling packet from server", e);
				}
			}

		} catch (Exception e) {
			Log.severe(e);
		}
		close();
	}
	
	
	private boolean executeCallbacks(PacketServer ps, List<Pair<Predicate<PacketServer>, ResponseCallback<PacketServer>>> callbacks) {
		boolean executedOne = false;
		synchronized (callbacks) {
			for(Iterator<Pair<Predicate<PacketServer>, ResponseCallback<PacketServer>>> it = callbacks.iterator(); it.hasNext();) {
				Pair<Predicate<PacketServer>, ResponseCallback<PacketServer>> c = it.next();
				try {
					if (c.getValue0().test(ps)) {
						it.remove();
						c.getValue1().call(ps);
						executedOne = true;
					}
				} catch (Exception e) {
					Log.severe("Exception while executing callback", e);
				}
			}
		}
		return executedOne;
	}
	

	private void forceReadBytes(byte[] buff) throws IOException {
		int pos = 0;
		do {
			int nbR = in.read(buff, pos, buff.length - pos);
			if (nbR == -1) throw new IOException("Can't read required amount of byte");
			pos += nbR;
		} while (pos < buff.length);
	}

	public void send(PacketClient packet) throws IOException {
		synchronized (outSynchronizer) {
			out.write(packet.getFullSerializedPacket());
			out.flush();
		}
	}

	
	public void sendAndGetResponse(PacketClient packet, Predicate<PacketServer> responseCondition, ResponseCallback<PacketServer> callback, boolean avoidListener) throws IOException {
		Pair<Predicate<PacketServer>, ResponseCallback<PacketServer>> p = new Pair<>(responseCondition, callback);
		if (avoidListener)
			callbacksAvoidListener.add(p);
		else
			callbacks.add(p);
		send(packet);
	}
	
	
	public PacketServer sendAndWaitForResponse(PacketClient packet, Predicate<PacketServer> responseCondition) throws IOException, InterruptedException {
		AtomicReference<PacketServer> psStorage = new AtomicReference<>(null);
		synchronized (psStorage) {
			sendAndGetResponse(packet, responseCondition, packetServer -> {
				synchronized (psStorage) {
					psStorage.set(packetServer);
					psStorage.notifyAll();
				}
			}, true);
			
			psStorage.wait();
			return psStorage.get();
		}
	}
	
	

	@Override
	public void close() {
		try {
			synchronized (outSynchronizer) {
				if (isClosed.get()) return;
				socket.close();
				isClosed.set(true);

				try {
					listener.onDisconnect(this);
				} catch (Exception e) {
					Log.severe("Exception while calling TCPClientListener.onDisconnect()", e);
				}
			}
		} catch (IOException e) {
			Log.warning(e);
		}
	}

	public void sendSilently(PacketClient packet) {
		try {
			send(packet);
		} catch (IOException e) {}
	}

	public SocketAddress getServerAddress() {
		return addr;
	}

	public boolean isClosed() {
		return isClosed.get() || socket.isClosed();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("thread", getName())
				.append("socket", socket.getRemoteSocketAddress()).toString();
	}

}
