package fr.pandacube.util.network.packet.packets.core_slave;

import fr.pandacube.util.network.packet.PacketClient;
import fr.pandacube.util.network.packet.bytebuffer.ByteBuffer;

public class Packet30ClientDeclareProcess extends PacketClient {
	
	private String processName;
	private String type;
	
	public Packet30ClientDeclareProcess() {
		super((byte)0x30);
	}
	
	public void setProcessName(String pN) {
		processName = pN;
	}
	
	public void setType(String t) {
		type = t;
	}
	
	public String getProcessName() { return processName; }
	public String getType() { return type; }

	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(processName);
		buffer.putString(type);

	}

	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		processName = buffer.getString();
		type = buffer.getString();
	}

}
