package fr.pandacube.lib.paper.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * Utility class around chat coloring.
 */
public class BukkitChatColorUtil {


	
	
	/**
	 * Returns the {@link TextColor} that is visually the closest from the provided {@link DyeColor} when used on a sign.
	 * @param dye the provided dye color
	 * @return the closest chat color from {@code dye}
	 */
	public static TextColor fromDyeToSignColor(DyeColor dye) {
		// following code invalid due to text color on sign does not use rgb value of dye color.
		//org.bukkit.Color col = dye.getColor();
		//return ChatColor.of(new Color(col.asRGB()));

		// the color values are extracted from IG screenshot of dyed text signs.
		return switch (dye) {
			case BLACK -> TextColor.fromHexString("#000000");
			case RED -> TextColor.fromHexString("#650000");
			case GREEN -> TextColor.fromHexString("#006500");
			case BROWN -> TextColor.fromHexString("#361B07");
			case BLUE -> TextColor.fromHexString("#000065");
			case PURPLE -> TextColor.fromHexString("#3F0C5F");
			case CYAN -> TextColor.fromHexString("#006565");
			case LIGHT_GRAY -> TextColor.fromHexString("#535353");
			case GRAY -> TextColor.fromHexString("#323232");
			case PINK -> TextColor.fromHexString("#652947");
			case LIME -> TextColor.fromHexString("#4B6500");
			case YELLOW -> TextColor.fromHexString("#656500");
			case LIGHT_BLUE -> TextColor.fromHexString("#3C4B51");
			case MAGENTA -> TextColor.fromHexString("#650065");
			case ORANGE -> TextColor.fromHexString("#65280C");
			case WHITE -> TextColor.fromHexString("#656565");
		};
	}
	

	@SuppressWarnings("deprecation")
	public static ChatColor toBukkit(net.md_5.bungee.api.ChatColor color) {
		return ChatColor.valueOf(color.getName().toUpperCase());
	}

	@SuppressWarnings("deprecation")
	public static ChatColor toBukkit(TextColor color) {
		return toBukkit(NamedTextColor.nearestTo(color));
	}

	@SuppressWarnings("deprecation")
	public static ChatColor toBukkit(NamedTextColor color) {
		return ChatColor.valueOf(color.toString().toUpperCase());
	}

	@SuppressWarnings("deprecation")
	public static NamedTextColor toAdventure(ChatColor color) {
		return NamedTextColor.NAMES.value(color.name().toLowerCase());
	}

	public static NamedTextColor toAdventure(net.md_5.bungee.api.ChatColor color) {
		return NamedTextColor.NAMES.value(color.getName());
	}
	
}
