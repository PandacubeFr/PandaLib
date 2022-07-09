package fr.pandacube.lib.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public class OfflineUUID {

	public static UUID getFromNickName(String nickname) {
		byte[] from_str = ("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8);
		return UUID.nameUUIDFromBytes(from_str);
	}

	public static UUID[] getFromNickNames(String[] nicknames) {
		Objects.requireNonNull(nicknames);

		UUID[] uuids = new UUID[nicknames.length];
		for (int i = 0; i < nicknames.length; i++)
			uuids[i] = getFromNickName(nicknames[i]);
		return uuids;
	}
	
	
	
	
	
	public static void main(String[] args) {
		if (args.length == 0) {
			try (Scanner s = new Scanner(System.in)) {
				for(;;) {
					System.out.print("Please input a player name: ");
					if (!s.hasNext())
						break;
					String line = s.nextLine();
					System.out.println(getFromNickName(line));
				}
			}
		}
		else {
			for (String arg : args)
				System.out.println("" + arg + ":" + getFromNickName(arg));
		}
	}
}
