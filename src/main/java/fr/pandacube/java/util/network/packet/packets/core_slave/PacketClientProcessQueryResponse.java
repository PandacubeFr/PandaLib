package fr.pandacube.java.util.network.packet.packets.core_slave;

import fr.pandacube.java.util.network.packet.PacketClient;
import fr.pandacube.java.util.network.packet.bytebuffer.ByteBuffer;
import fr.pandacube.java.util.network.packet.packets.core_slave.PacketServerProcessQuery.QueryType;

public class PacketClientProcessQueryResponse extends PacketClient {

	private QueryType type;
	private int queryId;
	private byte[] responseData = null;
	
	
	public PacketClientProcessQueryResponse() {
		super((byte)0x32);
	}
	
	public QueryType getType() { return type; }
	public int getQueryId() { return queryId; }
	public byte[] getResponseData() { return responseData; }
	

	@Override
	public void serializeToByteBuffer(ByteBuffer buffer) {
		buffer.putInt(type.ordinal());
		buffer.putInt(queryId);
		buffer.putSizedByteArray(responseData);
	}

	@Override
	public void deserializeFromByteBuffer(ByteBuffer buffer) {
		type = QueryType.values()[buffer.getInt()];
		queryId = buffer.getInt();
		responseData = buffer.getSizedByteArray();
	}
	

	public static PacketClientProcessQueryResponse destroyResponse(int queryId) {
		PacketClientProcessQueryResponse q = new PacketClientProcessQueryResponse();
		q.type = QueryType.DESTROY;
		q.queryId = queryId;
		return q;
	}
	
	public static PacketClientProcessQueryResponse isAliveResponse(int queryId, boolean resp) {
		PacketClientProcessQueryResponse q = new PacketClientProcessQueryResponse();
		q.type = QueryType.IS_ALIVE;
		q.queryId = queryId;
		q.responseData = new byte[] {(byte)(resp ? 1 : 0)};
		return q;
	}
	
	public static PacketClientProcessQueryResponse exitStatusResponse(int queryId, int resp) {
		PacketClientProcessQueryResponse q = new PacketClientProcessQueryResponse();
		q.type = QueryType.EXIT_STATUS;
		q.queryId = queryId;
		q.responseData = new ByteBuffer(4, CHARSET).putInt(resp).array();
		return q;
	}
	
}
