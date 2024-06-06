package fr.pandacube.lib.chat;

import java.util.Objects;

import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

import fr.pandacube.lib.chat.Chat.FormatableChat;

/**
 * Abstract class holding the publicly accessible methods to create an instance of {@link Chat} component.
 */
public abstract class ChatStatic {



    private static FormatableChat chatComponent(Component c) {
        return new FormatableChat(componentToBuilder(c));
    }

    /**
     * Creates a {@link FormatableChat} from the provided Bungee {@link BaseComponent}.
     * @param c the {@link BaseComponent}.
     * @return a new {@link FormatableChat}.
     */
    public static FormatableChat chatComponent(BaseComponent c) {
        return new FormatableChat(componentToBuilder(Chat.toAdventure(c)));
    }

    /**
     * Creates a {@link FormatableChat} from the provided {@link ComponentLike}.
     * If the provided component is an instance of {@link Chat}, its content will be duplicated, and the provided one
     * will be untouched.
     * @param c the {@link ComponentLike}.
     * @return a new {@link FormatableChat}.
     */
    public static FormatableChat chatComponent(ComponentLike c) {
        return chatComponent(c.asComponent());
    }

    /**
     * Creates a {@link FormatableChat} with an empty main text content.
     * @return a new empty {@link FormatableChat}.
     */
    public static FormatableChat chat() {
        return new FormatableChat(Component.text());
    }

    /**
     * Creates a {@link FormatableChat} from the provided Bungee {@link BaseComponent BaseComponent[]}.
     * @param c the array of {@link BaseComponent}.
     * @return a new {@link FormatableChat}.
     */
    public static FormatableChat chatComponent(BaseComponent[] c) {
        return chatComponent(Chat.toAdventure(c));
    }






    /**
     * Creates a {@link FormatableChat} with the provided plain text as its main text content.
     * @param plainText the text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its main text content.
     * @throws IllegalArgumentException if the {@code plainText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)}
     *         instead.
     */
    public static FormatableChat text(Object plainText) {
        if (plainText instanceof ComponentLike) {
            throw new IllegalArgumentException("Expected any object except instance of " + ComponentLike.class + ". Received " + plainText + ". Please use ChatStatic.chatComponent(ComponentLike) instead.");
        }
        return new FormatableChat(Component.text().content(Objects.toString(plainText)));
    }


    /**
     * Creates a {@link FormatableChat} with the provided legacy text as its content, using the section {@code "§"}
     * character.
     * @param legacyText the legacy text to use as the content, that uses the {@code "§"} character.
     * @return a new {@link FormatableChat} with the provided text as its content.
     * @throws IllegalArgumentException If the {@code legacyText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)}
     *         instead.
     */
    public static FormatableChat legacyText(Object legacyText) {
        return legacyText(legacyText, LegacyComponentSerializer.SECTION_CHAR);
    }


    /**
     * Creates a {@link FormatableChat} with the provided legacy text as its content, using the ampersand {@code "&"}
     * character.
     * @param legacyText the legacy text to use as the content, that uses the {@code "&"} character.
     * @return a new {@link FormatableChat} with the provided text as its content.
     * @throws IllegalArgumentException If the {@code legacyText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)}
     *         instead.
     */
    public static FormatableChat legacyAmpersandText(Object legacyText) {
        return legacyText(legacyText, LegacyComponentSerializer.AMPERSAND_CHAR);
    }


    /**
     * Creates a {@link FormatableChat} with the provided legacy text as its content, using the specified
     * legacyCharacter.
     * @param legacyText the legacy text to use as the content.
     * @param legacyCharacter the character used in the provided text to prefix color and format code.
     * @return a new {@link FormatableChat} with the provided text as its content.
     * @throws IllegalArgumentException If the {@code legacyText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)}
     *         instead.
     */
    private static FormatableChat legacyText(Object legacyText, char legacyCharacter) {
        if (legacyText instanceof ComponentLike) {
            throw new IllegalArgumentException("Expected any object except instance of " + ComponentLike.class + ". Received " + legacyText + ". Please use ChatStatic.chatComponent(ComponentLike) instead.");
        }
        return chatComponent(LegacyComponentSerializer.legacy(legacyCharacter).deserialize(Objects.toString(legacyText)));
    }


