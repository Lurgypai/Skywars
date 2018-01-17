package net.arcanemc.skywars2;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LootPool {
	
	private static boolean exists;
	private ArrayList<ItemStack> spawn = new ArrayList<ItemStack>();
	private ArrayList<ItemStack> outer = new ArrayList<ItemStack>();
	private ArrayList<ItemStack> inner = new ArrayList<ItemStack>();
	private ArrayList<ItemStack> center = new ArrayList<ItemStack>();
	private final int NUMITEMS = 5;
	public static Optional<LootPool> obtain() {
		if(!exists) {
			return Optional.of(new LootPool());
		} else {
			return Optional.ofNullable(null);
		}
	}
	
	private LootPool() {
		//spawn chests
		spawn.add(makeItem(Material.STONE_AXE));
		spawn.add(makeItem(Material.GOLD_AXE));
		spawn.add(makeItem(Material.WOOD, 16, (byte)4));
		spawn.add(makeItem(Material.SNOW_BALL, 8));
		spawn.add(makeItem(Material.SNOW_BALL, 16));
		spawn.add(makeItem(Material.STONE_SPADE));
		spawn.add(makeItem(Material.WOOD_PICKAXE));
		spawn.add(makeItem(Material.GOLD_PICKAXE));
		spawn.add(makeItem(Material.LEATHER_BOOTS));
		spawn.add(makeItem(Material.LEATHER_CHESTPLATE));
		spawn.add(makeItem(Material.GOLD_HELMET));
		spawn.add(makeItem(Material.IRON_INGOT, 3));
		spawn.add(makeItem(Material.IRON_INGOT, 5));
		spawn.add(makeItem(Material.DIAMOND));
		spawn.add(makeItem(Material.DIAMOND, 3));
		spawn.add(makeItem(Material.EXP_BOTTLE, 16));
		spawn.add(makeItem(Material.BOW));
		spawn.add(makeItem(Material.ARROW, 4));
		spawn.add(makeItem(Material.ARROW, 4));
		spawn.add(makeItem(Material.ARROW, 4));
		spawn.add(makeItem(Material.APPLE, 4));
		spawn.add(makeItem(Material.APPLE, 4));
		spawn.add(makeItem(Material.APPLE, 3));
		//outer
		outer.add(makeItem(Material.STONE_AXE));
		outer.add(makeItem(Material.STONE_SWORD));
		outer.add(makeItem(Material.GOLD_SWORD));
		outer.add(makeItem(Material.IRON_AXE));
		outer.add(makeItem(Material.EGG, 16));
		outer.add(makeItem(Material.BOW));
		outer.add(makeItem(Material.ARROW, 16));
		outer.add(makeItem(Material.ARROW, 4));
		outer.add(makeItem(Material.ARROW, 4));
		outer.add(makeItem(Material.IRON_PICKAXE));
		outer.add(makeItem(Material.GOLD_CHESTPLATE));
		outer.add(makeItem(Material.GOLD_LEGGINGS));
		outer.add(makeItem(Material.CHAINMAIL_CHESTPLATE));
		outer.add(makeItem(Material.CHAINMAIL_BOOTS));
		outer.add(makeItem(Material.CHAINMAIL_HELMET));
		outer.add(makeItem(Material.BRICK, 32));
		outer.add(makeItem(Material.BRICK, 48));
		outer.add(makeItem(Material.DIAMOND, 2));
		outer.add(makeItem(Material.DIAMOND, 5));
		outer.add(makeItem(Material.DIAMOND, 7));
		outer.add(makeItem(Material.FLINT_AND_STEEL));
		outer.add(makeItem(Material.WATER_BUCKET));
		outer.add(makeItem(Material.GOLDEN_CARROT, 4));
		outer.add(makeItem(Material.GOLDEN_CARROT, 3));
		outer.add(makeItem(Material.GOLDEN_CARROT, 3));
		//inner
		inner.add(makeItem(Material.IRON_SWORD));
		inner.add(makeItem(Material.IRON_AXE));
		inner.add(makeItem(Material.DIAMOND_PICKAXE));
		inner.add(makeItem(Material.BOW));
		inner.add(makeItem(Material.BOW));
		inner.add(makeItem(Material.BOW));
		inner.add(makeItem(Material.ARROW, 32));
		inner.add(makeItem(Material.ARROW, 32));
		inner.add(makeItem(Material.ARROW, 32));
		inner.add(makeItem(Material.ARROW, 32));
		inner.add(makeItem(Material.ARROW, 64));
		inner.add(makeItem(Material.BRICK, 32));
		inner.add(makeItem(Material.BRICK, 48));
		inner.add(makeItem(Material.DIAMOND, 2));
		inner.add(makeItem(Material.DIAMOND, 5));
		inner.add(makeItem(Material.DIAMOND, 7));
		inner.add(makeItem(Material.IRON_BOOTS));
		inner.add(makeItem(Material.IRON_LEGGINGS));
		inner.add(makeItem(Material.IRON_CHESTPLATE));
		inner.add(makeItem(Material.IRON_HELMET));
		inner.add(makeItem(Material.IRON_BOOTS));
		inner.add(makeItem(Material.IRON_LEGGINGS));
		inner.add(makeItem(Material.IRON_CHESTPLATE));
		inner.add(makeItem(Material.IRON_HELMET));
		inner.add(makeItem(Material.DIAMOND_HELMET));
		inner.add(makeItem(Material.DIAMOND_HELMET));
		inner.add(makeItem(Material.FLINT_AND_STEEL));
		inner.add(makeItem(Material.WATER_BUCKET));
		inner.add(makeItem(Material.LAVA_BUCKET));
		inner.add(makeItem(Material.COOKED_BEEF, 2));
		inner.add(makeItem(Material.COOKED_BEEF, 2));
		inner.add(makeItem(Material.COOKED_BEEF, 2));
		inner.add(makeItem(Material.COOKED_BEEF, 2));
		//center
		center.add(makeItem(Material.DIAMOND_AXE));
		center.add(makeItem(Material.DIAMOND_PICKAXE));
		center.add(makeItem(Material.DIAMOND_SWORD));
		center.add(makeItem(Material.DIAMOND_HELMET));
		center.add(makeItem(Material.DIAMOND_LEGGINGS));
		center.add(makeItem(Material.DIAMOND_BOOTS));
		center.add(makeItem(Material.EXP_BOTTLE, 32));
		center.add(makeItem(Material.OBSIDIAN, 10));
		center.add(makeItem(Material.TNT, 16));
		center.add(makeItem(Material.LAVA_BUCKET));
		center.add(makeItem(Material.COOKED_BEEF, 5));
		center.add(makeItem(Material.COOKED_BEEF, 5));
	}
	
	public static enum Level{
		SPAWN,
		OUTER_RING,
		INNER_RING,
		CENTER;	
	}
	
	public void generateLoot(Chest chest, Level l) {
		Inventory inv = chest.getBlockInventory();
		inv.clear();
		ArrayList<Integer> slots = Skywars.generateRandomOrder(NUMITEMS, 0, inv.getSize());
		
		switch(l) {
		case SPAWN:
			ArrayList<Integer> sitems = Skywars.generateRandomOrder(NUMITEMS, 0, spawn.size());
			for(int i = 0; i != NUMITEMS; i++) {
				inv.setItem(slots.get(i), spawn.get(sitems.get(i)));
			}
			break;
		case OUTER_RING:
			ArrayList<Integer> oitems = Skywars.generateRandomOrder(NUMITEMS, 0, outer.size());
			for(int i = 0; i != NUMITEMS; i++) {
				inv.setItem(slots.get(i), outer.get(oitems.get(i)));
			}
			break;
		case INNER_RING:
			ArrayList<Integer> iitems = Skywars.generateRandomOrder(NUMITEMS, 0, inner.size());
			for(int i = 0; i != NUMITEMS; i++) {
				inv.setItem(slots.get(i), inner.get(iitems.get(i)));
			}
			break;
		case CENTER:
			ArrayList<Integer> citems = Skywars.generateRandomOrder(NUMITEMS, 0, center.size());
			for(int i = 0; i != NUMITEMS; i++) {
				inv.setItem(slots.get(i), center.get(citems.get(i)));
			}
			break;
		}
	}
	
	private ItemStack makeItem(String name, Material type, int number, byte dataValue) {
		ItemStack item = new ItemStack(type, number, dataValue);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack makeItem(Material type, int number, byte dataValue) {
		ItemStack item = new ItemStack(type, number, dataValue);
		return item;
	}
	
	private ItemStack makeItem(Material type, int number) {
		ItemStack item = new ItemStack(type, number, (byte)0);
		return item;
	}
	
	private ItemStack makeItem(Material type) {
		ItemStack item = new ItemStack(type, 1, (byte)0);
		return item;
	}
}
