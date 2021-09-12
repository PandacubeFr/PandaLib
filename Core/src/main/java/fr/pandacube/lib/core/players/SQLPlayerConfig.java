package fr.pandacube.lib.core.players;

import java.util.UUID;

import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.db.SQLElement;
import fr.pandacube.lib.core.db.SQLElementList;
import fr.pandacube.lib.core.db.SQLFKField;
import fr.pandacube.lib.core.db.SQLField;

public class SQLPlayerConfig extends SQLElement<SQLPlayerConfig> {

	public SQLPlayerConfig() {
		super();
	}

	public SQLPlayerConfig(int id) {
		super(id);
	}

	/*
	 * Nom de la table
	 */
	@Override
	protected String tableName() {
		return "player_config";
	}

	/*
	 * Champs de la table
	 */
	public static final SQLFKField<SQLPlayerConfig, UUID, SQLPlayer> playerId = foreignKey(false, SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<SQLPlayerConfig, String> key = field(VARCHAR(255), false);
	public static final SQLField<SQLPlayerConfig, String> value = field(VARCHAR(8192), false);
	
	
	
	public static String get(UUID p, String k, String deflt) throws DBException {
		SQLPlayerConfig res = DB.getFirst(SQLPlayerConfig.class, playerId.eq(p).and(key.eq(k)));
		return res == null ? deflt : res.get(value);
	}
	
	public static String get(UUID p, String k) throws DBException {
		return get(p, k, null);
	}
	
	public static void set(UUID p, String k, String v) throws DBException {
		if (v == null) {
			unset(p, k);
			return;
		}

		SQLPlayerConfig entry = DB.getFirst(SQLPlayerConfig.class, playerId.eq(p).and(key.eq(k)));
		
		if (entry == null) {
			entry = new SQLPlayerConfig();
			entry.set(playerId, p);
			entry.set(key, k);
		}
		
		entry.set(value, v);
		entry.save();
	}
	
	public static void unset(UUID p, String k) throws DBException {

		SQLPlayerConfig entry = DB.getFirst(SQLPlayerConfig.class, playerId.eq(p).and(key.eq(k)));
		
		if (entry != null)
			entry.delete();
	}

	
	public static SQLElementList<SQLPlayerConfig> getAllFromPlayer(UUID p, String likeQuery) throws DBException {
		return DB.getAll(SQLPlayerConfig.class, playerId.eq(p).and(key.like(likeQuery)));
	}
	
	public static SQLElementList<SQLPlayerConfig> getAllWithKeys(String likeQuery) throws DBException {
		return DB.getAll(SQLPlayerConfig.class, key.like(likeQuery));
	}
	
	public static SQLElementList<SQLPlayerConfig> getAllWithKeyValue(String k, String v) throws DBException {
		return DB.getAll(SQLPlayerConfig.class, key.eq(k).and(value.eq(v)));
	}
	
	
	
	
}
