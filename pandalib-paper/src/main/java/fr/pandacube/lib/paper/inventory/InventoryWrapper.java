package fr.pandacube.lib.paper.inventory;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Wrapper for an {@link Inventory}.
 * Can be overridden to add specific behaviour to some methods.
 */
public class InventoryWrapper implements Inventory {
    private final Inventory base;

    /**
     * Creates a wrapper for the provided inventory.
     * @param base the wrapped inventory. Cannot be null.
     * @throws NullPointerException if the base inventory is null.
     */
    public InventoryWrapper(Inventory base) {
        this.base = Objects.requireNonNull(base, "base inventory cannot be null.");
    }


    @Override
    public int getSize() {
        return base.getSize();
    }

    @Override
    public int getMaxStackSize() {
        return base.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        base.setMaxStackSize(size);
    }

    @Override
    public @Nullable ItemStack getItem(int index) {
        return base.getItem(index);
    }

    @Override
    public void setItem(int index, @Nullable ItemStack item) {
        base.setItem(index, item);
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> addItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        return base.addItem(items);
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        return base.removeItem(items);
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItemAnySlot(@NotNull ItemStack... items) throws IllegalArgumentException {
        return base.removeItemAnySlot(items);
    }

    @Override
    public @Nullable ItemStack @NotNull [] getContents() {
        return base.getContents();
    }

    @Override
    public void setContents(@Nullable ItemStack @NotNull [] items) throws IllegalArgumentException {
        base.setContents(items);
    }

    @Override
    public @Nullable ItemStack @NotNull [] getStorageContents() {
        return base.getStorageContents();
    }

    @Override
    public void setStorageContents(@Nullable ItemStack @NotNull [] items) throws IllegalArgumentException {
        base.setStorageContents(items);
    }

    @Override
    public boolean contains(@NotNull Material material) throws IllegalArgumentException {
        return base.contains(material);
    }

    @Override
    public boolean contains(@Nullable ItemStack item) {
        return base.contains(item);
    }

    @Override
    public boolean contains(@NotNull Material material, int amount) throws IllegalArgumentException {
        return base.contains(material, amount);
    }

    @Override
    public boolean contains(@Nullable ItemStack item, int amount) {
        return base.contains(item, amount);
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
        return base.containsAtLeast(item, amount);
    }

    @Override
    public @NotNull HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        return base.all(material);
    }

    @Override
    public @NotNull HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
        return base.all(item);
    }

    @Override
    public int first(@NotNull Material material) throws IllegalArgumentException {
        return base.first(material);
    }

    @Override
    public int first(@NotNull ItemStack item) {
        return base.first(item);
    }

    @Override
    public int firstEmpty() {
        return base.firstEmpty();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public void remove(@NotNull Material material) throws IllegalArgumentException {
        base.remove(material);
    }

    @Override
    public void remove(@NotNull ItemStack item) {
        base.remove(item);
    }

    @Override
    public void clear(int index) {
        base.clear(index);
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public int close() {
        return base.close();
    }

    @Override
    public @NotNull List<HumanEntity> getViewers() {
        return base.getViewers();
    }

    @Override
    public @NotNull InventoryType getType() {
        return base.getType();
    }

    @Override
    public @Nullable InventoryHolder getHolder() {
        return base.getHolder();
    }

    @Override
    public @Nullable InventoryHolder getHolder(boolean useSnapshot) {
        return base.getHolder(useSnapshot);
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator() {
        return base.iterator();
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator(int index) {
        return base.iterator(index);
    }

    @Override
    public @Nullable Location getLocation() {
        return base.getLocation();
    }
}
