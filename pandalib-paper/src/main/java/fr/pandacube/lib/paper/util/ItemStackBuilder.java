package fr.pandacube.lib.paper.util;

import com.google.common.collect.Streams;
import fr.pandacube.lib.chat.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static fr.pandacube.lib.chat.ChatStatic.chatComponent;

public class ItemStackBuilder {

	/**
	 * Create a builder with a clone of the provided ItemStack.
	 * <p>
	 * The returned builder will not alter the provided ItemStack.
	 * If you want to modify the ItemStack with the builder, please use {@link #wrap(ItemStack)}.
	 * @param base the original ItemStack.
	 * @return the builder
	 */
	public static ItemStackBuilder of(ItemStack base) {
		return wrap(base.clone());
	}

	/**
	 * Create a builder of a new ItemStack with the specified Material.
	 * @param mat the material of the new built ItemStack
	 * @return the builder
	 */
	public static ItemStackBuilder of(Material mat) {
		return wrap(new ItemStack(mat));
	}

	/**
	 * Create a builder that will alter the data of the provided ItemStack.
	 * <p>
	 * The {@link #build()} method of the returned builder will return the same instance
	 * of ItemStack as the parameter of this method.
	 * <p>
	 * To create a builder that doesn't modify the provided ItemStack, use {@link #of(ItemStack)}.
	 * @param stack the wrapped item stack.
	 * @return the builder
	 */
	public static ItemStackBuilder wrap(ItemStack stack) {
		return new ItemStackBuilder(stack);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private final ItemStack stack;
	private ItemMeta cachedMeta;
	
	private ItemStackBuilder(ItemStack base) {
		stack = base;
	}
	
	private ItemMeta getOrInitMeta() {
		return (cachedMeta != null) ? cachedMeta : (cachedMeta = stack.getItemMeta());
	}

	public ItemStackBuilder meta(Consumer<ItemMeta> metaUpdater) {
		return meta(metaUpdater, ItemMeta.class);
	}

	public <T extends ItemMeta> ItemStackBuilder meta(Consumer<T> metaUpdater, Class<T> metaType) {
		stack.editMeta(metaType, m -> {
			metaUpdater.accept(m);
			cachedMeta = m;
		});
		return this;
	}
	
	public ItemStackBuilder amount(int a) {
		stack.setAmount(a);
		return this;
	}

	public ItemStackBuilder rawDisplayName(Component displayName) {
		return meta(m -> m.displayName(displayName));
	}
	
	public ItemStackBuilder displayName(ComponentLike displayName) {
		if (displayName != null) {
			return rawDisplayName(Chat.italicFalseIfNotSet(chatComponent(displayName)).asComponent());
		}
		else
			return rawDisplayName(null);
	}
	
	public ItemStackBuilder rawLore(List<Component> lore) {
		return meta(m -> m.lore(lore));
	}
	
	public ItemStackBuilder lore(List<? extends ComponentLike> lore) {
		if (lore != null) {
			return rawLore(lore.stream()
					.map(line -> Chat.italicFalseIfNotSet(chatComponent(line)).get())
					.toList());
		}
		else
			return rawLore(Collections.emptyList());
	}
	
	public ItemStackBuilder addLoreAfter(List<? extends ComponentLike> lores) {
		if (lores != null) {
			List<Component> baseLore = getOrInitMeta().lore();
			if (baseLore == null) baseLore = Collections.emptyList();
			return rawLore(
					Streams.concat(
							baseLore.stream(),
							lores.stream()
							.map(line -> Chat.italicFalseIfNotSet(chatComponent(line)).get())
					)
					.toList());
		}
		else
			return this;
	}
	
	public ItemStackBuilder addLoreAfter(ComponentLike... lores) {
		if (lores == null || lores.length == 0)
			return this;
		return addLoreAfter(Arrays.asList(lores));
	}

	/**
	 * Supports unsafe enchants.
	 */
	public ItemStackBuilder enchant(Enchantment enchantment, int level) {
		return meta(m -> m.addEnchant(enchantment, level, true));
	}

	public ItemStackBuilder flags(ItemFlag... flags) {
		return flags(true, flags);
	}

	public ItemStackBuilder flags(boolean add, ItemFlag... flags) {
		return add ? meta(m -> m.addItemFlags(flags))
				: meta(m -> m.removeItemFlags(flags));
	}

	public ItemStackBuilder hideEnchants() {
		return hideEnchants(true);
	}

	public ItemStackBuilder hideEnchants(boolean hide) {
		return flags(hide, ItemFlag.HIDE_ENCHANTS);
	}

	public ItemStackBuilder hideAttributes() {
		return hideAttributes(true);
	}

	public ItemStackBuilder hideAttributes(boolean hide) {
		return flags(hide, ItemFlag.HIDE_ATTRIBUTES);
	}

	public ItemStackBuilder fakeEnchant() {
		return fakeEnchant(true);
	}

	public ItemStackBuilder fakeEnchant(boolean apply) {
		if (apply) {
			enchant(Enchantment.UNBREAKING, 1);
			return hideEnchants();
		}
		return this;
	}

	public ItemStackBuilder unbreakable() {
		return unbreakable(true);
	}

	public ItemStackBuilder unbreakable(boolean unbreakable) {
		return meta(m -> m.setUnbreakable(unbreakable));
	}
	
	public ItemStackBuilder damage(int d) {
		return meta(m -> m.setDamage(d), Damageable.class);
	}
	
	
	
	
	
	
	
	
	
	public ItemStack build() {
		return stack;
	}
	

}
