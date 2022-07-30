package fr.pandacube.lib.chat;

import java.util.function.Supplier;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Class holding static configuration values for chat component rendering.
 */
public class ChatConfig {

    /**
     * The color used for decoration.
     */
    public static TextColor decorationColor = NamedTextColor.YELLOW;

    /**
     * The character used as a pattern for decoration.
     */
    public static char decorationChar = '-';

    /**
     * The default margin for left and right aligned text.
     */
    public static int nbCharMargin = 1;

    /**
     * The color used for successful messages.
     */
    public static TextColor successColor = NamedTextColor.GREEN;

    /**
     * The color used for error/failure messages.
     */
    public static TextColor failureColor = NamedTextColor.RED;

    /**
     * the color used for informational messages.
     */
    public static TextColor infoColor = NamedTextColor.GOLD;

    /**
     * The color used for warning messages.
     */
    public static TextColor warningColor = NamedTextColor.GOLD;

    /**
     * The color used to display data in a message.
     */
    public static TextColor dataColor = NamedTextColor.GRAY;

    /**
     * The color used for displayed URLs and clickable URLs.
     */
    public static TextColor urlColor = NamedTextColor.GREEN;

    /**
     * The color used for displayed commands and clickable commands.
     */
    public static TextColor commandColor = NamedTextColor.GRAY;

    /**
     * The color sued to display a command that is highlighted. For example, the current page in a pagination.
     */
    public static TextColor highlightedCommandColor = NamedTextColor.WHITE;

    /**
     * The color used for broadcasted messages.
     * It is often used in combination with {@link #prefix}.
     */
    public static TextColor broadcastColor = NamedTextColor.YELLOW;
    
    /**
     * The prefix used for prefixed messages.
     * It can be a sylized name of the server, like {@code "[Pandacube] "}.
     * It is often used in combination with {@link #broadcastColor}.
     */
    public static Supplier<Chat> prefix;

    /**
     * Gets the width of the configured {@link #prefix}.
     * @param console if the width has to be calculated for the console or not.
     * @return the width of the configured {@link #prefix}.
     */
    public static int getPrefixWidth(boolean console) {
        Chat c;
        return prefix == null ? 0 : (c = prefix.get()) == null ? 0 : ChatUtil.componentWidth(c.getAdv(), console);
    }
}
