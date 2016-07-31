package fr.pandacube.java.util.network.packet.packets.global;

import java.io.PrintWriter;
import java.io.StringWriter;

import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class PacketServerException extends PacketServer {
	
	private String exception;
	
	public PacketServerException() {
		super((byte)0xE0);
	}
	
	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putString(exception);
	}
	
	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		exception = buffer.getString();
	}
	
	
	public void setException(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		exception = sw.toString();
	}
	
	public String getExceptionString() {
		return exception;
	}
	
}
