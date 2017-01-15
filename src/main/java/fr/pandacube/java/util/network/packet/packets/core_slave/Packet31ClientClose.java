package fr.pandacube.java.util.network.packet.packets.core_slave;

import fr.pandacube.java.util.network.packet.PacketClient;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class Packet31ClientClose extends PacketClient {
	
	public Packet31ClientClose() {
		super((byte)0x31);
	}

	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		// no data
	}

	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		// no data
	}

}
