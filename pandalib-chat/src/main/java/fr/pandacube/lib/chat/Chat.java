package fr.pandacube.lib.chat;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.TranslationArgumentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A builder for chat components.
 * <p>
 * Use one of the provided static methods to create a new instance.
 * <p>
 * This class implements {@link ComponentLike} and {@link HoverEventSource} so they can be used directly in
 * Adventure API and its implementation without using the final methods of this builder.
 * <p>
 * The unique possible concrete subclass of this class, {@link FormatableChat}, takes care of the formatting of the
 * built component. The rationale for this design is explained in the documentation of {@link FormatableChat}.
 */
public abstract sealed class Chat extends ChatStatic implements HoverEventSource<Component>, ComponentLike {

    /* package */ final ComponentBuilder<?, ?> builder;
    /* package */ boolean console = false;
    /* package */ Integer maxWidth = null;

    /* package */ Chat(ComponentBuilder<?, ?> b) {
        builder = Objects.requireNonNull(b, "Provided component builder must not be null");
    }









    /*
     * Builder terminal operation and serialization
     */


    /**
     * Builds the component into Adventure Component instance.
     * @return the {@link Component} built from this {@link Chat} component.
     */
    public Component getAdv() {
        return builder.build();
    }

    /**
     * Builds the component into BungeeCord {@link BaseComponent} instance.
     * @return the {@link BaseComponent} built from this {@link Chat} component.
     */
    public BaseComponent get() {
        return toBungee(getAdv());
    }

    /**
     * Builds the component into BungeeCord {@link BaseComponent} array.
     * @return the {@link BaseComponent} array built from this {@link Chat} component.
     */
    public BaseComponent[] getAsArray() {
        return toBungeeArray(getAdv());
    }

    private static final LegacyComponentSerializer LEGACY_SERIALIZER_BUNGEE_FRIENDLY = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    /**
     * Converts the built component into legacy text.
     * @return the legacy text. RGB colors are in BungeeCord format.
     */
    public String getLegacyText() {
        return LEGACY_SERIALIZER_BUNGEE_FRIENDLY.serialize(getAdv());
    }

    /**
     * Converts the built component into plain text.
     * @return the plain text of this component.
     */
    public String getPlainText() {
        return PlainTextComponentSerializer.plainText().serializeOr(getAdv(), "");
    }

    @Override
    public @NotNull HoverEvent<Component> asHoverEvent(@NotNull UnaryOperator<Component> op) {
        return HoverEvent.showText(op.apply(getAdv()));
    }

    /**
     * Builds the component into Adventure Component instance.
     * @return the {@link Component} built from this {@link Chat} component.
     */
    @Override
    public @NotNull Component asComponent() {
        return getAdv();
    }

    /**
     * Builds the component into Adventure Component instance, also down sampling the RGB colors to named colors.
     * @return the {@link Component} built from this {@link Chat} component, with down-sampled colors.
     */
    public Component getAsDownSampledColorsComponent() {
        String json = GsonComponentSerializer.colorDownsamplingGson().serialize(getAdv());
        return GsonComponentSerializer.gson().deserialize(json);
    }

    /**
     * Returns a new {@link Chat} consisting of this {@link Chat} instance, with the RGB colors down-sampled to named colors.
     * @return a new {@link Chat} instance, with down-sampled colors.
     */
    public Chat getAsDownSampledColors() {
        return chatComponent(getAsDownSampledColorsComponent());
    }

    /**
     * Returns a MiniMessage representation of this {@link Chat} component.
     * @return the MiniMessage representation if this {@link Chat} component.
     */
    public String getMiniMessage() {
        return MiniMessage.miniMessage().serialize(getAdv());
    }












    /*
     * Sub-component appending
     */


    /**
     * Appends a component to this component.
     * @param comp the component to append.
     * @return this.
     */
    public Chat then(Component comp) {
        if (comp instanceof TextComponent txtComp) {
            if (!txtComp.hasStyling() && (txtComp.content().isEmpty())) {
                // no need to add the provided component to the current component.
                // but eventual child component must be added
                if (!txtComp.children().isEmpty()) {
                    for (Component child : txtComp.children())
                        then(child);
                }
                return this;
            }
        }
        builder.append(comp);
        return this;
    }

