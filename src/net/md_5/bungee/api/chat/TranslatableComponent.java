/*
 * Decompiled with CFR 0_114.
 */
package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class TranslatableComponent extends BaseComponent {
	private final ResourceBundle locales = ResourceBundle.getBundle("mojang-translations/en_US");
	private final Pattern format = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
	private String translate;
	private List<BaseComponent> with;

	public TranslatableComponent(TranslatableComponent original) {
		super(original);
		setTranslate(original.getTranslate());
		if (original.getWith() != null) {
			ArrayList<BaseComponent> temp = new ArrayList<BaseComponent>();
			for (BaseComponent baseComponent : original.getWith())
				temp.add(baseComponent.duplicate());
			setWith(temp);
		}
	}

	public /* varargs */ TranslatableComponent(String translate, Object... with) {
		setTranslate(translate);
		ArrayList<BaseComponent> temp = new ArrayList<BaseComponent>();
		for (Object w : with) {
			if (w instanceof String) {
				temp.add(new TextComponent((String) w));
				continue;
			}
			temp.add((BaseComponent) w);
		}
		setWith(temp);
	}

	@Override
	public BaseComponent duplicate() {
		return new TranslatableComponent(this);
	}

	public void setWith(List<BaseComponent> components) {
		for (BaseComponent component : components)
			component.parent = this;
		with = components;
	}

	public void addWith(String text) {
		this.addWith(new TextComponent(text));
	}

	public void addWith(BaseComponent component) {
		if (with == null) with = new ArrayList<BaseComponent>();
		component.parent = this;
		with.add(component);
	}

	@Override
	protected void toPlainText(StringBuilder builder) {
		String trans;
		try {
			trans = locales.getString(translate);
		} catch (MissingResourceException ex) {
			trans = translate;
		}
		Matcher matcher = format.matcher(trans);
		int position = 0;
		int i = 0;
		while (matcher.find(position)) {
			int pos = matcher.start();
			if (pos != position) builder.append(trans.substring(position, pos));
			position = matcher.end();
			String formatCode = matcher.group(2);
			switch (formatCode.charAt(0)) {
			case 'd':
			case 's': {
				String withIndex = matcher.group(1);
				with.get(withIndex != null ? Integer.parseInt(withIndex) - 1 : i++).toPlainText(builder);
				break;
			}
			case '%': {
				builder.append('%');
			}
			}
		}
		if (trans.length() != position) builder.append(trans.substring(position, trans.length()));
		super.toPlainText(builder);
	}

	@Override
	protected void toLegacyText(StringBuilder builder) {
		String trans;
		try {
			trans = locales.getString(translate);
		} catch (MissingResourceException e) {
			trans = translate;
		}
		Matcher matcher = format.matcher(trans);
		int position = 0;
		int i = 0;
		while (matcher.find(position)) {
			int pos = matcher.start();
			if (pos != position) {
				addFormat(builder);
				builder.append(trans.substring(position, pos));
			}
			position = matcher.end();
			String formatCode = matcher.group(2);
			switch (formatCode.charAt(0)) {
			case 'd':
			case 's': {
				String withIndex = matcher.group(1);
				with.get(withIndex != null ? Integer.parseInt(withIndex) - 1 : i++).toLegacyText(builder);
				break;
			}
			case '%': {
				addFormat(builder);
				builder.append('%');
			}
			}
		}
		if (trans.length() != position) {
			addFormat(builder);
			builder.append(trans.substring(position, trans.length()));
		}
		super.toLegacyText(builder);
	}

	private void addFormat(StringBuilder builder) {
		builder.append(getColor());
		if (isBold()) builder.append(ChatColor.BOLD);
		if (isItalic()) builder.append(ChatColor.ITALIC);
		if (isUnderlined()) builder.append(ChatColor.UNDERLINE);
		if (isStrikethrough()) builder.append(ChatColor.STRIKETHROUGH);
		if (isObfuscated()) builder.append(ChatColor.MAGIC);
	}

	public ResourceBundle getLocales() {
		return locales;
	}

	public Pattern getFormat() {
		return format;
	}

	public String getTranslate() {
		return translate;
	}

	public List<BaseComponent> getWith() {
		return with;
	}

	public void setTranslate(String translate) {
		this.translate = translate;
	}

	@Override
	public String toString() {
		return "TranslatableComponent(locales=" + getLocales() + ", format=" + getFormat() + ", translate="
				+ getTranslate() + ", with=" + getWith() + ")";
	}

	public TranslatableComponent() {}
}