    /**
     * Creates a {@link FormatableChat} with the provided MiniMessage text as its content.
     * @param miniMessageText the MiniMessage text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its content.
     */
    public static FormatableChat miniMessageText(String miniMessageText) {
        return chatComponent(MiniMessage.miniMessage().deserialize(miniMessageText));
    }


    /**
     * Creates a {@link FormatableChat} with the provided plain text as its main text content, and colored using the
     * {@link ChatConfig#infoColor configured info color}.
     * @param plainText the text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its main text content, and the configured color.
     * @throws IllegalArgumentException if the {@code plainText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)} and
     *         {@link FormatableChat#infoColor()} instead.
     */
    public static FormatableChat infoText(Object plainText) {
        return text(plainText).infoColor();
    }

    /**
     * Creates a {@link FormatableChat} with the provided plain text as its main text content, and colored using the
     * {@link ChatConfig#warningColor configured warning color}.
     * @param plainText the text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its main text content, and the configured color.
     * @throws IllegalArgumentException if the {@code plainText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)} and
     *         {@link FormatableChat#warningColor()} instead.
     */
    public static FormatableChat warningText(Object plainText) {
        return text(plainText).warningColor();
    }

    /**
     * Creates a {@link FormatableChat} with the provided plain text as its main text content, and colored using the
     * {@link ChatConfig#dataColor configured data color}.
     * @param plainText the text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its main text content, and the configured color.
     * @throws IllegalArgumentException if the {@code plainText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)} and
     *         {@link FormatableChat#dataColor()} instead.
     */
    public static FormatableChat dataText(Object plainText) {
        return text(plainText).dataColor();
    }

    /**
     * Creates a {@link FormatableChat} with the provided plain text as its main text content, and colored using the
     * {@link ChatConfig#decorationColor configured decorationColor color}.
     * @param plainText the text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its main text content, and the configured color.
     * @throws IllegalArgumentException if the {@code plainText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)} and
     *         {@link FormatableChat#decorationColor()} instead.
     */
    public static FormatableChat decorationText(Object plainText) {
        return text(plainText).decorationColor();
    }

    /**
     * Creates a {@link FormatableChat} with the provided plain text as its main text content, and colored using the
     * {@link ChatConfig#successColor configured success color}.
     * @param plainText the text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its main text content, and the configured color.
     * @throws IllegalArgumentException if the {@code plainText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)} and
     *         {@link FormatableChat#successColor()} instead.
     */
    public static FormatableChat successText(Object plainText) {
        return text(plainText).successColor();
    }

    /**
     * Creates a {@link FormatableChat} with the provided plain text as its main text content, and colored using the
     * {@link ChatConfig#failureColor configured failure color}.
     * @param plainText the text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its main text content, and the configured color.
     * @throws IllegalArgumentException if the {@code plainText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)} and
     *         {@link FormatableChat#failureColor()} instead.
     */
    public static FormatableChat failureText(Object plainText) {
        return text(plainText).failureColor();
    }

    /**
     * Creates a {@link FormatableChat} with the provided legacy text as its main text content, and colored in white in
     * case there is no color on the generated parent component.
     * @param legacyText the legacy text to use as the content.
     * @return a new {@link FormatableChat} with the provided text as its main text content, and the configured color.
     * @throws IllegalArgumentException if the {@code plainText} parameter is instance of {@link Chat} or
     *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)} and
     *         {@link FormatableChat#failureColor()} instead.
     */
    public static FormatableChat playerNameText(String legacyText) {
        FormatableChat fc = legacyText(legacyText);
        fc.builder.colorIfAbsent(NamedTextColor.WHITE);
        return fc;
    }

    /**
     * Creates a {@link FormatableChat} from the provided {@link Component}, coloring in white the generated parent
     * component in case there is no color defined.
     * If the provided component is an instance of {@link Chat}, its content will be duplicated, and the provided one
     * will be untouched.
     * @param c the {@link Component}.
     * @return a new {@link FormatableChat}.
     */
    public static FormatableChat playerNameComponent(ComponentLike c) {
        FormatableChat fc = chatComponent(c);
        fc.builder.colorIfAbsent(NamedTextColor.WHITE);
        return fc;
    }




