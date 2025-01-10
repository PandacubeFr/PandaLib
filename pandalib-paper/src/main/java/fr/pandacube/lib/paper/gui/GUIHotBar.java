package fr.pandacube.lib.paper.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.pandacube.lib.util.log.Log;
import fr.pandacube.lib.paper.util.BukkitEvent;

/**
 * Managed a "lobby" type hot bar menu/inventory. It represents items in the
 * player inventory on which you can right-click on it.
 * The player can't move or drop these items.
 */
public class GUIHotBar implements Listener {

	private final Map<ItemStack, BiConsumer<PlayerInventory, ItemStack>> itemsAndSetters = new HashMap<>();
	
	private final Map<ItemStack, Consumer<Player>> itemsAndClickListeners = new HashMap<>();
	
	private final int defaultSlot;
	
	private final List<Player> currentPlayers = new ArrayList<>();

	/**
	 * Setup a new gui hot  bar. You should not instantiate more than one hot bar.
	 * @param defaultSlot the default slot (currently held item) when the player joins the hot bar.
	 */
	public GUIHotBar(int defaultSlot) {
		this.defaultSlot = Math.max(0, Math.min(8, defaultSlot));
		
		BukkitEvent.register(this);
	}

	/**
	 * Disables this hot bar.
	 * @param clearPlayerMenuItems if the items of this hot bar should be removed from the players inventories.
	 */
	public void disable(boolean clearPlayerMenuItems) {
		removeAllPlayers(clearPlayerMenuItems);

		BukkitEvent.unregister(this);
	}
	
	/**
	 * Add the item to this hot bar menu. if there is already players hooked to this hot bar, the item will be directly added to
	 * their inventories.
	 * @param i the item stack.
	 * @param setter code executed to put the item in the inventory. Additionally, check for permission before doing the addition.
	 * @param run the Runnable to run when the user right-click on the item in the hot bar.
	 * @return itself for daisy-chaining.
	 */
	public GUIHotBar addItem(ItemStack i, BiConsumer<PlayerInventory, ItemStack> setter, Consumer<Player> run) {
		itemsAndSetters.put(i, setter);
		itemsAndClickListeners.put(i, run);
		
		for (Player p : currentPlayers)
			addItemToPlayer(p, i);
		
		return this;
	}
	
	/**
	 * Add the hot bar elements to this player, or update them if applicable.
	 * <br>
	 * The player is automatically removed when they quit. You can remove it before by calling {@link #removePlayer(Player, boolean)}.
	 * @param p the player to add.
	 */
	public void addPlayer(Player p) {
		if (!currentPlayers.contains(p))
			currentPlayers.add(p);
		
		for (ItemStack is : itemsAndSetters.keySet()) {
			addItemToPlayer(p, is);
		}
		
		p.getInventory().setHeldItemSlot(defaultSlot);
	}
	
	/**
	 * Detach this player from this hot bar manager and removes the managed items from the players inventory.
	 * @param p the player to remove.
	 */
	public void removePlayer(Player p) {
		removePlayer(p, true);
	}

	/**
	 * Detach this player from this hot bar manager and optionally removes the managed items from the players inventory.
	 * @param p the player to remove.
	 * @param clearMenuItems if the items from this hot bar should be removed from the player inventory.
	 */
	public void removePlayer(Player p, boolean clearMenuItems) {
		if (!currentPlayers.contains(p))
			return;

		if (clearMenuItems) {
			for (ItemStack is : itemsAndSetters.keySet()) {
				removeItemFromPlayer(p, is);
			}
		}
		
		currentPlayers.remove(p);

	}

	/**
	 * Tells if the provided player is attached to this hot bar.
	 * @param p the player to check.
	 * @return true if the player is attached, false otherwise.
	 */
	public boolean containsPlayer(Player p) {
		return currentPlayers.contains(p);
	}


	/**
	 * Detach all players from this hot bar.
	 */
	public void removeAllPlayers() {
		removeAllPlayers(true);
	}

	/**
	 * Detach all players from this hot bar.
	 * @param clearMenuItems if the items from this hot bar should be removed from the player inventory.
	 */
	public void removeAllPlayers(boolean clearMenuItems) {
		for (Player p : new ArrayList<>(currentPlayers))
			removePlayer(p, clearMenuItems);
	}
	
	
	
	
	
	private void addItemToPlayer(Player p, ItemStack is) {
		if (!itemsAndSetters.containsKey(is))
			throw new IllegalArgumentException("The provided ItemStack is not registered in this GUIHotBar");
		if (!currentPlayers.contains(p))
			throw new IllegalArgumentException("The provided Player is not registered in this GUIHotBar");
		itemsAndSetters.get(is).accept(p.getInventory(), is.clone());
	}
	
	private void removeItemFromPlayer(Player p, ItemStack is) {
		p.getInventory().remove(is);
	}
	
	
	
	

	@EventHandler
	void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!currentPlayers.contains(event.getPlayer()))
			return;
		
		ItemStack item = event.getItemDrop().getItemStack();
		for (ItemStack managed : itemsAndSetters.keySet()) {
			if (item.isSimilar(managed)) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	
	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		if (!currentPlayers.contains(event.getPlayer()))
			return;
		
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		
		ItemStack item = event.getItem();
		if (item == null)
			return;
		
		Player p = event.getPlayer();
		
		for (ItemStack is : itemsAndClickListeners.keySet()) {
			if (item.isSimilar(is)) {
				try {
					itemsAndClickListeners.get(is).accept(p);
				} catch (Exception e) {
					Log.severe(e);
				}
				event.setCancelled(true);
				return;
			}
		}
	}
	
	
	
	@EventHandler
	void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null || !(event.getClickedInventory() instanceof PlayerInventory inv))
			return;

		if (!currentPlayers.contains((Player) inv.getHolder()))
			return;
		
		ItemStack item = event.getCurrentItem();
		
		for (ItemStack is : itemsAndSetters.keySet()) {
			if (item != null && item.isSimilar(is)) {
				try {
					itemsAndClickListeners.get(is).accept((Player) inv.getHolder());
				} catch (Exception e) {
					Log.severe(e);
				}
				event.setCancelled(true);
				return;
			}
		}
	}
	

	
	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
	}
	
	
	@EventHandler
	void onPlayerDeath(PlayerDeathEvent event) {
		if (!currentPlayers.contains(event.getEntity()))
			return;
		event.getDrops().removeAll(itemsAndSetters.keySet());
	}
	
	@EventHandler
	void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!currentPlayers.contains(event.getPlayer()))
			return;

		addPlayer(event.getPlayer());
	}
	
	
}
