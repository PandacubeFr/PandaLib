package fr.pandacube.util.net;

import java.util.Arrays;

public class PPacketAnswer extends PPacket {
	/* package */ int answer;

	/**
	 * Construct a new PPacketAnswer based on the content of the provided buffer before his position.
	 * @param n the name of the packet.
	 * @param buff the buffer where the data comes from. Only the content before {@link ByteBuffer#getPosition()} is copied.
	 */
	public PPacketAnswer(PPacket answered, String n, ByteBuffer buff) {
		this(answered, n, Arrays.copyOf(buff.array(), buff.getPosition()));
	}
	
	public PPacketAnswer(PPacket answered, String n, byte[] c) {
		super(n, c);
		answer = answered.id;
	}
	
	/* package */ PPacketAnswer(String n, int i, int a, byte[] c) {
		super(n, i, c);
		answer = a;
	}
	
	

	
	public static PPacketAnswer buildSingleStringContentPacketAnswer(PPacket answered, String name, String content) {
		ByteBuffer pwBuff = new ByteBuffer().putString(content);
		return new PPacketAnswer(answered, name, Arrays.copyOf(pwBuff.array(), pwBuff.getPosition()));
	}

	
	
	
	/* package */ static PPacketAnswer buildLoginOkPacket(PPacket loginPacket) {
		return new PPacketAnswer(loginPacket, "login_ok", new byte[0]);
	}
	/* package */ static PPacketAnswer buildExceptionPacket(PPacket answered, String message) {
		return buildSingleStringContentPacketAnswer(answered, "exception", message);
	}
}
