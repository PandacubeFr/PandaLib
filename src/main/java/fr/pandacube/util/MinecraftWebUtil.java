package fr.pandacube.util;

public class MinecraftWebUtil {
	
	
	

	/**
		Convert a legacy Minecraft color coded String into HTML Format.
		
		@param 
	*/
	public static String fromMinecraftColorCodeToHTML(char code_prefix, String ligne)
	{
		String color_char = "0123456789abcdefr";
		
		String builder = "";
		char currentColor = 'r';
		boolean bold = false, italic = false, underlined = false, strikethrough = false;
		
		for (int i=0; i<ligne.length(); i++) {
			char c = ligne.charAt(i);
			
			if (c == code_prefix && (i<ligne.length()-1)) {
				i++;
				c = ligne.charAt(i);
				if (color_char.contains(new Character(c).toString().toLowerCase())) {
					if (bold) {
						builder += "</span>";
						bold = false;
					}
					if (italic) {
						builder += "</span>";
						italic = false;
					}
					if (underlined) {
						builder += "</span>";
						underlined = false;
					}
					if (strikethrough) {
						builder += "</span>";
						strikethrough = false;
					}
					if (Character.toLowerCase(c) != currentColor) {
						if (currentColor != 'r')
							builder += "</span>";
						if (Character.toLowerCase(c) != 'r')
							builder += "<span class=\"c" + Character.toUpperCase(c) + "\">";
						currentColor = Character.toLowerCase(c);
					}
					
				}
				else if (Character.toLowerCase(c) == 'l') {
					if (!bold) {
						builder += "<span class=\"cL\">";
						bold = true;
					}
				}
				else if (Character.toLowerCase(c) == 'm') {
					if (!strikethrough) {
						builder += "<span class=\"cM\">";
						strikethrough = true;
					}
				}
				else if (Character.toLowerCase(c) == 'n') {
					if (!underlined) {
						builder += "<span class=\"cN\">";
						underlined = true;
					}
				}
				else if (Character.toLowerCase(c) == 'o') {
					if (!italic) {
						builder += "<span class=\"cO\">";
						italic = true;
					}
				}
				else {
					builder += code_prefix + c;
				}
				
				
			}
			else
				builder += c;
		}
		
		return builder;
		
	}
	
	

}
