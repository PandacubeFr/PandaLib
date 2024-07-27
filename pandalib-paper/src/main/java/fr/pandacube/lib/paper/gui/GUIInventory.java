package fr.pandacube.lib.paper.gui;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.paper.PandaLibPaper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An inventory based GUI.
 */
public class GUIInventory implements Listener {

	private final Player player;
	private final Inventory inv;
	private Consumer<InventoryCloseEvent> onCloseEvent;
	private boolean isOpened = false;
	private final Map<Integer, Consumer<InventoryClickEvent>> onClickEvents;

	/**
	 * Create a new inventory based GUI.
	 * @param p the player for which to create the GUI.
	 * @param nbLines the number of inventory lines for the interface.
	 * @param title the title of the GUI (title of the inventory)
	 * @param closeEventAction the action to perform when the player closes the GUI inventory
	 */
	public GUIInventory(Player p, int nbLines, Chat title, Consumer<InventoryCloseEvent> closeEventAction) {
		if (title == null)
			inv = Bukkit.createInventory(null, nbLines * 9);
		else
			inv = Bukkit.createInventory(null, nbLines * 9, title.get());

		setCloseEvent(closeEventAction);

		onClickEvents = new HashMap<>();

		player = p;

		Bukkit.getPluginManager().registerEvents(this, PandaLibPaper.getPlugin());

	}
	
	protected void setCloseEvent(Consumer<InventoryCloseEvent> closeEventAction) {
		onCloseEvent = closeEventAction;
	}

	/**
	 * Set a button on the provided slot, if this slot is still empty.
	 * @param p the slot index.
	 * @param iStack the item to put in the slot.
	 * @param clickEventActions the action to perform when the user clicks that button. The event passed as a parameter
	 *                         is already cancelled. It is possible to un-cancel it if needed.
	 */
	public void setButtonIfEmpty(int p, ItemStack iStack, Consumer<InventoryClickEvent> clickEventActions) {
		if (inv.getItem(p) == null)
			setButton(p, iStack, clickEventActions);
	}

	/**
	 * Set a button on the provided slot.
	 * @param p the slot index.
	 * @param iStack the item to put in the slot.
	 * @param clickEventActions the action to perform when the user clicks that button. The event passed as a parameter
	 *                         is already cancelled. It is possible to un-cancel it if needed.
	 */
	public void setButton(int p, ItemStack iStack, Consumer<InventoryClickEvent> clickEventActions) {
		inv.setItem(p, iStack);
		changeClickEventAction(p, clickEventActions);
	}

	/**
	 * Update/replace the action to perform for a specific slot.
	 * @param p the slot index.
	 * @param clickEventActions the action to perform when the user clicks that button. The event passed as a parameter
	 *                         is already cancelled. It is possible to un-cancel it if needed.
	 */
	public void changeClickEventAction(int p, Consumer<InventoryClickEvent> clickEventActions) {
		onClickEvents.put(p, clickEventActions);
	}

	/**
	 * Returns the item that is in the provided slot.
	 * @param p the slot index.
	 * @return the item that is in the provided slot.
	 */
	public ItemStack getItemStack(int p) {
		return inv.getItem(p);
	}

	/**
	 * Makes the GUI inventory appears for the player.
	 */
	public void open() {
		if (isOpened) return;
		player.openInventory(inv);
		isOpened = true;
	}

	/**
	 * Force this GUI to be closes, without the intervention of the player.
	 * The bukkit API will call the {@link InventoryCloseEvent}, triggering eventual actions associated with this event.
	 */
	public void forceClose() {
		if (!isOpened) return;
		player.closeInventory(); // internally calls the InventoryCloseEvent
	}

	/**
	 * Tells if this inventory is open for the player.
	 * @return true if this inventory is open for the player, false otherwise.
	 */
	public boolean isOpen() {
		return isOpened;
	}

	/**
	 * Clears the content of the GUI and the click event actions.
	 */
	public void clear() {
		onClickEvents.clear();
		inv.clear();
	}

	/**
	 * Clears a part of the GUI and the click event actions.
	 * @param firstElement the first element to remove.
	 * @param nbElement the number of element to remove.
	 */
	public void clear(int firstElement, int nbElement) {
		for (int i = firstElement; i < firstElement + nbElement; i++) {
			inv.setItem(i, null);
			onClickEvents.remove(i);
		}
	}

	/**
	 * Gets the underlying inventory of this GUI.
	 * @return the underlying inventory of this GUI.
	 */
	public Inventory getInventory() {
		return inv;
	}

	/**
	 * Inventory click event handler.
	 * @param event the event.
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!event.getWhoClicked().equals(player)) return;
		if (!isOpened) return;
		if (!event.getView().getTopInventory().equals(inv)) return;

		event.setCancelled(true);

		// on ne réagit pas aux clics hors de l'inventaire du dessus.
		if (event.getClickedInventory() != event.getView().getTopInventory()) return;

		Consumer<InventoryClickEvent> callback = onClickEvents.get(event.getSlot());
		if (callback != null)
			callback.accept(event);

	}

	/**
	 * Inventory close event handler.
	 * @param event the event.
	 */
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!event.getPlayer().equals(player)) return;
		if (!isOpened) return;
		if (!event.getView().getTopInventory().equals(inv)) return;

		HandlerList.unregisterAll(this);

		if (onCloseEvent != null)
			onCloseEvent.accept(event);
		isOpened = false;
	}
	
	

	

	
	
	public static int nbLineForNbElements(int nb) {
		return nb / 9 + ((nb % 9 == 0) ? 0 : 1);
	}
	
	

}
