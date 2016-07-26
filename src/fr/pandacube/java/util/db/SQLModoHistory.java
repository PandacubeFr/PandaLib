package fr.pandacube.java.util.db;

import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLModoHistory extends SQLElement<SQLModoHistory> {

	public SQLModoHistory() {
		super();
	}

	public SQLModoHistory(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_modo_history";
	}

	public static final SQLFKField<SQLModoHistory, String, SQLPlayer> modoId = new SQLFKField<>("modoId", SQLType.CHAR(36), true,
			SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<SQLModoHistory, ActionType> actionType = new SQLField<>("actionType", SQLType.ENUM(ActionType.class),
			false);
	public static final SQLField<SQLModoHistory, Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLFKField<SQLModoHistory, String, SQLPlayer> playerId = new SQLFKField<>("playerId", SQLType.CHAR(36), false,
			SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<SQLModoHistory, Long> value = new SQLField<>("value", SQLType.BIGINT, true);
	public static final SQLField<SQLModoHistory, String> message = new SQLField<>("message", SQLType.VARCHAR(2048), false);

	public UUID getModoId() {
		String id = get(modoId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setModoId(UUID pName) {
		set(modoId, (pName == null) ? (String) null : pName.toString());
	}

	public UUID getPlayerId() {
		String id = get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setPlayerId(UUID pName) {
		set(playerId, (pName == null) ? (String) null : pName.toString());
	}

	public enum ActionType {
		BAN, UNBAN, MUTE, UNMUTE, REPORT, KICK
	}

}
