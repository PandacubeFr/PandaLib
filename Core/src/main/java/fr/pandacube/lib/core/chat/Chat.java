package fr.pandacube.lib.core.chat;

import java.awt.Color;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract sealed class Chat extends ChatStatic implements HoverEventSource<Component>, ComponentLike {
	
	protected ComponentBuilder<?, ?> builder;
	protected boolean console = false;
	
	/* package */ Chat(ComponentBuilder<?, ?> b) {
		Objects.requireNonNull(b, "Provided component builder must not be null");
		builder = b;
	}
	
	
	public Component getAdv() {
		return builder.build();
	}
	
	public BaseComponent get() {
		return toBungee(getAdv());
	}
	
	public BaseComponent[] getAsArray() {
		return toBungeeArray(getAdv());
	}
	
	private static final LegacyComponentSerializer LEGACY_SERIALIZER_BUNGEE_FIENDLY = LegacyComponentSerializer.builder()
			.hexColors()
			.useUnusualXRepeatedCharacterHexFormat()
			.build();
	public String getLegacyText() {
		return LEGACY_SERIALIZER_BUNGEE_FIENDLY.serialize(getAdv());
	}
	
	public String getPlainText() {
		return PlainTextComponentSerializer.plainText().serializeOr(getAdv(), "");
	}
	
	
	
	
	
	
	
	public Chat then(Component comp) {
		if (comp instanceof TextComponent) {
			TextComponent txtComp = (TextComponent) comp;
			if (!txtComp.hasStyling() && (txtComp.content() == null || txtComp.content().isEmpty())) {
				// no need to add the provided component to the current component.
				// but eventual child component must be added
				if (txtComp.children() != null && !txtComp.children().isEmpty()) {
					for (Component child : txtComp.children())
						then(child);
				}
				return this;
			}
		}
		builder.append(comp);
		return this;
	}
	public Chat then(BaseComponent subComponent) {
		return then(toAdventure(subComponent));
	}
	public Chat then(Chat comp) {
		return then(comp.getAdv());
	}
	public Chat then(BaseComponent[] components) {
		return then(toAdventure(components));
	}
	
	public Chat thenText(Object plainText) { return then(text(plainText)); }
	public Chat thenInfo(Object plainText) { return then(infoText(plainText)); }
	public Chat thenWarning(Object plainText) { return then(warningText(plainText)); }
	public Chat thenSuccess(Object plainText) { return then(successText(plainText)); }
	public Chat thenFailure(Object plainText) { return then(failureText(plainText)); }
	public Chat thenData(Object plainText) { return then(dataText(plainText)); }
	public Chat thenDecoration(Object plainText) { return then(decorationText(plainText)); }
	public Chat thenPlayerName(String legacyText) { return then(playerNameText(legacyText)); }
	public Chat thenPlayerName(Component comp) { return then(playerNameComponent(comp)); }
	public Chat thenNewLine() { return then(Component.newline()); }
	public Chat thenLegacyText(Object legacyText) { return then(legacyText(legacyText)); }
	public Chat thenTranslation(String key, Object... with) { return then(translation(key, with)); }
	public Chat thenKeyBind(String key) { return then(keybind(key)); }
	public Chat thenScore(String name, String objective) { return then(score(name, objective)); }
	
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
		return then(ChatUtil.leftText(chat().decorationColor().thenText(" ").then(leftText).thenText(" "), config.decorationChar,
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
		return then(ChatUtil.rightText(chat().decorationColor().thenText(" ").then(rightText).thenText(" "), config.decorationChar,
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
		return then(ChatUtil.centerText(chat().decorationColor().thenText(" ").then(centerText).thenText(" "), config.decorationChar,
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static final class FormatableChat extends Chat {
		/* package */ FormatableChat(ComponentBuilder<?, ?> c) {
			super(c);
		}
		
		public FormatableChat console(boolean c) { console = c; return this; }

		public FormatableChat color(TextColor c) { builder.color(c); return this; }
		public FormatableChat color(ChatColor c) { return color(c == null ? null : TextColor.color(c.getColor().getRGB())); }
		public FormatableChat color(Color c) { return color(c == null ? null : TextColor.color(c.getRGB())); }
		public FormatableChat color(String c) { return color(c == null ? null : ChatColor.of(c)); }
		
		public FormatableChat black() { return color(NamedTextColor.BLACK); }
		public FormatableChat darkBlue() { return color(NamedTextColor.DARK_BLUE); }
		public FormatableChat darkGreen() { return color(NamedTextColor.DARK_GREEN); }
		public FormatableChat darkAqua() { return color(NamedTextColor.DARK_AQUA); }
		public FormatableChat darkRed() { return color(NamedTextColor.DARK_RED); }
		public FormatableChat darkPurple() { return color(NamedTextColor.DARK_PURPLE); }
		public FormatableChat gold() { return color(NamedTextColor.GOLD); }
		public FormatableChat gray() { return color(NamedTextColor.GRAY); }
		public FormatableChat darkGray() { return color(NamedTextColor.DARK_GRAY); }
		public FormatableChat blue() { return color(NamedTextColor.BLUE); }
		public FormatableChat green() { return color(NamedTextColor.GREEN); }
		public FormatableChat aqua() { return color(NamedTextColor.AQUA); }
		public FormatableChat red() { return color(NamedTextColor.RED); }
		public FormatableChat lightPurple() { return color(NamedTextColor.LIGHT_PURPLE); }
		public FormatableChat yellow() { return color(NamedTextColor.YELLOW); }
		public FormatableChat white() { return color(NamedTextColor.WHITE); }

		public FormatableChat successColor() { return color(config.successColor); }
		public FormatableChat failureColor() { return color(config.failureColor); }
		public FormatableChat infoColor() { return color(config.infoColor); }
		public FormatableChat warningColor() { return color(config.warningColor); }
		public FormatableChat dataColor() { return color(config.dataColor); }
		public FormatableChat decorationColor() { return color(config.decorationColor); }
		public FormatableChat urlColor() { return color(config.urlColor); }
		public FormatableChat commandColor() { return color(config.commandColor); }
		public FormatableChat highlightedCommandColor() { return color(config.highlightedCommandColor); }
		public FormatableChat broadcastColor() { return color(config.broadcastColor); }
		
		private FormatableChat setStyle(Consumer<Style.Builder> styleOp) { builder.style(styleOp); return this; }
		
		private FormatableChat setDecoration(TextDecoration deco, Boolean state) {
			return setStyle(b -> b.decoration(deco, State.byBoolean(state)));
		}
		
		public FormatableChat bold(Boolean b) { return setDecoration(TextDecoration.BOLD, b); }
		public FormatableChat bold() { return bold(true); }
		
		public FormatableChat italic(Boolean i) { return setDecoration(TextDecoration.ITALIC, i); }
		public FormatableChat italic() { return italic(true); }
		
		public FormatableChat underlined(Boolean u) { return setDecoration(TextDecoration.UNDERLINED, u); }
		public FormatableChat underlined() { return underlined(true); }
	    
		public FormatableChat strikethrough(Boolean s) { return setDecoration(TextDecoration.STRIKETHROUGH, s); }
		public FormatableChat strikethrough() { return strikethrough(true); }
	    
		public FormatableChat obfuscated(Boolean o) { return setDecoration(TextDecoration.OBFUSCATED, o); }
		public FormatableChat obfuscated() { return obfuscated(true); }
		
		public FormatableChat font(Key f) { return setStyle(s -> s.font(f)); }
	    
		public FormatableChat shiftClickInsertion(String i) { builder.insertion(i); return this; }
		
		private FormatableChat click(ClickEvent e) { builder.clickEvent(e); return this; }
		public FormatableChat clickCommand(String cmdWithSlash) { return click(ClickEvent.runCommand(cmdWithSlash)); }
		public FormatableChat clickSuggest(String cmdWithSlash) { return click(ClickEvent.suggestCommand(cmdWithSlash)); }
		public FormatableChat clickClipboard(String value) { return click(ClickEvent.copyToClipboard(value)); }
		public FormatableChat clickURL(String url) { return click(ClickEvent.openUrl(url)); }
		public FormatableChat clickBookPage(int page) { return click(ClickEvent.changePage(page)); }

		public FormatableChat hover(HoverEventSource<?> e) { builder.hoverEvent(e); return this; }
		public FormatableChat hover(Chat v) { return hover(v.getAdv()); }
		public FormatableChat hover(BaseComponent v) { return hover(toAdventure(v)); }
		public FormatableChat hover(BaseComponent[] v) { return hover(toAdventure(v)); }
		public FormatableChat hover(String legacyText) { return hover(legacyText(legacyText)); }
		
	}
	
	
	
	
	
	
	@Override
	public @NonNull HoverEvent<Component> asHoverEvent(@NonNull UnaryOperator<Component> op) {
		return HoverEvent.showText(op.apply(getAdv()));
	}
	
	@Override
	public @NonNull Component asComponent() {
		return getAdv();
	}
	
	
	
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Chat))
			return false;
		return getAdv().equals(((Chat)obj).getAdv());
	}
	
	@Override
	public int hashCode() {
		return getAdv().hashCode();
	}
	
	@Override
	public String toString() {
		return getPlainText();
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

	/* package */ static ComponentLike[] filterObjToComponentLike(Object[] values) {
		if (values == null)
			return null;
		ComponentLike[] ret = new ComponentLike[values.length];
		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			if (v instanceof BaseComponent[])
				ret[i] = toAdventure((BaseComponent[]) v);
			else if (v instanceof BaseComponent)
				ret[i] = toAdventure((BaseComponent) v);
			else if (v instanceof ComponentLike)
				ret[i] = (ComponentLike) v;
			else
				ret[i] = Component.text(Objects.toString(v));
		}
		return ret;
	}
	

	public static Component toAdventure(BaseComponent[] components) {
		return BungeeComponentSerializer.get().deserialize(components);
	}
	public static Component toAdventure(BaseComponent component) {
		return toAdventure(new BaseComponent[] { component });
	}
	
	public static BaseComponent[] toBungeeArray(Component component) {
		return BungeeComponentSerializer.get().serialize(component);
	}
	public static BaseComponent toBungee(Component component) {
		BaseComponent[] arr = toBungeeArray(component);
		return arr.length == 1 ? arr[0] : new net.md_5.bungee.api.chat.TextComponent(arr);
	}
	
	
	public static ComponentBuilder<?, ?> componentToBuilder(Component c) {
		ComponentBuilder<?, ?> builder;
		if (c instanceof TextComponent) {
			builder = Component.text()
					.content(((TextComponent) c).content());
		}
		else if (c instanceof TranslatableComponent) {
			builder = Component.translatable()
					.key(((TranslatableComponent) c).key())
					.args(((TranslatableComponent) c).args());
		}
		else if (c instanceof SelectorComponent) {
			builder = Component.selector()
					.pattern(((SelectorComponent) c).pattern());
		}
		else if (c instanceof ScoreComponent) {
			builder = Component.score()
					.name(((ScoreComponent) c).name())
					.objective(((ScoreComponent) c).objective());
		}
		else if (c instanceof KeybindComponent) {
			builder = Component.keybind()
					.keybind(((KeybindComponent) c).keybind());
		}
		else if (c instanceof BlockNBTComponent) {
			builder = Component.blockNBT()
					.interpret(((BlockNBTComponent) c).interpret())
					.nbtPath(((BlockNBTComponent) c).nbtPath())
					.pos(((BlockNBTComponent) c).pos());
		}
		else if (c instanceof EntityNBTComponent) {
			builder = Component.entityNBT()
					.interpret(((EntityNBTComponent) c).interpret())
					.nbtPath(((EntityNBTComponent) c).nbtPath())
					.selector(((EntityNBTComponent) c).selector());
		}
		else if (c instanceof StorageNBTComponent) {
			builder = Component.storageNBT()
					.interpret(((StorageNBTComponent) c).interpret())
					.nbtPath(((StorageNBTComponent) c).nbtPath())
					.storage(((StorageNBTComponent) c).storage());
		}
		else {
			throw new IllegalArgumentException("Unknows component type " + c.getClass());
		}
		return builder.style(c.style()).append(c.children());
	}
	
	
	public static Chat italicFalseIfNotSet(Chat c) {
		c.builder.style(b -> {
			if (b.build().decoration(TextDecoration.ITALIC) == State.NOT_SET) {
				((FormatableChat) c).italic(false);
			}
		});
		return c;
	}
	
	
	
	
	protected static final Config config = new Config();
	
	public static Config getConfig() {
		return config;
	}
	
	public static class Config {
		public TextColor decorationColor = NamedTextColor.YELLOW;
		public char decorationChar = '-';
		public int nbCharMargin = 1;
		public TextColor successColor = NamedTextColor.GREEN;
		public TextColor failureColor = NamedTextColor.RED;
		public TextColor infoColor = NamedTextColor.GOLD;
		public TextColor warningColor = NamedTextColor.GOLD;
		public TextColor dataColor = NamedTextColor.GRAY;
		public TextColor urlColor = NamedTextColor.GREEN;
		public TextColor commandColor = NamedTextColor.GRAY;
		public TextColor highlightedCommandColor = NamedTextColor.WHITE;
		public TextColor broadcastColor = NamedTextColor.YELLOW;
		public Supplier<Chat> prefix;
		
		public int getPrefixWidth(boolean console) {
			Chat c = prefix == null ? null : prefix.get();
			return c == null ? 0 : ChatUtil.componentWidth(c.getAdv(), console);
		}
	}
	
	
	
}
