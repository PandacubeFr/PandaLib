package fr.pandacube.lib.paper.util;

import org.bukkit.DyeColor;

import net.md_5.bungee.api.ChatColor;

public class BukkitChatColorUtil {


	
	
	/**
	 * Returns the {@link ChatColor} that is visually the closest from the provided {@link DyeColor} when used on a sign.
	 * 
	 * Multiple {@link DyeColor} may return the same 
	 * @param dye the provided dye color
	 * @return the closest chat color from {@code dye}
	 */
	public static ChatColor fromDyeToSignColor(DyeColor dye) {
		//org.bukkit.Color col = dye.getColor();
		//return ChatColor.of(new Color(col.asRGB()));
		// hmmm this is not that simple, of course
		
		// black 
		switch (dye) {
		case BLACK:
			return ChatColor.of("#000000");
		case RED:
			return ChatColor.of("#650000");
		case GREEN:
			return ChatColor.of("#006500");
		case BROWN:
			return ChatColor.of("#361B07");
		case BLUE:
			return ChatColor.of("#000065");
		case PURPLE:
			return ChatColor.of("#3F0C5F");
		case CYAN:
			return ChatColor.of("#006565");
		case LIGHT_GRAY:
			return ChatColor.of("#535353");
		case GRAY:
			return ChatColor.of("#323232");
		case PINK:
			return ChatColor.of("#652947");
		case LIME:
			return ChatColor.of("#4B6500");
		case YELLOW:
			return ChatColor.of("#656500");
		case LIGHT_BLUE:
			return ChatColor.of("#3C4B51");
		case MAGENTA:
			return ChatColor.of("#650065");
		case ORANGE:
			return ChatColor.of("#65280C");
		case WHITE:
			return ChatColor.of("#656565");
		}
		throw new IllegalArgumentException("Unknown DyeColor: " + dye);
	}
	
	
	
	public static org.bukkit.ChatColor fromBungeeToBukkit(ChatColor color) {
		return org.bukkit.ChatColor.valueOf(color.getName().toUpperCase());
	}
	
	
	
}
