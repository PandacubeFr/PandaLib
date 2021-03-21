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

import fr.pandacube.lib.core.util.Log;
import fr.pandacube.lib.paper.util.BukkitEvent;

/**
 * Managed a « lobby » type hotbar menu/inventory. It represents items in the player inventory on which you can right click on it.
 * The player can't move or drop these items.
 *
 */
public class GUIHotBar implements Listener {

	private Map<ItemStack, BiConsumer<PlayerInventory, ItemStack>> itemsAndSetters = new HashMap<>();
	
	private Map<ItemStack, Consumer<Player>> itemsAndRunnables = new HashMap<>();
	
	private final int defltSlot;
	
	private List<Player> currentPlayers = new ArrayList<>();
	
	public GUIHotBar(int defaultSlot) {
		defltSlot = Math.max(0, Math.min(8, defaultSlot));
		
		BukkitEvent.register(this);
	}
	
	/**
	 * Add the item to this hotbar menu. if there is already players hooked to this hotbar, the item will be directly added to
	 * their inventories.
	 * @param i the item stack
	 * @param setter code executed to put the item in the inventory. Additionally check for permission before doing the addition.
	 * @param run the Runnable to run when the user right click on the item in the hotbar.
	 * @return
	 */
	public GUIHotBar addItem(ItemStack i, BiConsumer<PlayerInventory, ItemStack> setter, Consumer<Player> run) {
		itemsAndSetters.put(i, setter);
		itemsAndRunnables.put(i, run);
		
		for (Player p : currentPlayers)
			addItemToPlayer(p, i);
		
		return this;
	}
	
	/**
	 * Add the hotbar elements to this player.
	 * 
	 * The players is automatically removed when it quit. You can remove it before by calling {@link #removePlayer(Player)}.
	 * @param p
	 */
	public void addPlayer(Player p) {
		if (!currentPlayers.contains(p))
			currentPlayers.add(p);
		
		for (ItemStack is : itemsAndSetters.keySet()) {
			addItemToPlayer(p, is);
		}
		
		p.getInventory().setHeldItemSlot(defltSlot);
	}
	
	/**
	 * Detach this player from this hotbar manager and removes the managed items from the players inventory.
	 * @param p
	 */
	public void removePlayer(Player p) {
		if (!currentPlayers.contains(p))
			return;
		
		for (ItemStack is : itemsAndSetters.keySet()) {
			removeItemFromPlayer(p, is);
		}
		
		currentPlayers.remove(p);

	}
	
	
	public boolean containsPlayer(Player p) {
		return currentPlayers.contains(p);
	}
	
	
	
	public void removeAllPlayers() {
		for (Player p : new ArrayList<>(currentPlayers))
			removePlayer(p);
	}
	
	
	
	
	
	public void addItemToPlayer(Player p, ItemStack is) {
		if (!itemsAndSetters.containsKey(is))
			throw new IllegalArgumentException("The provided ItemStack is not registered in this HotbarMenu");
		if (!currentPlayers.contains(p))
			throw new IllegalArgumentException("The provided Player is not registered in this HotbarMenu");
		itemsAndSetters.get(is).accept(p.getInventory(), is.clone());
	}
	
	public void removeItemFromPlayer(Player p, ItemStack is) {
		p.getInventory().remove(is);
	}
	
	
	
	

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!currentPlayers.contains(event.getPlayer()))
			return;
		
		ItemStack item = event.getItemDrop().getItemStack();
		for (ItemStack managed : itemsAndSetters.keySet()) {
			if (item != null && item.isSimilar(managed)) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!currentPlayers.contains(event.getPlayer()))
			return;
		
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		
		ItemStack item = event.getItem();
		if (item == null)
			return;
		
		Player p = event.getPlayer();
		
		for (ItemStack is : itemsAndRunnables.keySet()) {
			if (item.isSimilar(is)) {
				try {
					itemsAndRunnables.get(is).accept(p);
				} catch (Exception e) {
					Log.severe(e);
				}
				event.setCancelled(true);
				return;
			}
		}
	}
	
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null || !(event.getClickedInventory() instanceof PlayerInventory))
			return;
		
		PlayerInventory inv = (PlayerInventory) event.getClickedInventory();
		
		if (!currentPlayers.contains(inv.getHolder()))
			return;
		
		ItemStack item = event.getCurrentItem();
		
		for (ItemStack is : itemsAndSetters.keySet()) {
			if (item != null && item.isSimilar(is)) {
				try {
					itemsAndRunnables.get(is).accept((Player) inv.getHolder());
				} catch (Exception e) {
					Log.severe(e);
				}
				event.setCancelled(true);
				return;
			}
		}
	}
	

	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
	}
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!currentPlayers.contains(event.getEntity()))
			return;
		event.getDrops().removeAll(itemsAndSetters.keySet());
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!currentPlayers.contains(event.getPlayer()))
			return;

		currentPlayers.remove(event.getPlayer());
		addPlayer(event.getPlayer());
	}
	
	
}
