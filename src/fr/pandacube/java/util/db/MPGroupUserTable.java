package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MPGroupUserTable extends SQLTable<MPGroupUserElement> {
	
	public MPGroupUserTable() throws SQLException {
		super("pandacube_mp_group_user");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "groupId INT NOT NULL,"
				+ "playerId VARCHAR(36) NOT NULL";
	}

	@Override
	protected MPGroupUserElement getElementInstance(ResultSet sqlResult)
			throws SQLException {
		return new MPGroupUserElement(
				sqlResult.getInt("id"),
				sqlResult.getInt("groupId"),
				sqlResult.getString("playerId"));
	}
	
	
	/**
	 * Retourne l'instance de MPGroupUserElement correcpondant à la présence d'un joueur dans un groupe
	 * @param group le groupe concerné, sous forme d'instance de MPGroupElement
	 * @param player l'identifiant du joueur
	 * @return null si la correspondance n'a pas été trouvée
	 * @throws SQLException
	 */
	public MPGroupUserElement getPlayerInGroup(MPGroupElement group, UUID player) throws SQLException {
		if (player == null || group == null) return null;
		return getFirst("groupId = "+group.getId()+" AND playerId = '"+player+"'", "id");
	}

}