    /**
     * Creates a {@link FormatableChat} with the provided translation key and parameters.
     * @param key the translation key.
     * @param with the translation parameters.
     * @return a new {@link FormatableChat} with the provided translation key and parameters.
     */
    public static FormatableChat translation(String key, Object... with) {
        return new FormatableChat(Component.translatable().key(key).arguments(Chat.filterObjToTranslationArgumentLike(with)));
    }

    /**
     * Creates a {@link FormatableChat} with the provided keybinding.
     * @param key the keybinding to display.
     * @return a new {@link FormatableChat} with the provided keybinding.
     */
    public static FormatableChat keybind(String key) {
        return new FormatableChat(Component.keybind().keybind(key));
    }

    /**
     * Creates a {@link FormatableChat} with the provided score name and objective.
     * @param name the score name.
     * @param objective the score objective.
     * @return a new {@link FormatableChat} with the provided score name and objective.
     */
    public static FormatableChat score(String name, String objective) {
        return new FormatableChat(Component.score().name(name).objective(objective));
    }






    /**
     * Creates a {@link FormatableChat} that leads to a URL when clicked.
     * @param inner the component to make clickable.
     * @param url the target url. Must start with {@code "http://"} or {@code "https://"}.
     * @param hover the content to display when hovering the component.
     * @return a new {@link FormatableChat} that leads to a URL when clicked.
     */
    public static FormatableChat clickableURL(ComponentLike inner, String url, HoverEventSource<?> hover) {
        Objects.requireNonNull(url, "url");
        if (inner == null)
            inner = text(url);
        if (hover == null)
            hover = text(ChatUtil.wrapInLimitedPixels(url, 240));
        return (FormatableChat) chat().clickURL(url).urlColor().hover(hover).then(inner);
    }

    /**
     * Creates a {@link FormatableChat} that leads to a URL when clicked.
     * <p>
     * When hovered, the component will display the url. To customize the hover content, use
     * {@link #clickableURL(ComponentLike, String, HoverEventSource)}.
     * @param inner the component to make clickable.
     * @param url the target url. Must start with {@code "http://"} or {@code "https://"}.
     * @return a new {@link FormatableChat} that leads to a URL when clicked.
     */
    public static FormatableChat clickableURL(ComponentLike inner, String url) {
        return clickableURL(inner, url, null);
    }

    /**
     * Creates a {@link FormatableChat} that leads to a URL when clicked.
     * <p>
     * The text on which to click will be the URL itself. To configure the clicked text, use
     * {@link #clickableURL(ComponentLike, String, HoverEventSource)}.
     * @param url the target url. Must start with {@code "http://"} or {@code "https://"}.
     * @param hover the content to display when hovering the component.
     * @return a new {@link FormatableChat} that leads to a URL when clicked.
     */
    public static FormatableChat clickableURL(String url, HoverEventSource<?> hover) {
        return clickableURL(null, url, hover);
    }

    /**
     * Creates a {@link FormatableChat} that leads to a URL when clicked.
     * <p>
     * The text on which to click will be the URL itself. To configure the clicked text, use
     * {@link #clickableURL(ComponentLike, String)}.
     * <p>
     * When hovered, the component will display the url. To customize the hover content, use
     * {@link #clickableURL(String, HoverEventSource)}.
     * @param url the target url. Must start with {@code "http://"} or {@code "https://"}.
     * @return a new {@link FormatableChat} that leads to a URL when clicked.
     */
    public static FormatableChat clickableURL(String url) {
        return clickableURL(null, url, null);
    }






