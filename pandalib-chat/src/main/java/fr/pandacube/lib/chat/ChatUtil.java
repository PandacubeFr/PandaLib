package fr.pandacube.lib.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.md_5.bungee.api.ChatColor;

import fr.pandacube.lib.chat.Chat.FormatableChat;

/**
 * Provides various methods and properties to manipulate text displayed in chat an other parts of the game.
 */
public class ChatUtil {

	/*
	 * Note : this field is for easy listing of all characters with special sizes. It will all be reported to
	 * #CHAR_SIZES on class initialization for optimization.
	 */
	private static final Map<Integer, String> SIZE_CHARS_MAPPING = Map.ofEntries(
			Map.entry(-6, "§"),
			Map.entry(2, "!.,:;i|¡'"),
			Map.entry(3, "`lìí’‘"),
			Map.entry(4, " I[]tï×"),
			Map.entry(5, "\"()*<>fk{}"),
			Map.entry(7, "@~®©«»"),
			Map.entry(9, "├└")
	);

	/**
	 * The default text pixel width for a character in the default Minecraft font.
	 * If a character has another width, it should be found in {@link #CHAR_SIZES}.
	 */
	public static final int DEFAULT_CHAR_SIZE = 6;

	/**
	 * Mapping indicating the text pixel with for specific characters in the default Minecraft font.
	 * If a character doesn’t have a mapping in this map, then its width is {@link #DEFAULT_CHAR_SIZE}.
	 */
	public static final Map<Character, Integer> CHAR_SIZES;
	static {
		Map<Character, Integer> charSizes = new HashMap<>();
		for (var e : SIZE_CHARS_MAPPING.entrySet()) {
			int size = e.getKey();
			for (char c : e.getValue().toCharArray()) {
				charSizes.put(c, size);
			}
		}
		CHAR_SIZES = Collections.unmodifiableMap(charSizes);
	}


	/**
	 * The default width of the Minecraft Java Edition chat window, in text pixels.
	 */
	public static final int DEFAULT_CHAT_WIDTH = 320;

	/**
	 * The width of a Minecraft sign, in text pixels.
	 */
	public static final int SIGN_WIDTH = 90;

	/**
	 * The width of a Minecraft book content, in text pixels.
	 */
	public static final int BOOK_WIDTH = 116;

	/**
	 * The width of a Minecraft server MOTD message, in text pixels.
	 */
	public static final int MOTD_WIDTH = 270;

	/**
	 * The width of a Minecraft Bedrock Edition form button, in text pixels.
	 */
	public static final int BEDROCK_FORM_WIDE_BUTTON = 178;

	/**
	 * The default number of character per lines for the console.
	 */
	public static final int CONSOLE_NB_CHAR_DEFAULT = 50;






	/**
	 * Create a {@link Chat} that is a cliquable URL link.
	 * It is equivalent to the HTML {@code <a>} tag pointing to another page.
	 * @param text the link text.
	 * @param url the destination url. must starts with {@code http} or {@code https}.
	 * @return a {@link Chat} that is a cliquable URL link.
	 * @deprecated it uses String for displayed text. Use {@link Chat#clickableURL(Chat, String)} instead.
	 */
	@Deprecated(forRemoval = true, since = "2022-07-27")
	public static FormatableChat createURLLink(String text, String url) {
		return Chat.clickableURL(text == null ? null : Chat.legacyText(text), url);
	}

	/**
	 * Create a {@link Chat} that is a cliquable URL link.
	 * It is equivalent to the HTML {@code <a>} tag pointing to another page.
	 * @param text the link text.
	 * @param url the destination url. must starts with {@code http} or {@code https}.
	 * @param hoverText the text displayed when hovering the link.
	 * @return a {@link Chat} that is a cliquable URL link.
	 * @deprecated it uses String for displayed text. Use {@link Chat#clickableURL(Chat, String, Chat)} instead.
	 */
	@Deprecated(forRemoval = true, since = "2022-07-27")
	public static FormatableChat createURLLink(String text, String url, String hoverText) {
		return Chat.clickableURL(text == null ? null : Chat.legacyText(text), url, hoverText == null ? null : Chat.legacyText(hoverText));
	}

