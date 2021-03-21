package fr.pandacube.lib.core.chat;

import java.awt.Color;
import java.util.UUID;
import java.util.function.Supplier;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;

public abstract class Chat extends ChatStatic {
	
	protected BaseComponent component;
	protected boolean console = false;
	
	public Chat(BaseComponent c) {
		component = c;
	}
	
	public BaseComponent get() {
		return component;
	}
	
	public BaseComponent[] getAsArray() {
		return new BaseComponent[] { component };
	}
	
	public String getLegacyText() {
		return component.toLegacyText();
	}
	
	
	
	
	
	
	public Chat then(BaseComponent subComponent) {
		// here are some optimizations to avoid unnecessary component nesting
		if (subComponent instanceof TextComponent) {
			TextComponent txtComp = (TextComponent) subComponent;
			if (!txtComp.hasFormatting() && (txtComp.getText() == null || txtComp.getText().isEmpty())) {
				// no need to add the provided component to the current component.
				// but eventual child component must be added
				if (txtComp.getExtra() != null) {
					for (BaseComponent child : txtComp.getExtra())
						then(child);
				}
				return this;
			}
		}
		component.addExtra(subComponent);
		return this;
	}
	public Chat then(Chat comp) { return then(comp.get()); }
	public Chat then(BaseComponent[] components) {
		if (components != null) {
			for (BaseComponent c : components) {
				then(c);
			}
		}
		return this;
	}
	
	public Chat thenText(Object plainText) { return then(text(plainText)); }
	
	public Chat thenInfo(Object plainText) { return then(infoText(plainText)); }
	
	public Chat thenSuccess(Object plainText) { return then(successText(plainText)); }
	
	public Chat thenFailure(Object plainText) { return then(failureText(plainText)); }
	
	public Chat thenData(Object plainText) { return then(dataText(plainText)); }
	
	public Chat thenDecoration(Object plainText) { return then(decorationText(plainText)); }
	
	public Chat thenPlayerName(String legacyText) { return then(playerNameText(legacyText)); }
	
	public Chat thenNewLine() { return thenText("\n"); }
	
	public Chat thenLegacyText(Object legacyText) { return then(legacyText(legacyText)); }
	
	public Chat thenTranslation(String key, Object... with) { return then(translation(key, with)); }
	
	public Chat thenKeyBind(String key) { return then(keybind(key)); }
	
	public Chat thenScore(String name, String objective, String value) { return then(score(name, objective, value)); }
	
	

	
	public Chat thenURLLink(Chat inner, String url, Chat hover) { return then(ChatUtil.createURLLink(inner, url, hover)); }
	public Chat thenURLLink(Chat inner, String url) { return thenURLLink(inner, url, null); }
	public Chat thenURLLink(String url, Chat hover) { return thenURLLink(text(url), url, hover); }
	public Chat thenURLLink(String url) { return thenURLLink(text(url), url); }
	
	public Chat thenCommandLink(Chat inner, String cmdWithSlash, Chat hover) { return then(ChatUtil.createCommandLink(inner, cmdWithSlash, hover)); }
	public Chat thenCommandLink(Chat inner, String cmdWithSlash) { return thenCommandLink(inner, cmdWithSlash, null); }
	public Chat thenCommandLink(String cmdWithSlash, Chat hover) { return thenCommandLink(text(cmdWithSlash), cmdWithSlash, hover); }
	public Chat thenCommandLink(String cmdWithSlash) { return thenCommandLink(text(cmdWithSlash), cmdWithSlash); }
	
	public Chat thenCommandSuggest(Chat inner, String cmdWithSlash, Chat hover) { return then(ChatUtil.createCommandSuggest(inner, cmdWithSlash, hover)); }
	public Chat thenCommandSuggest(Chat inner, String cmdWithSlash) { return thenCommandSuggest(inner, cmdWithSlash, null); }
	public Chat thenCommandSuggest(String cmdWithSlash, Chat hover) { return thenCommandSuggest(text(cmdWithSlash), cmdWithSlash, hover); }
	public Chat thenCommandSuggest(String cmdWithSlash) { return thenCommandSuggest(text(cmdWithSlash), cmdWithSlash); }
	
	
	

