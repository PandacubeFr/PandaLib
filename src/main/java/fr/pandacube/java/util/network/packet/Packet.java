package fr.pandacube.java.util.network.packet;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fr.pandacube.java.Pandacube;
import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteSerializable;
import fr.pandacube.java.util.network.packet.packets.core_slave.Packet31ClientClose;
import fr.pandacube.java.util.network.packet.packets.core_slave.Packet30ClientDeclareProcess;
import fr.pandacube.java.util.network.packet.packets.core_slave.Packet32ClientProcessQueryResponse;
import fr.pandacube.java.util.network.packet.packets.core_slave.PacketB0ServerClose;
import fr.pandacube.java.util.network.packet.packets.core_slave.PacketB2ServerConnectSuccess;
import fr.pandacube.java.util.network.packet.packets.core_slave.PacketB1ServerProcessDeclarationConfirm;
import fr.pandacube.java.util.network.packet.packets.core_slave.PacketB3ServerProcessInput;
import fr.pandacube.java.util.network.packet.packets.core_slave.PacketB4ServerProcessQuery;
import fr.pandacube.java.util.network.packet.packets.global.Packet50ClientAuthenticate;
import fr.pandacube.java.util.network.packet.packets.global.Packet51ClientLogRecord;
import fr.pandacube.java.util.network.packet.packets.global.PacketD1ServerCantAuthenticate;
import fr.pandacube.java.util.network.packet.packets.global.PacketD0ServerException;
import fr.pandacube.java.util.network.packet.packets.web.Packet00ClientWebRequest;
import fr.pandacube.java.util.network.packet.packets.web.Packet80ServerWebResponse;

/** <pre>
 * Identification des packets réseaux
 * byte      (xxxxxxxx)
 *                      client :       server :
 * clt / sv  (x-------) (0-------)     (1-------)
 *                      0x00 - 0x7F    0x80 - 0xFF
 * use case  (-xxx----)
 * - web     (-000----) 0x00 - 0x0F    0x80 - 0x8F (client is Apache, server is PandacubeCore master (PandacubeWeb))
 * - spigot  (-001----) 0x10 - 0x1F    0x90 - 0x9F (client is PandacubeSpigot, server is PandacubeCore master)
 * - bungee  (-010----) 0x20 - 0x2F    0xA0 - 0xAF (client is PandacubeBungee, server is PandacubeCore master)
 * -coreslave(-011----) 0x30 - 0x3F    0xB0 - 0xBF (client is PandacubeCore slave, sv is PandacubeCore master)
 * - global  (-101----) 0x50 - 0x5F    0xD0 - 0xDF
 * 
 * - reserved if not enough packet id in certain use case
 *           (-11x----) 0x60 - 0x7F    0xE0 - 0xFF
 * 
 * packet id (----xxxx)
 * </pre>
 */
public abstract class Packet implements ByteSerializable {

	private final byte code;

	public Packet(byte c) {
		code = c;
	}

	public byte getCode() {
		return code;
	}

	public byte[] getFullSerializedPacket() {
		ByteBuffer internal = new ByteBuffer(CHARSET).putObject(this);
		byte[] data = Arrays.copyOfRange(internal.array(), 0, internal.getPosition());

		return new ByteBuffer(5 + data.length, CHARSET).putByte(code).putInt(data.length).putByteArray(data).array();
	}

	public static final Charset CHARSET = Pandacube.NETWORK_CHARSET;

	private static Map<Byte, Class<? extends Packet>> packetTypes = new HashMap<>();

	public static Packet constructPacket(byte[] data) {
		if (!packetTypes.containsKey(data[0]))
			throw new PacketException("Packet identifier not recognized: 0x" + String.format("%02X", data[0])
					+ ". Maybe this packet is not registered with Packet.addPacket()");

		try {
			Packet p = packetTypes.get(data[0]).newInstance();
			ByteBuffer dataBuffer = new ByteBuffer(Arrays.copyOfRange(data, 5, data.length), CHARSET);
			p.deserializeFromByteBuffer(dataBuffer);
			return p;
		} catch (Exception e) {
			throw new PacketException("Error while constructing packet", e);
		}
	}

	private static <T extends Packet> void addPacket(Class<T> packetClass) {
		try {
			Packet p = packetClass.newInstance();
			packetTypes.put(p.code, packetClass);
		} catch (Exception e) {
			Log.severe(e);
		}
	}
	

	static {

		/*
		 * Ajout des types de packets (client + serveur)
		 */
		addPacket(Packet31ClientClose.class);
		addPacket(Packet30ClientDeclareProcess.class);
		addPacket(Packet32ClientProcessQueryResponse.class);
		addPacket(PacketB0ServerClose.class);
		addPacket(PacketB2ServerConnectSuccess.class);
		addPacket(PacketB1ServerProcessDeclarationConfirm.class);
		addPacket(PacketB3ServerProcessInput.class);
		addPacket(PacketB4ServerProcessQuery.class);
		
		addPacket(Packet50ClientAuthenticate.class);
		addPacket(Packet51ClientLogRecord.class);
		addPacket(PacketD1ServerCantAuthenticate.class);
		addPacket(PacketD0ServerException.class);
		
		addPacket(Packet00ClientWebRequest.class);
		addPacket(Packet80ServerWebResponse.class);
		
	}

}
