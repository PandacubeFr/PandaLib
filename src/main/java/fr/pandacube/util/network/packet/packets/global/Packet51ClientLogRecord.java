package fr.pandacube.util.network.packet.packets.global;

import fr.pandacube.util.network.packet.PacketClient;
import fr.pandacube.util.network.packet.bytebuffer.ByteBuffer;

public class Packet51ClientLogRecord extends PacketClient {
	
	private long time;
	private String level;
	private String threadName;
	private String message;
	private String throwable;
	
	public Packet51ClientLogRecord() {
		super((byte)0x51);
	}
	
	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		time = buffer.getLong();
		level = buffer.getString();
		threadName = buffer.getString();
		message = buffer.getString();
		throwable = buffer.getString();
	}
	
	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putLong(time);
		buffer.putString(level);
		buffer.putString(threadName);
		buffer.putString(message);
		buffer.putString(throwable);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getThrowable() {
		return throwable;
	}

	public void setThrowable(String throwable) {
		this.throwable = throwable;
	}

}
