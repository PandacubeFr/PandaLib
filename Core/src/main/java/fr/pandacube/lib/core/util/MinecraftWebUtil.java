package fr.pandacube.lib.core.util;

public class MinecraftWebUtil {
	
	
	

	/**
		Convert a legacy Minecraft color coded String into HTML Format.
	*/
	// TODO update to support RGB colors (Bungee and Essentials notation).
	// See JavaScript implementation at https://www.pandacube.fr/assets/js/global.js
	public static String fromMinecraftColorCodeToHTML(char code_prefix, String ligne)
	{
		String color_char = "0123456789abcdefr";
		
		StringBuilder builder = new StringBuilder();
		char currentColor = 'r';
		boolean bold = false, italic = false, underlined = false, strikethrough = false;
		
		for (int i=0; i<ligne.length(); i++) {
			char c = ligne.charAt(i);
			
			if (c == code_prefix && (i<ligne.length()-1)) {
				i++;
				c = ligne.charAt(i);
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
					builder.append(code_prefix + c);
				}
				
				
			}
			else
				builder.append(c);
		}
		
		return builder.toString();
		
	}
	
	

}
