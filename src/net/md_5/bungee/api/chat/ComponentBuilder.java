/*
 * Decompiled with CFR 0_114.
 */
package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public class ComponentBuilder {
	private TextComponent current;
	private final List<BaseComponent> parts = new ArrayList<>();

	public ComponentBuilder(ComponentBuilder original) {
		current = new TextComponent(original.current);
		for (BaseComponent baseComponent : original.parts)
			parts.add(baseComponent.duplicate());
	}

	public ComponentBuilder(String text) {
		current = new TextComponent(text);
	}

	public ComponentBuilder append(String text) {
		return this.append(text, FormatRetention.ALL);
	}

	public ComponentBuilder append(String text, FormatRetention retention) {
		parts.add(current);
		current = new TextComponent(current);
		current.setText(text);
		retain(retention);
		return this;
	}

	public ComponentBuilder color(ChatColor color) {
		current.setColor(color);
		return this;
	}

	public ComponentBuilder bold(boolean bold) {
		current.setBold(bold);
		return this;
	}

	public ComponentBuilder italic(boolean italic) {
		current.setItalic(italic);
		return this;
	}

	public ComponentBuilder underlined(boolean underlined) {
		current.setUnderlined(underlined);
		return this;
	}

	public ComponentBuilder strikethrough(boolean strikethrough) {
		current.setStrikethrough(strikethrough);
		return this;
	}

	public ComponentBuilder obfuscated(boolean obfuscated) {
		current.setObfuscated(obfuscated);
		return this;
	}

	public ComponentBuilder insertion(String insertion) {
		current.setInsertion(insertion);
		return this;
	}

	public ComponentBuilder event(ClickEvent clickEvent) {
		current.setClickEvent(clickEvent);
		return this;
	}

	public ComponentBuilder event(HoverEvent hoverEvent) {
		current.setHoverEvent(hoverEvent);
		return this;
	}

	public ComponentBuilder reset() {
		return retain(FormatRetention.NONE);
	}

	public ComponentBuilder retain(FormatRetention retention) {
		TextComponent previous = current;
		switch (retention) {
		case NONE: {
			current = new TextComponent(current.getText());
			break;
		}
		case ALL: {
			break;
		}
		case EVENTS: {
			current = new TextComponent(current.getText());
			current.setInsertion(previous.getInsertion());
			current.setClickEvent(previous.getClickEvent());
			current.setHoverEvent(previous.getHoverEvent());
			break;
		}
		case FORMATTING: {
			current.setClickEvent(null);
			current.setHoverEvent(null);
		}
		}
		return this;
	}

	public BaseComponent[] create() {
		parts.add(current);
		return parts.toArray(new BaseComponent[parts.size()]);
	}

	public static enum FormatRetention {
		NONE, FORMATTING, EVENTS, ALL;

		private FormatRetention() {}
	}

}
