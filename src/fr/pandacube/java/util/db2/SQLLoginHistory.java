package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLLoginHistory extends SQLElement {

	
	public SQLLoginHistory() { super(); }
	public SQLLoginHistory(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_login_history"; }
	
	
	public static final SQLField<Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLField<String> playerId = new SQLField<>("playerId", SQLType.CHAR(36), false);
	public static final SQLField<String> ip = new SQLField<>("ip", SQLType.VARCHAR(128), true);
	public static final SQLField<ActionType> actionType = new SQLField<>("actionType", SQLType.ENUM(ActionType.class), false);
	public static final SQLField<Integer> nbOnline = new SQLField<>("nbOnline", SQLType.INT, false);
	public static final SQLField<String> playerName = new SQLField<>("playerName", SQLType.VARCHAR(16), true);
	public static final SQLField<Integer> minecraftVersion = new SQLField<>("minecraftVersion", SQLType.INT, false, 0);
	
	
	
	
	
	public UUID getPlayerId() {
		String id = (String)get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setPlayerId(UUID pName) {
		set(playerId, (pName == null) ? (String)null : pName.toString());
	}
	
	
	
	
	
	public enum ActionType {
		LOGIN, LOGOUT
	}
}
