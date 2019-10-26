package fr.pandacube.util.network.packet.packets.global;

import fr.pandacube.util.network.packet.PacketClient;
import fr.pandacube.util.network.packet.bytebuffer.ByteBuffer;

public class Packet50ClientAuthenticate extends PacketClient {
	
	private String password;
	private String additionalData = "";
	
	public Packet50ClientAuthenticate() {
		super((byte)0x50);
	}
	

	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(password);
		buffer.putString(additionalData);
	}

	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		password = buffer.getString();
		additionalData = buffer.getString();
	}


	public String getPassword() { return password; }
	public void setPassword(String p) { password = p; }

	public String getAdditionalData() { return additionalData; }
	public void setAdditionalData(String data) { additionalData = data; }

}
