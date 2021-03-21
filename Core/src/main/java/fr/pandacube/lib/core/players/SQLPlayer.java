package fr.pandacube.lib.core.players;

import java.sql.Date;
import java.util.Set;
import java.util.UUID;

import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.db.SQLElement;
import fr.pandacube.lib.core.db.SQLElementList;
import fr.pandacube.lib.core.db.SQLField;

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
		return "player";
	}

	/*
	 * Champs de la table
	 */
	public static final SQLField<SQLPlayer, UUID> playerId = field(CHAR36_UUID, false);
	public static final SQLField<SQLPlayer, String> playerName = field(VARCHAR(16), false);
	public static final SQLField<SQLPlayer, UUID> token = field(CHAR36_UUID, true);
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
