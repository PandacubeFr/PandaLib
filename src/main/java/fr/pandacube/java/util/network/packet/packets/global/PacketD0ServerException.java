package fr.pandacube.java.util.network.packet.packets.global;

import java.io.PrintWriter;
import java.io.StringWriter;

import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class PacketD0ServerException extends PacketServer {
	
	private String exception;
	
	public PacketD0ServerException() {
		super((byte)0xD0);
	}
	
	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(exception);
	}
	
	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		exception = buffer.getString();
	}
	
	
	public void setException(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		exception = sw.toString();
	}
	
	public String getExceptionString() {
		return exception;
	}
	
}
