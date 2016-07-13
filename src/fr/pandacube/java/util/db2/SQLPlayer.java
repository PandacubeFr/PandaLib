package fr.pandacube.java.util.db2;

import java.sql.Date;
import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.ORM;
import fr.pandacube.java.util.db2.sql_tools.ORMException;
import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp.SQLComparator;


public class SQLPlayer extends SQLElement {
	
	public SQLPlayer() { super(); }
	public SQLPlayer(int id) { super(id); }
	
	
	/*
	 * Nom de la table
	 */
	@Override
	protected String tableName() { return "pandacube_player"; }
	
	/*
	 * Champs de la table
	 */
	public static final SQLField<String>  playerId          = new SQLField<>("playerId",                   SQLType.CHAR(36),     false);
	public static final SQLField<String>  token             = new SQLField<>("token",                      SQLType.CHAR(36),     true);
	public static final SQLField<String>  mailCheck         = new SQLField<>("mailCheck",                  SQLType.VARCHAR(255), true);
	public static final SQLField<String>  password          = new SQLField<>("password",                   SQLType.VARCHAR(255), true);
	public static final SQLField<String>  mail              = new SQLField<>("mail",                       SQLType.VARCHAR(255), true);
	public static final SQLField<String>  playerDisplayName = new SQLField<>("playerDisplayName",          SQLType.VARCHAR(255), false);
	public static final SQLField<Long>    firstTimeInGame   = new SQLField<>("firstTimeInGame",            SQLType.BIGINT,       false, 0L);
	public static final SQLField<Long>    timeWebRegister   = new SQLField<>("timeWebRegister",            SQLType.BIGINT,       true);
	public static final SQLField<Long>    lastTimeInGame    = new SQLField<>("lastTimeInGame",             SQLType.BIGINT,       true);
	public static final SQLField<Long>    lastWebActivity   = new SQLField<>("lastWebActivity",            SQLType.BIGINT,       false, 0L);
	public static final SQLField<String>  onlineInServer    = new SQLField<>("onlineInServer",             SQLType.VARCHAR(32),  true);
	public static final SQLField<String>  skinURL           = new SQLField<>("skinURL",                    SQLType.VARCHAR(255), true);
	public static final SQLField<Boolean> isVanish          = new SQLField<>("isVanish",                   SQLType.BOOLEAN,      false, (Boolean)false);
	public static final SQLField<Date>    birthday          = new SQLField<>("birthday",                   SQLType.DATE,         true);
	public static final SQLField<Integer> lastYearCelebBday = new SQLField<>("lastYearCelebratedBirthday", SQLType.INT,          false, 0);
	public static final SQLField<Long>    banTimeout        = new SQLField<>("banTimeout",                 SQLType.BIGINT,       true);
	public static final SQLField<Long>    muteTimeout       = new SQLField<>("muteTimeout",                SQLType.BIGINT,       true);
	public static final SQLField<Boolean> isWhitelisted     = new SQLField<>("isWhitelisted",              SQLType.BOOLEAN,      false, (Boolean)false);
	public static final SQLField<Long>    bambou            = new SQLField<>("bambou",                     SQLType.BIGINT,       false, 0L);
	public static final SQLField<String>  grade             = new SQLField<>("grade",                      SQLType.VARCHAR(36),  false, "default");

	
	
	
	/*
	 * Getteurs spécifique (encapsulation)
	 */
	
	public UUID getPlayerId() {
		String id = (String)get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	public UUID getToken() {
		String id = (String)get(token);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	
	
	
	
	/*
	 * Setteurs spécifique (encapsulation)
	 */
	
	
	
	public void setPlayerId(UUID pName) {
		set(playerId, (pName == null) ? (String)null : pName.toString());
	}
	public void setToken(UUID t) {
		set(token, (t == null) ? (String)null : t.toString());
	}
	
	
	
	
	
	
	
	
	
	
	public static SQLPlayer getPlayerFromUUID(UUID playerId) throws ORMException {
		return ORM.getFirst(SQLPlayer.class,
				new SQLWhereComp(SQLPlayer.playerId, SQLComparator.EQ, playerId.toString()),
				null);
	}
	
}
