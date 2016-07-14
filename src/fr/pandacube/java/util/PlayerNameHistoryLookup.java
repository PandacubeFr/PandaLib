package fr.pandacube.java.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * This class performs a name lookup for a player and gets back all the name
 * changes of the player (if any).
 * <br/>
 * <a href="https://bukkit.org/threads/player-name-history-lookup.412679/">https
 * ://bukkit.org/threads/player-name-history-lookup.412679/</a>
 *
 * @since 25-3-2016
 * @author mine-care (AKA fillpant)
 *
 */
public class PlayerNameHistoryLookup {

	/**
	 * The URL from Mojang API that provides the JSON String in response.
	 */
	private static final String LOOKUP_URL = "https://api.mojang.com/user/profiles/%s/names";

	private static final Gson JSON_PARSER = new Gson();

	/**
	 * <h1>NOTE: Avoid running this method <i>Synchronously</i> with the main
	 * thread!It blocks while attempting to get a response from Mojang servers!
	 * </h1>
	 *
	 * @param player The UUID of the player to be looked up.
	 * @return Returns an array of {@link PreviousPlayerNameEntry} objects, or
	 *         null if the response couldn't be interpreted.
	 * @throws IOException {@link #getPlayerPreviousNames(String)}
	 */
	public static PreviousPlayerNameEntry[] getPlayerPreviousNames(UUID player) throws IOException {
		return getPlayerPreviousNames(player.toString());
	}

	/**
	 * <h1>NOTE: Avoid running this method <i>Synchronously</i> with the main
	 * thread! It blocks while attempting to get a response from Mojang servers!
	 * </h1>
	 * Alternative method accepting an {@link OfflinePlayer} (and therefore
	 * {@link Player}) objects as parameter.
	 *
	 * @param uuid The UUID String to lookup
	 * @return Returns an array of {@link PreviousPlayerNameEntry} objects, or
	 *         null if the response couldn't be interpreted.
	 * @throws IOException {@link #getRawJsonResponse(String)}
	 */
	public static PreviousPlayerNameEntry[] getPlayerPreviousNames(String uuid) throws IOException {
		if (uuid == null || uuid.isEmpty()) return null;
		uuid = uuid.replace("-", "");
		String response = getRawJsonResponse(new URL(String.format(LOOKUP_URL, uuid)));
		PreviousPlayerNameEntry[] names = JSON_PARSER.fromJson(response, PreviousPlayerNameEntry[].class);
		return names;
	}

	/**
	 * This is a helper method used to read the response of Mojang's API
	 * webservers.
	 *
	 * @param u the URL to connect to
	 * @return a String with the data read.
	 * @throws IOException Inherited by {@link BufferedReader#readLine()},
	 *         {@link BufferedReader#close()}, {@link URL},
	 *         {@link HttpURLConnection#getInputStream()}
	 */
	private static String getRawJsonResponse(URL u) throws IOException {
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setDoInput(true);
		con.setConnectTimeout(2000);
		con.setReadTimeout(2000);
		con.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String response = in.readLine();
		in.close();
		return response;
	}

	/**
	 * This class represents the typical response expected by Mojang servers
	 * when requesting the name history of a player.
	 */
	public class PreviousPlayerNameEntry {
		private String name;
		@SerializedName("changedToAt")
		private long changeTime;

		/**
		 * Gets the player name of this entry.
		 *
		 * @return The name of the player.
		 */
		public String getPlayerName() {
			return name;
		}

		/**
		 * Get the time of change of the name.
		 * <br>
		 * <b>Note: This will return 0 if the name is the original (initial)
		 * name of the player! Make sure you check if it is 0 before handling!
		 * <br>
		 * Parsing 0 to a Date will result in the date "01/01/1970".</b>
		 *
		 * @return a timestamp in miliseconds that you can turn into a date or
		 *         handle however you want :)
		 */
		public long getChangeTime() {
			return changeTime;
		}

		/**
		 * Check if this name is the name used to register the account (the
		 * initial/original name)
		 *
		 * @return a boolean, true if it is the the very first name of the
		 *         player, otherwise false.
		 */
		public boolean isPlayersInitialName() {
			return getChangeTime() == 0;
		}

		@Override
		public String toString() {
			return "Name: " + name + " Date of change: " + new Date(changeTime).toString();
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println(Arrays.toString(getPlayerPreviousNames("a18d9b2c-e18f-4933-9e15-36452bc36857")));
	}

}
