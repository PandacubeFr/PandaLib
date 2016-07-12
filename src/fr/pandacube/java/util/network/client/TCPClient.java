package fr.pandacube.java.util.network.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import fr.pandacube.java.Pandacube;
import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.network.packet.Packet;
import fr.pandacube.java.util.network.packet.PacketClient;
import fr.pandacube.java.util.network.packet.PacketException;
import fr.pandacube.java.util.network.packet.PacketServer;

public class TCPClient extends Thread implements Closeable {
	
	private Socket socket;
	private SocketAddress addr;
	private TCPClientListener listener;
	private InputStream in;
	private OutputStream out;
	private Object outSynchronizer = new Object();
	
	private AtomicBoolean isClosed = new AtomicBoolean(false);
	
	
	
	
	public TCPClient(InetSocketAddress a, String connName, TCPClientListener l) throws IOException {
		super("TCPCl "+connName);
		if (a == null || l == null)
			throw new IllegalArgumentException("les arguments ne peuvent pas être null");
		socket = new Socket();
		socket.setReceiveBufferSize(Pandacube.NETWORK_TCP_BUFFER_SIZE);
		socket.setSendBufferSize(Pandacube.NETWORK_TCP_BUFFER_SIZE);
		socket.setSoTimeout(Pandacube.NETWORK_TIMEOUT);
		socket.connect(a);
		addr = a;
		listener = l;
		listener.onConnect(this);
	}
	
	
	@Override
	public void run() {
		
		try {
			byte[] code = new byte[1];
			while(!socket.isClosed() && in.read(code) != -1) {
				byte[] sizeB = new byte[4];
				if (in.read(sizeB) != 4)
					throw new IOException("Socket "+addr+" fermé");
				
				int size = ByteBuffer.wrap(sizeB).getInt();
				
				byte[] content = new byte[size];
				
				forceReadBytes(content);
				
				byte[] packetData = ByteBuffer.allocate(1+4+size).put(code).put(sizeB).put(content).array();
				
				
				try {
					if (listener == null)
						throw new InvalidServerMessage("Le serveur ne peut actuellement pas prendre en charge de nouvelles requêtes. Les listeners n'ont pas encore été définis");
					
					Packet p = Packet.constructPacket(packetData);
					
					if (!(p instanceof PacketServer))
						throw new InvalidServerMessage("Le type de packet reçu n'est pas un packet attendu : "+p.getClass().getCanonicalName());
					
					PacketServer ps = (PacketServer) p;
					
					listener.onPacketReceive(this, ps);
				} catch (PacketException|InvalidServerMessage e) {
					Log.getLogger().log(Level.SEVERE, "Message du serveur mal formé", e);
				} catch (Exception e) {
					Log.getLogger().log(Level.SEVERE, "Erreur lors de la prise en charge du message par le serveur", e);
				}
			}
			
			
		} catch (SocketTimeoutException e) {
			System.err.println("Le serveur a prit trop de temps à répondre");
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}
	

	
	private void forceReadBytes(byte[] buff) throws IOException {
		int pos = 0;
		do {
			int nbR = in.read(buff, pos, buff.length-pos);
			if (nbR == -1)
				throw new IOException("Can't read required amount of byte");
			pos += nbR;
		} while (pos < buff.length);
	}
	

	
	
	public void send(PacketClient packet) throws IOException {
		synchronized (outSynchronizer) {
			out.write(packet.getFullSerializedPacket());
			out.flush();
		}
	}
	
	@Override
	public void close() {
		try {
			synchronized (outSynchronizer) {
				if (isClosed.get())
					return;
				socket.close();
				isClosed.set(true);
				listener.onDisconnect(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendSilently(PacketClient packet) {
		try {
			send(packet);
		} catch (IOException e) { }
	}
	
	
	
	
	
	public SocketAddress getServerAddress() {
		return addr;
	}
	
	
	
	public boolean isClosed() {
		return isClosed.get() || socket.isClosed();
	}
	
	
	
	
	
	public static class InvalidServerMessage extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public InvalidServerMessage(String message) {
			super(message);
		}
	}
	
}
