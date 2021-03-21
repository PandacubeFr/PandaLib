package fr.pandacube.lib.core.chat;

import static fr.pandacube.lib.core.chat.ChatStatic.chat;
import static fr.pandacube.lib.core.chat.ChatStatic.chatComponent;
import static fr.pandacube.lib.core.chat.ChatStatic.legacyText;
import static fr.pandacube.lib.core.chat.ChatStatic.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;

import fr.pandacube.lib.core.chat.Chat.FormatableChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class ChatUtil {

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
	
	
	
	
	
	

	public static BaseComponent createURLLink(String text, String url) {
		return createURLLink(legacyText(text), url, null);
	}
	
	public static BaseComponent createURLLink(String text, String url, String hoverText) {
		return createURLLink(legacyText(text), url, hoverText != null ? legacyText(hoverText) : null);
	}
	
	/* package */ static BaseComponent createURLLink(Chat element, String url, Chat hover) {
		String dispURL = (url.length() > 50) ? (url.substring(0, 48) + "...") : url;
		return chat()
				.clickURL(url)
				.color(Chat.getConfig().urlColor)
				.hoverText(
						hover != null ? hover : Chat.text(dispURL)
				)
				.then(element)
				.get();
	}
	
	
	

	
	
	
	
	public static BaseComponent createCommandLink(String text, String commandWithSlash, String hoverText) {
		return createCommandLink(text, commandWithSlash, hoverText == null ? null : legacyText(hoverText));
	}
	public static BaseComponent createCommandLink(String text, String commandWithSlash, Chat hoverText) {
		return createCommandLink(legacyText(text), commandWithSlash, hoverText);
	}
	
	/* package */ static BaseComponent createCommandLink(Chat d, String commandWithSlash, Chat hoverText) {
		FormatableChat c = chat()
				.clickCommand(commandWithSlash)
				.color(Chat.getConfig().commandColor);
		if (hoverText != null)
			c.hoverText(hoverText);
		return c.then(d).get();
	}

	
	
	
	
	
	
	
	

	public static BaseComponent createCommandSuggest(String text, String commandWithSlash, String hoverText) {
		return createCommandSuggest(text, commandWithSlash, hoverText == null ? null : legacyText(hoverText));
	}
	public static BaseComponent createCommandSuggest(String text, String commandWithSlash, Chat hoverText) {
		return createCommandSuggest(legacyText(text), commandWithSlash, hoverText);
	}
	
	/* package */ static BaseComponent createCommandSuggest(Chat d, String commandWithSlash, Chat hoverText) {
		FormatableChat c = chat()
				.clickSuggest(commandWithSlash)
				.color(Chat.getConfig().commandColor);
		if (hoverText != null)
			c.hoverText(hoverText);
		return c.then(d).get();
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
		
		Chat d = chat().thenLegacyText(prefix);
		boolean first = true;
		int previous = 0;
		
		for (int page : pagesToDisplay) {
			if (!first) {
				if (page == previous + 1) {
					d.thenText(" ");
				}
				else {
					if (cmdFormat.endsWith("%d")) {
						d.thenText(" ");
						d.then(createCommandSuggest("...", cmdFormat.substring(0, cmdFormat.length() - 2), "Choisir la page"));
						d.thenText(" ");
					}
					else
						d.thenText(" ... ");
				}
			}
			else
				first = false;
			
			FormatableChat pDisp = chatComponent(createCommandLink(Integer.toString(page), String.format(cmdFormat, page), "Aller à la page " + page));
			if (page == currentPage) {
				pDisp.color(Chat.getConfig().highlightedCommandColor);
			}
			d.then(pDisp);
			
			previous = page;
		}
		
		
		return d.get();
	}
	
	
	
	
	
	
	
	
	
	
	public static BaseComponent centerText(BaseComponent text, char repeatedChar, ChatColor decorationColor,
			boolean console) {

		int textWidth = componentWidth(text, console);
		int maxWidth = (console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH;
		
		if (textWidth > maxWidth)
			return text;
		
		int repeatedCharWidth = charW(repeatedChar, console, false);
		int sideWidth = (maxWidth - textWidth) / 2;
		int sideNbChar = sideWidth / repeatedCharWidth;
		
		if (sideNbChar == 0)
			return text;
		
		String sideChars = repeatedChar(repeatedChar, sideNbChar);

		Chat d = Chat.chat()
				.then(text(sideChars).color(decorationColor))
				.then(text);
		if (repeatedChar != ' ')
			d.then(text(sideChars).color(decorationColor));

		return d.get();

	}

	public static BaseComponent leftText(BaseComponent text, char repeatedChar, ChatColor decorationColor, int nbLeft,
			boolean console) {
		
		int textWidth = componentWidth(text, console);
		int maxWidth = (console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH;
		int repeatedCharWidth = charW(repeatedChar, console, false);
		int leftWidth = nbLeft * repeatedCharWidth;
		
		if (textWidth + leftWidth > maxWidth)
			return text;

		int rightNbChar = (maxWidth - (textWidth + leftWidth)) / repeatedCharWidth;
		
		Chat d = chat()
				.then(text(repeatedChar(repeatedChar, nbLeft)).color(decorationColor))
				.then(text);
		if (repeatedChar != ' ') {
			d.then(text(repeatedChar(repeatedChar, rightNbChar)).color(decorationColor));
		}
		return d.get();
		
	}

	public static BaseComponent rightText(BaseComponent text, char repeatedChar, ChatColor decorationColor, int nbRight,
			boolean console) {
		
		int textWidth = componentWidth(text, console);
		int maxWidth = (console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH;
		int repeatedCharWidth = charW(repeatedChar, console, false);
		int rightWidth = nbRight * repeatedCharWidth;
		
		if (textWidth + rightWidth > maxWidth)
			return text;

		int leftNbChar = (maxWidth - (textWidth + rightWidth)) / repeatedCharWidth;
		
		Chat d = chat()
				.then(text(repeatedChar(repeatedChar, leftNbChar)).color(decorationColor))
				.then(text);
		if (repeatedChar != ' ') {
			d.then(text(repeatedChar(repeatedChar, nbRight)).color(decorationColor));
		}
		return d.get();

	}

	public static BaseComponent emptyLine(char repeatedChar, ChatColor decorationColor, boolean console) {
		int count = ((console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH) / charW(repeatedChar, console, false);
		return text(repeatedChar(repeatedChar, count)).color(decorationColor).get();
	}

	private static String repeatedChar(char repeatedChar, int count) {
		char[] c = new char[count];
		Arrays.fill(c, repeatedChar);
		return new String(c);
	}
	
	
	
	
	
	
	
	
	
	public static int componentWidth(BaseComponent[] components, boolean console) {
		if (components == null)
			return 0;
		
		int count = 0;
		
		for (BaseComponent c : components)
			count += componentWidth(c, console);
		
		return count;
	}
	
	public static int componentWidth(BaseComponent component, boolean console) {
		if (component == null)
			return 0;
		
		int count = 0;
		
		if (component instanceof TextComponent) {
			count += strWidth(((TextComponent)component).getText(), console, component.isBold());
		}
		else if (component instanceof TranslatableComponent) {
			for (BaseComponent c : ((TranslatableComponent)component).getWith())
				count += componentWidth(c, console);
		}
		
		if (component.getExtra() != null) {
			for (BaseComponent c : component.getExtra())
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
						|| c == 'r' || c == 'R'
						|| c == 'x' || c == 'X')
					bold = false;
				
			}
			else if (c == ' ') {
				if (currentLineSize + currentWordSize > pixelWidth && currentLineSize > 0) { // wrap before word
					lines.add(currentLine);
					String lastStyle = ChatColorUtil.getLastColors(currentLine);
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
					String lastStyle = ChatColorUtil.getLastColors(currentLine);
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
				String lastStyle = ChatColorUtil.getLastColors(currentLine);
				
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
	
	
	
	
	
	
	
	

	
	
	
	
	public static String truncatePrefix(String prefix, int maxLength) {
		if (prefix.length() > maxLength) {
			String lastColor = ChatColorUtil.getLastColors(prefix);
			prefix = truncateAtLengthWithoutReset(prefix, maxLength);
			if (!ChatColorUtil.getLastColors(prefix).equals(lastColor))
				prefix = truncateAtLengthWithoutReset(prefix, maxLength - lastColor.length()) + lastColor;
		}
		return prefix;
	}
	
	
	public static String truncateAtLengthWithoutReset(String prefix, int l) {
		if (prefix.length() > l) {
			prefix = prefix.substring(0, l);
			if (prefix.endsWith("§"))
				prefix = prefix.substring(0, prefix.length()-1);
		}
		return prefix;
	}
	
	
	
	
	
	


	public static BaseComponent toUniqueBaseComponent(BaseComponent... baseComponents) {
		if (baseComponents == null || baseComponents.length == 0)
			return new TextComponent();
		if (baseComponents.length == 1)
			return baseComponents[0];
		return new TextComponent(baseComponents);
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
