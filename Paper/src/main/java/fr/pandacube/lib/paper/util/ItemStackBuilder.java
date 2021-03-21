package fr.pandacube.lib.paper.util;

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
import net.md_5.bungee.api.chat.BaseComponent;

public class ItemStackBuilder {

	public static ItemStackBuilder of(ItemStack base) {
		return new ItemStackBuilder(base.clone());
	}
	
	public static ItemStackBuilder of(Material mat) {
		return new ItemStackBuilder(new ItemStack(mat));
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
	
	public ItemStackBuilder rawDisplayName(BaseComponent[] displayName) {
		if (displayName != null)
			getOrInitMeta().setDisplayNameComponent(displayName);
		else
			getOrInitMeta().setDisplayName(null);
		updateMeta();
		return this;
	}
	
	public ItemStackBuilder displayName(Chat displayName) {
		if (displayName != null) {
			if (displayName.get().isItalicRaw() == null)
				((FormatableChat)displayName).italic(false);
			return rawDisplayName(displayName.getAsArray());
		}
		else
			return rawDisplayName(null);
	}
	
	public ItemStackBuilder rawLore(List<BaseComponent[]> lore) {
		getOrInitMeta().setLoreComponents(lore);
		updateMeta();
		return this;
	}
	
	public ItemStackBuilder lore(List<Chat> lore) {
		if (lore != null) {
			return rawLore(lore.stream()
					.map(line -> {
						if (line.get().isItalicRaw() == null)
							((FormatableChat)line).italic(false);
						return line.getAsArray();
					})
					.collect(Collectors.toList()));
		}
		else
			return rawLore(Collections.emptyList());
	}
	
	public ItemStackBuilder addLoreAfter(List<Chat> lore) {
		if (lore != null) {
			List<BaseComponent[]> baseLore = getOrInitMeta().getLoreComponents();
			if (baseLore == null) baseLore = Collections.emptyList();
			return rawLore(
					Streams.concat(
							baseLore.stream(),
							lore.stream()
							.map(line -> {
								if (line.get().isItalicRaw() == null)
									((FormatableChat)line).italic(false);
								return line.getAsArray();
							})
					)
					.collect(Collectors.toList()));
		}
		else
			return this;
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
