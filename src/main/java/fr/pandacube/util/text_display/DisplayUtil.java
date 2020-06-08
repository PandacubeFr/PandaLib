package fr.pandacube.util.text_display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class DisplayUtil {

	public static final int DEFAULT_CHAR_SIZE = 6;
	public static final Map<Integer, String> CHARS_SIZE = new ImmutableMap.Builder<Integer, String>()
			.put(-6, "§")
			.put(2, "!.,:;i|¡'")
			.put(3, "`lìí’‘")
			.put(4, " I[]tï×")
			.put(5, "\"()*<>fk{}")
			.put(7, "@~®©«»")
			.put(9, "├└")
			.build();
	
	

	public static final int DEFAULT_CHAT_WIDTH = 320;
	public static final int SIGN_WIDTH = 90;
	public static final int BOOK_WIDTH = 116;

	public static final int CONSOLE_NB_CHAR_DEFAULT = 50;

	public static final ChatColor COLOR_TITLE = ChatColor.GOLD;
	public static final ChatColor COLOR_LINK = ChatColor.GREEN;
	public static final ChatColor COLOR_COMMAND = ChatColor.GRAY;
	
	
	
	
	
	
	
	public static BaseComponent createURLLink(String text, String url, String hoverText) {
		return _createURLLink(new Display(text), url, hoverText);
	}
	public static BaseComponent createURLLink(BaseComponent text, String url, String hoverText) {
		return _createURLLink(new Display(text), url, hoverText);
	}
	public static BaseComponent createURLLink(BaseComponent[] text, String url, String hoverText) {
		return _createURLLink(new Display(text), url, hoverText);
	}
	
	private static BaseComponent _createURLLink(Display d, String url, String hoverText) {
		String dispURL = (url.length() > 50) ? (url.substring(0, 48) + "...") : url;
		return d.clickURL(url)
				.hoverText(ChatColor.GRAY + ((hoverText == null) ? "Cliquez pour accéder au site :" : hoverText) + "\n"
						+ ChatColor.GRAY + dispURL)
				.color(COLOR_LINK).get();
	}
	
	
	

	
	
	
	
	public static BaseComponent createCommandLink(String text, String commandWithSlash, String hoverText) {
		return createCommandLink(text, commandWithSlash, hoverText == null ? null : TextComponent.fromLegacyText(hoverText));
	}
	public static BaseComponent createCommandLink(String text, String commandWithSlash, BaseComponent hoverText) {
		return createCommandLink(text, commandWithSlash, hoverText == null ? null : new BaseComponent[] {hoverText});
	}
	public static BaseComponent createCommandLink(String text, String commandWithSlash, BaseComponent[] hoverText) {
		return _createCommandLink(new Display(text), commandWithSlash, hoverText);
	}
	
	private static BaseComponent _createCommandLink(Display d, String commandWithSlash, BaseComponent[] hoverText) {
		d.clickCommand(commandWithSlash).color(COLOR_COMMAND);
		if (hoverText != null) d.hoverText(hoverText);
		return d.get();
	}

	
	
	
	
	
	
	
	

	public static BaseComponent createCommandSuggest(String text, String commandWithSlash, String hoverText) {
		return createCommandSuggest(text, commandWithSlash, hoverText == null ? null : TextComponent.fromLegacyText(hoverText));
	}
	public static BaseComponent createCommandSuggest(String text, String commandWithSlash, BaseComponent hoverText) {
		return createCommandSuggest(text, commandWithSlash, hoverText == null ? null : new BaseComponent[] {hoverText});
	}
	public static BaseComponent createCommandSuggest(String text, String commandWithSlash, BaseComponent[] hoverText) {
		return _createCommandSuggest(new Display(text), commandWithSlash, hoverText);
	}
	
	private static BaseComponent _createCommandSuggest(Display d, String commandWithSlash, BaseComponent[] hoverText) {
		d.clickSuggest(commandWithSlash).color(COLOR_COMMAND);
		if (hoverText != null) d.hoverText(hoverText);
		return d.get();
	}


	
	
	
	
	
	
	/**
	 * @param cmdFormat the command with %d inside to be replaced with the page number (must start with slash)
	 */
	public static BaseComponent createPagination(String prefix, String cmdFormat, int currentPage, int nbPages, int nbPagesToDisplay) {
		Set<Integer> pagesToDisplay = new TreeSet<>();
		
		for (int i = 0; i < nbPagesToDisplay && i < nbPages && nbPages - i > 0; i++) {
			pagesToDisplay.add(i + 1);
			pagesToDisplay.add(nbPages - i);
		}
		
		for (int i = currentPage - nbPagesToDisplay + 1; i < currentPage + nbPagesToDisplay; i++) {
			if (i > 0 && i <= nbPages)
				pagesToDisplay.add(i);
		}
		
		Display d = new Display(prefix);
		boolean first = true;
		int previous = 0;
		
		for (int page : pagesToDisplay) {
			if (!first) {
				if (page == previous + 1) {
					d.next(" ");
				}
				else {
					if (cmdFormat.endsWith("%d")) {
						d.next(" ");
						d.next(createCommandSuggest("...", cmdFormat.substring(0, cmdFormat.length() - 2), "Choisir la page"));
						d.next(" ");
					}
					else
						d.next(" ... ");
				}
			}
			else
				first = false;
			
			d.next(createCommandLink(Integer.toString(page), String.format(cmdFormat, page), "Aller à la page " + page));
			if (page == currentPage) {
				d.color(ChatColor.WHITE);
			}
			
			previous = page;
		}
		
		
		return d.get();
	}
	
	
	
	
	
	
	
	
	// TODO refaire les 4 methodes ci-dessous
	
	
	
	
	public static BaseComponent centerText(BaseComponent text, char repeatedChar, ChatColor decorationColor,
			boolean console) {

		int textWidth = strWidth(text.toPlainText(), console, false);
		if (textWidth > ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH)) return text;

		String current = text.toPlainText();
		int count = 0;
		do {
			count++;
			current = repeatedChar + current + repeatedChar;
		} while (strWidth(current, console, false) <= ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH));
		count--;

		String finalLeftOrRight = "";

		for (int i = 0; i < count; i++)
			finalLeftOrRight += repeatedChar;

		Display d = new Display().next(finalLeftOrRight).color(decorationColor).next(text);

		if (repeatedChar != ' ') d.next(finalLeftOrRight).color(decorationColor);

		return d.get();

	}

	public static BaseComponent leftText(BaseComponent text, char repeatedChar, ChatColor decorationColor, int nbLeft,
			boolean console) {

		int textWidth = strWidth(text.toPlainText(), console, false);
		if (textWidth > ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH) || textWidth
				+ nbLeft * charW(repeatedChar, console, false) > ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH))
			return text;

		Display d = new Display();

		String finalLeft = "";
		if (nbLeft > 0) {
			for (int i = 0; i < nbLeft; i++)
				finalLeft += repeatedChar;
			d.next(finalLeft).color(decorationColor);
		}
		d.next(text);

		int count = 0;
		String current = finalLeft + text.toPlainText();
		do {
			count++;
			current += repeatedChar;
		} while (strWidth(current, console, false) <= ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH));
		count--;

		if (repeatedChar != ' ') {
			String finalRight = "";
			for (int i = 0; i < count; i++)
				finalRight += repeatedChar;
			d.next(finalRight).color(decorationColor);
		}

		return d.get();

	}

	public static BaseComponent rightText(BaseComponent text, char repeatedChar, ChatColor decorationColor, int nbRight,
			boolean console) {

		int textWidth = strWidth(text.toPlainText(), console, false);
		if (textWidth > ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH) || textWidth
				+ nbRight * charW(repeatedChar, console, false) > ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH))
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
		} while (strWidth(current, console, false) <= ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH));
		count--;

		String finalLeft = "";
		for (int i = 0; i < count; i++)
			finalLeft += repeatedChar;

		Display d = new Display().next(finalLeft).color(decorationColor).next(text);

		if (repeatedChar != ' ') {
			String finalRight = "";
			for (int i = 0; i < nbRight; i++)
				finalRight += repeatedChar;
			d.next(finalRight).color(decorationColor);
		}

		return d.get();

	}

	public static BaseComponent emptyLine(char repeatedChar, ChatColor decorationColor, boolean console) {
		int count = ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH) / charW(repeatedChar, console, false);
		String finalLine = "";
		for (int i = 0; i < count; i++)
			finalLine += repeatedChar;

		return new Display().next(finalLine).color(decorationColor).get();
	}

	
	
	
	
	
	
	
	
	
	
	public static int componentWidth(BaseComponent[] components, boolean console) {
		return Arrays.stream(components).mapToInt(c -> componentWidth(c, console)).sum();
	}
	
	public static int componentWidth(BaseComponent component, boolean console) {
		int count = 0;
		for (BaseComponent c : component.getExtra())
			count += componentWidth(c, console);
		if (component instanceof TextComponent) {
			count += strWidth(((TextComponent)component).getText(), console, component.isBold());
		}
		else if (component instanceof TranslatableComponent) {
			for (BaseComponent c : ((TranslatableComponent)component).getWith())
				count += componentWidth(c, console);
		}
		return count;
	}

	public static int strWidth(String str, boolean console, boolean bold) {
		int count = 0;
		for (char c : str.toCharArray())
			count += charW(c, console, bold);
		return (count < 0) ? 0 : count;
	}

	private static int charW(char c, boolean console, boolean bold) {
		if (console) return (c == '§') ? -1 : 1;
		for (int px : CHARS_SIZE.keySet())
			if (CHARS_SIZE.get(px).indexOf(c) >= 0) return px + (bold ? 1 : 0);
		return 6 + (bold ? 1 : 0);
	}
	
	
	
	
	
	
	
	
	public static List<String> wrapInLimitedPixels(String legacyText, int pixelWidth) {
		List<String> lines = new ArrayList<>();
		
		legacyText += "\n"; // workaround to force algorithm to compute last lines;
		
		String currentLine = "";
		int currentLineSize = 0;
		int index = 0;
		
		String currentWord = "";
		int currentWordSize = 0;
		boolean bold = false;
		boolean firstCharCurrentWorldBold = false;
		
		do {
			char c = legacyText.charAt(index);
			if (c == ChatColor.COLOR_CHAR && index < legacyText.length() - 1) {
				currentWord += c;
				c = legacyText.charAt(++index);
				currentWord += c;
				
				if (c == 'l' || c == 'L') // bold
					bold = true;
				if ((c >= '0' && c <= '9') // reset bold
						|| (c >= 'a' && c <= 'f')
						|| (c >= 'A' && c <= 'F')
						|| c == 'r' || c == 'R')
					bold = false;
				
			}
			else if (c == ' ') {
				if (currentLineSize + currentWordSize > pixelWidth && currentLineSize > 0) { // wrap before word
					lines.add(currentLine);
					String lastStyle = getLastColors(currentLine);
					if (currentWord.charAt(0) == ' ') {
						currentWord = currentWord.substring(1);
						currentWordSize -= charW(' ', false, firstCharCurrentWorldBold);
					}
					currentLine = (lastStyle.equals("§r") ? "" : lastStyle) + currentWord;
					currentLineSize = currentWordSize;
				}
				else {
					currentLine += currentWord;
					currentLineSize += currentWordSize;
				}
				currentWord = ""+c;
				currentWordSize = charW(c, false, bold);
				firstCharCurrentWorldBold = bold;
			}
			else if (c == '\n') {
				if (currentLineSize + currentWordSize > pixelWidth && currentLineSize > 0) { // wrap before word
					lines.add(currentLine);
					String lastStyle = getLastColors(currentLine);
					if (currentWord.charAt(0) == ' ') {
						currentWord = currentWord.substring(1);
						currentWordSize -= charW(' ', false, firstCharCurrentWorldBold);
					}
					currentLine = (lastStyle.equals("§r") ? "" : lastStyle) + currentWord;
					currentLineSize = currentWordSize;
				}
				else {
					currentLine += currentWord;
					currentLineSize += currentWordSize;
				}
				// wrap after
				lines.add(currentLine);
				String lastStyle = getLastColors(currentLine);
				
				currentLine = lastStyle.equals("§r") ? "" : lastStyle;
				currentLineSize = 0;
				currentWord = "";
				currentWordSize = 0;
				firstCharCurrentWorldBold = bold;
			}
			else {
				currentWord += c;
				currentWordSize += charW(c, false, bold);
			}
			
		} while(++index < legacyText.length());
		
		
		
		
		
		
		return lines;
	}
	
	
	
	
	
	
	
	
	

	
	public static String getLastColors(String legacyText) {
        String result = "";
        int length = legacyText.length();

        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = legacyText.charAt(index);
            if (section == ChatColor.COLOR_CHAR && index < length - 1) {
                char c = legacyText.charAt(index + 1);
                ChatColor color = getChatColorByChar(c);

                if (color != null) {
                    result = color.toString() + result;

                    // Once we find a color or reset we can stop searching
                    char col = color.toString().charAt(1);
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
	
	
	
	
	
	
	
	
	
	
	/**
	 * Generate a tree view based on the tree structure {@code node}.
	 * 
	 * Each element in the returned array represent 1 line of the tree view.
	 * Thus, the caller may send each line separately or at once depending of the quantity of data.
	 * @param node
	 * @return A array of component, each element being a single line.
	 */
	public static BaseComponent[] treeView(DisplayTreeNode node, boolean console) {
		List<TextComponent> ret = treeView_(node, console);
		return ret.toArray(new BaseComponent[ret.size()]);
	}

	private static final String TREE_MIDDLE_CONNECTED = "├";
	private static final String TREE_END_CONNECTED = "└";
	private static final String TREE_MIDDLE_OPEN = "│§0`§r";
	private static final String TREE_END_OPEN = "§0```§r";
	private static final String TREE_MIDDLE_OPEN_CONSOLE = "│";
	private static final String TREE_END_OPEN_CONSOLE = " "; // nbsp
	
	private static List<TextComponent> treeView_(DisplayTreeNode node, boolean console) {
		List<TextComponent> ret = new ArrayList<>();
		
		TextComponent curr = new TextComponent();
		curr.addExtra(node.component);
		curr.setText("");
		
		ret.add(curr);
		
		for (int i = 0; i < node.children.size(); i++) {
			List<TextComponent> childComponents = treeView_(node.children.get(i), console);
			boolean last = i == node.children.size() - 1;
			for (int j = 0; j < childComponents.size(); j++) {
				TextComponent cComp = childComponents.get(j);
				String prefix = last ? (j == 0 ? TREE_END_CONNECTED : (console ? TREE_END_OPEN_CONSOLE : TREE_END_OPEN))
						: (j == 0 ? TREE_MIDDLE_CONNECTED : (console ? TREE_MIDDLE_OPEN_CONSOLE : TREE_MIDDLE_OPEN));
				cComp.setText(prefix + cComp.getText());
				ret.add(cComp);
			}
		}
		
		
		return ret;
	}
	
	
	
	
	
	public static class DisplayTreeNode {
		public final BaseComponent component;
		public final List<DisplayTreeNode> children = new ArrayList<>();
		
		public DisplayTreeNode(BaseComponent cmp) {
			component = cmp;
		}
		
		public DisplayTreeNode addChild(DisplayTreeNode child) {
			children.add(child);
			return this;
		}
	}
}
