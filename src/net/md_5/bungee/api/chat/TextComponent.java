/*
 * Decompiled with CFR 0_114.
 */
package net.md_5.bungee.api.chat;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class TextComponent extends BaseComponent {
	private static final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
	private String text;

	public static BaseComponent[] fromLegacyText(String message) {
		ArrayList<TextComponent> components = new ArrayList<TextComponent>();
		StringBuilder builder = new StringBuilder();
		TextComponent component = new TextComponent();
		Matcher matcher = url.matcher(message);
		block8:
		for (int i = 0; i < message.length(); ++i) {
			TextComponent old;
			char c2 = message.charAt(i);
			if (c2 == '\u00a7') {
				ChatColor format;
				if ((c2 = message.charAt(++i)) >= 'A' && c2 <= 'Z') c2 = (char) (c2 + 32);
				if ((format = ChatColor.getByChar(c2)) == null) continue;
				if (builder.length() > 0) {
					old = component;
					component = new TextComponent(old);
					old.setText(builder.toString());
					builder = new StringBuilder();
					components.add(old);
				}
				switch (format) {
				case BOLD: {
					component.setBold(true);
					continue block8;
				}
				case ITALIC: {
					component.setItalic(true);
					continue block8;
				}
				case UNDERLINE: {
					component.setUnderlined(true);
					continue block8;
				}
				case STRIKETHROUGH: {
					component.setStrikethrough(true);
					continue block8;
				}
				case MAGIC: {
					component.setObfuscated(true);
					continue block8;
				}
				case RESET: {
					format = ChatColor.WHITE;
				}
				default:
				}
				component = new TextComponent();
				component.setColor(format);
				continue;
			}
			int pos = message.indexOf(32, i);
			if (pos == -1) pos = message.length();
			if (matcher.region(i, pos).find()) {
				if (builder.length() > 0) {
					old = component;
					component = new TextComponent(old);
					old.setText(builder.toString());
					builder = new StringBuilder();
					components.add(old);
				}
				old = component;
				component = new TextComponent(old);
				String urlString = message.substring(i, pos);
				component.setText(urlString);
				component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
						urlString.startsWith("http") ? urlString : "http://" + urlString));
				components.add(component);
				i += pos - i - 1;
				component = old;
				continue;
			}
			builder.append(c2);
		}
		if (builder.length() > 0) {
			component.setText(builder.toString());
			components.add(component);
		}
		if (components.isEmpty()) components.add(new TextComponent(""));
		return components.toArray(new BaseComponent[components.size()]);
	}

	public TextComponent(TextComponent textComponent) {
		super(textComponent);
		setText(textComponent.getText());
	}

	public /* varargs */ TextComponent(BaseComponent... extras) {
		setText("");
		setExtra(new ArrayList<BaseComponent>(Arrays.asList(extras)));
	}

	@Override
	public BaseComponent duplicate() {
		return new TextComponent(this);
	}

	@Override
	protected void toPlainText(StringBuilder builder) {
		builder.append(text);
		super.toPlainText(builder);
	}

	@Override
	protected void toLegacyText(StringBuilder builder) {
		builder.append(getColor());
		if (isBold()) builder.append(ChatColor.BOLD);
		if (isItalic()) builder.append(ChatColor.ITALIC);
		if (isUnderlined()) builder.append(ChatColor.UNDERLINE);
		if (isStrikethrough()) builder.append(ChatColor.STRIKETHROUGH);
		if (isObfuscated()) builder.append(ChatColor.MAGIC);
		builder.append(text);
		super.toLegacyText(builder);
	}

	@Override
	public String toString() {
		return String.format("TextComponent{text=%s, %s}", text, super.toString());
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@ConstructorProperties(value = { "text" })
	public TextComponent(String text) {
		this.text = text;
	}

	public TextComponent() {}

}
