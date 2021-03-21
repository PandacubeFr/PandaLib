package fr.pandacube.lib.core.players;

import java.util.UUID;

import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.db.SQLElement;
import fr.pandacube.lib.core.db.SQLFKField;
import fr.pandacube.lib.core.db.SQLField;
import fr.pandacube.lib.core.util.Log;

public class SQLPlayerNameHistory extends SQLElement<SQLPlayerNameHistory> {
	
	public SQLPlayerNameHistory() {
		super();
	}

	public SQLPlayerNameHistory(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "player_name_history";
	}
	
	public static final SQLFKField<SQLPlayerNameHistory, UUID, SQLPlayer> playerId = foreignKey(false, SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<SQLPlayerNameHistory, String> playerName = field(VARCHAR(16), false);
	public static final SQLField<SQLPlayerNameHistory, Long> timeChanged = field(BIGINT, true);
	
	
	public static void updateIfNeeded(UUID player, String name, long time) {
		SQLPlayerNameHistory histEl;
		try {
			histEl = DB.getFirst(SQLPlayerNameHistory.class, playerId.eq(player).and(playerName.eq(name)));

			if (histEl == null) {
				histEl = new SQLPlayerNameHistory();
				histEl.set(playerId, player);
				histEl.set(playerName, name);
				histEl.set(timeChanged, time);
				histEl.save();
			}
			else if (time < histEl.get(timeChanged)) {
				histEl.set(timeChanged, time);
				histEl.save();
			}
		} catch (DBException e) {
			Log.severe(e);
		}
	}

}
