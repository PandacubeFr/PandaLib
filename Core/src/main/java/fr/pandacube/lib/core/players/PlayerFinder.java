package fr.pandacube.lib.core.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.UncheckedExecutionException;

import fr.pandacube.lib.core.commands.SuggestionsSupplier;
import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.db.SQLOrderBy;
import fr.pandacube.lib.core.util.LevenshteinDistance;
import fr.pandacube.lib.core.util.Log;

/*
 * Etape de recherche de joueur :
 * utiliser directement la table pandacube_player
 * chercher dans l'historique de login
 */
public class PlayerFinder {
	
	private static Cache<UUID, String> playerLastKnownName = CacheBuilder.newBuilder()
			.expireAfterWrite(2, TimeUnit.MINUTES)
			.maximumSize(1000)
			.build();
	
	record PlayerIdCacheKey(String pName, boolean old) { }
	private static Cache<PlayerIdCacheKey, UUID> playerId = CacheBuilder.newBuilder()
			.expireAfterWrite(2, TimeUnit.MINUTES)
			.maximumSize(1000)
			.build();
	
	public static void clearCacheEntry(UUID pId, String pName) {
		playerLastKnownName.invalidate(pId);
		playerId.invalidate(new PlayerIdCacheKey(pName.toLowerCase(), true));
		playerId.invalidate(new PlayerIdCacheKey(pName.toLowerCase(), false));
	}

	public static String getLastKnownName(UUID id) {
		if (id == null) return null;
		
		try {
			return playerLastKnownName.get(id, () -> {
				try {
					return getDBPlayer(id).get(SQLPlayer.playerName); // eventual NPE will be ignored
				} catch (NullPointerException|DBException e) {
					Log.severe("Can't search for player name from uuid in database", e);
					throw e;
				}
			});
		} catch (ExecutionException e) {
			// ignored (ORM Exception)
		} catch (UncheckedExecutionException e) {
			Log.severe("Can’t retrieve player last known name of " + id, e);
		}

		return null;
	}

