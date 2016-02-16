package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import fr.pandacube.java.util.PlayerFinder;

public class MPMessageTable extends SQLTable<MPMessageElement> {

	public MPMessageTable() throws SQLException {
		super("pandacube_mp_message");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "time BIGINT NOT NULL,"
				+ "securityKey INT NOT NULL,"
				+ "viewerId VARCHAR(36) NOT NULL,"
				+ "sourceId VARCHAR(36) NULL," // Null si la source est la console ou une autre entité qu'un joueur
				+ "destId VARCHAR(36) NULL,"
				+ "destGroup INT NULL,"
				+ "message VARCHAR(512) NOT NULL,"
				+ "wasRead TINYINT NOT NULL,"
				+ "deleted TINYINT NOT NULL,"
				+ "serverSync TINYINT NOT NULL";
	}

	@Override
	protected MPMessageElement getElementInstance(ResultSet sqlResult)
			throws SQLException {
		MPMessageElement el = new MPMessageElement(
				sqlResult.getInt("id"),
				sqlResult.getLong("time"),
				sqlResult.getInt("securityKey"),
				sqlResult.getString("viewerId"),
				sqlResult.getString("sourceId"),
				sqlResult.getString("message"),
				sqlResult.getBoolean("wasRead"),
				sqlResult.getBoolean("serverSync"));
		String destId = sqlResult.getString("destId");
		el.setDestId(destId==null ? null : UUID.fromString(destId));
		
		int group = sqlResult.getInt("destGroup");
		el.setDestGroup(sqlResult.wasNull()?null:group);
		
		el.setDeleted(sqlResult.getBoolean("deleted"));
		
		return el;
	}
	
	
	
	
	
	public List<MPMessageElement> getAllUnsyncMessage() throws SQLException {
		return getAll("serverSync = 0", "time ASC", null, null);
	}
	
	

	public List<MPMessageElement> getAllUnreadForPlayer(UUID player) throws SQLException {
		return getForPlayer(player, true, null, false);
	}
	
	
	public List<MPMessageElement> getOneDiscussionForPlayer(UUID player, Object discussion, Integer numberLast, boolean revert) throws SQLException {
		if (player == null) return null;
		if (discussion != null && !(discussion instanceof String) && !(discussion instanceof UUID)) return null;
		if (discussion != null && discussion instanceof String && !PlayerFinder.isValidPlayerName(discussion.toString())) return null;
		
		String where = "viewerId = '"+player+"'";
		if (discussion == null)
			where += " AND sourceId IS NULL AND destGroup IS NULL";
		else if (discussion instanceof String)
			where += " AND destGroup IN (SELECT id FROM "+ORM.getTable(MPGroupTable.class).getTableName()+" WHERE groupName LIKE '"+discussion+"')";
		else if (discussion instanceof UUID && discussion.equals(player))
			where += " AND destId LIKE '"+discussion+"' AND sourceId LIKE '"+discussion+"' AND destGroup IS NULL";
		else // discussion instanceof UUID
			where += " AND (destId LIKE '"+discussion+"' OR sourceId LIKE '"+discussion+"') AND destGroup IS NULL";
		
		return getAll(where, (revert)?"time DESC":"time ASC", numberLast, null);
	}
	
	
	public List<MPMessageElement> getForPlayer(UUID player, boolean onlyUnread, Integer numberLast, boolean revert) throws SQLException {
		if (player == null) return null;
		
		String where = "viewerId = '"+player+"'";
		if (onlyUnread)
			where += " AND wasRead = 0";
		
		return getAll(where, (revert)?"time DESC":"time ASC", numberLast, null);
	}
	

}
