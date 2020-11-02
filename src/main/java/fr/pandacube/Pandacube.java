package fr.pandacube;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.TimeZone;

import fr.pandacube.util.text_display.Chat;
import net.md_5.bungee.api.ChatColor;

public class Pandacube {
	
	public static final Locale LOCALE = Locale.FRANCE;
	
	public static final TimeZone TIMEZONE = TimeZone.getTimeZone("Europe/Paris");

	public static final Charset NETWORK_CHARSET = Charset.forName("UTF-8");

	public static final int NETWORK_TCP_BUFFER_SIZE = 1024 * 1024;

	public static final int NETWORK_TIMEOUT = 0; // no timeout (milli-seconds)
	
	
	
	

	//public static final ChatColor CHAT_GREEN_1_NORMAL = ChatColor.of("#5f9765"); // h=126 s=23 l=48
	
	public static final ChatColor CHAT_GREEN_1_NORMAL = ChatColor.of("#3db849"); // h=126 s=50 l=48
	public static final ChatColor CHAT_GREEN_2 = ChatColor.of("#5ec969"); // h=126 s=50 l=58
	public static final ChatColor CHAT_GREEN_3 = ChatColor.of("#85d68d"); // h=126 s=50 l=68
	public static final ChatColor CHAT_GREEN_4 = ChatColor.of("#abe3b0"); // h=126 s=50 l=78

	public static final ChatColor CHAT_GREEN_SATMAX = ChatColor.of("#00ff19"); // h=126 s=100 l=50
	public static final ChatColor CHAT_GREEN_1_SAT = ChatColor.of("#20d532"); // h=126 s=50 l=48
	public static final ChatColor CHAT_GREEN_2_SAT = ChatColor.of("#45e354"); // h=126 s=50 l=58
	public static final ChatColor CHAT_GREEN_3_SAT = ChatColor.of("#71ea7d"); // h=126 s=50 l=68
	public static final ChatColor CHAT_GREEN_4_SAT = ChatColor.of("#9df0a6"); // h=126 s=50 l=78

	public static final ChatColor CHAT_BROWN_1 = ChatColor.of("#b26d3a"); // h=26 s=51 l=46
	public static final ChatColor CHAT_BROWN_2 = ChatColor.of("#cd9265"); // h=26 s=51 l=60
	public static final ChatColor CHAT_BROWN_3 = ChatColor.of("#e0bb9f"); // h=26 s=51 l=75

	public static final ChatColor CHAT_BROWN_1_SAT = ChatColor.of("#b35c19"); // h=26 s=75 l=40
	public static final ChatColor CHAT_BROWN_2_SAT = ChatColor.of("#e28136"); // h=26 s=51 l=55
	public static final ChatColor CHAT_BROWN_3_SAT = ChatColor.of("#ecab79"); // h=26 s=51 l=70
	
	public static final ChatColor CHAT_GRAY_MID = ChatColor.of("#888888");
	
	public static final ChatColor CHAT_BROADCAST_COLOR = ChatColor.YELLOW;
	
	
	public static final ChatColor CHAT_DECORATION_COLOR = CHAT_GREEN_1_NORMAL;
	public static final char CHAT_DECORATION_CHAR = '-';
	public static final ChatColor CHAT_URL_COLOR = CHAT_GREEN_1_NORMAL; 
	public static final ChatColor CHAT_COMMAND_COLOR = CHAT_GRAY_MID;
	public static final ChatColor CHAT_COMMAND_HIGHLIGHTED_COLOR = ChatColor.WHITE;
	public static final ChatColor CHAT_INFO_COLOR = CHAT_GREEN_4;
	public static final ChatColor CHAT_SUCCESS_COLOR = CHAT_GREEN_SATMAX;
	public static final ChatColor CHAT_FAILURE_COLOR = ChatColor.of("#ff3333");
	public static final ChatColor CHAT_DATA_COLOR = CHAT_GRAY_MID;


	public static final ChatColor CHAT_PM_PREFIX_DECORATION = Pandacube.CHAT_BROWN_2_SAT;
	public static final ChatColor CHAT_PM_SELF_MESSAGE = Pandacube.CHAT_GREEN_2;
	public static final ChatColor CHAT_PM_OTHER_MESSAGE = Pandacube.CHAT_GREEN_4;
	
	
	public static final Chat CHAT_MESSAGE_PREFIX() {
		return Chat.text("[")
			.color(CHAT_BROADCAST_COLOR)
			.thenDecoration("Pandacube")
			.thenText("] ");
	}
			
	
	
	
	/**
	 * Number of decoration character to put between the text and the border of
	 * the line for left and right aligned text.
	 */
	public static final int CHAT_NB_CHAR_MARGIN = 1;
	
	
	
	
	static {
		// initialize class to avoid NCDFE when updating the plugin
		@SuppressWarnings({ "deprecation", "unused" })
		Class<?>
		c1 = fr.pandacube.util.network_api.server.RequestAnalyser.class,
		c2 = fr.pandacube.util.network_api.server.RequestAnalyser.BadRequestException.class,
		c3 = fr.pandacube.util.network_api.server.Response.class,
		c4 = fr.pandacube.util.text_display.ChatUtil.class;
	}

}
