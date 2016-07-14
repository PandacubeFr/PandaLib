package fr.pandacube.java.external_tools;

import java.nio.charset.Charset;
import java.util.UUID;

public class OfflineUUID {
	public static void main(String[] args) {
		for (String arg : args)
			System.out.println("" + arg + ":" + getFromNickName(arg));
		if (args.length == 0)
			throw new IllegalArgumentException("no argument given. Please give at least one argument.");
	}

	public static UUID getFromNickName(String nickname) {
		String str = "OfflinePlayer:" + nickname;
		byte[] from_str = str.getBytes(Charset.forName("UTF-8"));
		return UUID.nameUUIDFromBytes(from_str);
	}

	public static UUID[] getFromNickName(String[] nicknames) {
		if (nicknames == null) throw new NullPointerException();

		UUID[] uuids = new UUID[nicknames.length];
		for (int i = 0; i < nicknames.length; i++)
			uuids[i] = getFromNickName(nicknames[i]);
		return uuids;
	}
}
