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

public enum LegacyChatFormat {

    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINED('n'),
    ITALIC('o'),
    RESET('r');





    public static final char COLOR_CHAR = LegacyComponentSerializer.SECTION_CHAR;
    public static final String COLOR_STR_PREFIX = Character.toString(COLOR_CHAR);

    private static final Map<Character, LegacyChatFormat> BY_CHAR;
    private static final Map<TextFormat, LegacyChatFormat> BY_FORMAT;
    private static final Map<LegacyFormat, LegacyChatFormat> BY_LEGACY;


    public static LegacyChatFormat of(char code) {
        return BY_CHAR.get(Character.toLowerCase(code));
    }

    public static LegacyChatFormat of(TextFormat format) {
        LegacyChatFormat colorOrDecoration = BY_FORMAT.get(format);
        if (colorOrDecoration != null)
            return colorOrDecoration;
        if (format.getClass().getSimpleName().equals("Reset")) // an internal class of legacy serializer library
            return RESET;
        throw new IllegalArgumentException("Unsupported format of type " + format.getClass());
    }

    public static LegacyChatFormat of(LegacyFormat advLegacy) {
        return BY_LEGACY.get(advLegacy);
    }









    public final char code;
    public final LegacyFormat advLegacyFormat;

    LegacyChatFormat(char code) {
        this.code = code;
        advLegacyFormat = LegacyComponentSerializer.parseChar(code);
    }

    public TextColor getTextColor() {
        return advLegacyFormat.color();
    }

    public boolean isColor() {
        return getTextColor() != null;
    }

    public TextDecoration getTextDecoration() {
        return advLegacyFormat.decoration();
    }

    public boolean isDecoration() {
        return getTextDecoration() != null;
    }

    public boolean isReset() {
        return this == RESET;
    }

    @Override
    public String toString() {
        return COLOR_STR_PREFIX + code;
    }



    static {
        BY_CHAR = Arrays.stream(values()).sequential().collect(Collectors.toMap(e -> e.code, e -> e, (e1, e2) -> e1, LinkedHashMap::new));
        BY_FORMAT = Arrays.stream(values()).sequential()
                .filter(e -> e.isColor() || e.isDecoration())
                .collect(Collectors.toMap(e -> {
            if (e.isColor())
                return e.getTextColor();
            return e.getTextDecoration();
        }, e -> e, (e1, e2) -> e1, LinkedHashMap::new));
        BY_LEGACY = Arrays.stream(values()).sequential().collect(Collectors.toMap(e -> e.advLegacyFormat, e -> e, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
