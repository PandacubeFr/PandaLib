package fr.pandacube.java.util.network.packet.packets.core_slave;

import fr.pandacube.java.util.RandomUtil;
import fr.pandacube.java.util.network.packet.PacketServer;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;

public class PacketServerProcessQuery extends PacketServer {
	
	private String processName;
	private QueryType type;
	private int queryId = RandomUtil.rand.nextInt();
	private byte[] queryData = null;

	
	public PacketServerProcessQuery() {
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
	

	public static PacketServerProcessQuery startQuery(String processName) {
		PacketServerProcessQuery q = new PacketServerProcessQuery();
		q.processName = processName;
		q.type = QueryType.START;
		return q;
	}
	
	public static PacketServerProcessQuery destroyQuery(String processName, boolean wait) {
		PacketServerProcessQuery q = new PacketServerProcessQuery();
		q.processName = processName;
		q.type = QueryType.DESTROY;
		q.queryData = new byte[] {(byte)(wait ? 1 : 0)};
		return q;
	}
	
	public static PacketServerProcessQuery isAliveQuery(String processName) {
		PacketServerProcessQuery q = new PacketServerProcessQuery();
		q.processName = processName;
		q.type = QueryType.IS_ALIVE;
		return q;
	}
	
	public static PacketServerProcessQuery exitStatusQuery(String processName) {
		PacketServerProcessQuery q = new PacketServerProcessQuery();
		q.processName = processName;
		q.type = QueryType.EXIT_STATUS;
		return q;
	}
	
	
	public enum QueryType {
		START, DESTROY, IS_ALIVE, EXIT_STATUS;
	}

}
