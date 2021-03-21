package fr.pandacube.util.text_display;

import java.util.Objects;

import fr.pandacube.util.text_display.Chat.FormatableChat;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.Keybinds;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public abstract class ChatStatic {

	

	public static FormatableChat chatComponent(BaseComponent c) {
		return new FormatableChat(c);
	}
	
	public static FormatableChat chat() {
		return chatComponent(new TextComponent());
	}
	
	public static FormatableChat chatComponent(BaseComponent[] c) {
		return chatComponent(new TextComponent(c));
	}

	public static FormatableChat text(Object plainText) {
		return chatComponent(new TextComponent(Objects.toString(plainText)));
	}

	public static FormatableChat legacyText(Object legacyText) {
		return chatComponent(TextComponent.fromLegacyText(Objects.toString(legacyText), null));
	}

	public static FormatableChat infoText(Object plainText) {
		return text(plainText).infoColor();
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
		return legacyText(legacyText).white();
	}

	public static FormatableChat translation(String key, Object... with) {
		return chatComponent(new TranslatableComponent(key, Chat.filterChatToBaseComponent(with)));
	}

	/** @param key one of the values in {@link Keybinds}. */
	public static FormatableChat keybind(String key) {
		return chatComponent(new KeybindComponent(key));
	}

	public static FormatableChat score(String name, String objective, String value) {
		return chatComponent(new ScoreComponent(name, objective, value));
	}
}
