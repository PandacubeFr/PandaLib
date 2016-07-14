/*
 * Decompiled with CFR 0_114.
 */
package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public abstract class BaseComponent {
	BaseComponent parent;
	private ChatColor color;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private String insertion;
	private List<BaseComponent> extra;
	private ClickEvent clickEvent;
	private HoverEvent hoverEvent;

	BaseComponent(BaseComponent old) {
		setColor(old.getColorRaw());
		setBold(old.isBoldRaw());
		setItalic(old.isItalicRaw());
		setUnderlined(old.isUnderlinedRaw());
		setStrikethrough(old.isStrikethroughRaw());
		setObfuscated(old.isObfuscatedRaw());
		setInsertion(old.getInsertion());
		setClickEvent(old.getClickEvent());
		setHoverEvent(old.getHoverEvent());
		if (old.getExtra() != null) for (BaseComponent component : old.getExtra())
			this.addExtra(component.duplicate());
	}

	public abstract BaseComponent duplicate();

	public static /* varargs */ String toLegacyText(BaseComponent... components) {
		StringBuilder builder = new StringBuilder();
		for (BaseComponent msg : components)
			builder.append(msg.toLegacyText());
		return builder.toString();
	}

	public static /* varargs */ String toPlainText(BaseComponent... components) {
		StringBuilder builder = new StringBuilder();
		for (BaseComponent msg : components)
			builder.append(msg.toPlainText());
		return builder.toString();
	}

	public ChatColor getColor() {
		if (color == null) {
			if (parent == null) return ChatColor.WHITE;
			return parent.getColor();
		}
		return color;
	}

	public ChatColor getColorRaw() {
		return color;
	}

	public boolean isBold() {
		if (bold == null) return parent != null && parent.isBold();
		return bold;
	}

	public Boolean isBoldRaw() {
		return bold;
	}

	public boolean isItalic() {
		if (italic == null) return parent != null && parent.isItalic();
		return italic;
	}

	public Boolean isItalicRaw() {
		return italic;
	}

	public boolean isUnderlined() {
		if (underlined == null) return parent != null && parent.isUnderlined();
		return underlined;
	}

	public Boolean isUnderlinedRaw() {
		return underlined;
	}

	public boolean isStrikethrough() {
		if (strikethrough == null) return parent != null && parent.isStrikethrough();
		return strikethrough;
	}

	public Boolean isStrikethroughRaw() {
		return strikethrough;
	}

	public boolean isObfuscated() {
		if (obfuscated == null) return parent != null && parent.isObfuscated();
		return obfuscated;
	}

	public Boolean isObfuscatedRaw() {
		return obfuscated;
	}

	public void setExtra(List<BaseComponent> components) {
		for (BaseComponent component : components)
			component.parent = this;
		extra = components;
	}

	public void addExtra(String text) {
		this.addExtra(new TextComponent(text));
	}

	public void addExtra(BaseComponent component) {
		if (extra == null) extra = new ArrayList<BaseComponent>();
		component.parent = this;
		extra.add(component);
	}

	public boolean hasFormatting() {
		return color != null || bold != null || italic != null || underlined != null || strikethrough != null
				|| obfuscated != null || hoverEvent != null || clickEvent != null;
	}

	public String toPlainText() {
		StringBuilder builder = new StringBuilder();
		this.toPlainText(builder);
		return builder.toString();
	}

	void toPlainText(StringBuilder builder) {
		if (extra != null) for (BaseComponent e2 : extra)
			e2.toPlainText(builder);
	}

	public String toLegacyText() {
		StringBuilder builder = new StringBuilder();
		this.toLegacyText(builder);
		return builder.toString();
	}

	void toLegacyText(StringBuilder builder) {
		if (extra != null) for (BaseComponent e2 : extra)
			e2.toLegacyText(builder);
	}

	public void setColor(ChatColor color) {
		this.color = color;
	}

	public void setBold(Boolean bold) {
		this.bold = bold;
	}

	public void setItalic(Boolean italic) {
		this.italic = italic;
	}

	public void setUnderlined(Boolean underlined) {
		this.underlined = underlined;
	}

	public void setStrikethrough(Boolean strikethrough) {
		this.strikethrough = strikethrough;
	}

	public void setObfuscated(Boolean obfuscated) {
		this.obfuscated = obfuscated;
	}

	public void setInsertion(String insertion) {
		this.insertion = insertion;
	}

	public void setClickEvent(ClickEvent clickEvent) {
		this.clickEvent = clickEvent;
	}

	public void setHoverEvent(HoverEvent hoverEvent) {
		this.hoverEvent = hoverEvent;
	}

	@Override
	public String toString() {
		return "BaseComponent(color=" + (getColor()) + ", bold=" + bold + ", italic=" + italic + ", underlined="
				+ underlined + ", strikethrough=" + strikethrough + ", obfuscated=" + obfuscated + ", insertion="
				+ getInsertion() + ", extra=" + getExtra() + ", clickEvent=" + getClickEvent() + ", hoverEvent="
				+ getHoverEvent() + ")";
	}

	public BaseComponent() {}

	public String getInsertion() {
		return insertion;
	}

	public List<BaseComponent> getExtra() {
		return extra;
	}

	public ClickEvent getClickEvent() {
		return clickEvent;
	}

	public HoverEvent getHoverEvent() {
		return hoverEvent;
	}
}
