package fr.pandacube.lib.chat;

import java.util.function.Supplier;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Class holding static configuration values for chat component rendering.
 */
@SuppressWarnings("CanBeFinal")
public class ChatConfig {

    /**
     * The color used for decoration.
     */
    public static TextColor decorationColor = PandaTheme.CHAT_GREEN_1_NORMAL;

    /**
     * The character used as a pattern for decoration.
     */
    public static char decorationChar = '-';

    /**
     * The number of decoration character to put between the text and the border of
     * the line for left and right aligned text.
     */
    public static int nbCharMargin = 1;

    /**
     * The color used for successful messages.
     */
    public static TextColor successColor = PandaTheme.CHAT_GREEN_MAX_SAT;

    /**
     * The color used for error/failure messages.
     */
    public static TextColor failureColor = PandaTheme.CHAT_RED_FAILURE;

    /**
     * the color used for informational messages.
     */
    public static TextColor infoColor = PandaTheme.CHAT_GREEN_4;

    /**
     * The color used for warning messages.
     */
    public static TextColor warningColor = PandaTheme.CHAT_BROWN_2_SAT;

    /**
     * The color used to display data in a message.
     */
    public static TextColor dataColor = NamedTextColor.GRAY;

    /**
     * The color used for displayed URLs and clickable URLs.
     */
    public static TextColor urlColor = PandaTheme.CHAT_GREEN_1_NORMAL;

    /**
     * The color used for displayed commands and clickable commands.
     */
    public static TextColor commandColor = PandaTheme.CHAT_GRAY_MID;

    /**
     * The color sued to display a command that is highlighted. For example, the current page in a pagination.
     */
    public static TextColor highlightedCommandColor = NamedTextColor.WHITE;

    /**
     * The color used for broadcast messages.
     * It is often used in combination with {@link #prefix}.
     */
    public static TextColor broadcastColor = NamedTextColor.YELLOW;
    
    /**
     * The prefix used for prefixed messages.
     * It can be a stylized name of the server, like {@code "[Pandacube] "}.
     * It is often used in combination with {@link #broadcastColor}.
     */
    public static Supplier<Chat> prefix = PandaTheme::CHAT_MESSAGE_PREFIX;

    /**
     * Gets the width of the configured {@link #prefix}.
     * @param console if the width has to be calculated for the console or not.
     * @return the width of the configured {@link #prefix}.
     */
    public static int getPrefixWidth(boolean console) {
        Chat c;
        return prefix == null ? 0 : (c = prefix.get()) == null ? 0 : ChatUtil.componentWidth(c.get(), console);
    }


    /**
     * A set of predefined colors.
     */
    public static class PandaTheme {
        /** Green 1 color. */
        public static final TextColor CHAT_GREEN_1_NORMAL = TextColor.fromHexString("#3db849"); // h=126 s=50 l=48
        /** Green 2 color. */
        public static final TextColor CHAT_GREEN_2 = TextColor.fromHexString("#5ec969"); // h=126 s=50 l=58
        /** Green 3 color. */
        public static final TextColor CHAT_GREEN_3 = TextColor.fromHexString("#85d68d"); // h=126 s=50 l=68
        /** Green 4 color. */
        public static final TextColor CHAT_GREEN_4 = TextColor.fromHexString("#abe3b0"); // h=126 s=50 l=78

        /** Green max saturation color. */
        public static final TextColor CHAT_GREEN_MAX_SAT = TextColor.fromHexString("#00ff19"); // h=126 s=100 l=50
        /** Green 1 saturated color. */
        public static final TextColor CHAT_GREEN_1_SAT = TextColor.fromHexString("#20d532"); // h=126 s=50 l=48
        /** Green 2 saturated color. */
        public static final TextColor CHAT_GREEN_2_SAT = TextColor.fromHexString("#45e354"); // h=126 s=50 l=58
        /** Green 3 saturated color. */
        public static final TextColor CHAT_GREEN_3_SAT = TextColor.fromHexString("#71ea7d"); // h=126 s=50 l=68
        /** Green 4 saturated color. */
        public static final TextColor CHAT_GREEN_4_SAT = TextColor.fromHexString("#9df0a6"); // h=126 s=50 l=78

        /** Brown 1 color. */
        public static final TextColor CHAT_BROWN_1 = TextColor.fromHexString("#b26d3a"); // h=26 s=51 l=46
        /** Brown 2 color. */
        public static final TextColor CHAT_BROWN_2 = TextColor.fromHexString("#cd9265"); // h=26 s=51 l=60
        /** Brown 3 color. */
        public static final TextColor CHAT_BROWN_3 = TextColor.fromHexString("#e0bb9f"); // h=26 s=51 l=75

        /** Brown 1 saturated color. */
        public static final TextColor CHAT_BROWN_1_SAT = TextColor.fromHexString("#b35c19"); // h=26 s=75 l=40
        /** Brown 2 saturated color. */
        public static final TextColor CHAT_BROWN_2_SAT = TextColor.fromHexString("#e28136"); // h=26 s=51 l=55
        /** Brown 3 saturated color. */
        public static final TextColor CHAT_BROWN_3_SAT = TextColor.fromHexString("#ecab79"); // h=26 s=51 l=70

        /** Gray medium color. */
        public static final TextColor CHAT_GRAY_MID = TextColor.fromHexString("#888888");

        /** Red failure color. */
        public static final TextColor CHAT_RED_FAILURE = TextColor.fromHexString("#ff3333");


        /** Color used for private message prefix decoration. */
        public static final TextColor CHAT_PM_PREFIX_DECORATION = CHAT_BROWN_2_SAT;
        /** Color used for sent message text. */
        public static final TextColor CHAT_PM_SELF_MESSAGE = CHAT_GREEN_2;
        /** Color used for received message text. */
        public static final TextColor CHAT_PM_OTHER_MESSAGE = CHAT_GREEN_4;

        /** Discord color. */
        public static final TextColor CHAT_DISCORD_LINK_COLOR = TextColor.fromHexString("#00aff4");

        /**
         * Generate a prefix for broadcast message.
         * @return a prefix for broadcast message.
         */
        public static Chat CHAT_MESSAGE_PREFIX() {
            return Chat.text("[")
                    .broadcastColor()
                    .thenDecoration("Serveur")
                    .thenText("] ");
        }

        private PandaTheme() {}

    }

    private ChatConfig() {}
}
