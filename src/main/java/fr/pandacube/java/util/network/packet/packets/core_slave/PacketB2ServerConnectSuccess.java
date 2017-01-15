package fr.pandacube.java.util.network.packet.packets.core_slave;

import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class PacketB2ServerConnectSuccess extends PacketServer {
	
	public PacketB2ServerConnectSuccess() {
		super((byte)0xB2);
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
