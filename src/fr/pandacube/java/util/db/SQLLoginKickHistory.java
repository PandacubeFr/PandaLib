package fr.pandacube.java.util.db;

import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLLoginKickHistory extends SQLElement<SQLLoginKickHistory> {

	public SQLLoginKickHistory() {
		super();
	}

	public SQLLoginKickHistory(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_loginkick_history";
	}

	public static final SQLField<SQLLoginKickHistory, Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLFKField<SQLLoginKickHistory, String, SQLPlayer> playerId = new SQLFKField<>("playerId", SQLType.CHAR(36), false,
			SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<SQLLoginKickHistory, String> ip = new SQLField<>("ip", SQLType.VARCHAR(128), true);
	public static final SQLField<SQLLoginKickHistory, String> playerName = new SQLField<>("playerName", SQLType.VARCHAR(16), true);
	public static final SQLField<SQLLoginKickHistory, Integer> minecraftVersion = new SQLField<>("minecraftVersion", SQLType.INT, false, 0);
	public static final SQLField<SQLLoginKickHistory, String> hostName = new SQLField<>("hostName", SQLType.VARCHAR(128), true);
	public static final SQLField<SQLLoginKickHistory, String> kickReason = new SQLField<>("kickReason", SQLType.VARCHAR(512), true);

	public UUID getPlayerId() {
		String id = get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setPlayerId(UUID pName) {
		set(playerId, (pName == null) ? (String) null : pName.toString());
	}
}
