package fr.pandacube.lib.net;

import java.util.Arrays;

import com.google.common.annotations.Beta;

@Beta
public class PPacket {
	public final String name;
	/* package */ int id;
	public final byte[] content;
	
	/**
	 * Construct a new PPacket based on the content of the provided buffer before his position.
	 * @param n the name of the packet.
	 * @param buff the buffer where the data comes from. Only the content before {@link ByteBuffer#getPosition()} is copied.
	 */
	public PPacket(String n, ByteBuffer buff) {
		this(n, Arrays.copyOf(buff.array(), buff.getPosition()));
	}
	
	public PPacket(String n, byte[] c) {
		name = n;
		content = c;
	}
	
	/* package */ PPacket(String n, int i, byte[] c) {
		this(n, c);
		id = i;
	}
	
	public ByteBuffer getContentAsBuffer() {
		return new ByteBuffer(content);
	}
	
	
	
	
	
	
	
	
	public static PPacket buildSingleStringContentPacket(String name, String content) {
		return new PPacket(name, new ByteBuffer().putString(content));
	}
	
	
	
	
	
	/* package */ static PPacket buildLoginPacket(String password) {
		return buildSingleStringContentPacket("login", password);
	}
	/* package */ static PPacket buildBadFormatPacket(String message) {
		return buildSingleStringContentPacket("bad_format", message);
	}
	/* package */ static PPacket buildLoginBadPacket() {
		return new PPacket("login_bad", new byte[0]);
	}
}
