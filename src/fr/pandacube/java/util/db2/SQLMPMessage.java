package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.PlayerFinder;
import fr.pandacube.java.util.db2.sql_tools.ORM;
import fr.pandacube.java.util.db2.sql_tools.ORMException;
import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLElementList;
import fr.pandacube.java.util.db2.sql_tools.SQLFKField;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLOrderBy;
import fr.pandacube.java.util.db2.sql_tools.SQLType;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereChain;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereLike;
import fr.pandacube.java.util.db2.sql_tools.SQLOrderBy.Direction;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereChain.SQLBoolOp;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp.SQLComparator;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereNull;

public class SQLMPMessage extends SQLElement {
	
	
	public SQLMPMessage() { super(); }
	public SQLMPMessage(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_mp_message"; }
	
	
	public static final SQLField<Long>                time        = new SQLField<>("time",        SQLType.BIGINT,       false);
	public static final SQLField<Integer>             securityKey = new SQLField<>("securityKey", SQLType.INT,          false);
	public static final SQLFKField<String, SQLPlayer> viewerId    = new SQLFKField<>("viewerId",  SQLType.CHAR(36),     false, SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLFKField<String, SQLPlayer> sourceId    = new SQLFKField<>("sourceId",  SQLType.CHAR(36),     true,  SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLFKField<String, SQLPlayer> destId      = new SQLFKField<>("destId",    SQLType.CHAR(36),     true,  SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLFKField<Integer, SQLMPGroup> destGroup = SQLFKField.idFK("destGroup",  SQLType.INT,          true,  SQLMPGroup.class);
	public static final SQLField<String>              message     = new SQLField<>("message",     SQLType.VARCHAR(512), false);
	public static final SQLField<Boolean>             wasRead     = new SQLField<>("wasRead",     SQLType.BOOLEAN,      false);
	public static final SQLField<Boolean>             deleted     = new SQLField<>("deleted",     SQLType.BOOLEAN,      false, (Boolean) false);
	public static final SQLField<Boolean>             serverSync  = new SQLField<>("serverSync",  SQLType.BOOLEAN,      false);


	public UUID getViewerId() {
		String id = get(viewerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	public void setViewerId(UUID id) {
		set(viewerId, (id == null) ? null : id.toString());
	}
	

	public UUID getSourceId() {
		String id = get(sourceId);
		return (id == null) ? null : UUID.fromString(id);
	}
	public void setSourceId(UUID id) {
		set(sourceId, (id == null) ? null : id.toString());
	}
	

	public UUID getDestId() {
		String id = get(destId);
		return (id == null) ? null : UUID.fromString(id);
	}
	public void setDestId(UUID id) {
		set(destId, (id == null) ? null : id.toString());
	}
	
	
	
	
	
	
	

	
	
	public static SQLElementList<SQLMPMessage> getAllUnsyncMessage() throws ORMException {
		return ORM.getAll(SQLMPMessage.class,
				new SQLWhereComp(SQLMPMessage.serverSync, SQLComparator.EQ, false),
				new SQLOrderBy().addField(SQLMPMessage.time),
				null, null);
	}
	
	

	public static SQLElementList<SQLMPMessage> getAllUnreadForPlayer(UUID player) throws ORMException {
		return getForPlayer(player, true, null, false);
	}
	
	
	public static SQLElementList<SQLMPMessage> getOneDiscussionForPlayer(UUID player, Object discussion, Integer numberLast, boolean revert) throws ORMException {
		if (player == null) return null;
		if (discussion != null && !(discussion instanceof String) && !(discussion instanceof UUID)) return null;
		if (discussion != null && discussion instanceof String && !PlayerFinder.isValidPlayerName(discussion.toString())) return null;
		
		
		SQLWhereChain where = new SQLWhereChain(SQLBoolOp.AND)
				.add(new SQLWhereComp(SQLMPMessage.viewerId, SQLComparator.EQ, player.toString()));
		if (discussion == null) // message de système
			where.add(new SQLWhereNull(SQLMPMessage.sourceId, true))
					.add(new SQLWhereNull(SQLMPMessage.destGroup, true));
		else if (discussion instanceof String) { // message de groupe
			SQLMPGroup groupEl = ORM.getFirst(SQLMPGroup.class,
					new SQLWhereComp(SQLMPGroup.groupName, SQLComparator.EQ, (String)discussion), null);
			if (groupEl == null)
				return null;
			where.add(new SQLWhereComp(SQLMPMessage.destGroup, SQLComparator.EQ, groupEl.getId()));
		}
		else if (discussion instanceof UUID && discussion.equals(player)) // message à lui même
			where.add(new SQLWhereLike(SQLMPMessage.destId, discussion.toString()))
					.add(new SQLWhereLike(SQLMPMessage.sourceId, discussion.toString()))
					.add(new SQLWhereNull(SQLMPMessage.destGroup, true));
		else // discussion instanceof UUID
			where.add(new SQLWhereChain(SQLBoolOp.OR)
							.add(new SQLWhereLike(SQLMPMessage.destId, discussion.toString()))
							.add(new SQLWhereLike(SQLMPMessage.sourceId, discussion.toString())))
					.add(new SQLWhereNull(SQLMPMessage.destGroup, true));
		
		
		SQLOrderBy orderBy = new SQLOrderBy().addField(SQLMPMessage.time, revert ? Direction.DESC : Direction.ASC);
		
		return ORM.getAll(SQLMPMessage.class, where, orderBy, numberLast, null);
	}
	
	
	public static SQLElementList<SQLMPMessage> getForPlayer(UUID player, boolean onlyUnread, Integer numberLast, boolean revert) throws ORMException {
		if (player == null) return null;
		
		SQLWhereChain where = new SQLWhereChain(SQLBoolOp.AND);
		where.add(new SQLWhereComp(SQLMPMessage.viewerId, SQLComparator.EQ, player.toString()));
		if (onlyUnread)
			where.add(new SQLWhereComp(SQLMPMessage.wasRead, SQLComparator.EQ, false));
		
		SQLOrderBy orderBy = new SQLOrderBy().addField(SQLMPMessage.time, revert ? Direction.DESC : Direction.ASC);
		
		return ORM.getAll(SQLMPMessage.class, where, orderBy, numberLast, null);
	}
	
	
	
	
}
