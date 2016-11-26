package fr.pandacube.java.util.network.packet.packets.global;

import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class PacketServerCommand extends PacketServer {
	
	private String command;
	private boolean async;
	private boolean returnResult;
	
	public PacketServerCommand() {
		super((byte)0xD2);
	}

	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(command);
		buffer.putByte((byte) (async ? 1 : 0));
		buffer.putByte((byte) (returnResult ? 1 : 0));
	}

	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		command = buffer.getString();
		async = buffer.getByte() != 0;
		returnResult = buffer.getByte() != 0;
	}

}
