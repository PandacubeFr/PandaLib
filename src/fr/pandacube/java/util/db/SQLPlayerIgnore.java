package fr.pandacube.java.util.db;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.ORM;
import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLOrderBy;
import fr.pandacube.java.util.db.sql_tools.SQLType;
import fr.pandacube.java.util.db.sql_tools.SQLWhereChain;
import fr.pandacube.java.util.db.sql_tools.SQLWhereComp;
import fr.pandacube.java.util.db.sql_tools.SQLWhereChain.SQLBoolOp;
import fr.pandacube.java.util.db.sql_tools.SQLWhereComp.SQLComparator;

public class SQLPlayerIgnore extends SQLElement<SQLPlayerIgnore> {

	public SQLPlayerIgnore() {
		super();
	}

	public SQLPlayerIgnore(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_player_ignore";
	}

	public static final SQLFKField<SQLPlayerIgnore, String, SQLPlayer> ignorer = new SQLFKField<>("ignorer", SQLType.CHAR(36), false,
			SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLFKField<SQLPlayerIgnore, String, SQLPlayer> ignored = new SQLFKField<>("ignored", SQLType.CHAR(36), false,
			SQLPlayer.class, SQLPlayer.playerId);

	public UUID getIgnorerId() {
		String id = get(ignorer);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setIgnorerId(UUID pName) {
		set(ignorer, (pName == null) ? (String) null : pName.toString());
	}

	public UUID getIgnoredId() {
		String id = get(ignored);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setIgnoredId(UUID pName) {
		set(ignored, (pName == null) ? (String) null : pName.toString());
	}

	public static SQLPlayerIgnore getPlayerIgnoringPlayer(UUID ignorer, UUID ignored) throws Exception {
		return ORM.getFirst(SQLPlayerIgnore.class,
				new SQLWhereChain(SQLBoolOp.AND)
						.add(new SQLWhereComp(SQLPlayerIgnore.ignorer, SQLComparator.EQ, ignorer.toString()))
						.add(new SQLWhereComp(SQLPlayerIgnore.ignored, SQLComparator.EQ, ignored.toString())),
				null);
	}

	public static boolean isPlayerIgnoringPlayer(UUID ignorer, UUID ignored) throws Exception {
		return getPlayerIgnoringPlayer(ignorer, ignored) != null;
	}

	public static void setPlayerIgnorePlayer(UUID ignorer, UUID ignored, boolean newIgnoreState) throws Exception {
		SQLPlayerIgnore el = getPlayerIgnoringPlayer(ignorer, ignored);
		if (el == null && newIgnoreState) {
			el = new SQLPlayerIgnore();
			el.setIgnorerId(ignorer);
			el.setIgnoredId(ignored);
			el.save();
			return;
		}
		if (el != null && !newIgnoreState) {
			el.delete();
			return;
		}

	}

	public static List<UUID> getListIgnoredPlayer(UUID ignorer) throws Exception {
		List<SQLPlayerIgnore> els = ORM.getAll(SQLPlayerIgnore.class,
				new SQLWhereComp(SQLPlayerIgnore.ignorer, SQLComparator.EQ, ignorer.toString()),
				new SQLOrderBy().addField(ORM.getSQLIdField(SQLPlayerIgnore.class)), null, null);
		List<UUID> ret = new ArrayList<>(els.size());
		for (SQLPlayerIgnore el : els)
			ret.add(el.getIgnoredId());
		return ret;
	}

}
