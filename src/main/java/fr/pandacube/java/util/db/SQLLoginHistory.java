package fr.pandacube.java.util.db;

import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLLoginHistory extends SQLElement<SQLLoginHistory> {

	public SQLLoginHistory() {
		super();
	}

	public SQLLoginHistory(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_login_history";
	}

	public static final SQLField<SQLLoginHistory, Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLFKField<SQLLoginHistory, String, SQLPlayer> playerId = new SQLFKField<>("playerId", SQLType.CHAR(36), false,
			SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<SQLLoginHistory, String> ip = new SQLField<>("ip", SQLType.VARCHAR(128), true);
	public static final SQLField<SQLLoginHistory, ActionType> actionType = new SQLField<>("actionType", SQLType.ENUM(ActionType.class),
			false);
	public static final SQLField<SQLLoginHistory, Integer> nbOnline = new SQLField<>("nbOnline", SQLType.INT, false);
	public static final SQLField<SQLLoginHistory, String> playerName = new SQLField<>("playerName", SQLType.VARCHAR(16), true);
	public static final SQLField<SQLLoginHistory, Integer> minecraftVersion = new SQLField<>("minecraftVersion", SQLType.INT, false, 0);
	public static final SQLField<SQLLoginHistory, String> hostName = new SQLField<>("hostName", SQLType.VARCHAR(128), true);

	public UUID getPlayerId() {
		String id = get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setPlayerId(UUID pName) {
		set(playerId, (pName == null) ? (String) null : pName.toString());
	}

	public enum ActionType {
		LOGIN, LOGOUT
	}
}