	/**
	 * Draws a full line with the default decoration char, colored with the default decoration color.
	 * @return this, for method chaining
	 */
	public Chat thenEmptyCharLine() {
		return then(ChatUtil.emptyLine(config.decorationChar, config.decorationColor, console));
	}
	
	
	/**
	 * Draws a full line with the default decoration char, colored with the default decoration color,
	 * and with the provided Chat left aligned on the line, default to the decoration color, and surrounded with 1 space on each side.
	 * @return this, for method chaining
	 */
	public Chat thenLeftTextCharLine(Chat leftText) {
		return then(ChatUtil.leftText(chat().decorationColor().thenText(" ").then(leftText).thenText(" ").get(), config.decorationChar,
				config.decorationColor, config.nbCharMargin, console));
	}
	/**
	 * Draws a full line with the default decoration char, colored with the default decoration color,
	 * and with the provided component left aligned on the line, default to the decoration color, and surrounded with 1 space on each side.
	 * @return this, for method chaining
	 */
	public Chat thenLeftTextCharLine(BaseComponent leftText) {
		return thenLeftTextCharLine(chatComponent(leftText));
	}
	
	
	/**
	 * Draws a full line with the default decoration char, colored with the default decoration color,
	 * and with the provided Chat right aligned on the line, default to the decoration color, and surrounded with 1 space on each side.
	 * @return this, for method chaining
	 */
	public Chat thenRightTextCharLine(Chat rightText) {
		return then(ChatUtil.rightText(chat().decorationColor().thenText(" ").then(rightText).thenText(" ").get(), config.decorationChar,
				config.decorationColor, config.nbCharMargin, console));
	}
	/**
	 * Draws a full line with the default decoration char, colored with the default decoration color,
	 * and with the provided component right aligned on the line, default to the decoration color, and surrounded with 1 space on each side.
	 * @return this, for method chaining
	 */
	public Chat thenRightTextCharLine(BaseComponent leftText) {
		return thenRightTextCharLine(chatComponent(leftText));
	}
	
	
	/**
	 * Draws a full line with the default decoration char, colored with the default decoration color,
	 * and with the provided Chat centered on the line, default to the decoration color, and surrounded with 1 space on each side.
	 * @return this, for method chaining
	 */
	public Chat thenCenterTextCharLine(Chat centerText) {
		return then(ChatUtil.centerText(chat().decorationColor().thenText(" ").then(centerText).thenText(" ").get(), config.decorationChar,
				config.decorationColor, console));
	}
	/**
	 * Draws a full line with the default decoration char, colored with the default decoration color,
	 * and with the provided component centered on the line, default to the decoration color, and surrounded with 1 space on each side.
	 * @return this, for method chaining
	 */
	public Chat thenCenterTextCharLine(BaseComponent leftText) {
		return thenCenterTextCharLine(chatComponent(leftText));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FormatableChat extends Chat {
		public FormatableChat(BaseComponent c) {
			super(c);
		}
		
		public FormatableChat console(boolean c) { console = c; return this; }
		
		public FormatableChat color(ChatColor c) { component.setColor(c); return this; }
		public FormatableChat color(Color c) { return color(ChatColor.of(c)); }
		public FormatableChat color(String c) { return color(ChatColor.of(c)); }
		
		public FormatableChat black() { return color(ChatColor.BLACK); }
		public FormatableChat darkBlue() { return color(ChatColor.DARK_BLUE); }
		public FormatableChat darkGreen() { return color(ChatColor.DARK_GREEN); }
		public FormatableChat darkAqua() { return color(ChatColor.DARK_AQUA); }
		public FormatableChat darkRed() { return color(ChatColor.DARK_RED); }
		public FormatableChat darkPurple() { return color(ChatColor.DARK_PURPLE); }
		public FormatableChat gold() { return color(ChatColor.GOLD); }
		public FormatableChat gray() { return color(ChatColor.GRAY); }
		public FormatableChat darkGray() { return color(ChatColor.DARK_GRAY); }
		public FormatableChat blue() { return color(ChatColor.BLUE); }
		public FormatableChat green() { return color(ChatColor.GREEN); }
		public FormatableChat aqua() { return color(ChatColor.AQUA); }
		public FormatableChat red() { return color(ChatColor.RED); }
		public FormatableChat lightPurple() { return color(ChatColor.LIGHT_PURPLE); }
		public FormatableChat yellow() { return color(ChatColor.YELLOW); }
		public FormatableChat white() { return color(ChatColor.WHITE); }

		public FormatableChat successColor() { return color(config.successColor); }
		public FormatableChat failureColor() { return color(config.failureColor); }
		public FormatableChat infoColor() { return color(config.infoColor); }
		public FormatableChat dataColor() { return color(config.dataColor); }
		public FormatableChat decorationColor() { return color(config.decorationColor); }
		
		public FormatableChat font(String f) { component.setFont(f); return this; }
		
		public FormatableChat bold(Boolean b) { component.setBold(b); return this; }
		public FormatableChat bold() { return bold(true); }
		
		public FormatableChat italic(Boolean i) { component.setItalic(i); return this; }
		public FormatableChat italic() { return italic(true); }
		
		public FormatableChat underlined(Boolean u) { component.setUnderlined(u); return this; }
		public FormatableChat underlined() { return underlined(true); }
	    
		public FormatableChat strikethrough(Boolean s) { component.setStrikethrough(s); return this; }
		public FormatableChat strikethrough() { return strikethrough(true); }
	    
		public FormatableChat obfuscated(Boolean o) { component.setObfuscated(o); return this; }
		public FormatableChat obfuscated() { return obfuscated(true); }
	    
		public FormatableChat shiftClickInsertion(String i) { component.setInsertion(i); return this; }
		
		private FormatableChat clickEvent(ClickEvent e) { component.setClickEvent(e); return this; }
		private FormatableChat clickEvent(ClickEvent.Action a, String v) { return clickEvent(new ClickEvent(a, v)); }
		public FormatableChat clickCommand(String cmdWithSlash) { return clickEvent(ClickEvent.Action.RUN_COMMAND, cmdWithSlash); }
		public FormatableChat clickSuggest(String cmdWithSlash) { return clickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmdWithSlash); }
		public FormatableChat clickClipboard(String value) { return clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value); }
		public FormatableChat clickURL(String url) { return clickEvent(ClickEvent.Action.OPEN_URL, url); }
		public FormatableChat clickBookPage(int page) { return clickEvent(ClickEvent.Action.CHANGE_PAGE, Integer.toString(page)); }