    /**
     * Appends a BungeeCord {@link BaseComponent} to this component.
     * @param comp the component to append.
     * @return this.
     */
    public Chat then(BaseComponent comp) {
        return then(toAdventure(comp));
    }

    /**
     * Appends a component to this component.
     * @param comp the component to append.
     * @return this.
     */
    public Chat then(ComponentLike comp) {
        if (comp instanceof ChatFilledLine ac) {
            ac.console(console);
            if (maxWidth != null)
                ac.maxWidth(maxWidth);
        }
        return then(comp.asComponent());
    }

    /**
     * Appends a BungeeCord {@link BaseComponent} array to this component.
     * @param comp the components to append.
     * @return this.
     */
    public Chat then(BaseComponent[] comp) {
        return then(toAdventure(comp));
    }








    /*
     * Special sub-components appending
     */

    /**
     * Appends a plain text to this component.
     * @param plainText the plain text.
     * @return this.
     */
    public Chat thenText(Object plainText) { return then(text(plainText)); }

    /**
     * Appends a plain text to this component, colored using {@link ChatConfig#infoColor}.
     * @param plainText the plain text.
     * @return this.
     */
    public Chat thenInfo(Object plainText) { return then(infoText(plainText)); }

    /**
     * Appends a plain text to this component, colored using {@link ChatConfig#warningColor}.
     * @param plainText the plain text.
     * @return this.
     */
    public Chat thenWarning(Object plainText) { return then(warningText(plainText)); }

    /**
     * Appends a plain text to this component, colored using {@link ChatConfig#successColor}.
     * @param plainText the plain text.
     * @return this.
     */
    public Chat thenSuccess(Object plainText) { return then(successText(plainText)); }

    /**
     * Appends a plain text to this component, colored using {@link ChatConfig#failureColor}.
     * @param plainText the plain text.
     * @return this.
     */
    public Chat thenFailure(Object plainText) { return then(failureText(plainText)); }

    /**
     * Appends a plain text to this component, colored using {@link ChatConfig#dataColor}.
     * @param plainText the plain text.
     * @return this.
     */
    public Chat thenData(Object plainText) { return then(dataText(plainText)); }

    /**
     * Appends a plain text to this component, colored using {@link ChatConfig#decorationColor}.
     * @param plainText the plain text.
     * @return this.
     */
    public Chat thenDecoration(Object plainText) { return then(decorationText(plainText)); }

    /**
     * Appends a component with the provided legacy text as its main text content, and colored in white in case there is
     * no color on the generated parent component.
     * @param legacyText the legacy text.
     * @return this.
     */
    public Chat thenPlayerName(String legacyText) { return then(playerNameText(legacyText)); }

    /**
     * Appends the provided Component, coloring it in white in case there is no color defined. If the provided component
     * is an instance of Chat, its content will be duplicated, and the provided one will be untouched.
     * @param comp the component.
     * @return this.
     */
    public Chat thenPlayerName(ComponentLike comp) { return then(playerNameComponent(comp)); }

    /**
     * Appends a component consisting of a new line.
     * @return this.
     */
    public Chat thenNewLine() { return then(Component.newline()); }

    /**
     * Appends a component with the provided legacy text as its content, using the section {@code "§"} character.
     * @param legacyText the legacy text that uses the {@code "§"} character.
     * @return this.
     */
    public Chat thenLegacyText(Object legacyText) { return then(legacyText(legacyText)); }

    /**
     * Appends a component with the provided legacy text as its content, using the ampersand {@code "&"} character.
     * @param legacyText the legacy text that uses the {@code "&"} character.
     * @return this.
     */
    public Chat thenLegacyAmpersandText(Object legacyText) { return then(legacyAmpersandText(legacyText)); }

    /**
     * Appends a component with the provided MiniMessage text as its content.
     * @param miniMessageText the MiniMessage text.
     * @return this.
     */
    public Chat thenMiniMessage(String miniMessageText) { return then(miniMessageText(miniMessageText)); }

    /**
     * Appends a component with the provided translation key and parameters.
     * @param key the translation key.
     * @param with the translation parameters.
     * @return this.
     */
    public Chat thenTranslation(String key, Object... with) { return then(translation(key, with)); }

