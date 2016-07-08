package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLMPMessage extends SQLElement {
	
	
	public SQLMPMessage() { super(); }
	public SQLMPMessage(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_mp_message"; }
	
	
	public static final SQLField<Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLField<Integer> securityKey = new SQLField<>("securityKey", SQLType.INT, false);
	public static final SQLField<String> viewerId = new SQLField<>("viewerId", SQLType.CHAR(36), false);
	public static final SQLField<String> sourceId = new SQLField<>("sourceId", SQLType.CHAR(36), true);
	public static final SQLField<String> destId = new SQLField<>("destId", SQLType.CHAR(36), true);
	public static final SQLField<Integer> destGroup = new SQLField<>("destGroup", SQLType.INT, true);
	public static final SQLField<String> message = new SQLField<>("message", SQLType.VARCHAR(512), false);
	public static final SQLField<Boolean> wasRead = new SQLField<>("wasRead", SQLType.BOOLEAN, false);
	public static final SQLField<Boolean> deleted = new SQLField<>("deleted", SQLType.BOOLEAN, false);
	public static final SQLField<Boolean> serverSync = new SQLField<>("serverSync", SQLType.BOOLEAN, false);


	public UUID getViewerId() {
		String id = get(viewerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	public void setViewerId(UUID id) {
		set(viewerId, (id == null) ? null : id.toString());
	}
	

	public UUID getSourceId() {
		String id = get(sourceId);
		return (id == null) ? null : UUID.fromString(id);
	}
	public void setSourceId(UUID id) {
		set(sourceId, (id == null) ? null : id.toString());
	}
	

	public UUID getDestId() {
		String id = get(destId);
		return (id == null) ? null : UUID.fromString(id);
	}
	public void setDestId(UUID id) {
		set(destId, (id == null) ? null : id.toString());
	}
}