		private FormatableChat hoverEvent(HoverEvent e) { component.setHoverEvent(e); return this; }
		private FormatableChat hoverEvent(HoverEvent.Action a, Content v) { return hoverEvent(new HoverEvent(a, v)); }
		private FormatableChat hoverText(Text v) { return hoverEvent(HoverEvent.Action.SHOW_TEXT, v); }
		@SuppressWarnings("deprecation")
		public FormatableChat hoverText(BaseComponent v) {
			try {
				return hoverText(new Text( new BaseComponent[] {v}));
			} catch (NoSuchMethodError e) {
				return hoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {v}));
			}
		}
		public FormatableChat hoverText(Chat v) { return hoverText(v.get()); }
		public FormatableChat hoverText(String legacyText) { return hoverText(legacyText(legacyText)); }
		private FormatableChat hoverItem(Item v) { return hoverEvent(HoverEvent.Action.SHOW_ITEM, v); }
		/** @param id namespaced item id */
		public FormatableChat hoverItem(String id, int stackSize, ItemTag tag) { return hoverItem(new Item(id, stackSize, tag)); }
		/** @param id namespaced item id */
		public FormatableChat hoverItem(String id, int stackSize) { return hoverItem(id, stackSize, null); }
		/** @param id namespaced item id */
		public FormatableChat hoverItem(String id, ItemTag tag) { return hoverItem(id, -1, tag); }
		/** @param id namespaced item id */
		public FormatableChat hoverItem(String id) { return hoverItem(id, -1, null); }
		public FormatableChat hoverEntity(Entity e) { return hoverEvent(HoverEvent.Action.SHOW_ENTITY, e); }
		/** @param type namespaced entity type
		 * @param id cannot be null */
		public FormatableChat hoverEntity(String type, UUID id, BaseComponent displayName) { return hoverEntity(new Entity(type, id.toString(), displayName)); }
		/** @param type namespaced entity type
		 * @param id cannot be null */
		public FormatableChat hoverEntity(String type, UUID id) { return hoverEntity(type, id, null); }
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

	/* package */ static Object[] filterChatToBaseComponent(Object[] values) {
		if (values == null)
			return null;
		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			if (v instanceof Chat)
				values[i] = ((Chat) v).get();
		}
		return values;
	}
	
	
	
	
	
	
	protected static final Config config = new Config();
	
	public static Config getConfig() {
		return config;
	}
	
	public static class Config {
		public ChatColor decorationColor = ChatColor.YELLOW;
		public char decorationChar = '-';
		public int nbCharMargin = 1;
		public ChatColor successColor = ChatColor.GREEN;
		public ChatColor failureColor = ChatColor.RED;
		public ChatColor infoColor = ChatColor.GOLD;
		public ChatColor dataColor = ChatColor.GRAY;
		public ChatColor urlColor = ChatColor.GREEN;
		public ChatColor commandColor = ChatColor.GRAY;
		public ChatColor highlightedCommandColor = ChatColor.WHITE;
		public ChatColor broadcastColor = ChatColor.YELLOW;
		public Supplier<Chat> prefix;
		
	}
	
	
	
}
