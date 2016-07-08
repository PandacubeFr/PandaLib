package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLModoHistory extends SQLElement {

	
	public SQLModoHistory() { super(); }
	public SQLModoHistory(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_modo_history"; }
	
	
	public static final SQLField<String> modoId = new SQLField<>("modoId", SQLType.CHAR(36), true);
	public static final SQLField<ActionType> actionType = new SQLField<>("actionType", SQLType.ENUM(ActionType.class), false);
	public static final SQLField<Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLField<String> playerId = new SQLField<>("playerId", SQLType.CHAR(36), false);
	public static final SQLField<Long> value = new SQLField<>("value", SQLType.BIGINT, true);
	public static final SQLField<String> message = new SQLField<>("message", SQLType.VARCHAR(512), false);
	
	
	

	public UUID getModoId() {
		String id = (String)get(modoId);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setModoId(UUID pName) {
		set(modoId, (pName == null) ? (String)null : pName.toString());
	}
	
	

	public UUID getPlayerId() {
		String id = (String)get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setPlayerId(UUID pName) {
		set(playerId, (pName == null) ? (String)null : pName.toString());
	}
	
	
	


	public enum ActionType{
		BAN, UNBAN, MUTE, UNMUTE, REPORT, KICK
	}

}
