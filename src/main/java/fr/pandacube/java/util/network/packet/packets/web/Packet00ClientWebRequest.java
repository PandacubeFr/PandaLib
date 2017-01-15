package fr.pandacube.java.util.network.packet.packets.web;

import fr.pandacube.java.util.network.packet.PacketClient;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class Packet00ClientWebRequest extends PacketClient {
	
	private String password;
	private String jsonData;
	
	public Packet00ClientWebRequest() {
		super((byte)0x00);
	}

	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(password);
		buffer.putString(jsonData);
	}

	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		password = buffer.getString();
		jsonData = buffer.getString();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

}
