package fr.pandacube.java.util.network.packet.packets.core_slave;

import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class PacketServerProcessDeclarationConfirm extends PacketServer {
	
	private String serverName;
	
	public PacketServerProcessDeclarationConfirm() {
		super((byte)0xB1);
	}

	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(serverName);
	}

	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		serverName = buffer.getString();
	}

	public String getServerName() {
		return serverName;
	}

	public void setProcessName(String name) {
		serverName = name;
	}

}
