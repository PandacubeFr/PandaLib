package fr.pandacube.java.util.db2;

import java.sql.SQLException;
import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.ORM;
import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLFKField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereChain;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereChain.SQLBoolOp;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp.SQLComparator;

public class SQLMPGroupUser extends SQLElement {
	
	
	public SQLMPGroupUser() { super(); }
	public SQLMPGroupUser(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_mp_group_user"; }
	
	
	public static final SQLFKField<Integer, SQLMPGroup> groupId = SQLFKField.idFK( "groupId",  SQLType.INT,      false, SQLMPGroup.class);
	public static final SQLFKField<String, SQLPlayer>  playerId = new SQLFKField<>("playerId", SQLType.CHAR(36), false, SQLPlayer.class, SQLPlayer.playerId);
	
	// TODO ajouter un champ qui dit si le joueur est admin du groupe
	
	
	
	public UUID getPlayerId() {
		String id = get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setPlayerId(UUID id) {
		set(playerId, (id == null) ? null : id.toString());
	}
	
	
	
	
	
	
	

	
	
	/**
	 * Retourne l'instance de SQLMPGroupUser correcpondant à la présence d'un joueur dans un groupe
	 * @param group le groupe concerné, sous forme d'instance de SQLMPGroup
	 * @param player l'identifiant du joueur
	 * @return null si la correspondance n'a pas été trouvée
	 * @throws SQLException
	 */
	public static SQLMPGroupUser getPlayerInGroup(SQLMPGroup group, UUID player) throws Exception {
		if (player == null || group == null) return null;
		return ORM.getFirst(SQLMPGroupUser.class,
				new SQLWhereChain(SQLBoolOp.AND)
				.add(new SQLWhereComp(groupId, SQLComparator.EQ, group.getId()))
				.add(new SQLWhereComp(playerId, SQLComparator.EQ, player.toString())), null);
	}

}
