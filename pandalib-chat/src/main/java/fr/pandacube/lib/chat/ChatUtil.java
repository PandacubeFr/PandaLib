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
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.md_5.bungee.api.ChatColor;

import fr.pandacube.lib.chat.Chat.FormatableChat;

import static fr.pandacube.lib.chat.ChatStatic.chat;

/**
 * Provides various methods and properties to manipulate text displayed in chat and other parts of the game.
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
     * If a character doesn't have a mapping in this map, then its width is {@link #DEFAULT_CHAR_SIZE}.
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
     * Create a page navigator with clickable page numbers for the chat.
     * @param prefix the text to put before the
     * @param cmdFormat the command with %d inside to be replaced with the page number (must start with slash)
     * @param currentPage the current page number (it is highlighted, and the pages around are displayed, according to
     *                    {@code nbPagesToDisplay}).
     * @param nbPages the number of pages.
     * @param nbPagesToDisplay the number of pages to display around the first page, the last page and the
     *                         {@code currentPage}.
     * @return a {@link Chat} containing the created page navigator.
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
                        d.thenCommandSuggest(Chat.text("..."), cmdFormat.substring(0, cmdFormat.length() - 2), Chat.text("Choisir la page"));
                        d.thenText(" ");
                    }
                    else
                        d.thenText(" ... ");
                }
            }
            else
                first = false;

            FormatableChat pDisplay = Chat.clickableCommand(Chat.text(page), String.format(cmdFormat, page), Chat.text("Aller à la page " + page));
            if (page == currentPage) {
                pDisplay.highlightedCommandColor();
            }
            d.then(pDisplay);

            previous = page;
        }


        return d;
    }






    /**
     * Do like {@link String#join(CharSequence, Iterable)}, but for components, and the last separator is different from
     * the others. It is useful when enumerating things in a sentence, for instance :
     * <code>"a thing<u>, </u>a thing<u> and </u>a thing"</code>
     * (the coma being the usual separator, and {@code " and "} being the final separator).
     * @param regularSeparator the separator used everywhere except between the two last components to join.
     * @param finalSeparator the separator used between the two last components to join.
     * @param elements the components to join.
     * @return a new {@link Chat} instance with all the provided {@code component} joined using the separators.
     */
    public static FormatableChat joinGrammatically(ComponentLike regularSeparator, ComponentLike finalSeparator, List<? extends ComponentLike> elements) {
        int size = elements == null ? 0 : elements.size();
        int last = size - 1;
        return switch (size) {
            case 0, 1, 2 -> join(finalSeparator, elements);
            default -> (FormatableChat) join(regularSeparator, elements.subList(0, last))
                    .then(finalSeparator)
                    .then(elements.get(last));
        };
    }






    /**
     * Do like {@link String#join(CharSequence, Iterable)}, but for components.
     * @param separator the separator used everywhere except between the two last components to join.
     * @param elements the components to join.
     * @return a new {@link Chat} instance with all the provided {@code component} joined using the separators.
     */
    public static FormatableChat join(ComponentLike separator, Iterable<? extends ComponentLike> elements) {
        FormatableChat c = chat();
        if (elements == null)
            return c;
        boolean first = true;
        for (ComponentLike el : elements) {
            if (!first) {
                c.then(separator);
            }
            c.then(el);
            first = false;
        }
        return c;
    }










    /* package */ static String repeatedChar(char repeatedChar, int count) {
        char[] c = new char[count];
        Arrays.fill(c, repeatedChar);
        return new String(c);
    }


    /**
     * Compute the width of the provided component.
     * @param component the component to compute the width.
     * @param console true to compute the width when displayed on console (so it will count the characters),
     *                false to compute the width when displayed in game (so it will count the pixels).
     * @return the width of the provided component.
     */
    public static int componentWidth(Component component, boolean console) {
        return componentWidth(component, console, false);
    }

    /**
     * Compute the width of the provided component, with extra information about the parent component.
     * @param component the component to compute the width.
     * @param console true to compute the width when displayed on console (so it will count the characters),
     *                false to compute the width when displayed in game (so it will count the pixels).
     * @param parentBold if the component inherits a bold styling from an eventual parent component.
     * @return the width of the provided component.
     */
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

    /**
     * Compute the width of the provided text.
     * @param str the text to compute the width.
     * @param console true to compute the width when displayed on console (so it will count the characters),
     *                false to compute the width when displayed in game (so it will count the pixels).
     * @param bold if the text is bold (may change its width).
     * @return the width of the provided text.
     */
    public static int strWidth(String str, boolean console, boolean bold) {
        int count = 0;
        for (char c : str.toCharArray())
            count += charW(c, console, bold);
        return Math.max(count, 0);
    }

    /**
     * Compute the width of the provided character.
     * <p>
     * It uses the mapping in {@link #CHAR_SIZES} for in-game display. For console, every character is size 1.
     * The {@code §} character is treated has a negative value, to make legacy codes take 0 width.
     * @param c the character to compute the width.
     * @param console true to compute the width when displayed on console (so it will count the characters),
     *                false to compute the width when displayed in game (so it will count the pixels).
     * @param bold if the character is bold (may change its width).
     * @return the width of the provided character.
     */
    public static int charW(char c, boolean console, boolean bold) {
        if (console)
            return (c == '§') ? -1 : 1;
        return CHAR_SIZES.getOrDefault(c, DEFAULT_CHAR_SIZE) + (bold ? 1 : 0);
    }


    /**
     * Wraps the provided text in multiple lines, taking into account the legacy formatting.
     * <p>
     * This method only takes into account IG text width. Use a regular text-wrapper for console instead.
     * @param legacyText the text to wrap.
     * @param pixelWidth the width in which the text must fit.
     * @return the wrapped text in a {@link List} of {@link Chat} components.
     */
    public static List<Chat> wrapInLimitedPixelsToChat(String legacyText, int pixelWidth) {
        return wrapInLimitedPixels(legacyText, pixelWidth).stream()
                .map(ChatStatic::legacyText)
                .collect(Collectors.toList());
    }

    /**
     * Wraps the provided text in multiple lines, taking into account the legacy formatting.
     * <p>
     * This method only takes into account IG text width. Use a regular text-wrapper for console instead.
     * @param legacyText the text to wrap.
     * @param pixelWidth the width in which the text must fit.
     * @return the wrapped text in a {@link List} of line.
     */
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


    /**
     * Try to render a matrix of {@link Chat} components into a table in the chat or console.
     * @param data the component, in the form of {@link List} of {@link List} of {@link Chat}. The parent list holds
     *             the table lines (line 0 being the top line). Each sublist holds the cells content (element 0 is the
     *             leftText one). The row lengths can be different.
     * @param space a spacer to put between columns.
     * @param console true to display the table on the console (character alignement), false in game chat (pixel
     *                alignment, much harder).
     * @return a List containing each rendered line of the table.
     */
    public static List<Component> renderTable(List<List<Chat>> data, String space, boolean console) {
        List<List<Component>> compRows = new ArrayList<>(data.size());
        for (List<Chat> row : data) {
            List<Component> compRow = new ArrayList<>(row.size());
            for (Chat c : row) {
                compRow.add(c.getAdv());
            }
            compRows.add(compRow);
        }
        return renderTableComp(compRows, space, console);
    }


    /**
     * Try to render a matrix of {@link Component} components into a table in the chat or console.
     * @param data the component, in the form of {@link List} of {@link List} of {@link Component}. The parent list holds
     *             the table lines (line 0 being the top line). Each sublist holds the cells content (element 0 is the
     *             leftText one). The row lengths can be different.
     * @param space a spacer to put between columns.
     * @param console true to display the table on the console (character alignement), false in game chat (pixel
     *                alignment, much harder).
     * @return a List containing each rendered line of the table.
     */
    public static List<Component> renderTableComp(List<List<Component>> data, String space, boolean console) {
        // determine columns width
        List<Integer> nbPixelPerColumn = new ArrayList<>();
        for (List<Component> row : data) {
            for (int i = 0; i < row.size(); i++) {
                int w = componentWidth(row.get(i), console);
                if (nbPixelPerColumn.size() <= i)
                    nbPixelPerColumn.add(w);
                else if (nbPixelPerColumn.get(i) < w)
                    nbPixelPerColumn.set(i, w);
            }
        }

        // create the lines with appropriate spacing
        List<Component> spacedRows = new ArrayList<>(data.size());
        for (List<Component> row : data) {
            Chat spacedRow = chat();
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


    /**
     * Provides a component acting as a spacer of a specific width.
     * <p>
     * The returned component contains mostly spaces. If it has visible characters, the component color will be set to
     * black to be the least visible as possible.
     * <p>
     * For console, the method returns a {@link Component} with a regular space repeated {@code width} times.
     * For IG, the methods returns a {@link Component} with a combination of spaces and some small characters, with part
     * of them bold. For some specific width, the returned {@link Component} may not have the intended width.
     * @param width the width of the space to produce.
     * @param console true if the spacer is intended to be displayed on the console, false if it’s in game chat.
     * @return a component acting as a spacer of a specific width.
     */
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

    /**
     * Generate a (eventually multipart) progress bar using text.
     * @param values the values to render in the progress bar.
     * @param colors the colors attributed to each value.
     * @param total the total value of the progress bar.
     * @param width the width in which the progress bar should fit (in pixel for IG, in character count for console)
     * @param console true if the progress bar is intended to be displayed on the console, false if it’s in game chat.
     * @return a progress bar using text.
     */
    public static Chat progressBar(double[] values, TextColor[] colors, double total, int width, boolean console) {

        // 1. Compute char size for each values
        int progressPixelWidth = width - strWidth(PROGRESS_BAR_START + PROGRESS_BAR_END, console, false);
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

    /**
     * Generate a progress bar using text.
     * @param value the value to render in the progress bar.
     * @param color the color of the filled part of the bar.
     * @param total the total value of the progress bar.
     * @param width the width in which the progress bar should fit (in pixel for IG, in character count for console)
     * @param console true if the progress bar is intended to be displayed on the console, false if it’s in game chat.
     * @return a progress bar using text.
     */
    public static Chat progressBar(double value, TextColor color, double total, int width, boolean console) {
        return progressBar(new double[] { value }, new TextColor[] { color }, total, width, console);
    }


    /**
     * Truncate an eventually too long prefix (like team prefix or permission group prefix), keep the last color and
     * format.
     * @param prefix the prefix that eventually needs truncation.
     * @param maxLength the maximum length of the prefix.
     * @return a truncated prefix, with the last color kept.
     */
    public static String truncatePrefix(String prefix, int maxLength) {
        if (prefix.length() > maxLength) {
            String lastColor = ChatColorUtil.getLastColors(prefix);
            prefix = truncateAtLengthWithoutReset(prefix, maxLength);
            if (!ChatColorUtil.getLastColors(prefix).equals(lastColor))
                prefix = truncateAtLengthWithoutReset(prefix, maxLength - lastColor.length()) + lastColor;
        }
        return prefix;
    }

    /**
     * Truncate an eventually too long string, also taking care of removing an eventual {@code §} character leftText alone
     * at the end.
     * @param str the string to eventually truncate.
     * @param maxLength the maximum length of the string.
     * @return a truncated string.
     */
    public static String truncateAtLengthWithoutReset(String str, int maxLength) {
        if (str.length() > maxLength) {
            str = str.substring(0, maxLength);
            if (str.endsWith("§"))
                str = str.substring(0, str.length()-1);
        }
        return str;
    }


}
