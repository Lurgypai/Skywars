package net.arcanemc.skywars2;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.arcanemc.corev2.commands.Command;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class Commands {
	
	@Command(names = "swchest", permission = "skywars.chest", noPerms = "Admin only.", usage = "/swchest <type>")
	public void swchest(@Inject(data = Inject.Data.Sender, nullable = false) Player sender) {
		String type = "";
		ItemStack item = new ItemStack(Material.CHEST, 1, (byte)0);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("SW Chest: " + type);
		item.setItemMeta(meta);
		NBTTagCompound nbttc = new NBTTagCompound();
		
		if(type.equalsIgnoreCase("spawn")) {
			nbttc.setInt("swtype", 0);
		}else if(type.equalsIgnoreCase("outer")) {
			nbttc.setInt("swtype", 1);
		} else if (type.equalsIgnoreCase("inner")) {
			nbttc.setInt("swtype", 2);
		} else if (type.equalsIgnoreCase("center")) {
			nbttc.setInt("swtype", 3);
		}
		
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		nmsItem.setTag(nbttc);
		
		item = CraftItemStack.asBukkitCopy(nmsItem);
		sender.getInventory().addItem(item);
	}
}
