package fr.pandacube.lib.paper.players;

import fr.pandacube.lib.paper.inventory.DummyPlayerInventory;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftItemStack;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProblemReporter;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStackWithSlot;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.TagValueInput;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.TagValueOutput;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ValueInput;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ValueOutputTypedOutputList;
import fr.pandacube.lib.paper.util.ExperienceUtil;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.IntUnaryOperator;

/**
 * A wrapper to easily manipulate the player data file.
 *
 * @param data The NBT data structure as it is stored in the player file.
 */
public record PlayerDataWrapper(CompoundTag data) {

    /**
     * Creates a new wrapper for the provided player data.
     * @param data the NBT data to wrap.
     */
    public PlayerDataWrapper(CompoundTag data) {
        this.data = data == null ? new CompoundTag() : data;
    }

    /**
     * Gets a snapshot of the inventory of this player.
     * If the inventory is modified, the {@link #setInventory(PlayerInventory)} method should be called to update the
     * data in this wrapper.
     * @return the player inventory.
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

    /**
     * Sets the player inventory to the content of the provided one.
     * The internal data of this wrapper will be updated.
     * @param inv the inventory to store in this player data in place of the old one.
     */
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


    /**
     * Gets a snapshot of the enderchest of this player.
     * If the enderchest is modified, the {@link #setEnderChest(Inventory)} method should be called to update the
     * data in this wrapper.
     * @return the player enderchest.
     */
    public Inventory getEnderChest() {
        return getBukkitInventory("EnderItems", InventoryType.ENDER_CHEST, IntUnaryOperator.identity());
    }

    /**
     * Sets the player enderchest to the content of the provided one.
     * The internal data of this wrapper will be updated.
     * @param inv the enderchest content to store in this player data in place of the old enderchest.
     */
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

        ValueInput vi = TagValueInput.createGlobal(ProblemReporter.DISCARDING(), data);
        Iterable<?> listNMSItemStackWithSlot = ReflectWrapper.unwrap(vi.listOrEmpty(key, ItemStackWithSlot.CODEC()));

        Map<Integer, ItemStack> stacks = new TreeMap<>();

        for (Object nmsISWS : listNMSItemStackWithSlot) {
            ItemStackWithSlot isws = ReflectWrapper.wrap(nmsISWS, ItemStackWithSlot.class);

            int nbtSlot = isws.slot() & 255;
            Optional.of(isws.stack())
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

        TagValueOutput vo = TagValueOutput.createWrappingGlobal(ProblemReporter.DISCARDING(), data);
        ValueOutputTypedOutputList listNMSItemStackWithSlot = vo.list(key, ItemStackWithSlot.CODEC());

        for (Entry<Integer, ItemStack> is : stacks.entrySet()) {
            ItemStack stack = filterStack(is.getValue());
            if (stack == null)
                continue;

            listNMSItemStackWithSlot.add(ReflectWrapper.unwrap(new ItemStackWithSlot(is.getKey(), CraftItemStack.asNMSCopy(is.getValue()))));
        }
    }


    private ItemStack filterStack(ItemStack is) {
        return is == null || is.isEmpty() || is.getAmount() <= 0 ? null : is;
    }


    private int getHeldItemSlot() {
        if (!data.contains("SelectedItemSlot"))
            return 0;
        return data.getInt("SelectedItemSlot");
    }

    private void setHeldItemSlot(int slot) {
        data.putInt("SelectedItemSlot", slot);
    }


    /**
     * Gets the score of the player, as stored in the data with the key {@code Score}.
     * @return the value of Score.
     */
    public int getScore() {
        if (!data.contains("Score"))
            return 0;
        return data.getInt("Score");

    }

    /**
     * Sets the score of the player, as stored in the data with the key {@code Score}.
     * @param score the value of Score to set.
     */
    public void setScore(int score) {
        data.putInt("Score", score);
    }


    /**
     * Gets the total experience of the player, as stored in the data with the key {@code XpTotal}.
     * @return the value of XpTotal.
     */
    public int getTotalExperience() {
        if (!data.contains("XpTotal"))
            return 0;
        return data.getInt("XpTotal");
    }

    /**
     * Sets the total experience of the player, as stored in the data with the key {@code XpTotal}.
     * @param xp the value of XpTotal to set.
     */
    public void setTotalExperience(int xp) {
        data.putInt("XpTotal", xp);
        double levelAndExp = ExperienceUtil.getLevelFromExp(xp);
        int level = (int) levelAndExp;
        double expProgress = levelAndExp - level;
        data.putInt("XpLevel", level);
        data.putFloat("XpP", (float) expProgress);
    }

    /**
     * Thrown to indicate that an error occurred while loading the data of the player from the file.
     */
    public static class PlayerDataLoadException extends RuntimeException {
        /* package */ PlayerDataLoadException(String playerName,  UUID playerId, Throwable cause) {
            super("Unable to load data of player " + playerName + " (" + playerId + ")", cause);
        }
    }
}
