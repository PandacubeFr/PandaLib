package fr.pandacube.util.net;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.base.Objects;

import fr.pandacube.Pandacube;
import fr.pandacube.util.Log;

/**
 * A wrapper for a {@link Socket}. The connection must point to a software using {@link PServer}
 * as wrapper for the target {@link ServerSocket}.
 * <br>
 * This class provides a simple way to exchange data between client and server :
 * <li>Maintained connection with the server</li>
 * <li>Login with a password (send in the first packet)</li>
 * <li>Named packets</li>
 * <li>Binary data</li>
 * <li>Input stream in a separate Thread</li>
 * 
 */
public class PSocket extends Thread implements Closeable {

	private boolean server = false;
	private Socket socket;
	private SocketAddress addr;
	private DataInputStream in;
	private DataOutputStream out;
	private Object outSynchronizer = new Object();
	private String password;
	
	private AtomicBoolean isClosed = new AtomicBoolean(false);

	private List<PPacketListener<PPacket>> packetListeners = Collections.synchronizedList(new ArrayList<>());
	private List<PSocketConnectionListener> connectionListeners = Collections.synchronizedList(new ArrayList<>());
	private Map<Integer, PPacketListener<PPacketAnswer>> answersCallbacks = Collections.synchronizedMap(new HashMap<>());
	
	private int nextSendId = 0;

	/**
	 * Create a new PSocket that will connect to the specified SocketAddress.
	 * @param a The target server to connect to
	 * @param connName the name of the connection, used to name the Thread used to receive the packet.
	 * @param the password to send to the server.
	 */
	public PSocket(SocketAddress a, String connName, String pass) {
		super("PSocket " + connName);
		setDaemon(true);
		if (a == null) throw new IllegalArgumentException("les arguments ne peuvent pas être null");
		addr = a;
	}
	
	
	/* package */ PSocket(Socket s, String connName, String pass) {
		this(s.getRemoteSocketAddress(), connName, pass);
		socket = s;
		server = true;
	}
	
	

