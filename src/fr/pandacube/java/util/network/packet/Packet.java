package fr.pandacube.java.util.network.packet;


import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fr.pandacube.java.PandacubeUtil;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteSerializable;

public abstract class Packet implements ByteSerializable {
	
	private final byte code;
	
	public Packet(byte c) {
		code = c;
	}

	public byte getCode() { return code; }

	public byte[] getFullSerializedPacket() {
		ByteBuffer internal = new ByteBuffer(CHARSET).putObject(this);
		byte[] data = Arrays.copyOfRange(internal.array(), 0, internal.getPosition());
		
		return new ByteBuffer(5+data.length, CHARSET).putByte(code).putInt(data.length).putBytes(data).array();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static final Charset CHARSET = PandacubeUtil.NETWORK_CHARSET;
	
	private static Map<Byte, Class<? extends Packet>> packetTypes = new HashMap<Byte, Class<? extends Packet>>();
	
	public static Packet constructPacket(byte[] data) {
		if (!packetTypes.containsKey(data[0]))
			throw new PacketException("l'identifiant du packet ne correspond à aucun type de packet : "+data[0]);
		
		try {
			Packet p = packetTypes.get(data[0]).newInstance();
			ByteBuffer dataBuffer = new ByteBuffer(Arrays.copyOfRange(data, 5, data.length), CHARSET);
			p.deserializeFromByteBuffer(dataBuffer);
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PacketException("erreur lors de la construction du packet");
		}
	}
	
	private static <T extends Packet> void addPacket(Class<T> packetClass) {
		try {
			Packet p = (Packet)packetClass.newInstance();
			packetTypes.put(p.code, packetClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	static {
		
		/*
		 * Ajout des types de packets (client + serveur)
		 */
		// addPacket(PacketToto.class);
	}
	

}
