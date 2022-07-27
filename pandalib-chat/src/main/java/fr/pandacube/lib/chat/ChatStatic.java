package fr.pandacube.lib.chat;

import java.util.Objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

import fr.pandacube.lib.chat.Chat.FormatableChat;

public abstract class ChatStatic {



	private static FormatableChat chatComponent(Component c) {
		return new FormatableChat(Chat.componentToBuilder(c));
	}

	public static FormatableChat chatComponent(BaseComponent c) {
		return new FormatableChat(Chat.componentToBuilder(Chat.toAdventure(c)));
	}

	public static FormatableChat chatComponent(ComponentLike c) {
		return chatComponent(c.asComponent());
	}
	
	public static FormatableChat chat() {
		return new FormatableChat(Component.text());
	}
	
	public static FormatableChat chatComponent(BaseComponent[] c) {
		return chatComponent(Chat.toAdventure(c));
	}


	/**
	 * Create a Chat instance with the provided plain text as its main text content.
	 *
	 * @param plainText the text to use as he content of the new Chat instance.
	 * @return a Chat instance with the provided text as its main text content.
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
	 * Create a Chat instance with the provided legacy text as its main text content.
	 *
	 * @param legacyText the text to use as he content of the new Chat instance.
	 * @return a Chat instance with the provided text as its main text content.
	 * @throws IllegalArgumentException If the {@code plainText} parameter is instance of {@link Chat} or
	 *         {@link Component}. The caller should use {@link #chatComponent(ComponentLike)}
	 *         instead.
	 */
	public static FormatableChat legacyText(Object legacyText) {
		if (legacyText instanceof ComponentLike) {
			throw new IllegalArgumentException("Expected any object except instance of " + ComponentLike.class + ". Received " + legacyText + ". Please use ChatStatic.chatComponent(ComponentLike) instead.");
		}
		return chatComponent(LegacyComponentSerializer.legacySection().deserialize(Objects.toString(legacyText)));
	}

	public static FormatableChat infoText(Object plainText) {
		return text(plainText).infoColor();
	}

	public static FormatableChat warningText(Object plainText) {
		return text(plainText).warningColor();
	}

	public static FormatableChat dataText(Object plainText) {
		return text(plainText).dataColor();
	}

	public static FormatableChat decorationText(Object plainText) {
		return text(plainText).decorationColor();
	}

	public static FormatableChat successText(Object plainText) {
		return text(plainText).successColor();
	}

	public static FormatableChat failureText(Object plainText) {
		return text(plainText).failureColor();
	}

	public static FormatableChat playerNameText(String legacyText) {
		FormatableChat fc = legacyText(legacyText);
		fc.builder.colorIfAbsent(NamedTextColor.WHITE);
		return fc;
	}

	public static FormatableChat playerNameComponent(Component c) {
		FormatableChat fc = chatComponent(c);
		fc.builder.colorIfAbsent(NamedTextColor.WHITE);
		return fc;
	}

	public static FormatableChat translation(String key, Object... with) {
		return new FormatableChat(Component.translatable().key(key).args(Chat.filterObjToComponentLike(with)));
	}

	public static FormatableChat keybind(String key) {
		return new FormatableChat(Component.keybind().keybind(key));
	}

	public static FormatableChat score(String name, String objective) {
		return new FormatableChat(Component.score().name(name).objective(objective));
	}

	public static FormatableChat clickableURL(Chat inner, String url, Chat hover) {
		Objects.requireNonNull(url, "url");
		if (inner == null)
			inner = text(url);
		if (hover == null)
			hover = text(ChatUtil.wrapInLimitedPixels(url, 240));
		return (FormatableChat) chat().clickURL(url).urlColor().hover(hover).then(inner);
	}

	public static FormatableChat clickableURL(Chat inner, String url) {
		return clickableURL(inner, url, null);
	}

	public static FormatableChat clickableURL(String url, Chat hover) {
		return clickableURL(null, url, hover);
	}

	public static FormatableChat clickableURL(String url) {
		return clickableURL(null, url, null);
	}

	public static FormatableChat clickableCommand(Chat inner, String commandWithSlash, Chat hover) {
		Objects.requireNonNull(commandWithSlash, "commandWithSlash");
		if (!commandWithSlash.startsWith("/"))
			throw new IllegalArgumentException("commandWithSlash must start with a '/' character.");
		if (inner == null)
			inner = text(commandWithSlash);
		if (hover == null)
			hover = text(ChatUtil.wrapInLimitedPixels(commandWithSlash, 240));
		return (FormatableChat) chat().clickCommand(commandWithSlash).commandColor().hover(hover).then(inner);
	}

	public static FormatableChat clickableCommand(Chat inner, String commandWithSlash) {
		return clickableCommand(inner, commandWithSlash, null);
	}

	public static FormatableChat clickableCommand(String commandWithSlash, Chat hover) {
		return clickableCommand(null, commandWithSlash, hover);
	}

	public static FormatableChat clickableCommand(String commandWithSlash) {
		return clickableCommand(null, commandWithSlash, null);
	}

	public static FormatableChat clickableSuggest(Chat inner, String commandWithSlash, Chat hover) {
		Objects.requireNonNull(commandWithSlash, "commandWithSlash");
		if (!commandWithSlash.startsWith("/"))
			throw new IllegalArgumentException("commandWithSlash must start with a '/' character.");
		if (inner == null)
			inner = text(commandWithSlash);
		if (hover == null)
			hover = text(ChatUtil.wrapInLimitedPixels(commandWithSlash, 240));
		return (FormatableChat) chat().clickSuggest(commandWithSlash).commandColor().hover(hover).then(inner);
	}

	public static FormatableChat clickableSuggest(Chat inner, String commandWithSlash) {
		return clickableSuggest(inner, commandWithSlash, null);
	}

	public static FormatableChat clickableSuggest(String commandWithSlash, Chat hover) {
		return clickableSuggest(null, commandWithSlash, hover);
	}

	public static FormatableChat clickableSuggest(String commandWithSlash) {
		return clickableSuggest(null, commandWithSlash, null);
	}






	public static Chat prefixedAndColored(ComponentLike message) {
		return Chat.chat()
				.broadcastColor()
				.then(Chat.getConfig().prefix.get())
				.then(message);
	}
	
	

}
