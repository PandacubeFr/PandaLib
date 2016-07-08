package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLMPGroupUser extends SQLElement {
	
	
	public SQLMPGroupUser() { super(); }
	public SQLMPGroupUser(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_mp_group_user"; }
	
	
	public static final SQLField<Integer> groupId = new SQLField<>("groupId", SQLType.INT, false);
	public static final SQLField<String> playerId = new SQLField<>("playerId", SQLType.CHAR(36), false);
	
	// TODO ajouter un champ qui dit si le joueur est admin du groupe
	
	
	
	public UUID getPlayerId() {
		String id = get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setPlayerId(UUID id) {
		set(playerId, (id == null) ? null : id.toString());
	}
	

}
