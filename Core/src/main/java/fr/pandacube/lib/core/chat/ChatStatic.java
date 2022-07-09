package fr.pandacube.lib.core.chat;

import java.util.Objects;

import fr.pandacube.lib.core.chat.Chat.FormatableChat;
import fr.pandacube.lib.core.util.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class ChatStatic {



	public static FormatableChat chatComponent(Component c) {
		return new FormatableChat(Chat.componentToBuilder(c));
	}

	public static FormatableChat chatComponent(BaseComponent c) {
		return new FormatableChat(Chat.componentToBuilder(Chat.toAdventure(c)));
	}

	public static FormatableChat chatComponent(Chat c) {
		return chatComponent(c.getAdv());
	}
	
	public static FormatableChat chat() {
		return new FormatableChat(Component.text());
	}
	
	public static FormatableChat chatComponent(BaseComponent[] c) {
		return chatComponent(Chat.toAdventure(c));
	}

	public static FormatableChat text(Object plainText) {
		if (plainText instanceof Chat) {
			Log.warning("Using Chat instance as plain text. Please use proper API method. I’ll properly use your Chat instance this time...", new Throwable());
			return (FormatableChat) plainText;
		}
		if (plainText instanceof Component) {
			Log.warning("Using Component instance as plain text. Please use proper API method. I’ll properly use your Component this time...", new Throwable());
			return chatComponent((Component) plainText);
		}
		return new FormatableChat(Component.text().content(Objects.toString(plainText)));
	}

	public static FormatableChat legacyText(Object legacyText) {
		if (legacyText instanceof Chat) {
			Log.warning("Using Chat instance as legacy text. Please use proper API method. I’ll properly use your Chat instance this time...", new Throwable());
			return (FormatableChat) legacyText;
		}
		if (legacyText instanceof Component) {
			Log.warning("Using Component instance as legacy text. Please use proper API method. I’ll properly use your Component this time...", new Throwable());
			return chatComponent((Component) legacyText);
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
		return new FormatableChat(Component.translatable()
				.key(key)
				.args(Chat.filterObjToComponentLike(with))
				);
	}

	public static FormatableChat keybind(String key) {
		return new FormatableChat(Component.keybind()
				.keybind(key)
				);
	}

	public static FormatableChat score(String name, String objective) {
		return new FormatableChat(Component.score()
				.name(name)
				.objective(objective)
				);
	}
	
	

}