    /**
     * Creates a {@link FormatableChat} that runs a command when clicked.
     * @param inner the component to make clickable.
     * @param commandWithSlash the command to run. Must start with {@code "/"}.
     * @param hover the content to display when hovering the component.
     * @return a new {@link FormatableChat} that runs a command when clicked.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public static FormatableChat clickableCommand(ComponentLike inner, String commandWithSlash, HoverEventSource<?> hover) {
        Objects.requireNonNull(commandWithSlash, "commandWithSlash");
        if (!commandWithSlash.startsWith("/"))
            throw new IllegalArgumentException("commandWithSlash must start with a '/' character.");
        if (inner == null)
            inner = text(commandWithSlash);
        if (hover == null)
            hover = text(ChatUtil.wrapInLimitedPixels(commandWithSlash, 240));
        return (FormatableChat) chat().clickCommand(commandWithSlash).commandColor().hover(hover).then(inner);
    }

    /**
     * Creates a {@link FormatableChat} that runs a command when clicked.
     * <p>
     * When hovered, the component will display the command itself. To customize the hover content, use
     * {@link #clickableCommand(ComponentLike, String, HoverEventSource)}.
     * @param inner the component to make clickable.
     * @param commandWithSlash the command to run. Must start with {@code "/"}.
     * @return a new {@link FormatableChat} that runs a command when clicked.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public static FormatableChat clickableCommand(ComponentLike inner, String commandWithSlash) {
        return clickableCommand(inner, commandWithSlash, null);
    }

    /**
     * Creates a {@link FormatableChat} that runs a command when clicked.
     * <p>
     * The text on which to click will be the command itself. To configure the clicked text, use
     * {@link #clickableCommand(ComponentLike, String, HoverEventSource)}.
     * @param commandWithSlash the command to run. Must start with {@code "/"}.
     * @param hover the content to display when hovering the component.
     * @return a new {@link FormatableChat} that runs a command when clicked.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public static FormatableChat clickableCommand(String commandWithSlash, HoverEventSource<?> hover) {
        return clickableCommand(null, commandWithSlash, hover);
    }

    /**
     * Creates a {@link FormatableChat} that runs a command when clicked.
     * <p>
     * The text on which to click will be the command itself. To configure the clicked text, use
     * {@link #clickableCommand(ComponentLike, String)}.
     * <p>
     * When hovered, the component will display the command itself. To customize the hover content, use
     * {@link #clickableCommand(String, HoverEventSource)}.
     * @param commandWithSlash the command to run. Must start with {@code "/"}.
     * @return a new {@link FormatableChat} that runs a command when clicked.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public static FormatableChat clickableCommand(String commandWithSlash) {
        return clickableCommand(null, commandWithSlash, null);
    }






    /**
     * Creates a {@link FormatableChat} that pre-fill the chat box with a command when clicked.
     * @param inner the component to make clickable.
     * @param commandWithSlash the command to suggest. Must start with {@code "/"}.
     * @param hover the content to display when hovering the component.
     * @return a new {@link FormatableChat} that pre-fill the chat box with a command when clicked.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public static FormatableChat clickableSuggest(ComponentLike inner, String commandWithSlash, HoverEventSource<?> hover) {
        Objects.requireNonNull(commandWithSlash, "commandWithSlash");
        if (!commandWithSlash.startsWith("/"))
            throw new IllegalArgumentException("commandWithSlash must start with a '/' character.");
        if (inner == null)
            inner = text(commandWithSlash);
        if (hover == null)
            hover = text(ChatUtil.wrapInLimitedPixels(commandWithSlash, 240));
        return (FormatableChat) chat().clickSuggest(commandWithSlash).commandColor().hover(hover).then(inner);
    }

    /**
     * Creates a {@link FormatableChat} that pre-fill the chat box with a command when clicked.
     * <p>
     * When hovered, the component will display the command itself. To customize the hover content, use
     * {@link #clickableSuggest(ComponentLike, String, HoverEventSource)}.
     * @param inner the component to make clickable.
     * @param commandWithSlash the command to suggest. Must start with {@code "/"}.
     * @return a new {@link FormatableChat} that pre-fill the chat box with a command when clicked.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public static FormatableChat clickableSuggest(ComponentLike inner, String commandWithSlash) {
        return clickableSuggest(inner, commandWithSlash, null);
    }

    /**
     * Creates a {@link FormatableChat} that pre-fill the chat box with a command when clicked.
     * <p>
     * The text on which to click will be the command itself. To configure the clicked text, use
     * {@link #clickableSuggest(ComponentLike, String, HoverEventSource)}.
     * @param commandWithSlash the command to suggest. Must start with {@code "/"}.
     * @param hover the content to display when hovering the component.
     * @return a new {@link FormatableChat} that pre-fill the chat box with a command when clicked.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public static FormatableChat clickableSuggest(String commandWithSlash, HoverEventSource<?> hover) {
        return clickableSuggest(null, commandWithSlash, hover);
    }

    /**
     * Creates a {@link FormatableChat} that pre-fill the chat box with a command when clicked.
     * <p>
     * The text on which to click will be the command itself. To configure the clicked text, use
     * {@link #clickableSuggest(ComponentLike, String)}.
     * <p>
     * When hovered, the component will display the command itself. To customize the hover content, use
     * {@link #clickableSuggest(String, HoverEventSource)}.
     * @param commandWithSlash the command to suggest. Must start with {@code "/"}.
     * @return a new {@link FormatableChat} that pre-fill the chat box with a command when clicked.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public static FormatableChat clickableSuggest(String commandWithSlash) {
        return clickableSuggest(null, commandWithSlash, null);
    }








    /**
     * Creates a {@link FormatableChat} filling a chat line with decoration and a left-aligned text.
     * @param text the text aligned to the left.
     * @param decorationChar the character used for decoration around the text.
     * @param decorationColor the color used for the decoration characters.
     * @param console if the line is rendered on console (true) or IG (false).
     * @return a new {@link FormatableChat} filling a chat line with decoration and a left-aligned text.
     * @see ChatFilledLine#leftText(ComponentLike)
     */
    public static FormatableChat leftText(ComponentLike text, char decorationChar, TextColor decorationColor, boolean console) {
        return ChatFilledLine.leftText(text).decoChar(decorationChar).decoColor(decorationColor).spacesAroundText().console(console).toChat();
    }

