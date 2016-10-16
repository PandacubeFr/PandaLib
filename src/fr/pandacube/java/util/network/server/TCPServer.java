package fr.pandacube.java.util.network.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.apache.commons.lang.builder.ToStringBuilder;

import fr.pandacube.java.Pandacube;
import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.network.packet.Packet;
import fr.pandacube.java.util.network.packet.PacketClient;
import fr.pandacube.java.util.network.packet.PacketException;
import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.ResponseCallback;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;
import fr.pandacube.java.util.network.packet.packets.global.PacketServerException;
import javafx.util.Pair;

/**
 *
 * @author Marc Baloup
 *
 */
public class TCPServer extends Thread implements Closeable {
	private static AtomicInteger connectionCounterId = new AtomicInteger(0);

	private ServerSocket socket;
	private TCPServerListener listener;
	private String socketName;

	private List<TCPServerClientConnection> clients = Collections.synchronizedList(new ArrayList<>());

	private AtomicBoolean isClosed = new AtomicBoolean(false);

	public final BandwidthCalculation bandwidthCalculation = new BandwidthCalculation();

	public TCPServer(int port, String sckName, TCPServerListener l) throws IOException {
		super("TCPSv " + sckName);
		setDaemon(true);
		if (port <= 0 || port > 65535) throw new IllegalArgumentException("le numéro de port est invalide");
		socket = new ServerSocket();
		socket.setReceiveBufferSize(Pandacube.NETWORK_TCP_BUFFER_SIZE);
		socket.setPerformancePreferences(0, 1, 0);
		socket.bind(new InetSocketAddress(port));
		listener = l;
		try {
			listener.onSocketOpen(this);
		} catch(Exception e) {
			Log.severe("Exception while calling TCPServerListener.onSocketOpen()", e);
		}
		socketName = sckName;
	}

	@Override
	public void run() {

		try {
			while (true) {
				Socket socketClient = socket.accept();
				socketClient.setSendBufferSize(Pandacube.NETWORK_TCP_BUFFER_SIZE);
				socketClient.setSoTimeout(Pandacube.NETWORK_TIMEOUT);

				try {
					TCPServerClientConnection co = new TCPServerClientConnection(socketClient,
							connectionCounterId.getAndIncrement());
					clients.add(co);
					co.start();
				} catch (IOException e) {
					Log.getLogger().log(Level.SEVERE, "Connexion impossible avec " + socketClient.getInetAddress());
				}
			}
		} catch(SocketException e) {
			
		} catch (Exception e) {
			Log.warning("Plus aucune connexion ne peux être acceptée", e);
		}
	}

	public class TCPServerClientConnection extends Thread {
		private Socket socket;
		private InputStream in;
		private OutputStream out;
		private SocketAddress address;
		private TCPServerConnectionOutputThread outThread;
		private List<Pair<Predicate<PacketClient>, ResponseCallback<PacketClient>>> callbacks = Collections.synchronizedList(new ArrayList<>());
		private List<Pair<Predicate<PacketClient>, ResponseCallback<PacketClient>>> callbacksAvoidListener = Collections.synchronizedList(new ArrayList<>());


		public TCPServerClientConnection(Socket s, int coId) throws IOException {
			super("TCPSv " + socketName + " Conn#" + coId + " In");
			setDaemon(true);
			socket = s;
			in = socket.getInputStream();
			out = socket.getOutputStream();
			address = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
			try {
				listener.onClientConnect(TCPServer.this, this);
			} catch(Exception e) {
				Log.severe("Exception while calling TCPServerListener.onClientConnect()", e);
			}
			outThread = new TCPServerConnectionOutputThread(coId);
			outThread.start();
		}

		@Override
		public void run() {
			try {
				byte[] code = new byte[1];
				while (!socket.isClosed() && in.read(code) != -1) {
					byte[] sizeB = new byte[4];
					if (in.read(sizeB) != 4) throw new IOException("Socket " + address + " closed");

					int size = new ByteBuffer(sizeB, Packet.CHARSET).getInt();

					byte[] content = new byte[size];

					forceReadBytes(content);

					byte[] packetData = new ByteBuffer(1 + 4 + size, Packet.CHARSET).putByteArray(code).putByteArray(sizeB)
							.putByteArray(content).array();

					bandwidthCalculation.addPacket(this, true, packetData.length);

					try {
						Packet p = Packet.constructPacket(packetData);
						
						if (!(p instanceof PacketClient))
							throw new PacketException(p.getClass().getCanonicalName() + " is not an instanceof PacketClient");
						
						PacketClient pc = (PacketClient) p;
						
						boolean oneCallbackExecuted = executeCallbacks(pc, callbacksAvoidListener);
						
						if (!oneCallbackExecuted)
							listener.onPacketReceive(TCPServer.this, this, pc);
						
						executeCallbacks(pc, callbacks);
						
					} catch (Exception e) {
						Log.severe("Exception while handling packet. This exception will be sent to the client with PacketServerException packet.", e);
						PacketServerException packet = new PacketServerException();
						packet.setException(e);
						send(packet);
					}
				}

			} catch(SocketException e) {
			} catch (Exception e) {
				Log.severe("Closing connection " + address, e);
			}

			close();
		}
		