	/**
	 * Create a {@link Chat} that is a cliquable command link.
	 * When the players clicks on it, they will execute the command.
	 * @param text the link text.
	 * @param commandWithSlash the command to execute when clicked.
	 * @param hoverText the text displayed when hovering the link.
	 * @return a {@link Chat} that is a cliquable command link.
	 * @deprecated it uses String for displayed text. Use {@link Chat#clickableCommand(Chat, String, Chat)} instead.
	 */
	@Deprecated(forRemoval = true, since = "2022-07-27")
	public static FormatableChat createCommandLink(String text, String commandWithSlash, String hoverText) {
		return Chat.clickableCommand(text == null ? null : Chat.legacyText(text), commandWithSlash, hoverText == null ? null : Chat.legacyText(hoverText));
	}

	/**
	 * Create a {@link Chat} that is a cliquable command link.
	 * When the players clicks on it, they will execute the command.
	 * @param text the link text.
	 * @param commandWithSlash the command to execute when clicked.
	 * @param hoverText the text displayed when hovering the link.
	 * @return a {@link Chat} that is a cliquable command link.
	 * @deprecated it uses String for displayed text. Use {@link Chat#clickableCommand(Chat, String, Chat)} instead.
	 */
	@Deprecated(forRemoval = true, since = "2022-07-27")
	public static FormatableChat createCommandLink(String text, String commandWithSlash, Chat hoverText) {
		return Chat.clickableCommand(text == null ? null : Chat.legacyText(text), commandWithSlash, hoverText);
	}

	/**
	 * Create a {@link Chat} that is a cliquable command suggestion.
	 * When the players clicks on it, they will execute the command.
	 * @param inner the link text.
	 * @param commandWithSlash the command to put in the chat box when clicked.
	 * @param hover the text displayed when hovering the link.
	 * @return a {@link Chat} that is a cliquable command suggestion.
	 * @deprecated it uses String for displayed text. Use {@link Chat#clickableSuggest(Chat, String, Chat)} instead.
	 */
	@Deprecated(forRemoval = true, since = "2022-07-27")
	public static FormatableChat createCommandSuggest(String inner, String commandWithSlash, String hover) {
		return Chat.clickableSuggest(inner == null ? null : Chat.legacyText(inner), commandWithSlash, hover == null ? null : Chat.legacyText(hover));
	}

	/**
	 * Create a {@link Chat} that is a cliquable command suggestion.
	 * When the players clicks on it, they will execute the command.
	 * @param inner the link text.
	 * @param commandWithSlash the command to put in the chat box when clicked.
	 * @param hover the text displayed when hovering the link.
	 * @return a {@link Chat} that is a cliquable command suggestion.
	 * @deprecated it uses String for displayed text. Use {@link Chat#clickableSuggest(Chat, String, Chat)} instead.
	 */
	@Deprecated(forRemoval = true, since = "2022-07-27")
	public static FormatableChat createCommandSuggest(String inner, String commandWithSlash, Chat hover) {
		return Chat.clickableSuggest(inner == null ? null : Chat.legacyText(inner), commandWithSlash, hover);
	}


	
	
	
	
	
	
