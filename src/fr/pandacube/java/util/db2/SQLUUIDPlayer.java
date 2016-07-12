package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLFKField;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLUUIDPlayer extends SQLElement {
	
	public SQLUUIDPlayer() { super(); }
	public SQLUUIDPlayer(int id) { super(id); }

	@Override
	protected String tableName() { return "bungeeperms_uuidplayer"; }
	
	
	
	public static final SQLFKField<String, SQLPlayer> uuid   = new SQLFKField<>("uuid", SQLType.CHAR(36),    false, SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<String>              player = new SQLField<>("player", SQLType.VARCHAR(16), false);
	
	

	
	public UUID getUUID() {
		String id = get(uuid);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setUUID(UUID id) {
		set(uuid, (id == null) ? null : id.toString());
	}
	
}