    /**
     * Creates a {@link FormatableChat} filling a chat line with the configured decoration character and
     * color and a left-aligned text.
     * @param text the text aligned to the left.
     * @param console if the line is rendered on console (true) or IG (false).
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a left-aligned text.
     * @see ChatFilledLine#leftText(ComponentLike)
     * @see ChatConfig#decorationChar
     * @see ChatConfig#decorationColor
     */
    public static FormatableChat leftText(ComponentLike text, boolean console) {
        return ChatFilledLine.leftText(text).spacesAroundText().console(console).toChat();
    }

    /**
     * Creates a {@link FormatableChat} filling a chat line with decoration and a right-aligned text.
     * @param text the text aligned to the right.
     * @param decorationChar the character used for decoration around the text.
     * @param decorationColor the color used for the decoration characters.
     * @param console if the line is rendered on console (true) or IG (false).
     * @return a new {@link FormatableChat} filling a chat line with decoration and a right-aligned
     *         text.
     * @see ChatFilledLine#rightText(ComponentLike)
     */
    public static FormatableChat rightText(ComponentLike text, char decorationChar, TextColor decorationColor, boolean console) {
        return ChatFilledLine.rightText(text).decoChar(decorationChar).decoColor(decorationColor).spacesAroundText().console(console).toChat();
    }

    /**
     * Creates a {@link FormatableChat} filling a chat line with the configured decoration character and
     * color and a right-aligned text.
     * @param text the text aligned to the right.
     * @param console if the line is rendered on console (true) or IG (false).
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a right-aligned text.
     * @see ChatFilledLine#rightText(ComponentLike)
     * @see ChatConfig#decorationChar
     * @see ChatConfig#decorationColor
     */
    public static FormatableChat rightText(ComponentLike text, boolean console) {
        return ChatFilledLine.rightText(text).spacesAroundText().console(console).toChat();
    }