	/**
	 * Cherche un UUID de compte en se basant sur le pseudo passé en
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
		if (!isValidPlayerName(exactName))
			return null; // évite une recherche inutile dans la base de donnée

		try {
			return playerId.get(new PlayerIdCacheKey(exactName.toLowerCase(), old), () -> {
				try {
					SQLPlayer el = DB.getFirst(SQLPlayer.class,
							SQLPlayer.playerName.like(exactName.replace("_", "\\_")),
							SQLOrderBy.desc(SQLPlayer.lastTimeInGame));
					/*
					 * Si il n'y a pas 1 élément, alors soit le pseudo n'a jamais été attribué
					 * soit il a été changé, et nous avons l'ancien possesseur et le nouveau possesseur du pseudo.
					 */
					if (el != null)
						return el.get(SQLPlayer.playerId);
				} catch (Exception e) {
					Log.severe("Can't search for uuid from player name in database", e);
				}
				
				if (old) {
					try {
						SQLPlayerNameHistory el = DB.getFirst(SQLPlayerNameHistory.class,
								SQLPlayerNameHistory.playerName.like(exactName.replace("_", "\\_")),
								SQLOrderBy.desc(SQLPlayerNameHistory.timeChanged));
						if (el != null) return el.get(SQLPlayerNameHistory.playerId);
					} catch (Exception e) {
						Log.severe("Can't search for uuid from old player name in database", e);
					}
				}
				
				throw new Exception(); // ignored
			});
		} catch (ExecutionException e) {
			// ignored
		}
		
		return null;

	}
	
	/**
	 * Parse a player name or a player ID from the provided string, and returns the UUID of the player, if found.
	 * @param nameOrId a valid player name, or a UUID in the format of {@link UUID#toString()}
	 * @return the id of the player, or null if not found or if the input is invalid.
	 */
	public static UUID parsePlayer(String nameOrId) {
		if (nameOrId == null)
			return null;
		if (isValidPlayerName(nameOrId))
			return getPlayerId(nameOrId, true);
		try {
			return UUID.fromString(nameOrId);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isValidPlayerName(String name) {
		if (name == null) return false;
		return name.matches("[0-9a-zA-Z_]{2,16}");
	}

	public static SQLPlayer getDBPlayer(UUID id) throws Exception {
		if (id == null) return null;
		return SQLPlayer.getPlayerFromUUID(id);
	}
	
	
	
	
	
	
	
	private static final SuggestionsSupplier<?> TAB_PLAYER_OFFLINE = (sender, tokenIndex, token, args) -> {
		if (token.length() < 3) {
			return Collections.emptyList();
		}
		List<SearchResponseProfile> list = findPlayer(token, 10).profiles;
		if (!list.isEmpty() && list.get(0).d == 0)
			return Collections.singletonList(list.get(0).name);
		return list.stream().map(p -> p.name).collect(Collectors.toList());
	};
	
	@SuppressWarnings("unchecked")
	public static final <S> SuggestionsSupplier<S> TAB_PLAYER_OFFLINE() {
		return (SuggestionsSupplier<S>) TAB_PLAYER_OFFLINE;
	}
	
	
	public static SearchResponse findPlayer(String query, int resultsCount) {
		SearchResponse cacheData = searchCache.getUnchecked(query.toLowerCase());
		cacheData = new SearchResponse(cacheData.profiles.subList(0, Math.min(resultsCount, cacheData.profiles.size())));
		return cacheData;
	}
	

	public static int SEARCH_MAX_DISTANCE = 20;
	public static int MISSING_CHAR_DISTANCE = 1;
	public static int SURPLUS_CHAR_DISTANCE = 8;
	public static int DIFF_CHAR_DISTANCE = 8;
	public static int CLOSE_CHAR_DISTANCE = 4;
	
	public static int OLD_NICK_MULTIPLIER = 2;
	
	
	private static List<List<Character>> CONFUSABLE_CHARACTERS = ImmutableList.of(
			ImmutableList.of('o', '0'),
			ImmutableList.of('i', '1', 'l'),
			ImmutableList.of('b', '8')
	);
	private static ToIntBiFunction<Character, Character> CHAR_DISTANCE = (c1, c2) -> {
		if (c1.equals(c2))
			return 0;
		for (List<Character> charTab : CONFUSABLE_CHARACTERS) {
			if (charTab.contains(c1) && charTab.contains(c2))
				return CLOSE_CHAR_DISTANCE;
		}
		return DIFF_CHAR_DISTANCE;
	};
	
	record NamesCacheResult(String name, UUID id) { }
	private static LoadingCache<String, List<NamesCacheResult>> namesCache = CacheBuilder.newBuilder()
			.expireAfterWrite(2, TimeUnit.MINUTES)
			.maximumSize(1)
			.build(CacheLoader.from((String k) -> {
				List<NamesCacheResult> cached = new ArrayList<>();
				try {
					DB.forEach(SQLPlayerNameHistory.class, el -> {
						cached.add(new NamesCacheResult(el.get(SQLPlayerNameHistory.playerName), el.get(SQLPlayerNameHistory.playerId)));
					});
				} catch (DBException e) {
					throw new RuntimeException(e);
				}
				return cached;
			}));
	
	private static LoadingCache<String, SearchResponse> searchCache = CacheBuilder.newBuilder()
			.expireAfterWrite(2, TimeUnit.MINUTES)
			.maximumSize(100)
			.build(CacheLoader.from((String query) -> {
				List<FoundName> foundNames = new ArrayList<>();
				try {
					namesCache.get("").forEach(el -> {
						String name = el.name();
						int dist = new LevenshteinDistance(name.toLowerCase(), query, SURPLUS_CHAR_DISTANCE, MISSING_CHAR_DISTANCE, CHAR_DISTANCE).getCurrentDistance();
						if (dist <= SEARCH_MAX_DISTANCE) {
							FoundName n = new FoundName();
							n.dist = dist;
							n.id = el.id();
							n.name = name;
							foundNames.add(n);
						}
					});
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
				
				Map<UUID, SearchResponseProfile> profiles = new HashMap<>();
				
				foundNames.forEach(foundName -> {
					SearchResponseProfile profile = profiles.getOrDefault(foundName.id, new SearchResponseProfile());
					if (profile.id == null) {
						profile.id = foundName.id.toString();
						profile.names = new ArrayList<>();
						profiles.put(foundName.id, profile);
					}
					profile.names.add(foundName);
				});
				
				try {
					DB.forEach(SQLPlayer.class, SQLPlayer.playerId.in(profiles.keySet()), el -> {
						SearchResponseProfile profile = profiles.get(el.get(SQLPlayer.playerId));
						if (profile == null)
							return;
						profile.displayName = el.get(SQLPlayer.playerDisplayName);
						profile.name = el.get(SQLPlayer.playerName);
						FoundName currentName = null;
						for (FoundName foundName : profile.names) {
							if (foundName.name.equals(profile.name)) {
								currentName = foundName;
								profile.d = foundName.dist;
								break;
							}
						}
						
						if (currentName != null) {
							profile.names.remove(currentName);
						}
						else {
							int min = Integer.MAX_VALUE;
							for (FoundName foundName : profile.names) {
								if (foundName.dist < min) {
									min = foundName.dist;
								}
							}
							profile.d = min * OLD_NICK_MULTIPLIER + 1;
							
							if (profile.d > SEARCH_MAX_DISTANCE)
								profiles.remove(el.get(SQLPlayer.playerId));
						}
						
						// unset id field in old names entries to save memory and network activity
						profile.names.forEach(n -> n.id = null);
					});
				} catch (DBException e) {
					throw new RuntimeException(e);
				}
				
				
				List<SearchResponseProfile> searchResponseList = new ArrayList<>(profiles.values());
				searchResponseList.sort(null);
				
				searchResponseList.removeIf(p -> {
					if (p.name == null) { // if the current name was not found in the database
						Log.warning("Cannot find current name for player " + p.id, new Throwable());
						return true;
					}
					return false;
				});
				
				return new SearchResponse(searchResponseList);
			}));
	
	
	public static class SearchResponseProfile implements Comparable<SearchResponseProfile> {
		public int d;
		public String id;
		public String name;
		public String displayName;
		public List<FoundName> names;
		
		@Override
		public int compareTo(SearchResponseProfile o) {
			return Integer.compare(d, o.d);
		}
	}
	
	private static class FoundName {
		public UUID id;
		public String name;
		public int dist;
	}
	

	
	public static class SearchResponse {
		public final List<SearchResponseProfile> profiles;
		private SearchResponse(List<SearchResponseProfile> p) {
			profiles = p;
		}
	}
	
	
	
	

}
