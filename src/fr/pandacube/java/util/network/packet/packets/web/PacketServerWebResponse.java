package fr.pandacube.java.util.network.packet.packets.web;

import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class PacketServerWebResponse extends PacketServer {
	
	private String jsonData;
	
	public PacketServerWebResponse() {
		super((byte)0x80);
	}
	
	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(jsonData);
	}
	
	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		jsonData = buffer.getString();
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

}
