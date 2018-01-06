package net.arcanemc.skywars2.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitManager implements Listener {
	
	private final int ROWS = 3;
	private final int COLUMNS = 9;
	private ArrayList<Kit> kits = new ArrayList<Kit>();
	private HashMap<UUID, String> playerKits = new HashMap<UUID, String>();
	
	public KitManager() {
		/*add kits with format:
		 * name, description, tag, material, executor
		 * the executor implements KitExecutor, and is called when the item is clicked in game
		*/
		kits.add(new Kit("Poison", "Poison nearby players.", "poison", Material.GHAST_TEAR, new PoisonKitExecutor()));
	}
	
	@EventHandler
	public void click(PlayerInteractEvent e) {
		ItemStack item = e.getPlayer().getItemInHand();
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		if((nmsItem != null) && nmsItem.getTag().hasKey("kitItem")) {
			if(nmsItem.getTag().hasKey("selectkit")) {
				generateGui(e.getPlayer());
			} else {
				for(Kit kit : kits) {
					if(nmsItem.getTag().hasKey(kit.getTag())) {
						kit.onUseItem(e.getPlayer());
					}
				}
			}	
		}
	}
	
	@EventHandler
	public void selectItem(InventoryClickEvent e) {
		if(e.getClickedInventory().getName() == "Kits") {
			net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(e.getCurrentItem());
			if(nmsItem.getTag().hasKey("kitItem")) {
				for(Kit kit : kits) {
					if(nmsItem.getTag().hasKey(kit.getTag())) {
						playerKits.put(e.getWhoClicked().getUniqueId(), kit.getTag());
						e.getWhoClicked().sendMessage(ChatColor.GOLD + "Selected \"" + kit.getItem().getItemMeta().getDisplayName() + ChatColor.GOLD + "\"");
					}
				}
			}
			e.setCancelled(true);
		}
	}
	
	public void generateGui(Player player) {
		Inventory inv = Bukkit.createInventory(player, ROWS * COLUMNS, "Kits");
		int row = 0;
		int column = 0;
		for(Kit kit : kits) {
			inv.setItem((row * ROWS) + column, kit.getItem());
			column++;
			if(column == COLUMNS) {
				column = 0;
				row++;
			}
		}
		player.openInventory(inv);
	}
	
	public void givePlayerKit(Player player) {
		if(playerKits.containsKey(player.getUniqueId())) {
			for(Kit kit : kits) {
				if(playerKits.get(player.getUniqueId()) == kit.getTag()) {
					player.getInventory().addItem(kit.getItem());
				}
			}
		}
	}
}