    /**
     * Appends a component with the provided keybinding.
     * @param key the keybinding to display.
     * @return this.
     */
    public Chat thenKeyBind(String key) { return then(keybind(key)); }

    /**
     * Appends a component with the provided score name and objective.
     * @param name the score name.
     * @param objective the score objective.
     * @return this.
     */
    public Chat thenScore(String name, String objective) { return then(score(name, objective)); }


    /**
     * Appends a component that leads to a URL when clicked.
     * @param inner the component to make clickable.
     * @param url the target url. Must start with {@code "http://"} or {@code "https://"}.
     * @param hover the content to display when hovering the component.
     * @return this.
     */
    public Chat thenClickableURL(ComponentLike inner, String url, HoverEventSource<?> hover) { return then(clickableURL(inner, url, hover)); }

    /**
     * Appends a component that leads to a URL when clicked.
     * <p>
     * When hovered, the component will display the url. To customize the hover content, use
     * {@link #thenClickableURL(ComponentLike, String, HoverEventSource)}.
     * @param inner the component to make clickable.
     * @param url the target url. Must start with {@code "http://"} or {@code "https://"}.
     * @return this.
     */
    public Chat thenClickableURL(ComponentLike inner, String url) { return then(clickableURL(inner, url)); }

    /**
     * Appends a component that leads to a URL when clicked.
     * <p>
     * The text on which to click will be the URL itself. To configure the clicked text, use
     * {@link #thenClickableURL(ComponentLike, String, HoverEventSource)}.
     * @param url the target url. Must start with {@code "http://"} or {@code "https://"}.
     * @param hover the content to display when hovering the component.
     * @return this.
     */
    public Chat thenClickableURL(String url, HoverEventSource<?> hover) { return then(clickableURL(url, hover)); }

    /**
     * Appends a component that leads to a URL when clicked.
     * <p>
     * The text on which to click will be the URL itself. To configure the clicked text, use
     * {@link #thenClickableURL(ComponentLike, String)}.
     * <p>
     * When hovered, the component will display the url. To customize the hover content, use
     * {@link #thenClickableURL(String, HoverEventSource)}.
     * @param url the target url. Must start with {@code "http://"} or {@code "https://"}.
     * @return this.
     */
    public Chat thenClickableURL(String url) { return then(clickableURL(url)); }


