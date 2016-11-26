package fr.pandacube.java.util.network.packet.packets.core_slave;

import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class PacketServerProcessInput extends PacketServer {
	
	private String serverName;
	private byte[] dataToSend;
	
	public PacketServerProcessInput() {
		super((byte)0xB3);
	}
	
	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(serverName);
		buffer.putSizedByteArray(dataToSend);
	}
	
	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		serverName = buffer.getString();
		dataToSend = buffer.getSizedByteArray();
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public byte[] getDataToSend() {
		return dataToSend;
	}

	public void setDataToSend(byte[] dataToSend) {
		this.dataToSend = dataToSend;
	}
}
