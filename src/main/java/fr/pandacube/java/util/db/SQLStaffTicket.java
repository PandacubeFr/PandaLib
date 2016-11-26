package fr.pandacube.java.util.db;

import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLStaffTicket extends SQLElement<SQLStaffTicket> {

	public SQLStaffTicket() {
		super();
	}

	public SQLStaffTicket(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_staff_ticket";
	}

	public static final SQLFKField<SQLStaffTicket, String, SQLPlayer> playerId = new SQLFKField<>("playerId", SQLType.CHAR(36), false,
			SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<SQLStaffTicket, String> message = new SQLField<>("message", SQLType.VARCHAR(1024), false);
	public static final SQLField<SQLStaffTicket, Long> creationTime = new SQLField<>("creationTime", SQLType.BIGINT, false);
	public static final SQLFKField<SQLStaffTicket, String, SQLPlayer> staffPlayerId = new SQLFKField<>("staffPlayerId",
			SQLType.CHAR(36), true, SQLPlayer.class, SQLPlayer.playerId);

	public UUID getPlayerId() {
		String id = get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setPlayerId(UUID id) {
		set(playerId, (id == null) ? null : id.toString());
	}

	public UUID getstaffPlayerId() {
		String id = get(staffPlayerId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setstaffPlayerId(UUID id) {
		set(staffPlayerId, (id == null) ? null : id.toString());
	}

}
