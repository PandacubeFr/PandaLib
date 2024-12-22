package fr.pandacube.lib.chat;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextFormat;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convenient enum to uses legacy format while keeping compatibility with modern chat format and API (Adventure, ...)
 */
public enum LegacyChatFormat {

    /**
     * Black (0) color format code.
     */
    BLACK('0'),
    /**
     * Dark blue (1) color format code.
     */
    DARK_BLUE('1'),
    /**
     * Dark green (2) color format code.
     */
    DARK_GREEN('2'),
    /**
     * Dark aqua (3) color format code.
     */
    DARK_AQUA('3'),
    /**
     * Dark red (4) color format code.
     */
    DARK_RED('4'),
    /**
     * Dark purple (5) color format code.
     */
    DARK_PURPLE('5'),
    /**
     * Gold (6) color format code.
     */
    GOLD('6'),
    /**
     * Gray (7) color format code.
     */
    GRAY('7'),
    /**
     * Dark gray (8) color format code.
     */
    DARK_GRAY('8'),
    /**
     * Blue (9) color format code.
     */
    BLUE('9'),
    /**
     * Green (A) color format code.
     */
    GREEN('a'),
    /**
     * Aqua (B) color format code.
     */
    AQUA('b'),
    /**
     * Red (C) color format code.
     */
    RED('c'),
    /**
     * Light purple (D) color format code.
     */
    LIGHT_PURPLE('d'),
    /**
     * Yellow (E) color format code.
     */
    YELLOW('e'),
    /**
     * White (F) color format code.
     */
    WHITE('f'),
    /**
     * Obfuscated (K) decoration format code.
     */
    OBFUSCATED('k'),
    /**
     * Bold (L) decoration format code.
     */
    BOLD('l'),
    /**
     * Strikethrough (M) decoration format code.
     */
    STRIKETHROUGH('m'),
    /**
     * Underlined (N) decoration format code.
     */
    UNDERLINED('n'),
    /**
     * Italic (O) decoration format code.
     */
    ITALIC('o'),
    /**
     * Reset (R) format code.
     */
    RESET('r');


    /**
     * The character used by Minecraft for legacy chat format.
     */
    public static final char COLOR_CHAR = LegacyComponentSerializer.SECTION_CHAR;

    /** {@link #COLOR_CHAR} but as a String! */
    public static final String COLOR_STR_PREFIX = Character.toString(COLOR_CHAR);

    private static final Map<Character, LegacyChatFormat> BY_CHAR;
    private static final Map<TextFormat, LegacyChatFormat> BY_FORMAT;
    private static final Map<LegacyFormat, LegacyChatFormat> BY_LEGACY;


    /**
     * Gets the {@link LegacyChatFormat} from the provided chat color code.
     * @param code the character code from [0-9A-Fa-fK-Ok-oRr].
     * @return the {@link LegacyChatFormat} related to the provided code.
     */
    public static LegacyChatFormat of(char code) {
        return BY_CHAR.get(Character.toLowerCase(code));
    }

    /**
     * Gets the {@link LegacyChatFormat} from the provided {@link TextFormat} instance.
     * @param format the {@link TextFormat} instance.
     * @return the {@link LegacyChatFormat} related to the provided format.
     */
    public static LegacyChatFormat of(TextFormat format) {
        LegacyChatFormat colorOrDecoration = BY_FORMAT.get(format);
        if (colorOrDecoration != null)
            return colorOrDecoration;
        if (format.getClass().getSimpleName().equals("Reset")) // an internal class of legacy serializer library
            return RESET;
        throw new IllegalArgumentException("Unsupported format of type " + format.getClass());
    }

    /**
     * Gets the {@link LegacyChatFormat} from the provided {@link LegacyFormat} instance.
     * @param advLegacy the {@link LegacyFormat} instance.
     * @return the {@link LegacyChatFormat} related to the provided format.
     */
    public static LegacyChatFormat of(LegacyFormat advLegacy) {
        return BY_LEGACY.get(advLegacy);
    }


    /**
     * The format code of this chat format.
     */
    public final char code;

    /**
     * The Adventure legacy format instance related to this chat format.
     */
    public final LegacyFormat advLegacyFormat;

    LegacyChatFormat(char code) {
        this.code = code;
        advLegacyFormat = LegacyComponentSerializer.parseChar(code);
    }

    /**
     * Gets the related {@link TextColor}, or null if it's not a color.
     * @return the related {@link TextColor}, or null if it's not a color.
     */
    public TextColor getTextColor() {
        return advLegacyFormat.color();
    }

    /**
     * Tells if this format is a color.
     * @return true if this format is a color, false otherwise.
     */
    public boolean isColor() {
        return getTextColor() != null;
    }

    /**
     * Gets the related {@link TextDecoration}, or null if it's not a decoration.
     * @return the related {@link TextDecoration}, or null if it's not a decoration.
     */
    public TextDecoration getTextDecoration() {
        return advLegacyFormat.decoration();
    }

    /**
     * Tells if this format is a decoration (bold, italic, ...).
     * @return true if this format is a decoration, false otherwise.
     */
    public boolean isDecoration() {
        return getTextDecoration() != null;
    }

    /**
     * Tells if this format is the reset.
     * @return true if this format is the reset, false otherwise.
     */
    public boolean isReset() {
        return this == RESET;
    }

    @Override
    public String toString() {
        return COLOR_STR_PREFIX + code;
    }



    static {
        BY_CHAR = Arrays.stream(values()).sequential()
                .collect(Collectors.toMap(e -> e.code, e -> e, (e1, e2) -> e1, LinkedHashMap::new));
        BY_FORMAT = Arrays.stream(values()).sequential()
                .filter(e -> e.isColor() || e.isDecoration())
                .collect(Collectors.toMap(e -> {
                    if (e.isColor())
                        return e.getTextColor();
                    return e.getTextDecoration();
                }, e -> e, (e1, e2) -> e1, LinkedHashMap::new));
        BY_LEGACY = Arrays.stream(values()).sequential()
                .collect(Collectors.toMap(e -> e.advLegacyFormat, e -> e, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
