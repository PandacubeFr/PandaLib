package fr.pandacube.lib.paper.inventory;

import com.google.common.collect.Streams;
import fr.pandacube.lib.chat.Chat;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentType.Valued;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static fr.pandacube.lib.chat.ChatStatic.chatComponent;

/**
 * A builder for {@link ItemStack}.
 */
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
	
	private ItemStackBuilder(ItemStack base) {
		stack = base;
	}

	/**
	 * Runs the provided updater on the {@link ItemMeta} instance of the built stack.
	 * @param metaUpdater the updater that will modify the meta.
	 * @return itself.
	 */
	public ItemStackBuilder meta(Consumer<ItemMeta> metaUpdater) {
		return meta(metaUpdater, ItemMeta.class);
	}

	/**
	 * Runs the provided updater on the {@link ItemMeta} instance of the built stack.
	 * @param metaUpdater the updater that will modify the meta.
	 * @param metaType the type of the meta instance.
	 * @param <T> the type of item meta.
	 * @return itself.
	 */
	public <T extends ItemMeta> ItemStackBuilder meta(Consumer<T> metaUpdater, Class<T> metaType) {
		stack.editMeta(metaType, metaUpdater);
		return this;
	}

	/**
	 * Sets the amount of the built stack.
	 * @param a the new amount.
	 * @return itself.
	 */
	public ItemStackBuilder amount(int a) {
		stack.setAmount(a);
		return this;
	}

	/**
	 * Sets the display name of the item, directly passing to {@link ItemMeta#displayName(Component)}.
	 * @param displayName the new display name. Can be null to unset.
	 * @return itself.
	 */
	public ItemStackBuilder rawDisplayName(Component displayName) {
		return meta(m -> m.displayName(displayName));
	}

	/**
	 * Sets the display name of the item, filtering to make default italic to false.
	 * @param displayName the new display name. Can be null to unset.
	 * @return itself.
	 */
	public ItemStackBuilder displayName(ComponentLike displayName) {
		return rawDisplayName(displayName != null
				? Chat.italicFalseIfNotSet(chatComponent(displayName)).asComponent()
				: null);
	}

	/**
	 * Sets the lore of the item, directly passing to {@link ItemMeta#lore(List)}.
	 * @param lore the new lore. Can be null to unset.
	 * @return itself.
	 */
	public ItemStackBuilder rawLore(List<Component> lore) {
		return meta(m -> m.lore(lore));
	}


	/**
	 * Sets the lore of the item, filtering to make default italic to false.
	 * @param lore the new lore. Can be null to unset.
	 * @return itself.
	 */
	public ItemStackBuilder lore(List<? extends ComponentLike> lore) {
		if (lore != null) {
			return rawLore(lore.stream()
					.map(line -> Chat.italicFalseIfNotSet(chatComponent(line)).get())
					.toList());
		}
		else
			return rawLore(Collections.emptyList());
	}

	/**
	 * Adds new lore lines to the existing lore of the item.
	 * @param lores the added lore lines.
	 * @return itself.
	 */
	public ItemStackBuilder addLoreAfter(List<? extends ComponentLike> lores) {
		if (lores != null) {
			List<Component> baseLore = stack.getItemMeta().lore();
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

	/**
	 * Adds new lore lines to the existing lore of the item.
	 * @param lores the added lore lines.
	 * @return itself.
	 */
	public ItemStackBuilder addLoreAfter(ComponentLike... lores) {
		if (lores == null || lores.length == 0)
			return this;
		return addLoreAfter(Arrays.asList(lores));
	}

	/**
	 * Enchant the item.
	 * Supports unsafe enchants.
	 * @param enchantment the enchantment.
	 * @param level the enchant level.
	 * @return itself.
	 */
	public ItemStackBuilder enchant(Enchantment enchantment, int level) {
		return meta(m -> m.addEnchant(enchantment, level, true));
	}

	/**
	 * Adds the provided flags to the item.
	 * @param flags he flags to add.
	 * @return itself.
	 */
	public ItemStackBuilder flags(ItemFlag... flags) {
		return flags(true, flags);
	}

	/**
	 * Adds or removes the provided flags to the item.
	 * @param add true to add, false to remove.
	 * @param flags he flags to add.
	 * @return itself.
	 */
	public ItemStackBuilder flags(boolean add, ItemFlag... flags) {
		return add ? meta(m -> m.addItemFlags(flags))
				: meta(m -> m.removeItemFlags(flags));
	}

	/**
	 * Hides the enchants from the tooltip of the item.
	 * Will set the {@link ItemFlag#HIDE_ENCHANTS} flag of the item.
	 * @return itself.
	 */
	public ItemStackBuilder hideEnchants() {
		return hideEnchants(true);
	}

	/**
	 * Sets or unsets the {@link ItemFlag#HIDE_ENCHANTS} flag of the item.
	 * @param hide true to hide, false to show.
	 * @return itself.
	 */
	public ItemStackBuilder hideEnchants(boolean hide) {
		return flags(hide, ItemFlag.HIDE_ENCHANTS);
	}

	/**
	 * Hides the attributes from the tooltip of the item.
	 * Will set the {@link ItemFlag#HIDE_ATTRIBUTES} flag of the item.
	 * @return itself.
	 */
	public ItemStackBuilder hideAttributes() {
		return hideAttributes(true);
	}

	/**
	 * Sets or unsets the {@link ItemFlag#HIDE_ATTRIBUTES} flag of the item.
	 * @param hide true to hide, false to show.
	 * @return itself.
	 */
	public ItemStackBuilder hideAttributes(boolean hide) {
		return flags(hide, ItemFlag.HIDE_ATTRIBUTES);
	}

	/**
	 * Apply the enchantment glint to the item, event if it's not enchant.
	 * @return itself.
	 */
	public ItemStackBuilder fakeEnchant() {
		return fakeEnchant(true);
	}

	/**
	 * Sets the enchantment glint override to the item.
	 * @param apply true to enforce the enchantment glint, false to set to default.
	 * @return itself.
	 */
	public ItemStackBuilder fakeEnchant(boolean apply) {
		return meta(m -> m.setEnchantmentGlintOverride(apply ? true : null));
	}

	/**
	 * Sets this item as unbreakable.
	 * @return itself.
	 */
	public ItemStackBuilder unbreakable() {
		return unbreakable(true);
	}

	/**
	 * Sets the unbreakable status of this item.
	 * @param unbreakable the unbreakable status.
	 * @return itself.
	 */
	public ItemStackBuilder unbreakable(boolean unbreakable) {
		return meta(m -> m.setUnbreakable(unbreakable));
	}

	/**
	 * Sets the damage value of this item.
	 * @param d the new damage value.
	 * @return itself.
	 */
	public ItemStackBuilder damage(int d) {
		return meta(m -> m.setDamage(d), Damageable.class);
	}


	/**
	 * Sets a value for a data component of this item.
	 * @param dataType the data component type.
	 * @param dataValue the data component value.
	 * @return itself.
	 * @param <T> the data component API type.
	 */
	public <T> ItemStackBuilder data(Valued<T> dataType, T dataValue) {
		stack.setData(dataType, dataValue);
		return this;
	}

	/**
	 * Unset (set to empty) a value for a data component of this item.
	 * @param dataType the data component type.
	 * @return itself.
	 */
	public ItemStackBuilder unsetData(DataComponentType dataType) {
		stack.unsetData(dataType);
		return this;
	}

	/**
	 * Reset (act as default) a value for a data component of this item.
	 * @param dataType the data component type.
	 * @return itself.
	 */
	public ItemStackBuilder resetData(DataComponentType dataType) {
		stack.resetData(dataType);
		return this;
	}


	/**
	 * Sets the {@code can_break} data component to the provided list of {@link Material}.
	 * @param canBreak a list of {@link Material}.
	 * @return itself.
	 */
	public ItemStackBuilder canBreakMaterials(Collection<Material> canBreak) {
		return canBreak(canBreak.stream().map(Material::getKey).toList());
	}


	/**
	 * Sets the {@code can_break} data component to the provided list of {@link NamespacedKey}.
	 * @param canBreak a list of block predicate. If empty, unsets the data component. If null, reset to default.
	 * @return itself.
	 */
	@SuppressWarnings("removal")
	public ItemStackBuilder canBreak(Collection<NamespacedKey> canBreak) {
		@SuppressWarnings("unchecked")
		Collection<com.destroystokyo.paper.Namespaced> nsCanBreak = (Collection<com.destroystokyo.paper.Namespaced>) (Collection<?>) canBreak;
		return meta(m -> m.setPlaceableKeys(nsCanBreak));

		/*
		if (canBreak == null)
			return resetData(DataComponentTypes.CAN_BREAK);
		else if (canBreak.isEmpty())
			return unsetData(DataComponentTypes.CAN_BREAK);
		else
			return data(DataComponentTypes.CAN_BREAK, ItemAdventurePredicate.itemAdventurePredicate(canBreak));*/
	}

	/**
	 * Sets the {@code can_place_on} data component to the provided list of {@link Material}.
	 * @param canPlaceOn a list of {@link Material}.
	 * @return itself.
	 */
	public ItemStackBuilder canPlaceOnMaterials(Collection<Material> canPlaceOn) {
		return canPlaceOn(canPlaceOn.stream().map(Material::getKey).toList());
	}

	/**
	 * Sets the {@code can_place_on} data component to the provided list of {@link NamespacedKey}.
	 * @param canPlaceOn a list of block predicate. If empty, unsets the data component. If null, reset to default.
	 * @return itself.
	 */
	@SuppressWarnings("removal")
	public ItemStackBuilder canPlaceOn(Collection<NamespacedKey> canPlaceOn) {
		@SuppressWarnings("unchecked")
		Collection<com.destroystokyo.paper.Namespaced> nsCanPlaceOn = (Collection<com.destroystokyo.paper.Namespaced>) (Collection<?>) canPlaceOn;
		return meta(m -> m.setPlaceableKeys(nsCanPlaceOn));

		/* if (canPlaceOn == null)
			return resetData(DataComponentTypes.CAN_PLACE_ON);
		else if (canPlaceOn.isEmpty())
			return unsetData(DataComponentTypes.CAN_PLACE_ON);
		else
			return data(DataComponentTypes.CAN_PLACE_ON, ItemAdventurePredicate.itemAdventurePredicate(canPlaceOn)); */
	}


	/**
	 * Build the {@link ItemStack}.
	 * @return the build item stack.
	 */
	public ItemStack build() {
		return stack;
	}
	

}
