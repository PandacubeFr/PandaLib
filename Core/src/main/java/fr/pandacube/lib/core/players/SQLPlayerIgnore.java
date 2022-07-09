package fr.pandacube.lib.core.players;

import java.util.Map;
import java.util.UUID;

import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.db.SQLElement;
import fr.pandacube.lib.core.db.SQLFKField;

public class SQLPlayerIgnore extends SQLElement<SQLPlayerIgnore> {

	public SQLPlayerIgnore() {
		super();
	}

	public SQLPlayerIgnore(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "player_ignore";
	}

	public static final SQLFKField<SQLPlayerIgnore, UUID, SQLPlayer> ignorer = foreignKey(false, SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLFKField<SQLPlayerIgnore, UUID, SQLPlayer> ignored = foreignKey(false, SQLPlayer.class, SQLPlayer.playerId);


	public static SQLPlayerIgnore getPlayerIgnoringPlayer(UUID ignorer, UUID ignored) throws DBException {
		return DB.getFirst(SQLPlayerIgnore.class, SQLPlayerIgnore.ignorer.eq(ignorer).and(SQLPlayerIgnore.ignored.eq(ignored)));
	}

	public static boolean isPlayerIgnoringPlayer(UUID ignorer, UUID ignored) throws DBException {
		return getPlayerIgnoringPlayer(ignorer, ignored) != null;
	}

	public static void setPlayerIgnorePlayer(UUID ignorer, UUID ignored, boolean newIgnoreState) throws DBException {
		SQLPlayerIgnore el = getPlayerIgnoringPlayer(ignorer, ignored);
		if (el == null && newIgnoreState) {
			el = new SQLPlayerIgnore();
			el.set(SQLPlayerIgnore.ignorer, ignorer);
			el.set(SQLPlayerIgnore.ignored, ignored);
			el.save();
			return;
		}
		if (el != null && !newIgnoreState) {
			el.delete();
		}

	}

	public static Map<UUID, SQLPlayer> getIgnoredPlayer(UUID ignorer) throws DBException {
		return DB.getAll(SQLPlayerIgnore.class, SQLPlayerIgnore.ignorer.eq(ignorer))
				.getReferencedEntriesInGroups(SQLPlayerIgnore.ignored);
	}

}
