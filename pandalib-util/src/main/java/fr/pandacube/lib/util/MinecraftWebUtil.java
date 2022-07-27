package fr.pandacube.lib.util;

/**
 * Provides utility methods around Minecraft and Web stuff.
 */
public class MinecraftWebUtil {

	/**
	 * Convert a legacy Minecraft color coded String into HTML Format.
	 * <p>
	 * Each colored part of the text will be contained in a {@code <span>} tag with {@code class="cX"} where {@code X}
	 * is the color code from 0 to F in uppercase.
	 * The bold, striked, underlined and italic parts will be contained in a {@code <span>} tag with respectively the
	 * classes {@code cL}, {@code cM}, {@code cN} and {@code cO}.
	 * Some CSS properties are needed to apply the colors to the CSS classes.
	 * @param chatcolorPrefix the prefix used for the color codes
	 * @param legacyText the legacy text to convert to HTML.
	 * @return The text formated in HTML.
	 * @implNote the current implementation does not yet supports the RGB colors.
	*/
	// TODO update to support RGB colors (Bungee and Essentials notation). (see JS implementation at https://www.pandacube.fr/assets/js/global.js )
	// TODO moves this to pandalib-chat and use Adventure API to help serializing to HTML
	public static String fromMinecraftColorCodeToHTML(char chatcolorPrefix, String legacyText)
	{
		String color_char = "0123456789abcdefr";
		
		StringBuilder builder = new StringBuilder();
		char currentColor = 'r';
		boolean bold = false, italic = false, underlined = false, strikethrough = false;
		
		for (int i=0; i<legacyText.length(); i++) {
			char c = legacyText.charAt(i);
			
			if (c == chatcolorPrefix && (i<legacyText.length()-1)) {
				i++;
				c = legacyText.charAt(i);
				if (color_char.contains(String.valueOf(Character.toLowerCase(c)))) {
					if (bold) {
						builder.append("</span>");
						bold = false;
					}
					if (italic) {
						builder.append("</span>");
						italic = false;
					}
					if (underlined) {
						builder.append("</span>");
						underlined = false;
					}
					if (strikethrough) {
						builder.append("</span>");
						strikethrough = false;
					}
					if (Character.toLowerCase(c) != currentColor) {
						if (currentColor != 'r')
							builder.append("</span>");
						if (Character.toLowerCase(c) != 'r')
							builder.append("<span class=\"c").append(Character.toUpperCase(c)).append("\">");
						currentColor = Character.toLowerCase(c);
					}
					
				}
				else if (Character.toLowerCase(c) == 'l') {
					if (!bold) {
						builder.append("<span class=\"cL\">");
						bold = true;
					}
				}
				else if (Character.toLowerCase(c) == 'm') {
					if (!strikethrough) {
						builder.append("<span class=\"cM\">");
						strikethrough = true;
					}
				}
				else if (Character.toLowerCase(c) == 'n') {
					if (!underlined) {
						builder.append("<span class=\"cN\">");
						underlined = true;
					}
				}
				else if (Character.toLowerCase(c) == 'o') {
					if (!italic) {
						builder.append("<span class=\"cO\">");
						italic = true;
					}
				}
				else {
					builder.append(chatcolorPrefix + c);
				}
				
				
			}
			else
				builder.append(c);
		}
		
		return builder.toString();
		
	}
	
	

}
