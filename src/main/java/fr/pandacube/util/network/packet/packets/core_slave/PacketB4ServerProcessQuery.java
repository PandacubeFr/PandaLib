package fr.pandacube.util.network.packet.packets.core_slave;

import fr.pandacube.util.RandomUtil;
import fr.pandacube.util.network.packet.PacketServer;
import fr.pandacube.util.network.packet.bytebuffer.ByteBuffer;

public class PacketB4ServerProcessQuery extends PacketServer {
	
	private String processName;
	private QueryType type;
	private int queryId = RandomUtil.rand.nextInt();
	private byte[] queryData = null;

	
	public PacketB4ServerProcessQuery() {
		super((byte)0xB4);
	}

	public String getProcessName() { return processName; }
	public QueryType getType() { return type; }
	public int getQueryId() { return queryId; }
	public byte[] getQueryData() { return queryData; }

	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putInt(type.ordinal());
		buffer.putInt(queryId);
		buffer.putSizedByteArray(queryData);
	}

	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		type = QueryType.values()[buffer.getInt()];
		queryId = buffer.getInt();
		queryData = buffer.getSizedByteArray();
	}
	

	public static PacketB4ServerProcessQuery startQuery(String processName) {
		PacketB4ServerProcessQuery q = new PacketB4ServerProcessQuery();
		q.processName = processName;
		q.type = QueryType.START;
		return q;
	}
	
	public static PacketB4ServerProcessQuery destroyQuery(String processName, boolean wait) {
		PacketB4ServerProcessQuery q = new PacketB4ServerProcessQuery();
		q.processName = processName;
		q.type = QueryType.DESTROY;
		q.queryData = new byte[] {(byte)(wait ? 1 : 0)};
		return q;
	}
	
	public static PacketB4ServerProcessQuery isAliveQuery(String processName) {
		PacketB4ServerProcessQuery q = new PacketB4ServerProcessQuery();
		q.processName = processName;
		q.type = QueryType.IS_ALIVE;
		return q;
	}
	
	public static PacketB4ServerProcessQuery exitStatusQuery(String processName) {
		PacketB4ServerProcessQuery q = new PacketB4ServerProcessQuery();
		q.processName = processName;
		q.type = QueryType.EXIT_STATUS;
		return q;
	}
	
	
	public enum QueryType {
		START, DESTROY, IS_ALIVE, EXIT_STATUS;
	}

}
