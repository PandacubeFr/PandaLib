package fr.pandacube.lib.paper.util;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

/**
 * Color related utility class.
 */
public class ColorUtil {
	
	/*
	 * Rainbow
	 */
	private static final List<Color> rainbowColors = new ArrayList<>();
	
	/**
	 * Gets a rainbow color.
	 * @param variation from 0 (include) to 1 (exclude).
	 * 0 is red, 1/6 is yellow, 2/6 is green, 3/6 is cyan, 4/6 is blue, 5/6 is magenta
	 * @return the computed rainbow color.
	 */
	public static Color getRainbowColor(double variation) {
		synchronized (rainbowColors) {
			if (rainbowColors.isEmpty()) {
				for (int g=0; g<255; g++) rainbowColors.add(Color.fromRGB(255, g, 0));
				for (int r=255; r>0; r--) rainbowColors.add(Color.fromRGB(r, 255, 0));
				for (int b=0; b<255; b++) rainbowColors.add(Color.fromRGB(0, 255, b));
				for (int g=255; g>0; g--) rainbowColors.add(Color.fromRGB(0, g, 255));
				for (int r=0; r<255; r++) rainbowColors.add(Color.fromRGB(r, 0, 255));
				for (int b=255; b>0; b--) rainbowColors.add(Color.fromRGB(255, 0, b));
			}
		}
		
		while (variation >= 1) variation -= 1d;
		while (variation < 0)  variation += 1d;
		
		return rainbowColors.get((int)(variation * rainbowColors.size()));
	}



	/**
	 * Returns the {@link TextColor} that is visually the closest from the provided {@link DyeColor} when used on a sign.
	 * @param dye the provided dye color
	 * @return the closest chat color from {@code dye}
	 */
	public static TextColor getTextColorOfDyedSign(DyeColor dye) {
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


	
	private ColorUtil() {}

}