    /**
     * Creates a {@link FormatableChat} filling a chat line with decoration and a centered text.
     * @param text the text aligned to the center.
     * @param decorationChar the character used for decoration around the text.
     * @param decorationColor the color used for the decoration characters.
     * @param console if the line is rendered on console (true) or IG (false).
     * @return a new {@link FormatableChat} filling a chat line with decoration and a centered text.
     * @see ChatFilledLine#centerText(ComponentLike)
     */
    public static FormatableChat centerText(ComponentLike text, char decorationChar, TextColor decorationColor, boolean console) {
        return ChatFilledLine.centerText(text).decoChar(decorationChar).decoColor(decorationColor).spacesAroundText().console(console).toChat();
    }

    /**
     * Creates a {@link FormatableChat} filling a chat line with the configured decoration character and
     * color and a centered text.
     * @param text the text aligned to the center.
     * @param console if the line is rendered on console (true) or IG (false).
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a centered text.
     * @see ChatFilledLine#centerText(ComponentLike)
     * @see ChatConfig#decorationChar
     * @see ChatConfig#decorationColor
     */
    public static FormatableChat centerText(ComponentLike text, boolean console) {
        return ChatFilledLine.centerText(text).spacesAroundText().console(console).toChat();
    }

    /**
     * Creates a {@link FormatableChat} filling a chat line with a decoration character and color.
     * @param decorationChar the character used for decoration.
     * @param decorationColor the color used for the decoration characters.
     * @param console if the line is rendered on console (true) or IG (false).
     * @return a new {@link FormatableChat} filling a chat line with a decoration character and color.
     * @see ChatFilledLine#filled()
     */
    public static FormatableChat filledLine(char decorationChar, TextColor decorationColor, boolean console) {
        return ChatFilledLine.filled().decoChar(decorationChar).decoColor(decorationColor).console(console).toChat();
    }

    /**
     * Creates a {@link FormatableChat} filling a chat line with the configured decoration character and
     * color.
     * @param console if the line is rendered on console (true) or IG (false).
     * @return a new {@link FormatableChat} filling a chat line with a decoration character and color.
     * @see ChatFilledLine#filled()
     * @see ChatConfig#decorationChar
     * @see ChatConfig#decorationColor
     */
    public static FormatableChat filledLine(boolean console) {
        return ChatFilledLine.filled().console(console).toChat();
    }










    /**
     * Adds the configured prefix and broadcast color to the provided message.
     * @param message the message to decorate.
     * @return the decorated message.
     */
    public static Chat prefixedAndColored(ComponentLike message) {
        return Chat.chat()
                .broadcastColor()
                .then(ChatConfig.prefix.get())
                .then(message);
    }










    private static ComponentBuilder<?, ?> componentToBuilder(Component c) {
        ComponentBuilder<?, ?> builder = switch (c) {
            case TextComponent textComponent -> Component.text()
                    .content(textComponent.content());
            case TranslatableComponent translatableComponent -> Component.translatable()
                    .key(translatableComponent.key()).arguments(translatableComponent.arguments());
            case SelectorComponent selectorComponent -> Component.selector()
                    .pattern(selectorComponent.pattern());
            case ScoreComponent scoreComponent -> Component.score()
                    .name(scoreComponent.name())
                    .objective(scoreComponent.objective());
            case KeybindComponent keybindComponent -> Component.keybind()
                    .keybind(keybindComponent.keybind());
            case BlockNBTComponent blockNBTComponent -> Component.blockNBT()
                    .interpret(blockNBTComponent.interpret())
                    .nbtPath(blockNBTComponent.nbtPath())
                    .pos(blockNBTComponent.pos());
            case EntityNBTComponent entityNBTComponent -> Component.entityNBT()
                    .interpret(entityNBTComponent.interpret())
                    .nbtPath(entityNBTComponent.nbtPath())
                    .selector(entityNBTComponent.selector());
            case StorageNBTComponent storageNBTComponent -> Component.storageNBT()
                    .interpret(storageNBTComponent.interpret())
                    .nbtPath(storageNBTComponent.nbtPath())
                    .storage(storageNBTComponent.storage());
            case null, default -> throw new IllegalArgumentException("Unknown component type " + (c == null ? "null" : c.getClass()));
        };
        return builder.style(c.style()).append(c.children());
    }

    /**
     * Creates a new {@link ChatStatic} instance.
     */
    protected ChatStatic() {}


}
