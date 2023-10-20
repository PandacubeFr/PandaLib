package fr.pandacube.lib.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import fr.pandacube.lib.chat.Chat.FormatableChat;

/**
 * Builder for a {@link Chat} component for filling a chat line, with decoration and eventual aligned text.
 */
public class ChatFilledLine implements ComponentLike {

    /**
     * Builder for a filled line with the provided left-aligned text.
     * @param text the text to align ont the left.
     * @return a new {@link ChatFilledLine} builder.
     */
    public static ChatFilledLine leftText(ComponentLike text) {
        return new ChatFilledLine(text, Alignment.LEFT);
    }

    /**
     * Builder for a filled line with the provided right-aligned text.
     * @param text the text to align ont the right.
     * @return a new {@link ChatFilledLine} builder.
     */
    public static ChatFilledLine rightText(ComponentLike text) {
        return new ChatFilledLine(text, Alignment.RIGHT);
    }

    /**
     * Builder for a filled line with the provided centered text.
     * @param text the text to center.
     * @return a new {@link ChatFilledLine} builder.
     */
    public static ChatFilledLine centerText(ComponentLike text) {
        return new ChatFilledLine(text, Alignment.CENTER);
    }

    /**
     * Builder for a filled line with no text.
     * @return a new {@link ChatFilledLine} builder.
     */
    public static ChatFilledLine filled() {
        return new ChatFilledLine(null, Alignment.NONE);
    }






    private final ComponentLike text;
    private final Alignment alignment;
    private char decorationChar = ChatConfig.decorationChar;
    private TextColor decorationColor = ChatConfig.decorationColor;
    private boolean decorationBold = false;
    private int nbSide = ChatConfig.nbCharMargin;
    private boolean spacesAroundText = false;
    private boolean spacesDecorationRightSide = false;
    private boolean console = false;
    private Integer maxWidth = null;

    private ChatFilledLine(ComponentLike text, Alignment alignment) {
        this.text = text;
        this.alignment = alignment;
    }


    /**
     * Sets the decoration char.
     * @param decoChar the character that will fill the line.
     * @return this.
     */
    public ChatFilledLine decoChar(char decoChar) {
        decorationChar = decoChar;
        return this;
    }

    /**
     * Sets the decoration color.
     * @param decoColor the color of the characters filling the line.
     * @return this.
     */
    public ChatFilledLine decoColor(TextColor decoColor) {
        decorationColor = decoColor;
        return this;
    }

    /**
     * Sets the decoration in bold.
     * @return this.
     */
    public ChatFilledLine decoBold() {
        decorationBold = true;
        return this;
    }

    /**
     * Sets the number of side character when the text is aligned left or right.
     * @param nbSide the number of character that will separate the border from the side of the text.
     * @return this.
     */
    public ChatFilledLine nbSide(int nbSide) {
        this.nbSide = nbSide;
        return this;
    }

    /**
     * Adds spaces around the text.
     * @return this.
     */
    public ChatFilledLine spacesAroundText() {
        spacesAroundText = true;
        return this;
    }

    /**
     * If the {@link #decoChar(char)} is set to space, also add spaces at the right of the text
     * to reach the desired width.
     * @return this.
     */
    public ChatFilledLine spacesDecorationRightSide() {
        spacesDecorationRightSide = true;
        return this;
    }

    /**
     * Configure if the line will be rendered on console or not.
     * @param console true for console, false for game UI.
     * @return this.
     */
    public ChatFilledLine console(boolean console) {
        this.console = console;
        return this;
    }

    /**
     * Configure the width of the line.
     * @param maxWidth the width to consider when rendering the line. In pixel for game UI rendering, n character for
     *                 console rendering.
     * @return this.
     */
    public ChatFilledLine maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }


    /**
     * Renders this line to a {@link FormatableChat}.
     * @return a new {@link FormatableChat} built by this {@link ChatFilledLine}.
     */
    public FormatableChat toChat() {
        int maxWidth = (this.maxWidth != null)
                ? this.maxWidth
                : console ? ChatUtil.CONSOLE_NB_CHAR_DEFAULT : ChatUtil.DEFAULT_CHAT_WIDTH;

        if (alignment == Alignment.NONE) {
            int count = maxWidth / ChatUtil.charW(decorationChar, console, decorationBold);
            return Chat.text(ChatUtil.repeatedChar(decorationChar, count)).color(decorationColor).bold(decorationBold);
        }

        ComponentLike text = spacesAroundText
                ? Chat.text(" ").then(this.text).thenText(" ")
                : this.text;

        int textWidth = ChatUtil.componentWidth(text.asComponent(), console);

        if (textWidth > maxWidth)
            return (FormatableChat) text;

        int repeatedCharWidth = ChatUtil.charW(decorationChar, console, decorationBold);
        int nbCharLeft = 0, nbCharRight = 0;

        switch (alignment) {
            case CENTER -> {
                nbCharLeft = nbCharRight = (maxWidth - textWidth) / 2 / repeatedCharWidth;
                if (nbCharLeft == 0)
                    return (FormatableChat) text;
            }
            case LEFT, RIGHT -> {
                int remWidth = textWidth + nbSide * repeatedCharWidth;
                if (remWidth > maxWidth)
                    return (FormatableChat) text;
                boolean left = alignment == Alignment.LEFT;
                int nbOtherSide = (maxWidth - remWidth) / repeatedCharWidth;
                nbCharLeft  = left ? nbSide : nbOtherSide;
                nbCharRight = left ? nbOtherSide : nbSide;
            }
        }

        Chat d = Chat.chat()
                .then(Chat.text(ChatUtil.repeatedChar(decorationChar, nbCharLeft)).color(decorationColor).bold(decorationBold))
                .then(text);
        if (decorationChar != ' ' || spacesDecorationRightSide)
            d.then(Chat.text(ChatUtil.repeatedChar(decorationChar, nbCharRight)).color(decorationColor).bold(decorationBold));
        return (FormatableChat) d;
    }


    @Override
    public @NotNull Component asComponent() {
        return toChat().asComponent();
    }





    private enum Alignment {
        LEFT,
        CENTER,
        RIGHT,
        NONE
    }
}
