package fr.pandacube.lib.paper.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Streams;

import fr.pandacube.lib.core.chat.Chat;
import fr.pandacube.lib.core.chat.Chat.FormatableChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public class ItemStackBuilder {

	/**
	 * Create a builder with a clone of the provided ItemStack.
	 * 
	 * The returned builder will not alter the provided ItemStack.
	 * IF you want to modify the ItemStack with the builder, please use {@link #wrap(ItemStack)}.
	 * 
	 * @param base the original ItemStack.
	 * @return the builder
	 */
	public static ItemStackBuilder of(ItemStack base) {
		return wrap(base.clone());
	}

	/**
	 * Create a builder of a new ItemStack with the specified Material.
	 * 
	 * @param mat the material of the new builded ItemStack
	 * @return the builder
	 */
	public static ItemStackBuilder of(Material mat) {
		return wrap(new ItemStack(mat));
	}

	/**
	 * Create a builder that will alter the data of the provided ItemStack.
	 * 
	 * The {@link #build()} method of thez returned builder will return the same instance
	 * of ItemStack as the parameter of this method.
	 * 
	 * To create a builder that doesn’t modify the provided ItemStack, use {@link #of(ItemStack)}.
	 * @param stack
	 * @return the builder
	 */
	public static ItemStackBuilder wrap(ItemStack stack) {
		return new ItemStackBuilder(stack);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private ItemStack stack;
	private ItemMeta cachedMeta;
	
	private ItemStackBuilder(ItemStack base) {
		stack = base;
	}
	
	private ItemMeta getOrInitMeta() {
		return (cachedMeta != null) ? cachedMeta : (cachedMeta = stack.getItemMeta());
	}
	
	private void updateMeta() {
		stack.setItemMeta(cachedMeta);
	}
	
	public ItemStackBuilder amount(int a) {
		stack.setAmount(a);
		return this;
	}

	public ItemStackBuilder rawDisplayName(Component displayName) {
		if (displayName != null)
			getOrInitMeta().displayName(displayName);
		else
			getOrInitMeta().displayName(null);
		updateMeta();
		return this;
	}
	
	public ItemStackBuilder displayName(Chat displayName) {
		if (displayName != null) {
			if (displayName.getAdv().style().decoration(TextDecoration.ITALIC) == State.NOT_SET)
				((FormatableChat)displayName).italic(false);
			return rawDisplayName(displayName.getAdv());
		}
		else
			return rawDisplayName((Component) null);
	}
	
	public ItemStackBuilder rawLore(List<Component> lore) {
		getOrInitMeta().lore(lore);
		updateMeta();
		return this;
	}
	
	public ItemStackBuilder lore(List<Chat> lore) {
		if (lore != null) {
			return rawLore(lore.stream()
					.map(line -> Chat.italicFalseIfNotSet(line).getAdv())
					.collect(Collectors.toList()));
		}
		else
			return rawLore(Collections.emptyList());
	}
	
	public ItemStackBuilder addLoreAfter(List<Chat> lores) {
		if (lores != null) {
			List<Component> baseLore = getOrInitMeta().lore();
			if (baseLore == null) baseLore = Collections.emptyList();
			return rawLore(
					Streams.concat(
							baseLore.stream(),
							lores.stream()
							.map(line -> Chat.italicFalseIfNotSet(line).getAdv())
					)
					.collect(Collectors.toList()));
		}
		else
			return this;
	}
	
	public ItemStackBuilder addLoreAfter(Chat... lores) {
		if (lores == null || lores.length == 0)
			return this;
		return addLoreAfter(Arrays.asList(lores));
	}
	
	public ItemStackBuilder enchant(Enchantment ench, int level) {
		getOrInitMeta().addEnchant(ench, level, true);
		updateMeta();
		return this;
	}
	
	public ItemStackBuilder flags(ItemFlag... flags) {
		getOrInitMeta().addItemFlags(flags);
		updateMeta();
		return this;
	}
	
	public ItemStackBuilder hideEnchants() {
		return flags(ItemFlag.HIDE_ENCHANTS);
	}
	
	public ItemStackBuilder hideAttributes() {
		return flags(ItemFlag.HIDE_ATTRIBUTES);
	}
	
	public ItemStackBuilder fakeEnchant() {
		enchant(Enchantment.DURABILITY, 1);
		return hideEnchants();
	}
	
	public ItemStackBuilder damage(int d) {
		ItemMeta m = getOrInitMeta();
		if (m instanceof Damageable)
			((Damageable)m).setDamage(d);
		updateMeta();
		return this;
	}
	
	
	
	
	
	
	
	
	
	public ItemStack build() {
		return stack;
	}
	

}
