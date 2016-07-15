package fr.pandacube.java.util.db;

import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLUUIDPlayer extends SQLElement<SQLUUIDPlayer> {

	public SQLUUIDPlayer() {
		super();
	}

	public SQLUUIDPlayer(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "bungeeperms_uuidplayer";
	}

	public static final SQLFKField<SQLUUIDPlayer, String, SQLPlayer> uuid = new SQLFKField<>("uuid", SQLType.CHAR(36), false,
			SQLPlayer.playerId);
	public static final SQLField<SQLUUIDPlayer, String> player = new SQLField<>("player", SQLType.VARCHAR(16), false);

	public UUID getUUID() {
		String id = get(uuid);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setUUID(UUID id) {
		set(uuid, (id == null) ? null : id.toString());
	}

}
