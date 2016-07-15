package fr.pandacube.java.util.db;

import java.sql.Date;
import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.ORM;
import fr.pandacube.java.util.db.sql_tools.ORMException;
import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;
import fr.pandacube.java.util.db.sql_tools.SQLWhereComp;
import fr.pandacube.java.util.db.sql_tools.SQLWhereComp.SQLComparator;

public class SQLPlayer extends SQLElement<SQLPlayer> {

	public SQLPlayer() {
		super();
	}

	public SQLPlayer(int id) {
		super(id);
	}

	/*
	 * Nom de la table
	 */
	@Override
	protected String tableName() {
		return "pandacube_player";
	}

	/*
	 * Champs de la table
	 */
	public static final SQLField<SQLPlayer, String> playerId = new SQLField<>("playerId", SQLType.CHAR(36), false);
	public static final SQLField<SQLPlayer, String> token = new SQLField<>("token", SQLType.CHAR(36), true);
	public static final SQLField<SQLPlayer, String> mailCheck = new SQLField<>("mailCheck", SQLType.VARCHAR(255), true);
	public static final SQLField<SQLPlayer, String> password = new SQLField<>("password", SQLType.VARCHAR(255), true);
	public static final SQLField<SQLPlayer, String> mail = new SQLField<>("mail", SQLType.VARCHAR(255), true);
	public static final SQLField<SQLPlayer, String> playerDisplayName = new SQLField<>("playerDisplayName", SQLType.VARCHAR(255),
			false);
	public static final SQLField<SQLPlayer, Long> firstTimeInGame = new SQLField<>("firstTimeInGame", SQLType.BIGINT, false, 0L);
	public static final SQLField<SQLPlayer, Long> timeWebRegister = new SQLField<>("timeWebRegister", SQLType.BIGINT, true);
	public static final SQLField<SQLPlayer, Long> lastTimeInGame = new SQLField<>("lastTimeInGame", SQLType.BIGINT, true);
	public static final SQLField<SQLPlayer, Long> lastWebActivity = new SQLField<>("lastWebActivity", SQLType.BIGINT, false, 0L);
	public static final SQLField<SQLPlayer, String> onlineInServer = new SQLField<>("onlineInServer", SQLType.VARCHAR(32), true);
	public static final SQLField<SQLPlayer, String> skinURL = new SQLField<>("skinURL", SQLType.VARCHAR(255), true);
	public static final SQLField<SQLPlayer, Boolean> isVanish = new SQLField<>("isVanish", SQLType.BOOLEAN, false,
			(Boolean) false);
	public static final SQLField<SQLPlayer, Date> birthday = new SQLField<>("birthday", SQLType.DATE, true);
	public static final SQLField<SQLPlayer, Integer> lastYearCelebBday = new SQLField<>("lastYearCelebratedBirthday", SQLType.INT,
			false, 0);
	public static final SQLField<SQLPlayer, Long> banTimeout = new SQLField<>("banTimeout", SQLType.BIGINT, true);
	public static final SQLField<SQLPlayer, Long> muteTimeout = new SQLField<>("muteTimeout", SQLType.BIGINT, true);
	public static final SQLField<SQLPlayer, Boolean> isWhitelisted = new SQLField<>("isWhitelisted", SQLType.BOOLEAN, false,
			(Boolean) false);
	public static final SQLField<SQLPlayer, Long> bambou = new SQLField<>("bambou", SQLType.BIGINT, false, 0L);
	public static final SQLField<SQLPlayer, String> grade = new SQLField<>("grade", SQLType.VARCHAR(36), false, "default");

	/*
	 * Getteurs spécifique (encapsulation)
	 */

	public UUID getPlayerId() {
		String id = get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}

	public UUID getToken() {
		String id = get(token);
		return (id == null) ? null : UUID.fromString(id);
	}

	/*
	 * Setteurs spécifique (encapsulation)
	 */

	public void setPlayerId(UUID pName) {
		set(playerId, (pName == null) ? (String) null : pName.toString());
	}

	public void setToken(UUID t) {
		set(token, (t == null) ? (String) null : t.toString());
	}

	public static SQLPlayer getPlayerFromUUID(UUID playerId) throws ORMException {
		return ORM.getFirst(SQLPlayer.class,
				new SQLWhereComp(SQLPlayer.playerId, SQLComparator.EQ, playerId.toString()), null);
	}

}