	@Override
	public void run() {

		try {
			if (socket == null) {
				socket = new Socket();
				socket.setReceiveBufferSize(Pandacube.NETWORK_TCP_BUFFER_SIZE);
				socket.setSendBufferSize(Pandacube.NETWORK_TCP_BUFFER_SIZE);
				
				socket.setSoTimeout(10000); // initial timeout before login
			
				socket.connect(addr);
				
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			}
			
			// password check
			if (server) {
				PPacket packet = readPacket();
				if (packet == null || packet instanceof PPacketAnswer || !"login".equals(packet.name)) {
					send(PPacket.buildLoginBadPacket());
					close();
					return;
				}
				try {
					String receivedPassword = new ByteBuffer(packet.content).getString();
					if (!Objects.equal(receivedPassword, password)) {
						send(PPacket.buildLoginBadPacket());
						close();
						return;
					}
				} catch(Exception e) {
					send(PPacket.buildLoginBadPacket());
					close();
					return;
				}
				send(PPacketAnswer.buildLoginOkPacket(packet));
				// login ok at this point
				password = null;
			}
			else {
				send(PPacket.buildLoginPacket(password));
				PPacket packet = readPacket();
				if (packet == null) {
					Log.severe("bad packet received from server. Disconnecting.");
					close();
					return;
				}
				if (packet.name.equals("login_bad")) {
					Log.severe("Wrong password to connect to server. Disconnecting.");
					close();
					return;
				}
				if (!packet.name.equals("login_ok")) {
					Log.severe("Unexpected packet from server. Disconnecting.");
					close();
					return;
				}
				// login ok at this point
				password = null;
			}
			
			socket.setSoTimeout(Pandacube.NETWORK_TIMEOUT);
			
			Log.info(getName() + " connected.");
			
			connectionListeners.forEach(l -> {
				try {
					l.onConnect(this);
				} catch (Exception e) {
					Log.severe("Exception while calling PSocketConnectionListener.onConnect().", e);
				}
			});
			
			while (!socket.isClosed()) {
				PPacket packet = readPacket();
				
				if (packet == null) {
					send(PPacket.buildBadFormatPacket("Bad format for the last packet received. Closing connection."));
					break;
				}

				if (packet instanceof PPacketAnswer) {
					try {
						answersCallbacks.remove(((PPacketAnswer)packet).answer).onPacketReceive(this, (PPacketAnswer)packet);
					} catch (Exception e) {
						Log.severe("Exception while calling PPacketListener.onPacketReceive().", e);
						send(PPacketAnswer.buildExceptionPacket(packet, e.toString()));
					}
				}
				else {
					packetListeners.forEach(l -> {
						try {
							l.onPacketReceive(this, packet);
						} catch (Exception e) {
							Log.severe("Exception while calling PPacketListener.onPacketReceive().", e);
							sendSilently(PPacketAnswer.buildExceptionPacket(packet, e.toString()));
						}
					});
				}
			}

		} catch (Exception e) {
			Log.severe(e);
		}
		close();
	}
	
	
	/**
	 * Return the packet read in the socket, or null if the packet is in a bad format.
	 * @return the packet
	 * @throws IOException
	 * 
	 */
	private PPacket readPacket() throws IOException {
		byte nSize = in.readByte();
		if (nSize == 0) {
			return null;
		}
		boolean answer = nSize < 0;
		if (answer)
			nSize *= -1;
		
		
		byte[] nBytes = new byte[nSize];
		in.readFully(nBytes);
		String name = new String(nBytes, Pandacube.NETWORK_CHARSET);
		
		
		int packetId = in.readInt();
		
		
		int answerId = (answer) ? in.readInt() : -1;
		
		
		int cSize = in.readInt();
		if (cSize < 0 || cSize > 0xFFFFFF) { // can't be more that 16 MiB
			return null;
		}

		
		byte[] content = new byte[cSize];
		in.readFully(content);
		
		return answer ? new PPacketAnswer(name, packetId, answerId, content) : new PPacket(name, packetId, content);
	}
	
	
	
	
	
	
	/**
	 * Send the provided packet, without waiting for an answer.
	 * @param packet
	 * @throws IOException
	 */
	public void send(PPacket packet) throws IOException {
		if (packet == null)
			throw new IllegalArgumentException("packet can't be null");
		if (packet.name == null)
			throw new IllegalArgumentException("packet.name can't be null");
		if (packet.content == null)
			throw new IllegalArgumentException("packet.content can't be null");
		
		byte[] nameBytes = packet.name.getBytes(Pandacube.NETWORK_CHARSET);
		if (nameBytes.length > 127)
			throw new IllegalArgumentException("packet.name must take fewer than 128 bytes when converted to UTF-8");
		byte nameSize = (byte)nameBytes.length;
		
		boolean answer = packet instanceof PPacketAnswer;
		
		if (answer) nameSize *= -1;
			
		synchronized (outSynchronizer) {
			int packetId = nextSendId++;
			
			packet.id = packetId;
			
			out.write(new byte[] {nameSize});
			out.write(nameBytes);
			out.write(packetId);
			if (answer)
				out.write(((PPacketAnswer)packet).answer);
			out.write(packet.content.length);
			out.write(packet.content);
			out.flush();
		}
	}

	public void sendSilently(PPacket packet) {
		try {
			send(packet);
		} catch (IOException e) {}
	}
	
	
	
	public void send(PPacket packet, PPacketListener<PPacketAnswer> answerCallback) throws IOException {
		synchronized (answersCallbacks) {
			/*
			 * This synch block ensure that the callback will be put in the listeners Map before
			 * we receve the answer (in case this is really really fast)
			 */
			send(packet);
			answersCallbacks.put(packet.id, answerCallback);
		}
	}
	
	
	
	
	
	
	
	
	public void addPacketListener(PPacketListener<PPacket> l) {
		packetListeners.add(l);
	}
	
	public boolean removePacketListener(PPacketListener<PPacket> l) {
		return packetListeners.remove(l);
	}

	
	public void addConnectionListener(PSocketConnectionListener l) {
		connectionListeners.add(l);
	}
	
	public void removeConnectionListener(PSocketConnectionListener l) {
		connectionListeners.remove(l);
	}

	@Override
	public void close() {
		try {
			synchronized (outSynchronizer) {
				if (isClosed.get()) return;

				Log.info(getName() + " closing...");

				connectionListeners.forEach(l -> {
					try {
						l.onDisconnect(this);
					} catch (Exception e) {
						Log.severe("Exception while calling PSocketConnectionListener.onDisconnect().", e);
					}
				});
				
				socket.close();
				isClosed.set(true);
			}
		} catch (IOException e) {
			Log.warning(e);
		}
	}

	public SocketAddress getRemoteAddress() {
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