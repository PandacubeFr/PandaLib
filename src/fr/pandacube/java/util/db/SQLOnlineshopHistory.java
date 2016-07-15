package fr.pandacube.java.util.db;

import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLOnlineshopHistory extends SQLElement<SQLOnlineshopHistory> {

	public SQLOnlineshopHistory() {
		super();
	}

	public SQLOnlineshopHistory(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_onlineshop_history";
	}

	public static final SQLField<SQLOnlineshopHistory, Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLField<SQLOnlineshopHistory, String> transactionId = new SQLField<>("transactionId", SQLType.VARCHAR(255), true);
	public static final SQLField<SQLOnlineshopHistory, SourceType> sourceType = new SQLField<>("sourceType", SQLType.ENUM(SourceType.class),
			false);
	public static final SQLFKField<SQLOnlineshopHistory, String, SQLPlayer> sourcePlayerId = new SQLFKField<>("sourcePlayerId",
			SQLType.CHAR(36), true, SQLPlayer.playerId);
	public static final SQLField<SQLOnlineshopHistory, Double> sourceQuantity = new SQLField<>("sourceQuantity", SQLType.DOUBLE, false);
	public static final SQLField<SQLOnlineshopHistory, String> sourceName = new SQLField<>("sourceName", SQLType.VARCHAR(64), false);
	public static final SQLField<SQLOnlineshopHistory, DestType> destType = new SQLField<>("destType", SQLType.ENUM(DestType.class), false);
	public static final SQLFKField<SQLOnlineshopHistory, String, SQLPlayer> destPlayerId = new SQLFKField<>("destPlayerId", SQLType.CHAR(36),
			false, SQLPlayer.playerId);
	public static final SQLField<SQLOnlineshopHistory, Double> destQuantity = new SQLField<>("destQuantity", SQLType.DOUBLE, false);
	public static final SQLField<SQLOnlineshopHistory, String> destName = new SQLField<>("destName", SQLType.VARCHAR(64), false);

	public UUID getSourcePlayerId() {
		String id = get(sourcePlayerId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setSourcePlayerId(UUID pName) {
		set(sourcePlayerId, (pName == null) ? (String) null : pName.toString());
	}

	public UUID getDestPlayerId() {
		String id = get(destPlayerId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setDestPlayerId(UUID pName) {
		set(destPlayerId, (pName == null) ? (String) null : pName.toString());
	}

	public static enum SourceType {
		REAL_MONEY, BAMBOU
	}

	public static enum DestType {
		BAMBOU, GRADE
	}

}
