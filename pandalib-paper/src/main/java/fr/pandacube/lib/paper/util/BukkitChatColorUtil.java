package fr.pandacube.lib.paper.util;

import org.bukkit.DyeColor;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
		return switch (dye) {
			case BLACK -> ChatColor.of("#000000");
			case RED -> ChatColor.of("#650000");
			case GREEN -> ChatColor.of("#006500");
			case BROWN -> ChatColor.of("#361B07");
			case BLUE -> ChatColor.of("#000065");
			case PURPLE -> ChatColor.of("#3F0C5F");
			case CYAN -> ChatColor.of("#006565");
			case LIGHT_GRAY -> ChatColor.of("#535353");
			case GRAY -> ChatColor.of("#323232");
			case PINK -> ChatColor.of("#652947");
			case LIME -> ChatColor.of("#4B6500");
			case YELLOW -> ChatColor.of("#656500");
			case LIGHT_BLUE -> ChatColor.of("#3C4B51");
			case MAGENTA -> ChatColor.of("#650065");
			case ORANGE -> ChatColor.of("#65280C");
			case WHITE -> ChatColor.of("#656565");
		};
	}
	

	
	public static org.bukkit.ChatColor toBukkit(ChatColor color) {
		return org.bukkit.ChatColor.valueOf(color.getName().toUpperCase());
	}
	
	public static org.bukkit.ChatColor toBukkit(TextColor color) {
		return toBukkit(NamedTextColor.nearestTo(color));
	}
	
	public static org.bukkit.ChatColor toBukkit(NamedTextColor color) {
		return org.bukkit.ChatColor.valueOf(color.toString().toUpperCase());
	}
	
	public static NamedTextColor toAdventure(org.bukkit.ChatColor color) {
		return NamedTextColor.NAMES.value(color.name().toLowerCase());
	}
	
}
