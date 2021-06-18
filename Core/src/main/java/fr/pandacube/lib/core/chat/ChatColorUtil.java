package fr.pandacube.lib.core.chat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class ChatColorUtil {

	
	
	

    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoPpRr";
    public static final String ALL_COLORS = "0123456789AaBbCcDdEeFf";


    private static Pattern HEX_COLOR_PATTERN = Pattern.compile("§x(?>§[0-9a-f]){6}", Pattern.CASE_INSENSITIVE);
    private static Pattern ESS_COLOR_PATTERN = Pattern.compile("§#[0-9a-f]{6}", Pattern.CASE_INSENSITIVE);
    
    /**
     * Return the legacy format needed to reproduce the format at the end of the provided legacy text.
     * Supports standard chat colors and formats, BungeeCord Chat rgb format and EssentialsX rgb format.
     * The RGB value from EssentialsX format is converted to BungeeCord Chat when included in the returned value.
     * @param legacyText
     * @return
     */
	public static String getLastColors(String legacyText) {
        String result = "";
        int length = legacyText.length();

        for (int index = length - 2; index >= 0; index--) {
            if (legacyText.charAt(index) == ChatColor.COLOR_CHAR) {
            	
            	// detection of rgb color §x§0§1§2§3§4§5
                String rgb;
            	if (index > 11
            			&& legacyText.charAt(index - 12) == ChatColor.COLOR_CHAR
            			&& (legacyText.charAt(index - 11) == 'x'
            				|| legacyText.charAt(index - 11) == 'X')
            			&& HEX_COLOR_PATTERN.matcher(rgb = legacyText.substring(index - 12, index + 2)).matches()) {
	                result = rgb + result;
	                break;
	            }
            	
            	// detection of rgb color §#012345 (and converting it to bungee chat format)
            	if (index < length - 7
            			&& legacyText.charAt(index + 1) == '#'
            			&& ESS_COLOR_PATTERN.matcher(rgb = legacyText.substring(index, index + 8)).matches()) {
            		rgb = "§x§" + rgb.charAt(2) + "§" + rgb.charAt(3)
            		+ "§" + rgb.charAt(4) + "§" + rgb.charAt(5)
            		+ "§" + rgb.charAt(6) + "§" + rgb.charAt(7);
	                result = rgb + result;
            		break;
            	}
            	
            	// try detect non-rgb format
                char colorChar = legacyText.charAt(index + 1);
                ChatColor legacyColor = getChatColorByChar(colorChar);

                if (legacyColor != null) {
                    result = legacyColor.toString() + result;

                    // Once we find a color or reset we can stop searching
                    char col = legacyColor.toString().charAt(1);
                    if ((col >= '0' && col <= '9')
    						|| (col >= 'a' && col <= 'f')
    						|| col == 'r') {
                        break;
                    }
                }
            }
        }

        return result;
    }
	
	public static ChatColor getChatColorByChar(char code) {
        return ChatColor.getByChar(Character.toLowerCase(code));
    }
	
	
	

	/**
	 * Translate the color code of the provided string, that uses the the color char, to
	 * the {@code §} color code format.
	 * <p>
	 * This method is the improved version of {@link ChatColor#translateAlternateColorCodes(char, String)},
	 * because it takes into account essentials RGB color code, and {@code altColorChar} escaping (by doubling it).
	 * Essentials RGB color code are converted to Bungee chat RGB format, so the returned string can be converted
	 * to component (see {@link Chat#legacyText(Object)}).
	 * <p>
	 * This method should be used for user input (no permission check) or string configuration, but not string
	 * from another API or containing URLs.
	 */
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate)
    {
    	char colorChar = ChatColor.COLOR_CHAR;
    	StringBuilder acc = new StringBuilder();
        char[] b = textToTranslate.toCharArray();
        for ( int i = 0; i < b.length; i++ )
        {
        	if (i < b.length - 1 // legacy chat format
        			&& b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1)
            {
        		acc.append(colorChar);
            	acc.append(lowerCase(b[i + 1]));
            	i++;
        	}
        	else if (i < b.length - 13 // bungee chat RGB format
        			&& b[i] == altColorChar
        			&& lowerCase(b[i + 1]) == 'x'
        			&& b[i + 2] == altColorChar && ALL_COLORS.indexOf(b[i + 3]) > -1
        			&& b[i + 4] == altColorChar && ALL_COLORS.indexOf(b[i + 5]) > -1
        			&& b[i + 6] == altColorChar && ALL_COLORS.indexOf(b[i + 7]) > -1
        			&& b[i + 8] == altColorChar && ALL_COLORS.indexOf(b[i + 9]) > -1
        			&& b[i + 10] == altColorChar && ALL_COLORS.indexOf(b[i + 11]) > -1
        			&& b[i + 12] == altColorChar && ALL_COLORS.indexOf(b[i + 13]) > -1) {
        		acc.append(colorChar).append(lowerCase(b[i + 1]));
        		acc.append(colorChar).append(lowerCase(b[i + 3]));
        		acc.append(colorChar).append(lowerCase(b[i + 5]));
        		acc.append(colorChar).append(lowerCase(b[i + 7]));
        		acc.append(colorChar).append(lowerCase(b[i + 9]));
        		acc.append(colorChar).append(lowerCase(b[i + 11]));
        		acc.append(colorChar).append(lowerCase(b[i + 13]));
        		i+=13;
        	}
        	else if (i < b.length - 7 // Essentials chat RGB format
        			&& b[i] == altColorChar
        			&& b[i + 1] == '#'
        			&& ALL_COLORS.indexOf(b[i + 2]) > -1 && ALL_COLORS.indexOf(b[i + 3]) > -1
        			&& ALL_COLORS.indexOf(b[i + 4]) > -1 && ALL_COLORS.indexOf(b[i + 5]) > -1
        			&& ALL_COLORS.indexOf(b[i + 6]) > -1 && ALL_COLORS.indexOf(b[i + 7]) > -1) {
        		acc.append(colorChar).append('x');
        		acc.append(colorChar).append(lowerCase(b[i + 2]));
        		acc.append(colorChar).append(lowerCase(b[i + 3]));
        		acc.append(colorChar).append(lowerCase(b[i + 4]));
        		acc.append(colorChar).append(lowerCase(b[i + 5]));
        		acc.append(colorChar).append(lowerCase(b[i + 6]));
        		acc.append(colorChar).append(lowerCase(b[i + 7]));
        		i+=7;
        	}
        	else if (i < b.length - 1 && b[i] == altColorChar && b[i + 1] == altColorChar) {
        		acc.append(altColorChar);
            	i++;
            }
            else {
            	acc.append(b[i]);
            }
        }
        return acc.toString();
    }
    
    private static char lowerCase(char c) { return Character.toLowerCase(c); }
	
	
	

	/**
	 * Force a text to be italic, while keeping other formatting and colors.
	 * The text is prefixed with the ITALIC tag, but is not reset at the end.
	 * @param legacyText the original text
	 * @return the text fully italic
	 */
	public static String forceItalic(String legacyText) {
		return forceFormat(legacyText, ChatColor.ITALIC);
	}
	
	/**
	 * Force a text to be bold, while keeping other formatting and colors.
	 * The text is prefixed with the BOLD tag, but is not reset at the end.
	 * @param legacyText the original text
	 * @return the text fully bold
	 */
	public static String forceBold(String legacyText) {
		return forceFormat(legacyText, ChatColor.BOLD);
	}
	
	/**
	 * Force a text to be underlined, while keeping other formatting and colors.
	 * The text is prefixed with the UNDERLINE tag, but is not reset at the end.
	 * @param legacyText the original text
	 * @return the text fully underlined
	 */
	public static String forceUnderline(String legacyText) {
		return forceFormat(legacyText, ChatColor.UNDERLINE);
	}
	
	/**
	 * Force a text to be stroked through, while keeping other formatting and colors.
	 * The text is prefixed with the STRIKETHROUGH tag, but is not reset at the end.
	 * @param legacyText the original text
	 * @return the text fully stroked through
	 */
	public static String forceStrikethrough(String legacyText) {
		return forceFormat(legacyText, ChatColor.STRIKETHROUGH);
	}
	
	/**
	 * Force a text to be obfuscated, while keeping other formatting and colors.
	 * The text is prefixed with the MAGIC tag, but is not reset at the end.
	 * @param legacyText the original text
	 * @return the text fully obfuscated
	 */
	public static String forceObfuscated(String legacyText) {
		return forceFormat(legacyText, ChatColor.MAGIC);
	}
	
	
	
	private static String forceFormat(String legacyText, ChatColor format) {
		return format + legacyText
				.replace(format.toString(), "") // remove previous tag to make the result cleaner
				.replaceAll("§([a-frA-FR0-9])", "§$1" + format);
	}
	
	
	
	
	
	/**
	 * Replace the RESET tag of the input string to the specified color tag.
	 * @param legacyText the original text
	 * @param color the color to used to replace the RESET tag
	 * 			(can be a combination of a color tag followed by multiple format tag)
	 * @return the resulting text
	 */
	public static String resetToColor(String legacyText, String color) {
		return legacyText.replace(ChatColor.RESET.toString(), color);
	}
	
	
	
	
	
	
	public static ChatColor interpolateColor(float v0, float v1, float v, ChatColor cc0, ChatColor cc1) {
		Color c0 = cc0.getColor(), c1 = cc1.getColor();
		int r0 = c0.getRed(), g0 = c0.getGreen(), b0 = c0.getBlue(),
			r1 = c1.getRed(), g1 = c1.getGreen(), b1 = c1.getBlue();
		float normV = (v - v0) / (v1 - v0);
		return ChatColor.of(new Color(
				(int) (r0 + (r1 - r0) * normV),
				(int) (g0 + (g1 - g0) * normV),
				(int) (b0 + (b1 - b0) * normV)));
	}
	
	
	
	
	
	
	
	public static class ChatValueGradient {
		private record GradientValueColor(float value, ChatColor color) { }
		
		List<GradientValueColor> colors = new ArrayList<>();
		
		public synchronized ChatValueGradient add(float v, ChatColor col) {
			colors.add(new GradientValueColor(v, col));
			return this;
		}
		
		public synchronized ChatColor pickColorAt(float v) {
			if (colors.isEmpty())
				throw new IllegalStateException("Must define at least one color in this ChatValueGradient instance.");
			if (colors.size() == 1)
				return colors.get(0).color();
			
			colors.sort((p1, p2) -> Float.compare(p1.value(), p2.value()));
			
			if (v <= colors.get(0).value())
				return colors.get(0).color();
			if (v >= colors.get(colors.size() - 1).value())
				return colors.get(colors.size() - 1).color();
			
			int p1 = 1;
			for (; p1 < colors.size(); p1++) {
				if (colors.get(p1).value() >= v)
					break;
			}
			int p0 = p1 - 1;
			float v0 = colors.get(p0).value(), v1 = colors.get(p1).value();
			ChatColor cc0 = colors.get(p0).color(), cc1 = colors.get(p1).color();
			
			return interpolateColor(v0, v1, v, cc0, cc1);
		}
	}
	
}