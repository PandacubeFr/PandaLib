package fr.pandacube.lib.paper.util;

import com.google.common.base.Preconditions;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftItemStack;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.ListTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.IntUnaryOperator;

/**
 * A wrapper to easily manipulate vanilla player data.
 *
 * @param data The data as they are stored in the player file.
 */
public record PlayerDataWrapper(CompoundTag data) {

    /**
     * Creates a new wrapper for the provided player data.
     * @param data the data to wrap.
     */
    public PlayerDataWrapper(CompoundTag data) {
        this.data = data == null ? new CompoundTag() : data;
    }

    /**
     * Gets a snapshot of the inventory of this player.
     * If modified, call the {@link #setInventory(PlayerInventory)} to update the data.
     * @return the player inventory
     */
    public PlayerInventory getInventory() {
        return new DummyPlayerInventory(
                getBukkitInventory("Inventory", InventoryType.PLAYER, this::fromNBTtoBukkitInventorySlot),
                getHeldItemSlot());
    }

    private int fromNBTtoBukkitInventorySlot(int nbtSlot) {
        // cat   nbEl    NBTSlot      bukkitSlot  NBT->Bukkit
        // items   36                    0-35     ==
        // armor    4  starts at 100    36-39     -100 + 36
        // offhand  1  starts at 150      40      -150 + 40
        if (nbtSlot >= 0 && nbtSlot < 36) { // regular inventory slots
            return nbtSlot;
        }
        if (nbtSlot >= 100 && nbtSlot < 104) { // armor slots
            return nbtSlot - 100 + 36;
        }
        if (nbtSlot == 150) { // second hand
            return 40;
        }
        throw new IllegalArgumentException("Unrecognized NBT player inventory slot " + nbtSlot);
    }

    public void setInventory(PlayerInventory inv) {
        setBukkitInventory("Inventory", inv, this::fromBukkitToNBTInventorySlot);
        setHeldItemSlot(inv.getHeldItemSlot());
    }

    private int fromBukkitToNBTInventorySlot(int bukkitSlot) {
        if (bukkitSlot >= 0 && bukkitSlot < 36) { // regular inventory slots
            return bukkitSlot;
        }
        if (bukkitSlot >= 36 && bukkitSlot < 40) { // armor slots
            return bukkitSlot + 100 - 36;
        }
        if (bukkitSlot == 40) { // second hand
            return 150;
        }
        throw new IllegalArgumentException("Unrecognized Bukkit player inventory slot " + bukkitSlot);
    }


    public Inventory getEnderChest() {
        return getBukkitInventory("EnderItems", InventoryType.ENDER_CHEST, IntUnaryOperator.identity());
    }

    public void setEnderChest(Inventory inv) {
        setBukkitInventory("EnderItems", inv, IntUnaryOperator.identity());
    }


    private Inventory getBukkitInventory(String nbtKey, InventoryType bukkitType, IntUnaryOperator nbtToBukkitSlotConverter) {
        Map<Integer, ItemStack> stacks = getRawInventoryContent(nbtKey);
        Inventory inv = Bukkit.createInventory(null, bukkitType);
        if (stacks.isEmpty())
            return inv;
        for (Entry<Integer, ItemStack> is : stacks.entrySet()) {
            inv.setItem(nbtToBukkitSlotConverter.applyAsInt(is.getKey()), is.getValue());
        }
        return inv;
    }

    private Map<Integer, ItemStack> getRawInventoryContent(String key) {
        if (!data.contains(key, Tag.TAG_LIST()))
            return Map.of();
        ListTag list = data.getList(key, Tag.TAG_COMPOUND());
        if (list == null)
            return Map.of();

        Map<Integer, ItemStack> stacks = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int nbtSlot = itemTag.getByte("Slot") & 255;
            fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStack.parse(itemTag)
                    .map(nms -> filterStack(CraftItemStack.asCraftMirror(nms)))
                    .ifPresent(is -> stacks.put(nbtSlot, is));
        }
        return stacks;
    }


    private void setBukkitInventory(String nbtKey, Inventory inv, IntUnaryOperator bukkitToNBTSlotConverter) {
        Map<Integer, ItemStack> stacks = new TreeMap<>();
        if (inv == null) {
            setRawInventoryContent(nbtKey, stacks);
            return;
        }
        for (int bukkitSlot = 0; bukkitSlot < inv.getSize(); bukkitSlot++) {
            ItemStack is = filterStack(inv.getItem(bukkitSlot));
            if (is == null)
                continue;
            int nbtSlot = bukkitToNBTSlotConverter.applyAsInt(bukkitSlot);
            stacks.put(nbtSlot, is);
        }
        setRawInventoryContent(nbtKey, stacks);
    }

    private void setRawInventoryContent(String key, Map<Integer, ItemStack> stacks) {
        ListTag list = new ListTag();
        for (Entry<Integer, ItemStack> is : stacks.entrySet()) {
            ItemStack stack = filterStack(is.getValue());
            if (stack == null)
                continue;
            CompoundTag itemTag = new CompoundTag();
            itemTag.putByte("Slot", is.getKey().byteValue());
            list.add(list.size(), CraftItemStack.asNMSCopy(is.getValue()).save(itemTag));
        }
        data.put(key, list);
    }


    private ItemStack filterStack(ItemStack is) {
        return is == null || is.getType().isEmpty() || is.getAmount() == 0 ? null : is;
    }


    private int getHeldItemSlot() {
        if (!data.contains("SelectedItemSlot"))
            return 0;
        return data.getInt("SelectedItemSlot");
    }

    private void setHeldItemSlot(int slot) {
        data.putInt("SelectedItemSlot", slot);
    }


    public int getScore() {
        if (!data.contains("Score"))
            return 0;
        return data.getInt("Score");

    }

    public void setScore(int score) {
        data.putInt("Score", score);
    }


    public int getTotalExperience() {
        if (!data.contains("XpTotal"))
            return 0;
        return data.getInt("XpTotal");
    }

    public void setTotalExperience(int xp) {
        data.putInt("XpTotal", xp);
        double levelAndExp = ExperienceUtil.getLevelFromExp(xp);
        int level = (int) levelAndExp;
        double expProgress = levelAndExp - level;
        data.putInt("XpLevel", level);
        data.putFloat("XpP", (float) expProgress);
    }


    private static class DummyPlayerInventory extends InventoryWrapper implements PlayerInventory {

        private int heldItemSlot;

        public DummyPlayerInventory(Inventory base, int heldItemSlot) {
            super(base);
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
}
