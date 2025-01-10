package fr.pandacube.lib.paper.inventory;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Dummy implementation of a player inventory.
 */
public class DummyPlayerInventory extends InventoryWrapper implements PlayerInventory {

    private int heldItemSlot;

    /**
     * Creates a dummy player inventory.
     * @param base the inventory itself.
     * @param heldItemSlot the currently held item slot, from 0 to 8.
     */
    public DummyPlayerInventory(Inventory base, int heldItemSlot) {
        super(base);
        if (base.getSize() < 41)
            throw new IllegalArgumentException("base inventory should have a size of 41 (" + base.getSize() + " given).");
        if (heldItemSlot < 0 || heldItemSlot > 8)
            throw new IllegalArgumentException("heldItemSlot should be between 0 and 8 inclusive.");
        this.heldItemSlot = heldItemSlot;
    }

    @Override
    public @Nullable ItemStack @NotNull [] getStorageContents() {
        return Arrays.copyOfRange(getContents(), 0, 36);
    }

    @Override
    public @Nullable ItemStack @NotNull [] getArmorContents() {
        return Arrays.copyOfRange(getContents(), 36, 40);
    }

    @Override
    public void setArmorContents(@Nullable ItemStack[] items) {
        this.setSlots(items, 36, 4);
    }

    @Override
    public @Nullable ItemStack @NotNull [] getExtraContents() {
        return Arrays.copyOfRange(getContents(), 40, getSize());
    }

    @Override
    public void setExtraContents(@Nullable ItemStack[] items) {
        this.setSlots(items, 40, getSize() - 40);
    }

    private void setSlots(ItemStack[] items, int baseSlot, int length) {
        if (items == null) {
            items = new ItemStack[length];
        }
        Preconditions.checkArgument(items.length <= length, "items.length must be < %s", length);

        for (int i = 0; i < length; i++) {
            if (i >= items.length) {
                this.setItem(baseSlot + i, null);
            } else {
                this.setItem(baseSlot + i, items[i]);
            }
        }
    }

    @Override
    public ItemStack getHelmet() {
        return getItem(39);
    }

    @Override
    public void setHelmet(@Nullable ItemStack helmet) {
        setItem(39, helmet);
    }

    @Override
    public ItemStack getChestplate() {
        return getItem(38);
    }

    @Override
    public void setChestplate(@Nullable ItemStack chestplate) {
        setItem(38, chestplate);
    }

    @Override
    public ItemStack getLeggings() {
        return getItem(37);
    }

    @Override
    public void setLeggings(@Nullable ItemStack leggings) {
        setItem(37, leggings);
    }

    @Override
    public ItemStack getBoots() {
        return getItem(36);
    }

    @Override
    public void setBoots(@Nullable ItemStack boots) {
        setItem(36, boots);
    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack item) {
        Preconditions.checkArgument(slot != null, "slot must not be null");

        switch (slot) {
            case HAND -> this.setItemInMainHand(item);
            case OFF_HAND -> this.setItemInOffHand(item);
            case FEET -> this.setBoots(item);
            case LEGS -> this.setLeggings(item);
            case CHEST -> this.setChestplate(item);
            case HEAD -> this.setHelmet(item);
            default -> throw new IllegalArgumentException("Not implemented. This is a bug");
        }
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull EquipmentSlot slot) {
        return switch (slot) {
            case HAND -> this.getItemInMainHand();
            case OFF_HAND -> this.getItemInOffHand();
            case FEET -> Objects.requireNonNullElseGet(this.getBoots(), () -> new ItemStack(Material.AIR));
            case LEGS -> Objects.requireNonNullElseGet(this.getLeggings(), () -> new ItemStack(Material.AIR));
            case CHEST -> Objects.requireNonNullElseGet(this.getChestplate(), () -> new ItemStack(Material.AIR));
            case HEAD -> Objects.requireNonNullElseGet(this.getHelmet(), () -> new ItemStack(Material.AIR));
            case BODY -> new ItemStack(Material.AIR); // for horses/wolves armor
        };
    }

    @Override
    public @NotNull ItemStack getItemInMainHand() {
        return Objects.requireNonNullElse(getItem(heldItemSlot), new ItemStack(Material.AIR));
    }

    @Override
    public void setItemInMainHand(@Nullable ItemStack item) {
        setItem(heldItemSlot, item);
    }

    @Override
    public @NotNull ItemStack getItemInOffHand() {
        return Objects.requireNonNullElse(getItem(40), new ItemStack(Material.AIR));
    }

    @Override
    public void setItemInOffHand(@Nullable ItemStack item) {
        setItem(40, item);
    }

    @Override
    public @NotNull ItemStack getItemInHand() {
        return getItemInMainHand();
    }

    @Override
    public void setItemInHand(@Nullable ItemStack stack) {
        setItemInMainHand(stack);
    }

    @Override
    public int getHeldItemSlot() {
        return heldItemSlot;
    }

    @Override
    public void setHeldItemSlot(int slot) {
        if (slot < 0 || slot > 8)
            throw new IllegalArgumentException("Slot is not between 0 and 8 inclusive");
        heldItemSlot = slot;
    }

    @Override
    public @Nullable HumanEntity getHolder() {
        return null;
    }
}