	/**
	 * Create a page navigator with clickable page numbers for the chat.
	 * @param prefix the text to put before the
	 * @param cmdFormat the command with %d inside to be replaced with the page number (must start with slash)
	 * @param currentPage the current page number (it is highlighted, and the pages around are displayed, according to
	 *                    {@code nbPagesToDisplay}).
	 * @param nbPages the number of pages.
	 * @param nbPagesToDisplay the number of pages to display around the first page, the last page and the
	 *                         {@code currentPage}.
	 * @return a {@link Chat} containging the created page navigator.
	 */
	public static Chat createPagination(String prefix, String cmdFormat, int currentPage, int nbPages, int nbPagesToDisplay) {
		Set<Integer> pagesToDisplay = new TreeSet<>();
		
		for (int i = 0; i < nbPagesToDisplay && i < nbPages && nbPages - i > 0; i++) {
			pagesToDisplay.add(i + 1);
			pagesToDisplay.add(nbPages - i);
		}
		
		for (int i = currentPage - nbPagesToDisplay + 1; i < currentPage + nbPagesToDisplay; i++) {
			if (i > 0 && i <= nbPages)
				pagesToDisplay.add(i);
		}
		
		Chat d = ChatStatic.chat().thenLegacyText(prefix);
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
						d.then(Chat.clickableSuggest(Chat.text("..."), cmdFormat.substring(0, cmdFormat.length() - 2), Chat.text("Choisir la page")));
						d.thenText(" ");
					}
					else
						d.thenText(" ... ");
				}
			}
			else
				first = false;
			
			FormatableChat pDisp = Chat.clickableCommand(Chat.text(page), String.format(cmdFormat, page), Chat.text("Aller à la page " + page));
			if (page == currentPage) {
				pDisp.highlightedCommandColor();
			}
			d.then(pDisp);
			
			previous = page;
		}
		
		
		return d;
	}
	
	
	
	
	
	
	

	/**
	 * @param decorationColor support null values
	 */
	public static Chat centerText(Chat text, char repeatedChar, TextColor decorationColor, boolean console) {
		return centerText(text, repeatedChar, decorationColor, console, console ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH);
	}
	public static Chat centerText(Chat text, char repeatedChar, TextColor decorationColor, boolean console, int maxWidth) {
		return centerText(text, repeatedChar, decorationColor, false, console, maxWidth);
	}
	
	/**
	 * @param decorationColor support null values
	 */
	public static Chat centerText(Chat text, char repeatedChar, TextColor decorationColor, boolean decorationBold, boolean console, int maxWidth) {

		int textWidth = componentWidth(text.getAdv(), console);
		
		if (textWidth > maxWidth)
			return text;
		
		int repeatedCharWidth = charW(repeatedChar, console, decorationBold);
		int sideWidth = (maxWidth - textWidth) / 2;
		int sideNbChar = sideWidth / repeatedCharWidth;
		
		if (sideNbChar == 0)
			return text;
		
		String sideChars = repeatedChar(repeatedChar, sideNbChar);
		FormatableChat side = ChatStatic.text(sideChars).color(decorationColor);
		if (decorationBold)
			side.bold();

		Chat d = Chat.chat()
				.then(side)
				.then(text);
		if (repeatedChar != ' ')
			d.then(side);

		return d;

	}

	public static Chat leftText(Chat text, char repeatedChar, TextColor decorationColor, int nbLeft, boolean console) {
		return leftText(text, repeatedChar, decorationColor, nbLeft, console, console ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH);
	}

	public static Chat leftText(Chat text, char repeatedChar, TextColor decorationColor, int nbLeft, boolean console, int maxWidth) {
		
		int textWidth = componentWidth(text.getAdv(), console);
		int repeatedCharWidth = charW(repeatedChar, console, false);
		int leftWidth = nbLeft * repeatedCharWidth;
		
		if (textWidth + leftWidth > maxWidth)
			return text;

		int rightNbChar = (maxWidth - (textWidth + leftWidth)) / repeatedCharWidth;
		
		Chat d = ChatStatic.chat()
				.then(ChatStatic.text(repeatedChar(repeatedChar, nbLeft)).color(decorationColor))
				.then(text);
		if (repeatedChar != ' ') {
			d.then(ChatStatic.text(repeatedChar(repeatedChar, rightNbChar)).color(decorationColor));
		}
		return d;
		
	}

	public static Chat rightText(Chat text, char repeatedChar, TextColor decorationColor, int nbRight, boolean console) {
		return rightText(text, repeatedChar, decorationColor, nbRight, console, console ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH);
	}

	public static Chat rightText(Chat text, char repeatedChar, TextColor decorationColor, int nbRight,
			boolean console, int maxWidth) {
		
		int textWidth = componentWidth(text.getAdv(), console);
		int repeatedCharWidth = charW(repeatedChar, console, false);
		int rightWidth = nbRight * repeatedCharWidth;
		
		if (textWidth + rightWidth > maxWidth)
			return text;

		int leftNbChar = (maxWidth - (textWidth + rightWidth)) / repeatedCharWidth;
		
		Chat d = ChatStatic.chat()
				.then(ChatStatic.text(repeatedChar(repeatedChar, leftNbChar)).color(decorationColor))
				.then(text);
		if (repeatedChar != ' ') {
			d.then(ChatStatic.text(repeatedChar(repeatedChar, nbRight)).color(decorationColor));
		}
		return d;

	}

	public static Chat emptyLine(char repeatedChar, TextColor decorationColor, boolean console) {
		return emptyLine(repeatedChar, decorationColor, false, console);
	}

	public static Chat emptyLine(char repeatedChar, TextColor decorationColor, boolean decorationBold, boolean console) {
		return emptyLine(repeatedChar, decorationColor, decorationBold, console, (console) ? CONSOLE_NB_CHAR_DEFAULT : DEFAULT_CHAT_WIDTH);
	}

	public static Chat emptyLine(char repeatedChar, TextColor decorationColor, boolean decorationBold, boolean console, int maxWidth) {
		int count = maxWidth / charW(repeatedChar, console, decorationBold);
		FormatableChat line = ChatStatic.text(repeatedChar(repeatedChar, count)).color(decorationColor);
		if (decorationBold)
			line.bold();
		return line;
	}

	private static String repeatedChar(char repeatedChar, int count) {
		char[] c = new char[count];
		Arrays.fill(c, repeatedChar);
		return new String(c);
	}
	
	
	
	
	
	


	
	public static int componentWidth(Component component, boolean console) {
		return componentWidth(component, console, false);
	}
	
	public static int componentWidth(Component component, boolean console, boolean parentBold) {
		if (component == null)
			return 0;
		
		int count = 0;
		
		State currentBold = component.style().decoration(TextDecoration.BOLD);
		boolean actuallyBold = childBold(parentBold, currentBold);
		
		if (component instanceof TextComponent) {
			count += strWidth(((TextComponent)component).content(), console, actuallyBold);
		}
		else if (component instanceof TranslatableComponent) {
			for (Component c : ((TranslatableComponent)component).args())
				count += componentWidth(c, console, actuallyBold);
		}

		for (Component c : component.children())
			count += componentWidth(c, console, actuallyBold);

		return count;
	}
	
	private static boolean childBold(boolean parent, TextDecoration.State child) {
		return (parent && child != State.FALSE) || (!parent && child == State.TRUE);
	}

	public static int strWidth(String str, boolean console, boolean bold) {
		int count = 0;
		for (char c : str.toCharArray())
			count += charW(c, console, bold);
		return Math.max(count, 0);
	}

	public static int charW(char c, boolean console, boolean bold) {
		if (console)
			return (c == '§') ? -1 : 1;
		return CHAR_SIZES.getOrDefault(c, DEFAULT_CHAR_SIZE);
	}
	
	
	
	
	
	
	
	
	public static List<Chat> wrapInLimitedPixelsToChat(String legacyText, int pixelWidth) {
		return wrapInLimitedPixels(legacyText, pixelWidth).stream()
				.map(ChatStatic::legacyText)
				.collect(Collectors.toList());
	}
	
	public static List<String> wrapInLimitedPixels(String legacyText, int pixelWidth) {
		List<String> lines = new ArrayList<>();
		
		legacyText += "\n"; // workaround to force algorithm to compute last lines;
		
		String currentLine = "";
		int currentLineSize = 0;
		int index = 0;
		
		StringBuilder currentWord = new StringBuilder();
		int currentWordSize = 0;
		boolean bold = false;
		boolean firstCharCurrentWordBold = false;
		
		do {
			char c = legacyText.charAt(index);
			if (c == ChatColor.COLOR_CHAR && index < legacyText.length() - 1) {
				currentWord.append(c);
				c = legacyText.charAt(++index);
				currentWord.append(c);
				
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
						currentWord = new StringBuilder(currentWord.substring(1));
						currentWordSize -= charW(' ', false, firstCharCurrentWordBold);
					}
					currentLine = (lastStyle.equals("§r") ? "" : lastStyle) + currentWord;
					currentLineSize = currentWordSize;
				}
				else {
					currentLine += currentWord;
					currentLineSize += currentWordSize;
				}
				currentWord = new StringBuilder("" + c);
				currentWordSize = charW(c, false, bold);
				firstCharCurrentWordBold = bold;
			}
			else if (c == '\n') {
				if (currentLineSize + currentWordSize > pixelWidth && currentLineSize > 0) { // wrap before word
					lines.add(currentLine);
					String lastStyle = ChatColorUtil.getLastColors(currentLine);
					if (currentWord.charAt(0) == ' ') {
						currentWord = new StringBuilder(currentWord.substring(1));
					}
					currentLine = (lastStyle.equals("§r") ? "" : lastStyle) + currentWord;
				}
				else {
					currentLine += currentWord;
				}
				// wrap after
				lines.add(currentLine);
				String lastStyle = ChatColorUtil.getLastColors(currentLine);
				
				currentLine = lastStyle.equals("§r") ? "" : lastStyle;
				currentLineSize = 0;
				currentWord = new StringBuilder();
				currentWordSize = 0;
				firstCharCurrentWordBold = bold;
			}
			else {
				currentWord.append(c);
				currentWordSize += charW(c, false, bold);
			}
			
		} while(++index < legacyText.length());
		
		
		
		
		
		
		return lines;
	}
	
	
	
	

	public static List<Component> renderTable(List<List<Chat>> rows, String space, boolean console) {
		List<List<Component>> compRows = new ArrayList<>(rows.size());
		for (List<Chat> row : rows) {
			List<Component> compRow = new ArrayList<>(row.size());
			for (Chat c : row) {
				compRow.add(c.getAdv());
			}
			compRows.add(compRow);
		}
		return renderTableComp(compRows, space, console);
	}
	
	
	public static List<Component> renderTableComp(List<List<Component>> rows, String space, boolean console) {
		// determine columns width
		List<Integer> nbPixelPerColumn = new ArrayList<>();
		for (List<Component> row : rows) {
			for (int i = 0; i < row.size(); i++) {
				int w = componentWidth(row.get(i), console);
				if (nbPixelPerColumn.size() <= i)
					nbPixelPerColumn.add(w);
				else if (nbPixelPerColumn.get(i) < w)
					nbPixelPerColumn.set(i, w);
			}
		}
		
		// create the lines with appropriate spacing
		List<Component> spacedRows = new ArrayList<>(rows.size());
		for (List<Component> row : rows) {
			Chat spacedRow = Chat.chat();
			for (int i = 0; i < row.size() - 1; i++) {
				int w = componentWidth(row.get(i), console);
				int padding = nbPixelPerColumn.get(i) - w;
				spacedRow.then(row.get(i));
				spacedRow.then(customWidthSpace(padding, console));
				spacedRow.thenText(space);
			}
			if (!row.isEmpty())
				spacedRow.then(row.get(row.size() - 1));
			spacedRows.add(spacedRow.getAdv());
		}
		
		return spacedRows;
	}
	
	
	
	
	public static Component customWidthSpace(int width, boolean console) {
		if (console)
			return Chat.text(" ".repeat(width)).getAdv();
		return switch (width) {
			case 0, 1 -> Component.empty();
			case 2 -> Chat.text(".").black().getAdv();
			case 3 -> Chat.text("`").black().getAdv();
			case 6 -> Chat.text(". ").black().getAdv();
			case 7 -> Chat.text("` ").black().getAdv();
			case 11 -> Chat.text("`  ").black().getAdv();
			default -> {
				int nbSpace = width / 4;
				int nbBold = width % 4;
				int nbNotBold = nbSpace - nbBold;
				if (nbNotBold > 0) {
					if (nbBold > 0) {
						yield Chat.text(" ".repeat(nbNotBold)).bold(false)
								.then(Chat.text(" ".repeat(nbBold)).bold(true))
								.getAdv();
					}
					else
						yield Chat.text(" ".repeat(nbNotBold)).bold(false).getAdv();
				}
				else if (nbBold > 0) {
					yield Chat.text(" ".repeat(nbBold)).bold(true).getAdv();
				}
				throw new IllegalStateException("Should not be here (width=" + width + "; nbSpace=" + nbSpace + "; nbBold=" + nbBold + "; nbNotBold=" + nbNotBold + ")");
			}
		};
		// "." is 2 px
		// "`" is 3 px
		// " " is 4 px
		// 0  ""
		// 1  ""
		// 2  "."
		// 3  "`"
		// 4  " "
		// 5  "§l "
		// 6  ". "
		// 7  "` "
		// 8  "  "
		// 9  " §l "
		// 10 "§l  "
		// 11 "`  "
		// 12 "   "
	}
	
	
	
	
	

	private static final String PROGRESS_BAR_START = "[";
	private static final String PROGRESS_BAR_END = "]";
	private static final TextColor PROGRESS_BAR_EMPTY_COLOR = NamedTextColor.DARK_GRAY;
	private static final char PROGRESS_BAR_EMPTY_CHAR = '.';
	private static final char PROGRESS_BAR_FULL_CHAR = '|';
	
	public static Chat progressBar(double[] values, TextColor[] colors, double total, int pixelWidth, boolean console) {
		
		// 1. Compute char size for each values
		int progressPixelWidth = pixelWidth - strWidth(PROGRESS_BAR_START + PROGRESS_BAR_END, console, false);
		int charPixelWidth = charW(PROGRESS_BAR_EMPTY_CHAR, console, false);
		
		assert charPixelWidth == charW(PROGRESS_BAR_FULL_CHAR, console, false) : "PROGRESS_BAR_EMPTY_CHAR and PROGRESS_BAR_FULL_CHAR should have the same pixel width according to #charW(...)";
		
		int progressCharWidth = progressPixelWidth / charPixelWidth;
		
		int[] sizes = new int[values.length];
		double sumValuesBefore = 0;
		int sumSizesBefore = 0;

		for (int i = 0; i < values.length; i++) {
			sumValuesBefore += values[i];
			int charPosition = Math.min((int) Math.round(progressCharWidth * sumValuesBefore / total), progressCharWidth);
			sizes[i] = charPosition - sumSizesBefore;
			sumSizesBefore += sizes[i];
		}
		
		// 2. Generate rendered text
		Chat c = ChatStatic.text(PROGRESS_BAR_START);
		
		int sumSizes = 0;
		for (int i = 0; i < sizes.length; i++) {
			sumSizes += sizes[i];

			FormatableChat subC = ChatStatic.text(repeatedChar(PROGRESS_BAR_FULL_CHAR, sizes[i]));

			if (colors != null && i < colors.length && colors[i] != null)
				subC.color(colors[i]);
			
			c.then(subC);
		}
		
		return c
				.then(ChatStatic.text(repeatedChar(PROGRESS_BAR_EMPTY_CHAR, progressCharWidth - sumSizes))
						.color(PROGRESS_BAR_EMPTY_COLOR))
				.thenText(PROGRESS_BAR_END);
	}
	
	public static Chat progressBar(double value, TextColor color, double total, int pixelWidth, boolean console) {
		return progressBar(new double[] { value }, new TextColor[] { color }, total, pixelWidth, console);
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
	
	
	
	
	
	

	
	
	
	
	
	
	
	

	private static final String TREE_MIDDLE_CONNECTED = "├";
	private static final String TREE_END_CONNECTED = "└";
	private static final String TREE_MIDDLE_OPEN = "│§0`§r";
	private static final String TREE_END_OPEN = "§0```§r";
	private static final String TREE_MIDDLE_OPEN_CONSOLE = "│";
	private static final String TREE_END_OPEN_CONSOLE = " "; // nbsp
	
	/**
	 * Generate a tree view based on the tree structure {@code node}.
	 * 
	 * Each element in the returned list represent 1 line of the tree view.
	 * Thus, the caller may send each line separately or at once depending of the quantity of data.
	 * @return A array of component, each element being a single line.
	 */
	public static List<Chat> treeView(DisplayTreeNode node, boolean console) {
		List<Chat> ret = new ArrayList<>();
		
		ret.add(ChatStatic.chat()
				.then(node.component));
		
		for (int i = 0; i < node.children.size(); i++) {
			List<Chat> childComponents = treeView(node.children.get(i), console);
			boolean last = i == node.children.size() - 1;
			for (int j = 0; j < childComponents.size(); j++) {
				
				String prefix = last ? (j == 0 ? TREE_END_CONNECTED : (console ? TREE_END_OPEN_CONSOLE : TREE_END_OPEN))
						: (j == 0 ? TREE_MIDDLE_CONNECTED : (console ? TREE_MIDDLE_OPEN_CONSOLE : TREE_MIDDLE_OPEN));
				
				ret.add(ChatStatic.text(prefix)
						.then(childComponents.get(j)));
			}
		}
		
		
		return ret;
	}
	
	
	
	
	
	public static class DisplayTreeNode {
		public final Chat component;
		public final List<DisplayTreeNode> children = new ArrayList<>();
		
		public DisplayTreeNode(Chat cmp) {
			component = cmp;
		}
		
		public DisplayTreeNode addChild(DisplayTreeNode child) {
			children.add(child);
			return this;
		}
	}
}
