package fr.pandacube.util.network.packet.packets.web;

import fr.pandacube.util.network.packet.PacketServer;
import fr.pandacube.util.network.packet.bytebuffer.ByteBuffer;

public class Packet80ServerWebResponse extends PacketServer {
	
	private String jsonData;
	
	public Packet80ServerWebResponse() {
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
