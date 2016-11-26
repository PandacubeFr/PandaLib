package fr.pandacube.java.util.chat_display;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public class DisplayUtil {

	private static Map<Integer, String> charList = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(-6, "§");
			put(2, "!.,:;i|¡");
			put(3, "'`lìí");
			put(4, " I[]tï×");
			put(5, "\"()*<>fk{}");
			put(7, "@~®");
		}
	};

	private static final int defaultChatMaxWidth = 320;
	private static int chatMaxWidth = defaultChatMaxWidth;

	private static final int defaultNbCharPerLineForConsole = 50;
	private static int nbCharPerLineForConsole = defaultNbCharPerLineForConsole;

	public static final ChatColor COLOR_TITLE = ChatColor.GOLD;
	public static final ChatColor COLOR_LINK = ChatColor.GREEN;
	public static final ChatColor COLOR_COMMAND = ChatColor.GRAY;

	public static BaseComponent createURLLink(String textLink, String url, String hoverText) {
		String dispURL = (url.length() > 50) ? (url.substring(0, 48) + "...") : url;

		return new Display().nextComponent(textLink).setClickURL(url)
				.setHoverText(new Display(
						ChatColor.GRAY + ((hoverText == null) ? "Cliquez pour accéder au site :" : hoverText) + "\n"
								+ ChatColor.GRAY + dispURL))
				.setColor(COLOR_LINK).get();
	}

	public static BaseComponent createCommandLink(String textLink, String commandWithSlash, String hoverText) {
		Display d = new Display().nextComponent(textLink).setClickCommand(commandWithSlash).setColor(COLOR_COMMAND);
		if (hoverText != null) d.setHoverText(new Display(hoverText));
		return d.get();
	}

	public static BaseComponent createCommandSuggest(String textLink, String commandWithSlash, String hoverText) {
		Display d = new Display().nextComponent(textLink).setClickSuggest(commandWithSlash).setColor(COLOR_COMMAND);
		if (hoverText != null) d.setHoverText(new Display(hoverText));
		return d.get();
	}

	public static BaseComponent centerText(BaseComponent text, char repeatedChar, ChatColor decorationColor,
			boolean console) {

		int textWidth = strWidth(text.toPlainText(), console);
		if (textWidth > ((console) ? nbCharPerLineForConsole : chatMaxWidth)) return text;

		String current = text.toPlainText();
		int count = 0;
		do {
			count++;
			current = repeatedChar + current + repeatedChar;
		} while (strWidth(current, console) <= ((console) ? nbCharPerLineForConsole : chatMaxWidth));
		count--;

		String finalLeftOrRight = "";

		for (int i = 0; i < count; i++)
			finalLeftOrRight += repeatedChar;

		Display d = new Display().nextComponent(finalLeftOrRight).setColor(decorationColor).addComponent(text);

		if (repeatedChar != ' ') d.nextComponent(finalLeftOrRight).setColor(decorationColor);

		return d.get();

	}

	public static BaseComponent leftText(BaseComponent text, char repeatedChar, ChatColor decorationColor, int nbLeft,
			boolean console) {

		int textWidth = strWidth(text.toPlainText(), console);
		if (textWidth > ((console) ? nbCharPerLineForConsole : chatMaxWidth) || textWidth
				+ nbLeft * charW(repeatedChar, console) > ((console) ? nbCharPerLineForConsole : chatMaxWidth))
			return text;

		Display d = new Display();

		String finalLeft = "";
		if (nbLeft > 0) {
			for (int i = 0; i < nbLeft; i++)
				finalLeft += repeatedChar;
			d.nextComponent(finalLeft).setColor(decorationColor);
		}
		d.addComponent(text);

		int count = 0;
		String current = finalLeft + text.toPlainText();
		do {
			count++;
			current += repeatedChar;
		} while (strWidth(current, console) <= ((console) ? nbCharPerLineForConsole : chatMaxWidth));
		count--;

		if (repeatedChar != ' ') {
			String finalRight = "";
			for (int i = 0; i < count; i++)
				finalRight += repeatedChar;
			d.nextComponent(finalRight).setColor(decorationColor);
		}

		return d.get();

	}

	public static BaseComponent rightText(BaseComponent text, char repeatedChar, ChatColor decorationColor, int nbRight,
			boolean console) {

		int textWidth = strWidth(text.toPlainText(), console);
		if (textWidth > ((console) ? nbCharPerLineForConsole : chatMaxWidth) || textWidth
				+ nbRight * charW(repeatedChar, console) > ((console) ? nbCharPerLineForConsole : chatMaxWidth))
			return text;

		String tempText = text.toPlainText();
		if (nbRight > 0) {
			tempText += decorationColor;
			for (int i = 0; i < nbRight; i++)
				tempText += repeatedChar;
		}

		int count = 0;
		String current = tempText;
		do {
			count++;
			current = repeatedChar + current;
		} while (strWidth(current, console) <= ((console) ? nbCharPerLineForConsole : chatMaxWidth));
		count--;

		String finalLeft = "";
		for (int i = 0; i < count; i++)
			finalLeft += repeatedChar;

		Display d = new Display().nextComponent(finalLeft).setColor(decorationColor).addComponent(text);

		if (repeatedChar != ' ') {
			String finalRight = "";
			for (int i = 0; i < nbRight; i++)
				finalRight += repeatedChar;
			d.nextComponent(finalRight).setColor(decorationColor);
		}

		return d.get();

	}

	public static BaseComponent emptyLine(char repeatedChar, ChatColor decorationColor, boolean console) {
		int count = ((console) ? nbCharPerLineForConsole : chatMaxWidth) / charW(repeatedChar, console);
		String finalLine = "";
		for (int i = 0; i < count; i++)
			finalLine += repeatedChar;

		return new Display().nextComponent(finalLine).setColor(decorationColor).get();
	}

	public static int strWidth(String str, boolean console) {
		int count = 0;
		for (char c : str.toCharArray())
			count += charW(c, console);
		return (count < 0) ? 0 : count;
	}

	private static int charW(char c, boolean console) {
		if (console) return (c == '§') ? -1 : 1;
		for (int px : charList.keySet())
			if (charList.get(px).indexOf(c) >= 0) return px;
		return 6;
	}

	public static void setNbCharPerLineForConsole(int nb) {
		if (nb < 0) nb = 0;
		nbCharPerLineForConsole = nb;
	}

	public static void resetNbCharPerLineForConsole() {
		nbCharPerLineForConsole = defaultNbCharPerLineForConsole;
	}

	public static void setChatMaxWidth(int px) {
		if (px < 0) px = 0;
		chatMaxWidth = px;
	}

	public static void resetChatMaxWidth() {
		chatMaxWidth = defaultChatMaxWidth;
	}

}
