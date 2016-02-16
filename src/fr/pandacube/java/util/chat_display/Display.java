package fr.pandacube.java.util.chat_display;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Display {
	
	private BaseComponent first = new TextComponent("");
	
	private BaseComponent current = null;
	
	
	public Display() {
	}
	

	/**
	 * Après l'appel de ce contructeur, vous devez appeler nextComponent() pour initialiser la composante suivante
	 */
	public Display(String legacyText) {
		convertAndAddLegacy(legacyText);
	}

	/**
	 * Construit un message en mettant à la ligne après chaque chaine passé en paramètre.<br/>
	 * Après l'appel de ce contructeur, vous devez appeler nextComponent() pour initialiser la composante suivante
	 */
	public Display(String[] legacyText) {
		boolean f = true;
		for (String s : legacyText) {
			if (s == null) s = "";
			if (!f)
				first.addExtra("\n");
			f = false;
			convertAndAddLegacy(s);
		}
	}
	/**
	 * Construit un message en mettant à la ligne après chaque chaine passé en paramètre.<br/>
	 * Après l'appel de ce contructeur, vous devez appeler nextComponent() pour initialiser la composante suivante
	 */
	public Display(List<String> legacyText) {
		this(legacyText.toArray(new String[legacyText.size()]));
	}
	
	/**
	 * Après l'appel de ce contructeur, vous devez appeler nextComponent() pour initialiser la composante suivante
	 */
	public Display(BaseComponent firstComponent) {
		if (firstComponent == null) throw new IllegalArgumentException("le paramètre ne doit pas être null");
		first.addExtra(firstComponent);
	}
	
	
	

	/**
	 * Après l'appel de cette méthode, vous devez appeler nextComponent() pour initialiser la composante suivante
	 */
	public Display convertAndAddLegacy(String legacyText) {
		finalizeCurrentComponent();
		
		if (legacyText == null)
			return this;
		BaseComponent[] compo = TextComponent.fromLegacyText(legacyText);
		
		for (BaseComponent c : compo) {
			first.addExtra(c);
		}
		return this;
	}
	
	
	
	
	
	public Display nextComponent(String str) {
		finalizeCurrentComponent();
		if (str == null) str = "";
		current = new TextComponent(str);
		return this;
	}
	
	
	
	
	public Display addComponent(BaseComponent cmp) {
		if (cmp == null) throw new IllegalArgumentException("le paramètre ne doit pas être null");
		finalizeCurrentComponent();
		first.addExtra(cmp);
		return this;
	}

	/**
	 * Équivalent à <code>nextComponent("\n")</code>, sauf qu'un nouvel appel à nextComponent() est nécessaire après.
	 */
	public Display nextLine() {
		finalizeCurrentComponent();
		first.addExtra(new TextComponent("\n"));
		return this;
	}
	
	public Display setColor(ChatColor color) {
		current.setColor(color);
		return this;
	}
	
	public Display setBold(boolean b) {
		current.setBold(b);
		return this;
	}
	
	public Display setItalic(boolean i) {
		current.setItalic(i);
		return this;
	}
	
	public Display setUnderlined(boolean u) {
		current.setUnderlined(u);
		return this;
	}
	
	public Display setObfuscated(boolean o) {
		current.setObfuscated(o);
		return this;
	}
	
	public Display setStrikethrough(boolean s) {
		current.setStrikethrough(s);
		return this;
	}
	
	public Display setHoverText(Display content) {
		current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {content.get()}));
		return this;
	}
	
	public Display setClickURL(String url) {
		current.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		return this;
	}
	
	public Display setClickCommand(String cmd) {
		current.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
		return this;
	}
	
	public Display setClickSuggest(String cmd) {
		current.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
		return this;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void finalizeCurrentComponent() {
		if (current != null)
			first.addExtra(current);
		current = null;
	}
	
	
	
	public BaseComponent get() {
		finalizeCurrentComponent();
		return first;
	}

}
