package fr.pandacube.lib.util;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

/**
 * Utility class and program that generate offline UUIDs for provided player names.
 * <p>
 * You can generate the UUID programmatically using {@link #getFromNickName(String)} and
 * {@link #getFromNickNames(String[])}.
 * <p>
 * To use this class as a program, type
 * <pre>
 *     java -cp&lt;anyClassPathContainingThisClass&gt; fr.pandacube.lib.util.OfflineUUID [playernames...]
 * </pre>
 * Each argument will be interpreted as a player name. If there is no argument, the program will wait for them in the
 * input stream.
 * For each player name, the program will print the player name, a {@code tab} character, the UUID and a line separator.
 */
public class OfflineUUID {

	/**
	 * Generate the offline {@link UUID} of the provided player name.
	 * @param nickname the player name to obtain the offline UUID from.
	 * @return the offline {@link UUID} of the provided player name.
	 */
	public static UUID getFromNickName(String nickname) {
		byte[] from_str = ("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8);
		return UUID.nameUUIDFromBytes(from_str);
	}

	/**
	 * Generate the offline {@link UUID}s of the provided player names.
	 * @param nicknames an array of player name to obtain the offline UUIDs from.
	 * @return the offline {@link UUID}s of the provided player name in an array, at the same order as the input.
	 */
	public static UUID[] getFromNickNames(String[] nicknames) {
		Objects.requireNonNull(nicknames);

		UUID[] uuids = new UUID[nicknames.length];
		for (int i = 0; i < nicknames.length; i++)
			uuids[i] = getFromNickName(nicknames[i]);
		return uuids;
	}


	/**
	 * Main method for this class.
	 * @param args the arguments. One argument is one player name.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			try (Scanner s = new Scanner(System.in)) {
				for(;;) {
					System.err.print("Please input a player name: ");
					if (!s.hasNextLine())
						break;
					String line = s.nextLine();
					System.out.println(line + "\t" + getFromNickName(line));
				}
			}
		}
		else {
			for (String arg : args)
				System.out.println(arg + "\t" + getFromNickName(arg));
		}
	}

	private OfflineUUID() {}
}
