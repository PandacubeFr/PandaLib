package fr.pandacube.lib.paper.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableMap;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.core.players.IPlayerManager;
import fr.pandacube.lib.util.Log;
import fr.pandacube.lib.paper.util.ItemStackBuilder;

public class GUIInventory implements Listener {
	
	public static final Map<Enchantment, Integer> FAKE_ENCHANT = ImmutableMap.of(Enchantment.DURABILITY, 1);

	private final Player player;
	private final Inventory inv;
	private Consumer<InventoryCloseEvent> onCloseEvent;
	private boolean isOpened = false;
	private final Map<Integer, Consumer<InventoryClickEvent>> onClickEvents;

	public GUIInventory(Player p, int nbLines, Chat title, Consumer<InventoryCloseEvent> closeEventAction,
			Plugin pl) {
		if (title == null)
			inv = Bukkit.createInventory(null, nbLines * 9);
		else
			inv = Bukkit.createInventory(null, nbLines * 9, title.getAdv());
		
		if (IPlayerManager.getInstance().get(p.getUniqueId()).isBedrockClient()) {
			Log.warning("Opening GUI inventory for player on Bedrock client " + p.getName() + " (" + p.getUniqueId() + "). Please use a Form instead.", new Throwable());
		}

		setCloseEvent(closeEventAction);

		onClickEvents = new HashMap<>();

		player = p;

		Bukkit.getPluginManager().registerEvents(this, pl);

	}
	
	protected void setCloseEvent(Consumer<InventoryCloseEvent> closeEventAction) {
		onCloseEvent = closeEventAction;
	}

	/**
	 * @param clickEventActions (l'event passé en paramètre de la méthode done a
	 *        été pré-annulée. Pour la rétablir, faites un
	 *        event.setCancelled(false)).
	 */
	public void setButtonIfEmpty(int p, ItemStack iStack, Consumer<InventoryClickEvent> clickEventActions) {
		if (inv.getItem(p) == null)
			setButton(p, iStack, clickEventActions);
	}

	/**
	 * @param clickEventActions (l'event passé en paramètre de la méthode done a
	 *        été pré-annulée. Pour la rétablir, faites un
	 *        event.setCancelled(false)).
	 */
	public void setButton(int p, ItemStack iStack, Consumer<InventoryClickEvent> clickEventActions) {
		inv.setItem(p, iStack);
		changeClickEventAction(p, clickEventActions);
	}

	/**
	 * @param clickEventActions (l'event passé en paramètre de la méthode done a
	 *        été pré-annulée. Pour la rétablir, faites un
	 *        event.setCancelled(false)).
	 */
	public void changeClickEventAction(int p, Consumer<InventoryClickEvent> clickEventActions) {
		onClickEvents.put(p, clickEventActions);
	}

	public ItemStack getItemStack(int p) {
		return inv.getItem(p);
	}

	public void open() {
		if (isOpened) return;
		player.openInventory(inv);
		isOpened = true;
	}
	
	public void forceClose() {
		if (!isOpened) return;
		player.closeInventory(); // internally calls the InventoryCloseEvent
	}
	
	public boolean isOpen() {
		return isOpened;
	}

	public void clear() {
		onClickEvents.clear();
		inv.clear();
	}

	public void clear(int firstElement, int nbElement) {
		for (int i = firstElement; i < firstElement + nbElement; i++) {
			inv.setItem(i, null);
			onClickEvents.remove(i);
		}
	}

	public Inventory getInventory() {
		return inv;
	}

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
	
	
	
	

	
	
	
	

	public static ItemStack buildButton(ItemStack base, Integer amount, Chat displayName,
			List<Chat> lores, Map<Enchantment, Integer> enchantments) {
		
		ItemStackBuilder iStackBuilder = ItemStackBuilder.of(base);
		
		if (amount != null)
			iStackBuilder.amount(amount);
		if (displayName != null)
			iStackBuilder.displayName(displayName);
		if (lores != null)
			iStackBuilder.lore(lores);
		if (enchantments != null) {
			if (enchantments == FAKE_ENCHANT)
				iStackBuilder.fakeEnchant();
			else {
				for (Entry<Enchantment, Integer> e : enchantments.entrySet()) {
					iStackBuilder.enchant(e.getKey(), e.getValue());
				}
			}
		}
		
		return iStackBuilder.build();
	}
	public static ItemStack buildButton(ItemStack base, Chat displayName, List<Chat> lores, Map<Enchantment, Integer> enchantments) {
		return buildButton(base, null, displayName, lores, enchantments);
	}
	public static ItemStack buildButton(ItemStack base, Integer amount, Chat displayName, Map<Enchantment, Integer> enchantments) {
		return buildButton(base, amount, displayName, null, enchantments);
	}
	public static ItemStack buildButton(ItemStack base, Chat displayName, Map<Enchantment, Integer> enchantments) {
		return buildButton(base, null, displayName, null, enchantments);
	}
	public static ItemStack buildButton(ItemStack base, Integer amount, Chat displayName, List<Chat> lores) {
		return buildButton(base, amount, displayName, lores, null);
	}
	public static ItemStack buildButton(ItemStack base, Chat displayName, List<Chat> lores) {
		return buildButton(base, null, displayName, lores, null);
	}
	public static ItemStack buildButton(ItemStack base, Integer amount, Chat displayName) {
		return buildButton(base, amount, displayName, null, null);
	}
	public static ItemStack buildButton(ItemStack base, Chat displayName) {
		return buildButton(base, null, displayName, null, null);
	}
	public static ItemStack buildButton(ItemStack base, Integer amount) {
		return buildButton(base, amount, null, null, null);
	}
	
	
	

	public static ItemStack buildButton(Material m, int amount, Chat displayName, List<Chat> lores, Map<Enchantment, Integer> enchantments) {
		return buildButton(new ItemStack(m, amount), displayName, lores, enchantments);
	}
	public static ItemStack buildButton(Material m, Chat displayName, List<Chat> lores, Map<Enchantment, Integer> enchantments) {
		return buildButton(m, 1, displayName, lores, enchantments);
	}
	public static ItemStack buildButton(Material m, int amount, Chat displayName, Map<Enchantment, Integer> enchantments) {
		return buildButton(m, amount, displayName, null, enchantments);
	}
	public static ItemStack buildButton(Material m, Chat displayName, Map<Enchantment, Integer> enchantments) {
		return buildButton(m, 1, displayName, null, enchantments);
	}
	public static ItemStack buildButton(Material m, int amount, Chat displayName, List<Chat> lores) {
		return buildButton(m, amount, displayName, lores, null);
	}
	public static ItemStack buildButton(Material m, Chat displayName, List<Chat> lores) {
		return buildButton(m, 1, displayName, lores, null);
	}
	public static ItemStack buildButton(Material m, int amount, Chat displayName) {
		return buildButton(m, amount, displayName, null, null);
	}
	public static ItemStack buildButton(Material m, Chat displayName) {
		return buildButton(m, 1, displayName, null, null);
	}
	public static ItemStack buildButton(Material m, int amount) {
		return buildButton(m, amount, null, null, null);
	}
	
	
	


	
	

	
	
	public static int nbLineForNbElements(int nb) {
		return nb / 9 + ((nb % 9 == 0) ? 0 : 1);
	}
	
	

}