    /**
     * Appends a component that runs a command when clicked.
     * @param inner the component to make clickable.
     * @param cmdWithSlash the command to run. Must start with {@code "/"}.
     * @param hover the content to display when hovering the component.
     * @return this.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public Chat thenClickableCommand(ComponentLike inner, String cmdWithSlash, HoverEventSource<?> hover) { return then(clickableCommand(inner, cmdWithSlash, hover)); }

    /**
     * Appends a component that runs a command when clicked.
     * <p>
     * When hovered, the component will display the command itself. To customize the hover content, use
     * {@link #thenClickableCommand(ComponentLike, String, HoverEventSource)}.
     * @param inner the component to make clickable.
     * @param cmdWithSlash the command to run. Must start with {@code "/"}.
     * @return this.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public Chat thenClickableCommand(ComponentLike inner, String cmdWithSlash) { return then(clickableCommand(inner, cmdWithSlash)); }

    /**
     * Appends a component that runs a command when clicked.
     * <p>
     * The text on which to click will be the command itself. To configure the clicked text, use
     * {@link #thenClickableCommand(ComponentLike, String, HoverEventSource)}.
     * @param cmdWithSlash the command to run. Must start with {@code "/"}.
     * @param hover the content to display when hovering the component.
     * @return this.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public Chat thenClickableCommand(String cmdWithSlash, HoverEventSource<?> hover) { return then(clickableCommand(cmdWithSlash, hover)); }

    /**
     * Appends a component that runs a command when clicked.
     * <p>
     * The text on which to click will be the command itself. To configure the clicked text, use
     * {@link #thenClickableCommand(ComponentLike, String)}.
     * <p>
     * When hovered, the component will display the command itself. To customize the hover content, use
     * {@link #thenClickableCommand(String, HoverEventSource)}.
     * @param cmdWithSlash the command to run. Must start with {@code "/"}.
     * @return this.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public Chat thenClickableCommand(String cmdWithSlash) { return then(clickableCommand(cmdWithSlash)); }


    /**
     * Appends a component that pre-fill the chat box with a command when clicked.
     * @param inner the component to make clickable.
     * @param cmdWithSlash the command to suggest. Must start with {@code "/"}.
     * @param hover the content to display when hovering the component.
     * @return this.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public Chat thenCommandSuggest(ComponentLike inner, String cmdWithSlash, HoverEventSource<?> hover) { return then(clickableSuggest(inner, cmdWithSlash, hover)); }

    /**
     * Appends a component that pre-fill the chat box with a command when clicked.
     * <p>
     * When hovered, the component will display the command itself. To customize the hover content, use
     * {@link #thenCommandSuggest(ComponentLike, String, HoverEventSource)}.
     * @param inner the component to make clickable.
     * @param cmdWithSlash the command to suggest. Must start with {@code "/"}.
     * @return this.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public Chat thenCommandSuggest(ComponentLike inner, String cmdWithSlash) { return then(clickableSuggest(inner, cmdWithSlash)); }

    /**
     * Appends a component that pre-fill the chat box with a command when clicked.
     * <p>
     * The text on which to click will be the command itself. To configure the clicked text, use
     * {@link #thenCommandSuggest(ComponentLike, String, HoverEventSource)}.
     * @param cmdWithSlash the command to suggest. Must start with {@code "/"}.
     * @param hover the content to display when hovering the component.
     * @return this.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public Chat thenCommandSuggest(String cmdWithSlash, HoverEventSource<?> hover) { return then(clickableSuggest(cmdWithSlash, hover)); }

    /**
     * Appends a component that pre-fill the chat box with a command when clicked.
     * <p>
     * The text on which to click will be the command itself. To configure the clicked text, use
     * {@link #thenCommandSuggest(ComponentLike, String)}.
     * <p>
     * When hovered, the component will display the command itself. To customize the hover content, use
     * {@link #thenCommandSuggest(String, HoverEventSource)}.
     * @param cmdWithSlash the command to suggest. Must start with {@code "/"}.
     * @return this.
     * @throws IllegalArgumentException if {@code commandWithSlash} does not start with a {@code "/"}.
     */
    public Chat thenCommandSuggest(String cmdWithSlash) { return then(clickableSuggest(cmdWithSlash)); }


    /**
     * Appends a component filling a chat line with the configured decoration character and
     * color and a left-aligned text.
     * @param leftText the text aligned to the left.
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a left-aligned text.
     */
    public Chat thenLeftText(ComponentLike leftText) { return then(leftText(leftText, console)); }

    /**
     * Appends a component filling a chat line with the configured decoration character and
     * color and a left-aligned text.
     * @param leftText the text aligned to the left.
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a left-aligned text.
     * @deprecated uses Bungeecord chat API.
     */
    @Deprecated
    public Chat thenLeftText(BaseComponent leftText) { return thenLeftText(chatComponent(leftText)); }

    /**
     * Appends a component filling a chat line with the configured decoration character and
     * color and a right-aligned text.
     * @param rightText the text aligned to the right.
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a right-aligned text.
     */
    public Chat thenRightText(ComponentLike rightText) { return then(rightText(rightText, console)); }

    /**
     * Appends a component filling a chat line with the configured decoration character and
     * color and a right-aligned text.
     * @param rightText the text aligned to the right.
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a right-aligned text.
     * @deprecated uses Bungeecord chat API.
     */
    @Deprecated
    public Chat thenRightText(BaseComponent rightText) { return thenRightText(chatComponent(rightText)); }

    /**
     * Appends a component filling a chat line with the configured decoration character and
     * color and a centered text.
     * @param centerText the text aligned to the center.
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a centered text.
     */
    public Chat thenCenterText(ComponentLike centerText) {
        return then(centerText(centerText, console));
    }

    /**
     * Appends a component filling a chat line with the configured decoration character and
     * color and a centered text.
     * @param centerText the text aligned to the center.
     * @return a new {@link FormatableChat} filling a chat line with the configured decoration character
     *         and color and a centered text.
     * @deprecated uses Bungeecord chat API.
     */
    @Deprecated
    public Chat thenCenterText(BaseComponent centerText) {
        return thenCenterText(chatComponent(centerText));
    }