		private boolean executeCallbacks(PacketClient pc, List<Pair<Predicate<PacketClient>, ResponseCallback<PacketClient>>> callbacks) {
			boolean executedOne = false;
			synchronized (callbacks) {
				for(Iterator<Pair<Predicate<PacketClient>, ResponseCallback<PacketClient>>> it = callbacks.iterator(); it.hasNext();) {
					Pair<Predicate<PacketClient>, ResponseCallback<PacketClient>> c = it.next();
					try {
						if (c.getKey().test(pc)) {
							it.remove();
							c.getValue().call(pc);
							executedOne = true;
						}
					} catch (Exception e) {
						Log.severe("Exception while executing callback", e);
					}
				}
			}
			return executedOne;
		}

		public void send(PacketServer p) {
			outThread.addPacket(p);
		}
		
		public void sendAndGetResponse(PacketServer packet, Predicate<PacketClient> responseCondition, ResponseCallback<PacketClient> callback, boolean avoidListener) {
			Pair<Predicate<PacketClient>, ResponseCallback<PacketClient>> p = new Pair<>(responseCondition, callback);
			if (avoidListener)
				callbacksAvoidListener.add(p);
			else
				callbacks.add(p);
			send(packet);
		}
		

		
		/**
		 * 
		 * @param packet the packet to send
		 * @param responseCondition {@link Predicate} that check each received packet to know which
		 * is the expected one as a response of the sended packet.
		 * @param avoidListener 
		 * @param timeout
		 * @return
		 * @throws InterruptedException
		 */
		public PacketClient sendAndWaitForResponse(PacketServer packet, Predicate<PacketClient> responseCondition, boolean avoidListener, long timeout) throws InterruptedException {
			AtomicReference<PacketClient> pcStorage = new AtomicReference<>(null);
			synchronized (pcStorage) {
				sendAndGetResponse(packet, responseCondition, packetClient -> {
					synchronized (pcStorage) {
						pcStorage.set(packetClient);
						pcStorage.notifyAll();
					}
				}, true);
				
				pcStorage.wait(timeout);
				return pcStorage.get();
			}
		}

		private void forceReadBytes(byte[] buff) throws IOException {
			int pos = 0;
			do {
				int nbR = in.read(buff, pos, buff.length - pos);
				if (nbR == -1) throw new IOException("Can't read required amount of byte");
				pos += nbR;
			} while (pos < buff.length);
		}

		public void close() {
			if (socket.isClosed()) return;

			try {
				listener.onClientDisconnect(TCPServer.this, this);
			} catch(Exception e) {
				Log.severe("Exception while calling TCPServerListener.onClientDisconnect()", e);
			}
			clients.remove(this);

			try {
				Thread.sleep(200);
				socket.close();
				if (!Thread.currentThread().equals(outThread)) send(new PacketServer((byte) 0) {
					@Override public void serializeToByteBuffer(ByteBuffer buffer) {}
					@Override public void deserializeFromByteBuffer(ByteBuffer buffer) {}
				});
				// provoque une exception dans le thread de sortie, et la
				// termine
			} catch (Exception e) { }
		}

		private class TCPServerConnectionOutputThread extends Thread {
			private BlockingQueue<PacketServer> packetQueue = new LinkedBlockingDeque<PacketServer>();

			public TCPServerConnectionOutputThread(int coId) {
				super("TCPSv " + socketName + " Conn#" + coId + " Out");
				setDaemon(true);
			}

			private void addPacket(PacketServer packet) {
				packetQueue.add(packet);
			}

			@Override
			public void run() {
				try {
					while (!socket.isClosed()) {
						PacketServer packet = packetQueue.poll(1, TimeUnit.SECONDS);
						byte[] data;
						if (packet != null) {
							try {
								data = packet.getFullSerializedPacket();
								bandwidthCalculation.addPacket(TCPServerClientConnection.this, false, data.length);
								out.write(data);
								out.flush();
							} catch (IOException e) {
								throw e;
							} catch (Exception e) {
								Log.severe("Can't send packet "+packet.getClass(), e);
							}
						}

					}
				} catch (InterruptedException|IOException e) {}

				close();
			}

		}


		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("thread", getName())
					.append("socket", socket).toString();
		}
		
	}

	@Override
	public void close() {
		try {
			if (isClosed.get()) return;
			isClosed.set(true);

			clients.forEach(el -> el.close());
			
			try {
				listener.onSocketClose(this);
			} catch(Exception e) {
				Log.severe("Exception while calling TCPServerListener.onSocketClose()", e);
			}

			socket.close();
		} catch (IOException e) {}
	}

	public boolean isClosed() {
		return isClosed.get() || socket.isClosed();
	}
	
	
	
	public List<TCPServerClientConnection> getClients() {
		synchronized (clients) {
			return new ArrayList<>(clients);
		}
	}
	

	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("thread", getName())
				.append("socket", socket).toString();
	}

}
