package fr.pandacube.java.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.pandacube.java.util.db2.SQLLoginHistory;
import fr.pandacube.java.util.db2.SQLPlayer;
import fr.pandacube.java.util.db2.SQLUUIDPlayer;
import fr.pandacube.java.util.db2.sql_tools.ORM;
import fr.pandacube.java.util.db2.sql_tools.SQLOrderBy;
import fr.pandacube.java.util.db2.sql_tools.SQLOrderBy.Direction;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp.SQLComparator;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereLike;
import net.alpenblock.bungeeperms.BungeePerms;

/*
 * Etape de recherche de joueur :
 * Passer par bungeeperms (si accessible)
 * utiliser directement la table pseudo <-> uuid
 * chercher dans l'historique de login
 */
public class PlayerFinder {

	private static BungeePerms getPermPlugin() {
		try {
			return BungeePerms.getInstance();
		} catch (NoClassDefFoundError | Exception e) {
			return null;
		}

	}

	public static String getLastKnownName(UUID id) {
		if (id == null) return null;

		// on passe par le plugin de permission (mise en cache ? )
		BungeePerms pl = getPermPlugin();
		if (pl != null) return pl.getPermissionsManager().getUUIDPlayerDB().getPlayerName(id);

		// on tente en accédant directement à la table des identifiants
		try {
			SQLUUIDPlayer el = ORM.getFirst(SQLUUIDPlayer.class,
					new SQLWhereComp(SQLUUIDPlayer.uuid, SQLComparator.EQ, id.toString()), null);
			if (el != null) return el.get(SQLUUIDPlayer.player);
		} catch (Exception e) {
			Log.severe("Can't search for player name from uuid in database", e);
		}

		// le pseudo est introuvable
		return null;
	}

	public static List<String> getLocalNameHistory(UUID id) {
		List<String> ret = new ArrayList<>();

		if (id == null) return ret;

		String last = getLastKnownName(id);
		if (last != null) ret.add(last);

		try {
			List<SQLLoginHistory> els = ORM.getAll(SQLLoginHistory.class,
					new SQLWhereComp(SQLLoginHistory.playerId, SQLComparator.EQ, id.toString()),
					new SQLOrderBy().addField(SQLLoginHistory.time, Direction.DESC), null, null);

			for (SQLLoginHistory el : els) {
				String name = el.get(SQLLoginHistory.playerName);
				if (ret.contains(name)) continue;
				ret.add(name);
			}

		} catch (Exception e) {
			Log.severe("Can't search for olds players names from uuid in database", e);
		}

		return ret;

	}

	/**
	 * Cherche un identifiant de compte en se basant sur le pseudo passé en
	 * paramètre. La méthode
	 * cherchera d'abord dans les derniers pseudos connus. Puis, cherchera la
	 * dernière personne à
	 * s'être connecté avec ce pseudo sur le serveur.
	 *
	 * @param exactName le pseudo complet, insensible à la casse, et dans un
	 *        format de pseudo valide
	 * @param old si on doit chercher dans les anciens pseudos de joueurs
	 * @return l'UUID du joueur si trouvé, null sinon
	 */
	public static UUID getPlayerId(String exactName, boolean old) {
		if (!isValidPlayerName(exactName)) return null; // évite une recherche
														// inutile dans la base
														// de donnée

		// on tente d'abord via le plugin de permission
		BungeePerms pl = getPermPlugin();
		if (pl != null) return pl.getPermissionsManager().getUUIDPlayerDB().getUUID(exactName);

		// on tente en accédant directement à la table des identifiants
		try {
			SQLUUIDPlayer el = ORM.getFirst(SQLUUIDPlayer.class,
					new SQLWhereLike(SQLUUIDPlayer.player, exactName.replace("_", "\\_")), null);
			if (el != null) return el.getUUID();
		} catch (Exception e) {
			Log.severe("Can't search for uuid from player name in database", e);
		}

		if (!old) return null;

		// on recherche dans les anciens pseudos
		try {
			SQLLoginHistory el = ORM.getFirst(SQLLoginHistory.class,
					new SQLWhereLike(SQLLoginHistory.playerName, exactName.replace("_", "\\_")),
					new SQLOrderBy().addField(SQLLoginHistory.time, Direction.DESC));
			if (el != null) return el.getPlayerId();
		} catch (Exception e) {
			Log.severe("Can't search for uuid from old player name in database", e);
		}

		// on a pas trouvé
		return null;

	}

	/**
	 *
	 * @param query le pseudo, partiel ou complet, insensible à la casse, qu'on
	 *        recherche
	 * @param old si on cherche aussi dans les anciens pseudos
	 * @return
	 */
	public static List<PlayerSearchResult> searchForPlayers(String query, boolean old) {
		List<PlayerSearchResult> res = new ArrayList<>();

		if (!isValidPlayerName(query)) return res;

		// rechercher parmis les derniers pseudos connus de chaque joueurs
		try {
			List<SQLUUIDPlayer> els = ORM.getAll(SQLUUIDPlayer.class,
					new SQLWhereLike(SQLUUIDPlayer.player, "%" + query.replace("_", "\\_") + "%"), null, null, null);

			for (SQLUUIDPlayer el : els)
				res.add(new PlayerSearchResult(el.getUUID(), el.get(SQLUUIDPlayer.player), null));

		} catch (Exception e) {
			Log.severe("Can't search for players names in database", e);
		}

		if (!old) return res;

		// rechercher parmi les anciens pseudos de joueurs
		try {
			List<SQLLoginHistory> els = ORM.getAll(SQLLoginHistory.class,
					new SQLWhereLike(SQLLoginHistory.playerName, "%" + query.replace("_", "\\_") + "%"),
					new SQLOrderBy().addField(SQLLoginHistory.time, Direction.DESC), null, null);

			for (SQLLoginHistory el : els) {
				if (res.contains(new PlayerSearchResult(el.getPlayerId(), null, null))) continue;
				res.add(new PlayerSearchResult(el.getPlayerId(), getLastKnownName(el.getPlayerId()),
						el.get(SQLLoginHistory.playerName)));
			}

		} catch (Exception e) {
			Log.severe("Can't search for uuid from player name in database", e);
		}

		return res;
	}

	public static class PlayerSearchResult {
		public final UUID uuid;
		public String lastName;
		public final String nameFound;

		PlayerSearchResult(UUID id, String last, String found) {
			uuid = id;
			lastName = last;
			nameFound = found;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof PlayerSearchResult)) return false;
			return uuid.equals(((PlayerSearchResult) o).uuid);
		}

		@Override
		public int hashCode() {
			return uuid.hashCode();
		}
	}

	public static boolean isValidPlayerName(String name) {
		if (name == null) return false;
		return name.matches("[0-9a-zA-Z_]{2,16}");
	}

	public static SQLPlayer getDBPlayer(UUID id) throws Exception {
		if (id == null) return null;
		return ORM.getFirst(SQLPlayer.class, new SQLWhereComp(SQLPlayer.playerId, SQLComparator.EQ, id.toString()),
				null);
	}

}