    /**
     * Appends a component filling a chat line with the configured decoration character and color.
     * @return a new {@link FormatableChat} filling a chat line with a decoration character and color.
     */
    public Chat thenFilledLine() { return then(filledLine(console)); }











    /**
     * A {@link Chat} that can be formatted.
     * <p>
     * The purpose of subclassing {@link Chat} is to avoid ambiguity with the way the Bungee chat component builder works.
     * Here is an example of to use their builder (from
     * <a href="https://www.spigotmc.org/wiki/the-chat-component-api/#the-component-builder-api">the Spigot wiki</a>):
     * <pre>{@code
     * BaseComponent[] component = new ComponentBuilder("Hello ").color(ChatColor.RED)
     *         .append("world").color(ChatColor.DARK_RED).bold(true)
     *         .append("!").color(ChatColor.RED)
     *         .create();
     * }</pre>
     * Here, when you call a formatting method (like {@code bold(boolean)} or {@code color(ChatColor)}) after the
     * {@code append(String)} method, the formatting apply to the last subcomponent appended.
     * <p>
     * In our design, we want the formatting to apply to the currently built component, not the last appended one.
     * The purpose is to make the component structure clearer and have better control of the formatting over the
     * component hierarchy.
     * Here is the equivalent of the above code, with the {@link Chat} API:
     * <pre>{@code
     * Chat component = Chat.text("Hello ").red()
     *         .then(Chat.text("world").darkRed().bold())
     *         .thenText("!"); // short for .then(Chat.text("!"))
     *         // the red color for "!" is not needed because the parent component is already red.
     * }</pre>
     * When calling {@link #then(Component) #then(...)} on a {@link FormatableChat}, the method returns itself, cast
     * to {@link Chat}, to prevent future formatting (that the programmer would think it formats the previously appended
     * subcomponent). If the formatting of the currently built component is needed, since {@link Chat} is a sealed
     * class which only subclass is {@link FormatableChat}, you can cast the builder, and use the format methods again.
     * <pre>{@code
     * Chat component = Chat.text("Hello ").red()
     *         .then(Chat.text("world").darkRed().bold())
     *         .thenText("!");
     * // ok now I want to underline everything:
     * ((FormatableChat)component).underlined(); // this will not format only the last appended text.
     * }</pre>
     */
    public static final class FormatableChat extends Chat {
        /* package */ FormatableChat(ComponentBuilder<?, ?> c) {
            super(c);
        }


        /**
         * Configure if this component will be rendered on console or not.
         * @param c true for console, false for game UI.
         * @return this.
         */
        public FormatableChat console(boolean c) { console = c; return this; }
        /**
         * Configure the width of the line.
         * @param w the width to consider when rendering the line. In pixel for game UI rendering, n character for
         *                 console rendering.
         * @return this.
         */
        public FormatableChat maxWidth(int w) { maxWidth = w; return this; }

        /**
         * Sets the color of this component.
         * @param c the color.
         * @return this.
         */
        public FormatableChat color(TextColor c) { builder.color(c); return this; }
        /**
         * Sets the color of this component.
         * @param c the color.
         * @return this.
         */
        public FormatableChat color(ChatColor c) { return color(c == null ? null : TextColor.color(c.getColor().getRGB())); }
        /**
         * Sets the color of this component.
         * @param c the color.
         * @return this.
         */
        public FormatableChat color(Color c) { return color(c == null ? null : TextColor.color(c.getRGB())); }
        /**
         * Sets the color of this component.
         * @param c the color.
         * @return this.
         */
        public FormatableChat color(String c) { return color(c == null ? null : ChatColor.of(c)); }


