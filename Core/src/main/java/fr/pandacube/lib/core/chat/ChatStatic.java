package fr.pandacube.lib.core.chat;

import java.util.Objects;

import fr.pandacube.lib.core.chat.Chat.FormatableChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class ChatStatic {



	public static FormatableChat chatComponent(Component c) {
		return new FormatableChat(Chat.componentToBuilder(c));
	}

	public static FormatableChat chatComponent(BaseComponent c) {
		return new FormatableChat(Chat.componentToBuilder(Chat.toAdventure(c)));
	}
	
	public static FormatableChat chat() {
		return chatComponent(Component.empty());
	}
	
	public static FormatableChat chatComponent(BaseComponent[] c) {
		return chatComponent(Chat.toAdventure(c));
	}

	public static FormatableChat text(Object plainText) {
		return chatComponent(Component.text(Objects.toString(plainText)));
	}

	public static FormatableChat legacyText(Object legacyText) {
		return chatComponent(LegacyComponentSerializer.legacySection().deserialize(Objects.toString(legacyText)));
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

	public static FormatableChat playerNameComponent(Component c) {
		FormatableChat fc = chatComponent(c);
		if (c.color() == null)
			fc.white();
		return fc;
	}

	public static FormatableChat translation(String key, Object... with) {
		return chatComponent(Component.translatable(key, Chat.filterObjToComponentLike(with)));
	}

	/** @param key one of the values in {@link net.md_5.bungee.api.chat.Keybinds} */
	public static FormatableChat keybind(String key) {
		return chatComponent(Component.keybind(key));
	}

	public static FormatableChat score(String name, String objective) {
		return chatComponent(Component.score(name, objective));
	}
}
