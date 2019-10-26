package fr.pandacube.util.text_display;

import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Display {

	private BaseComponent root = new TextComponent("");

	private BaseComponent current = null;

	
	/*
	 * ****************
	 * * Constructors *
	 * ****************
	 */
	
	
	/**
	 * Create a new instance. The current component is not initialized.
	 */
	public Display() {}

	/**
	 * Create a new instance, with the current component already initialized with the parameter.
	 * @param legacyText a text that will be converted to a component and set to the current compoment.
	 */
	public Display(String legacyText) {
		next(legacyText);
	}

	/**
	 * Create a new instance, with the current component already initialized with the parameter.
	 * @param legacyText a list of text that will be joined by a line return followed by ChatColor.RESET,
	 * 		then converted to a component and set to the current component.
	 */
	public Display(List<String> legacyText) {
		this(String.join("\n"+ChatColor.RESET, legacyText));
	}

	/**
	 * Create a new instance, with the current component already initialized with the parameter.
	 * @param legacyText an array of text that will be joined by a line return followed by ChatColor.RESET,
	 * 		then converted to a component and set to the current component.
	 */
	public Display(String[] legacyText) {
		this(Arrays.asList(legacyText));
	}

	/**
	 * Create a new instance, with the current component already initialized with the parameter.
	 * @param firstComponent a component corresponding to the current component.
	 */
	public Display(BaseComponent firstComponent) {
		next(firstComponent);
	}

	/**
	 * Create a new instance, with the current component already initialized with the parameter.
	 * @param components an array of component that will be inside the current component.
	 */
	public Display(BaseComponent[] components) {
		if (components == null) throw new IllegalArgumentException("le paramètre ne doit pas être null");
		next(components);
	}
	

	/*
	 * ******************
	 * * next() methods *
	 * ******************
	 */

	/**
	 * Initialize the current component with the parameter.
	 * The previous component is stored in the root component.
	 * @param cmp a component corresponding to the new component.
	 * @return this
	 */
	public Display next(BaseComponent cmp) {
		if (cmp == null) throw new IllegalArgumentException("le paramètre ne doit pas être null");
		finalizeCurrentComponent();
		current = cmp;
		return this;
	}

	/**
	 * Initialize the current component with the parameter.
	 * The previous component is stored in the root component.
	 * @param str a text that will be converted to a component and set to the current compoment.
	 * @return this
	 */
	public Display next(String str) {
		return next(TextComponent.fromLegacyText(str == null ? "" : str));
	}

	/**
	 * Initialize the current component with the parameter.
	 * The previous component is stored in the root component.
	 * @param components an array of component that will be inside the current component.
	 * @return this
	 */
	public Display next(BaseComponent[] components) {
		BaseComponent bc = new TextComponent();
		for (BaseComponent c : components)
			bc.addExtra(c);
		return next(bc);
	}

	/**
	 * Initialize the current component with the parameter.
	 * The previous component is stored in the root component.
	 * @param cmp an other instance of Display that the root component become the current component of this instance.
	 * @return this
	 */
	public Display next(Display cmp) {
		if (cmp == null) throw new IllegalArgumentException("le paramètre ne doit pas être null");
		return next(cmp.get());
	}

	/**
	 * Initialize the current component with the text "\n".
	 * The previous component is stored in the root component.
	 * @return this
	 */
	public Display nextLine() {
		finalizeCurrentComponent();
		current = new TextComponent("\n");
		return this;
	}
	
	/*
	 * **************************
	 * * Style and behaviour of *
	 * *** current component ****
	 * **************************
	 */

	/**
	 * Set the color of the current component.
	 * @param color the colour. Can be null;
	 * @return this
	 */
	public Display color(ChatColor color) {
		current.setColor(color);
		return this;
	}

	/**
	 * Set if the current component is bold.
	 * @param b true if bold, false if not, null if undefined
	 * @return this
	 */
	public Display bold(Boolean b) {
		current.setBold(b);
		return this;
	}

	/**
	 * Set if the current component is italic.
	 * @param b true if italic, false if not, null if undefined
	 * @return this
	 */
	public Display italic(Boolean i) {
		current.setItalic(i);
		return this;
	}

	/**
	 * Set if the current component is underlined.
	 * @param b true if underlined, false if not, null if undefined
	 * @return this
	 */
	public Display underlined(Boolean u) {
		current.setUnderlined(u);
		return this;
	}

	/**
	 * Set if the current component is obfuscated.
	 * In Minecraft user interface, obfuscated text displays randomly generated character in place of the originals. The random text regenerate each frame.
	 * @param b true if obfuscated, false if not, null if undefined
	 * @return this
	 */
	public Display obfuscated(Boolean o) {
		current.setObfuscated(o);
		return this;
	}

	/**
	 * Set if the current component is strikethrough.
	 * @param b true if strikethrough, false if not, null if undefined
	 * @return this
	 */
	public Display strikethrough(Boolean s) {
		current.setStrikethrough(s);
		return this;
	}

	/**
	 * Set a text displayed as a tooltip when the cursor is hover the current component.
	 * This method is only relevant if this Display is intended to be displayed in the chat or in a book
	 * @param content the text as an array of component.
	 * @return this
	 */
	public Display hoverText(BaseComponent[] content) {
		current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content));
		return this;
	}

	/**
	 * Set a text displayed as a tooltip when the cursor is hover the current component.
	 * This method is only relevant if this Display is intended to be displayed in the chat or in a book
	 * @param content the text as a component
	 * @return this
	 */
	public Display hoverText(BaseComponent content) {
		return hoverText(new BaseComponent[] {content});
	}

	/**
	 * Set a text displayed as a tooltip when the cursor is hover the current component.
	 * This method is only relevant if this Display is intended to be displayed in the chat or in a book
	 * @param content the text as a legacy string.
	 * @return this
	 */
	public Display hoverText(String legacyContent) {
		return hoverText(TextComponent.fromLegacyText(legacyContent));
	}

	/**
	 * Set a text displayed as a tooltip when the cursor is hover the current component.
	 * This method is only relevant if this Display is intended to be displayed in the chat or in a book
	 * @param content the text as a {@link Display} instance.
	 * @return this
	 */
	public Display hoverText(Display content) {
		return hoverText(content.get());
	}

	/**
	 * Allow the player to click on the current component to access to the specified URL.
	 * This method is only relevant if this Display is intended to be displayed in the chat
	 * @param url the URL
	 * @return this
	 */
	public Display clickURL(String url) {
		current.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		return this;
	}

	/**
	 * Allow the player to click on the current component to run the specified command.
	 * This method is only relevant if this Display is intended to be displayed in the chat, in a book or on a sign.
	 * On the sign, all the commands are executed in a row when the player click on the sign.
	 * @param cmd the command, with the "/"
	 * @return this
	 */
	public Display clickCommand(String cmd) {
		current.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
		return this;
	}

	/**
	 * Allow the player to click on the current component to fill the textfield with the specified command.
	 * This method is only relevant if this Display is intended to be displayed in the chat.
	 * @param cmd the command
	 * @return this
	 */
	public Display clickSuggest(String cmd) {
		current.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
		return this;
	}

	/**
	 * Allow the player to shuft-click on the current component to insert the specified string into the textfield (at the cursor location).
	 * This method is only relevant if this Display is intended to be displayed in the chat.
	 * @param str the string
	 * @return this
	 */
	public Display clickInsertion(String str) {
		current.setInsertion(str);
		return this;
	}

	private void finalizeCurrentComponent() {
		if (current != null) root.addExtra(current);
		current = null;
	}

	/**
	 * Add the current compoment into the root component and return the root component.
	 * @return
	 */
	public BaseComponent get() {
		finalizeCurrentComponent();
		return root;
	}

	/**
	 * Add the current compoment into the root component and return all the components in an array.
	 * @return
	 */
	public BaseComponent[] getArray() {
		finalizeCurrentComponent();
		return root.getExtra().toArray(new BaseComponent[root.getExtra().size()]);
	}

}
