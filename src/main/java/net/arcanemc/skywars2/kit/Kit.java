package net.arcanemc.skywars2.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class Kit {
	
	private ItemStack item;
	private String name;
	private String desc;
	private String tag;
	private KitExecutor execute;
	
	public Kit(String name_, String desc_, String tag_, Material mat_, int num, KitExecutor execute_) {
		this.name = name_;
		this.desc = desc_;
		this.tag = tag_;
		this.item = Kit.generateItem(name, desc, num, mat_, tag);
		this.execute = execute_;
	}
	
	public void onUseItem(Player user) {
		execute.execute(user);
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public static ItemStack generateItem(String name, String desc, int num, Material mat, String tag) {
		ItemStack item =  new ItemStack(mat, num, (byte)0);
		
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbttc = new NBTTagCompound();
		nbttc.setBoolean("kitItem", true);
		nbttc.setBoolean(tag, true);
		nmsItem.setTag(nbttc);
		item = CraftItemStack.asBukkitCopy(nmsItem);
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE + name);
		List<String> lore =  new ArrayList<String>();
		lore.add(ChatColor.DARK_PURPLE + desc);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
}
