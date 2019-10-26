package fr.pandacube.util.network.packet.packets.core_slave;

import fr.pandacube.util.network.packet.PacketClient;
import fr.pandacube.util.network.packet.bytebuffer.ByteBuffer;
import fr.pandacube.util.network.packet.packets.core_slave.PacketB4ServerProcessQuery.QueryType;

public class Packet32ClientProcessQueryResponse extends PacketClient {

	private QueryType type;
	private int queryId;
	private byte[] responseData = null;
	
	
	public Packet32ClientProcessQueryResponse() {
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
	

	public static Packet32ClientProcessQueryResponse destroyResponse(int queryId) {
		Packet32ClientProcessQueryResponse q = new Packet32ClientProcessQueryResponse();
		q.type = QueryType.DESTROY;
		q.queryId = queryId;
		return q;
	}
	
	public static Packet32ClientProcessQueryResponse isAliveResponse(int queryId, boolean resp) {
		Packet32ClientProcessQueryResponse q = new Packet32ClientProcessQueryResponse();
		q.type = QueryType.IS_ALIVE;
		q.queryId = queryId;
		q.responseData = new byte[] {(byte)(resp ? 1 : 0)};
		return q;
	}
	
	public static Packet32ClientProcessQueryResponse exitStatusResponse(int queryId, int resp) {
		Packet32ClientProcessQueryResponse q = new Packet32ClientProcessQueryResponse();
		q.type = QueryType.EXIT_STATUS;
		q.queryId = queryId;
		q.responseData = new ByteBuffer(4, CHARSET).putInt(resp).array();
		return q;
	}
	
}
