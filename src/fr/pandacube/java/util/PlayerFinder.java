package fr.pandacube.java.util;

import java.util.UUID;

import net.alpenblock.bungeeperms.BungeePerms;

public class PlayerFinder {
	
	private static BungeePerms getPermPlugin() {
		try {
			return BungeePerms.getInstance();
		} catch(NoClassDefFoundError|Exception e) {
			return null;
		}
		
	}
	

	
	
	public static String getPlayerName(UUID id) {
		BungeePerms pl = getPermPlugin();
		if (pl == null) return null;
		
		return pl.getPermissionsManager().getUUIDPlayerDB().getPlayerName(id);
		
	}
	
	
	public static UUID getPlayerId(String name) {
		if (!isValidPlayerName(name)) return null; // évite une recherche inutile dans la base de donnée
		BungeePerms pl = getPermPlugin();
		if (pl == null) return null;
		
		return pl.getPermissionsManager().getUUIDPlayerDB().getUUID(name);
		
	}
	
	

	
	

	public static boolean isValidPlayerName(String name) {
		if (name == null) return false;
		return name.matches("[0-9a-zA-Z_]{2,16}");
	}
	
	
	
	
}