        /**
         * Sets the color of this component to {@link NamedTextColor#BLACK}.
         * @return this.
         */
        public FormatableChat black() { return color(NamedTextColor.BLACK); }
        /**
         * Sets the color of this component to {@link NamedTextColor#DARK_BLUE}.
         * @return this.
         */
        public FormatableChat darkBlue() { return color(NamedTextColor.DARK_BLUE); }
        /**
         * Sets the color of this component to {@link NamedTextColor#DARK_GREEN}.
         * @return this.
         */
        public FormatableChat darkGreen() { return color(NamedTextColor.DARK_GREEN); }
        /**
         * Sets the color of this component to {@link NamedTextColor#DARK_AQUA}.
         * @return this.
         */
        public FormatableChat darkAqua() { return color(NamedTextColor.DARK_AQUA); }
        /**
         * Sets the color of this component to {@link NamedTextColor#DARK_RED}.
         * @return this.
         */
        public FormatableChat darkRed() { return color(NamedTextColor.DARK_RED); }
        /**
         * Sets the color of this component to {@link NamedTextColor#DARK_PURPLE}.
         * @return this.
         */
        public FormatableChat darkPurple() { return color(NamedTextColor.DARK_PURPLE); }
        /**
         * Sets the color of this component to {@link NamedTextColor#GOLD}.
         * @return this.
         */
        public FormatableChat gold() { return color(NamedTextColor.GOLD); }
        /**
         * Sets the color of this component to {@link NamedTextColor#GRAY}.
         * @return this.
         */
        public FormatableChat gray() { return color(NamedTextColor.GRAY); }
        /**
         * Sets the color of this component to {@link NamedTextColor#DARK_GRAY}.
         * @return this.
         */
        public FormatableChat darkGray() { return color(NamedTextColor.DARK_GRAY); }
        /**
         * Sets the color of this component to {@link NamedTextColor#BLUE}.
         * @return this.
         */
        public FormatableChat blue() { return color(NamedTextColor.BLUE); }
        /**
         * Sets the color of this component to {@link NamedTextColor#GREEN}.
         * @return this.
         */
        public FormatableChat green() { return color(NamedTextColor.GREEN); }
        /**
         * Sets the color of this component to {@link NamedTextColor#AQUA}.
         * @return this.
         */
        public FormatableChat aqua() { return color(NamedTextColor.AQUA); }
        /**
         * Sets the color of this component to {@link NamedTextColor#RED}.
         * @return this.
         */
        public FormatableChat red() { return color(NamedTextColor.RED); }
        /**
         * Sets the color of this component to {@link NamedTextColor#LIGHT_PURPLE}.
         * @return this.
         */
        public FormatableChat lightPurple() { return color(NamedTextColor.LIGHT_PURPLE); }
        /**
         * Sets the color of this component to {@link NamedTextColor#YELLOW}.
         * @return this.
         */
        public FormatableChat yellow() { return color(NamedTextColor.YELLOW); }
        /**
         * Sets the color of this component to {@link NamedTextColor#WHITE}.
         * @return this.
         */
        public FormatableChat white() { return color(NamedTextColor.WHITE); }


