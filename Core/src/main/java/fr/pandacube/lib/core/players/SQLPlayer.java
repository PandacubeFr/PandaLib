package fr.pandacube.lib.core.players;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.UUID;

import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.db.SQLElement;
import fr.pandacube.lib.core.db.SQLElementList;
import fr.pandacube.lib.core.db.SQLField;
import fr.pandacube.lib.core.util.Log;

public class SQLPlayer extends SQLElement<SQLPlayer> {
	
	/** If the player birth year is internally 1800, it is considered as a non disclosed age.
	 * All player with an age below 13 should have their age automatically hidden.
	 */
	public static final int UNDISCLOSED_AGE_YEAR = 1800;
	

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
		return "player";
	}

	/*
	 * Champs de la table
	 */
	public static final SQLField<SQLPlayer, UUID> playerId = field(CHAR36_UUID, false);
	public static final SQLField<SQLPlayer, String> playerName = field(VARCHAR(16), false);
	public static final SQLField<SQLPlayer, String> token = field(CHAR(36), true);
	public static final SQLField<SQLPlayer, String> mailCheck = field(VARCHAR(255), true);
	public static final SQLField<SQLPlayer, String> password = field(VARCHAR(255), true);
	public static final SQLField<SQLPlayer, String> mail = field(VARCHAR(255), true);
	public static final SQLField<SQLPlayer, String> playerDisplayName = field(VARCHAR(255),
			false);
	public static final SQLField<SQLPlayer, Long> firstTimeInGame = field(BIGINT, false, 0L);
	public static final SQLField<SQLPlayer, Long> timeWebRegister = field(BIGINT, true);
	public static final SQLField<SQLPlayer, Long> lastTimeInGame = field(BIGINT, true);
	public static final SQLField<SQLPlayer, Long> lastWebActivity = field(BIGINT, false, 0L);
	public static final SQLField<SQLPlayer, String> onlineInServer = field(VARCHAR(32), true);
	public static final SQLField<SQLPlayer, String> skinURL = field(VARCHAR(255), true);
	public static final SQLField<SQLPlayer, Boolean> isVanish = field(BOOLEAN, false,
			(Boolean) false);
	public static final SQLField<SQLPlayer, Date> birthday = field(DATE, true);
	public static final SQLField<SQLPlayer, Integer> lastYearCelebratedBirthday = field(INT,
			false, 0);
	public static final SQLField<SQLPlayer, Long> banTimeout = field(BIGINT, true);
	public static final SQLField<SQLPlayer, Long> muteTimeout = field(BIGINT, true);
	public static final SQLField<SQLPlayer, Boolean> isWhitelisted = field(BOOLEAN, false,
			(Boolean) false);
	public static final SQLField<SQLPlayer, Long> bambou = field(BIGINT, false, 0L);
	public static final SQLField<SQLPlayer, String> grade = field(VARCHAR(36), false, "default");

	
	

	
	public Calendar getBirthday() {

		try {
			Date birthday = get(SQLPlayer.birthday);
			if (birthday == null) // le joueur n'a pas de date d'anniversaire
				return null;
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(birthday);
			return cal;
		} catch (Exception e) {
			Log.severe(e);
			return null;
		}
	}
	
	public void setBirthday(int day, int month, Integer year) {
		if (year == null)
			year = UNDISCLOSED_AGE_YEAR;
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(year, month, day, 0, 0, 0);
		
		set(SQLPlayer.birthday, new java.sql.Date(cal.getTimeInMillis()));
	}
	
	
	public boolean isWebRegistered() {
		return get(password) != null;
	}
	
	
	
	
	
	
	public static SQLPlayer getPlayerFromUUID(UUID pId) throws DBException {
		if (pId == null)
			return null;
		return DB.getFirst(SQLPlayer.class, playerId.eq(pId));
	}
	
	
	public static SQLElementList<SQLPlayer> getPlayersFromUUIDs(Set<UUID> playerIds) throws DBException {
		
		if (playerIds == null || playerIds.isEmpty()) {
			return new SQLElementList<>();
		}

		return DB.getAll(SQLPlayer.class, playerId.in(playerIds));
		
	}
}
