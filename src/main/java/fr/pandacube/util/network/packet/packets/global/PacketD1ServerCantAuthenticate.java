package fr.pandacube.util.network.packet.packets.global;

import fr.pandacube.util.network.packet.PacketServer;
import fr.pandacube.util.network.packet.bytebuffer.ByteBuffer;

public class PacketD1ServerCantAuthenticate extends PacketServer {
	
	public PacketD1ServerCantAuthenticate() {
		super((byte)0xD1);
	}
	
	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
	}
	
	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
	}

}