        /**
         * Sets the color of this component to {@link ChatConfig#successColor}.
         * @return this.
         */
        public FormatableChat successColor() { return color(ChatConfig.successColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#failureColor}.
         * @return this.
         */
        public FormatableChat failureColor() { return color(ChatConfig.failureColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#infoColor}.
         * @return this.
         */
        public FormatableChat infoColor() { return color(ChatConfig.infoColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#warningColor}.
         * @return this.
         */
        public FormatableChat warningColor() { return color(ChatConfig.warningColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#dataColor}.
         * @return this.
         */
        public FormatableChat dataColor() { return color(ChatConfig.dataColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#decorationColor}.
         * @return this.
         */
        public FormatableChat decorationColor() { return color(ChatConfig.decorationColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#urlColor}.
         * @return this.
         */
        public FormatableChat urlColor() { return color(ChatConfig.urlColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#commandColor}.
         * @return this.
         */
        public FormatableChat commandColor() { return color(ChatConfig.commandColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#highlightedCommandColor}.
         * @return this.
         */
        public FormatableChat highlightedCommandColor() { return color(ChatConfig.highlightedCommandColor); }
        /**
         * Sets the color of this component to {@link ChatConfig#broadcastColor}.
         * @return this.
         */
        public FormatableChat broadcastColor() { return color(ChatConfig.broadcastColor); }


        private FormatableChat setStyle(Consumer<Style.Builder> styleOp) { builder.style(styleOp); return this; }
        private FormatableChat setDecoration(TextDecoration deco, Boolean state) {
            return setStyle(b -> b.decoration(deco, State.byBoolean(state)));
        }


        /**
         * Sets the bold status of this component.
         * @param b true to enable, false to disable, or null to inherit from parent.
         * @return this.
         */
        public FormatableChat bold(Boolean b) { return setDecoration(TextDecoration.BOLD, b); }
        /**
         * Enables the bold status of this component.
         * @return this.
         */
        public FormatableChat bold() { return bold(true); }
        /**
         * Sets the italic status of this component.
         * @param i true to enable, false to disable, or null to inherit from parent.
         * @return this.
         */
        public FormatableChat italic(Boolean i) { return setDecoration(TextDecoration.ITALIC, i); }
        /**
         * Enables the italic status of this component.
         * @return this.
         */
        public FormatableChat italic() { return italic(true); }
        /**
         * Sets the underlined status of this component.
         * @param u true to enable, false to disable, or null to inherit from parent.
         * @return this.
         */
        public FormatableChat underlined(Boolean u) { return setDecoration(TextDecoration.UNDERLINED, u); }
        /**
         * Enables the underlined status of this component.
         * @return this.
         */
        public FormatableChat underlined() { return underlined(true); }
        /**
         * Sets the strikethrough status of this component.
         * @param s true to enable, false to disable, or null to inherit from parent.
         * @return this.
         */
        public FormatableChat strikethrough(Boolean s) { return setDecoration(TextDecoration.STRIKETHROUGH, s); }
        /**
         * Enables the strikethrough status of this component.
         * @return this.
         */
        public FormatableChat strikethrough() { return strikethrough(true); }
        /**
         * Sets the obfuscated status of this component.
         * @param o true to enable, false to disable, or null to inherit from parent.
         * @return this.
         */
        public FormatableChat obfuscated(Boolean o) { return setDecoration(TextDecoration.OBFUSCATED, o); }
        /**
         * Enables the obfuscated status of this component.
         * @return this.
         */
        public FormatableChat obfuscated() { return obfuscated(true); }


        /**
         * Sets the font of this component.
         * @param f the font namespaced key.
         * @return this.
         */
        public FormatableChat font(Key f) { return setStyle(s -> s.font(f)); }


        /**
         * Configure this component to insert the specified text at the cursor position when clicked.
         * @param i the text to insert.
         * @return this.
         */
        public FormatableChat shiftClickInsertion(String i) { builder.insertion(i); return this; }


        /**
         * Configure this component’s click event.
         * @param e the {@link ClickEvent}.
         * @return this.
         */
        private FormatableChat click(ClickEvent e) { builder.clickEvent(e); return this; }
        /**
         * Configure this component to execute the specified command when clicked.
         * @param cmdWithSlash the command to execute.
         * @return this.
         */
        public FormatableChat clickCommand(String cmdWithSlash) { return click(ClickEvent.runCommand(cmdWithSlash)); }
        /**
         * Configure this component to insert in the chat-box the specified command when clicked.
         * @param cmdWithSlash the command to suggest.
         * @return this.
         */
        public FormatableChat clickSuggest(String cmdWithSlash) { return click(ClickEvent.suggestCommand(cmdWithSlash)); }
        /**
         * Configure this component to copy into clipboard the specified text when clicked.
         * @param value the text to copy.
         * @return this.
         */
        public FormatableChat clickClipboard(String value) { return click(ClickEvent.copyToClipboard(value)); }
        /**
         * Configure this component to open the specified URL when clicked.
         * @param url the URL to open.
         * @return this.
         */
        public FormatableChat clickURL(String url) { return click(ClickEvent.openUrl(url)); }
        /**
         * Configure this component to change the page of the opened book when clicked.
         * @param page the page to go to.
         * @return this.
         */
        public FormatableChat clickBookPage(int page) { return click(ClickEvent.changePage(page)); }


        /**
         * Configure this component’s hover event.
         * @param e the {@link HoverEventSource}.
         * @return this.
         */
        public FormatableChat hover(HoverEventSource<?> e) { builder.hoverEvent(e); return this; }
        /**
         * Configure this component to show the provided component when hovered.
         * @param v the component to show.
         * @return this.
         */
        public FormatableChat hover(Component v) { return hover((HoverEventSource<Component>) v); }
        /**
         * Configure this component to show the provided component when hovered.
         * @param v the component to show.
         * @return this.
         */
        public FormatableChat hover(Chat v) { return hover((HoverEventSource<Component>) v); }
        /**
         * Configure this component to show the provided component when hovered.
         * @param v the component to show.
         * @return this.
         */
        public FormatableChat hover(ComponentLike v) { return hover(v.asComponent()); }
        /**
         * Configure this component to show the provided component when hovered.
         * @param v the component to show.
         * @return this.
         */
        public FormatableChat hover(BaseComponent v) { return hover(toAdventure(v)); }
        /**
         * Configure this component to show the provided component when hovered.
         * @param v the component to show.
         * @return this.
         */
        public FormatableChat hover(BaseComponent[] v) { return hover(toAdventure(v)); }
        /**
         * Configure this component to show the provided legacy text when hovered.
         * @param legacyText the legacy text to show.
         * @return this.
         */
        public FormatableChat hover(String legacyText) { return hover(legacyText(legacyText)); }

    }











    @Override
    public boolean equals(Object obj) {
        return obj instanceof Chat c
                && builder.equals(c.builder);
    }

    @Override
    public int hashCode() {
        return getAdv().hashCode();
    }

    @Override
    public String toString() {
        return getPlainText();
    }




    /* package */ static ComponentLike filterObjToComponentLike(Object v) {
        return switch (v) {
            case BaseComponent[] baseComponents -> toAdventure(baseComponents);
            case BaseComponent baseComponent -> toAdventure(baseComponent);
            case ComponentLike componentLike -> componentLike;
            case null, default -> Component.text(Objects.toString(v));
        };
    }

    /* package */ static ComponentLike[] filterObjToComponentLike(Object[] values) {
        if (values == null)
            return null;
        ComponentLike[] ret = new ComponentLike[values.length];
        for (int i = 0; i < values.length; i++) {
            ret[i] = filterObjToComponentLike(values[i]);
        }
        return ret;
    }


    /* package */ static TranslationArgumentLike[] filterObjToTranslationArgumentLike(Object[] values) {
        if (values == null)
            return null;
        TranslationArgumentLike[] ret = new TranslationArgumentLike[values.length];
        for (int i = 0; i < values.length; i++) {
            Object v = values[i];
            if (v instanceof Number n)
                ret[i] = TranslationArgument.numeric(n);
            else if (v instanceof Boolean b)
                ret[i] = TranslationArgument.bool(b);
            else
                ret[i] = TranslationArgument.component(filterObjToComponentLike(values[i]));
        }
        return ret;
    }


    /**
     * Converts the Bungee {@link BaseComponent} array into Adventure {@link Component}.
     * @param components the Bungee {@link BaseComponent} array.
     * @return a {@link Component}.
     */
    public static Component toAdventure(BaseComponent[] components) {
        return BungeeComponentSerializer.get().deserialize(components);
    }
    /**
     * Converts the Bungee {@link BaseComponent} into Adventure {@link Component}.
     * @param component the Bungee {@link BaseComponent}.
     * @return a {@link Component}.
     */
    public static Component toAdventure(BaseComponent component) {
        return toAdventure(new BaseComponent[] { component });
    }

    /**
     * Converts the Adventure {@link Component} into Bungee {@link BaseComponent} array.
     * @param component the Adventure {@link Component}.
     * @return a {@link BaseComponent} array.
     */
    public static BaseComponent[] toBungeeArray(Component component) {
        return BungeeComponentSerializer.get().serialize(component);
    }
    /**
     * Converts the Adventure {@link Component} into Bungee {@link BaseComponent}.
     * @param component the Adventure {@link Component}.
     * @return a {@link BaseComponent}.
     */
    public static BaseComponent toBungee(Component component) {
        BaseComponent[] arr = toBungeeArray(component);
        return arr.length == 1 ? arr[0] : new net.md_5.bungee.api.chat.TextComponent(arr);
    }

    /**
     * Force the italic formatting to be set to false if it is not explicitly set in the component.
     * This is useful for item lores that defaults to italic in the game UI.
     * @param c the {@link Chat} in which to set the italic property if needed.
     * @return the provided {@link Chat} instance.
     */
    public static Chat italicFalseIfNotSet(Chat c) {
        c.builder.style(b -> {
            if (b.build().decoration(TextDecoration.ITALIC) == State.NOT_SET) {
                ((FormatableChat) c).italic(false);
            }
        });
        return c;
    }


}
